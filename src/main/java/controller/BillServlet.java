package controller;

import dao.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.ByteArrayOutputStream;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@WebServlet("/admin/bill")
@MultipartConfig(
    maxFileSize = 1024 * 1024 * 5, // 5MB max file size
    maxRequestSize = 1024 * 1024 * 10 // 10MB max request size
)
public class BillServlet extends HttpServlet {

    private final BillDAO billDAO = new BillDAO();
    private final BillDetailDAO detailDAO = new BillDetailDAO();
    private final ContractDAO contractDAO = new ContractDAO();
    private final UtilityReadingDAO readingDAO = new UtilityReadingDAO();
    private final IncurredFeeDAO feeDAO = new IncurredFeeDAO();
    private final UtilityTypeDAO utilityTypeDAO = new UtilityTypeDAO();
    private final BlockDAO blockDAO = new BlockDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    IncurredFeeTypeDAO incurredFeeTypeDAO = new IncurredFeeTypeDAO();

    private LocalDate toLocalDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate(); // ✅ KHÔNG lỗi
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // fallback
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String step = req.getParameter("step");
        String blockIdStr = req.getParameter("blockId");
        String roomIdStr = req.getParameter("roomId");
        String contractIdStr = req.getParameter("contractId");
        String month = req.getParameter("month");
        if (month == null) {
            month = LocalDate.now().toString().substring(0, 7); // yyyy-MM
        }
        req.setAttribute("selectedMonth", month); // ✅ chứ không phải selectedMonth riêng
        req.setAttribute("month", month); // THÊM DÒNG NÀY để đảm bảo JSP nào cũng dùng được

        try {
            // --------- STEP 2-BƯỚC LẬP HÓA ĐƠN (STEPPER) ---------
            if ("step".equals(action) || req.getRequestURI().contains("bill-step")) {
                // Block list (luôn có)
                List<Block> blockList = blockDAO.getAllBlocks();
                req.setAttribute("blockList", blockList);
                List<IncurredFeeType> feeTypeList = incurredFeeTypeDAO.getAll();
                req.setAttribute("feeTypeList", feeTypeList);

                // Nếu đã chọn block, show roomList
                List<Room> roomList = new ArrayList<>();
                if (blockIdStr != null && !blockIdStr.isEmpty()) {
                    int blockId = Integer.parseInt(blockIdStr);
                    List<Room> allRooms = roomDAO.getRoomsByBlockId(blockId);
                    List<Contract> activeContracts = contractDAO.getActiveContractsInMonth(month);

                    Map<Integer, Contract> roomToContract = new HashMap<>();
                    for (Contract c : activeContracts) {
                        roomToContract.put(c.getRoomId(), c);
                    }
                    for (Room room : allRooms) {
                        Contract c = roomToContract.get(room.getRoomID());
                        if (c != null) {
                            room.setActiveContractCode(c.getContractId() + "");
                            room.setHasRecord(readingDAO.hasUtilityReading(room.getRoomID(), month));
                            room.setHasBill(billDAO.existsBillForRoomAndMonth(room.getRoomID(), month));
                            roomList.add(room);
                        }
                    }
                }
                req.setAttribute("blockId", blockIdStr);
                req.setAttribute("roomList", roomList);
                req.setAttribute("step", step != null ? step : "1");
                req.setAttribute("month", month);

                // === Bước 2: Chọn phòng để lập hóa đơn ===
                if ("2".equals(step) && roomIdStr != null && !roomIdStr.isEmpty()) {
                    int roomId = Integer.parseInt(roomIdStr);

                    // Lấy phòng và gán thuộc tính
                    Room room = roomDAO.getRoomById(roomId);
                    req.setAttribute("room", room);

                    // Lấy hợp đồng đang hoạt động của phòng
                    Contract activeContract = contractDAO.getActiveContractOfRoomInMonth(roomId, month);
                    if (activeContract == null) {
                        resp.sendRedirect(req.getContextPath() + "/admin/bill?step=1&action=step&blockId=" + blockIdStr + "&err=missing-contract");
                        return;
                    }
                    req.setAttribute("activeContract", activeContract);

                    // Lấy chỉ số tiện ích đã ghi
                    List<UtilityReading> readings = readingDAO.getReadingsByRoomAndMonth(roomId, month);
                    // ✅ Thêm đoạn này ngay sau đó:
                    boolean isEditable = true;
                    boolean canCreateBill = false;

                    for (UtilityReading ur : readings) {
                        Timestamp createdAt = ur.getUtilityReadingCreatedAt();
                        if (createdAt != null) {
                            LocalDate createdDate = createdAt.toLocalDateTime().toLocalDate();
                            LocalDate today = LocalDate.now();
                            long days = ChronoUnit.DAYS.between(createdDate, today);
                            if (days >= 3) {
                                isEditable = false;
                                canCreateBill = true;
                                break;
                            }
                        }
                    }
                    req.setAttribute("isEditable", isEditable);
                    req.setAttribute("canCreateBill", canCreateBill);
                    List<UtilityType> allTypes = utilityTypeDAO.getAll();

                    Map<Integer, UtilityReading> readingMap = new HashMap<>();
                    for (UtilityReading ur : readings) {
                        readingMap.put(ur.getUtilityTypeID(), ur);
                    }

                    List<Map<String, Object>> showList = new ArrayList<>();
                    double totalAmount = 0;

                    for (UtilityType ut : allTypes) {
                        Map<String, Object> m = new HashMap<>();
                        m.put("name", ut.getUtilityName());

                        UtilityReading ur = readingMap.get(ut.getUtilityTypeID());
                        String unitType = ut.getUnit();
                        String typeName = ut.getUtilityName().toLowerCase(); // đã lowercase
                        boolean isFree = false;

                        if (typeName.contains("electricity") && room.getIsElectricityFree() == 0) {
                            isFree = true;
                        }
                        if (typeName.contains("water") && room.getIsWaterFree() == 0) {
                            isFree = true;
                        }
                        if (typeName.contains("wifi") && room.getIsWifiFree() == 0) {
                            isFree = true;
                        }
                        if (typeName.contains("trash") && room.getIsTrashFree() == 0) {
                            isFree = true;
                        }

                        if (isFree) {
                            // Trường hợp tiện ích miễn phí
                            m.put("oldIndex", 0.0);
                            m.put("newIndex", 0.0);
                            m.put("used", 0.0);
                            m.put("unitPrice", 0.0);
                            m.put("amount", 0.0);
                        } else if ("month".equalsIgnoreCase(unitType)) {
                            // Gói cố định
                            m.put("oldIndex", null);
                            m.put("newIndex", null);
                            m.put("used", 1.0);

                            double priceUsed = (ur != null && ur.getPriceUsed() != null) ? ur.getPriceUsed().doubleValue() : 0.0;
                            m.put("unitPrice", priceUsed);
                            m.put("amount", priceUsed);
                            totalAmount += priceUsed;

                        } else {
                            // Tính theo chỉ số
                            double oldIdx = (ur != null && ur.getOldReading() != null) ? ur.getOldReading().doubleValue() : 0.0;
                            double newIdx = (ur != null && ur.getNewReading() != null) ? ur.getNewReading().doubleValue() : 0.0;
                            double used = newIdx - oldIdx;

                            double priceUsed = (ur != null && ur.getPriceUsed() != null) ? ur.getPriceUsed().doubleValue() : 0.0;
                            double unitPrice = (ur != null && ur.getOldPrice() != null) ? ur.getOldPrice().doubleValue() : 0.0;

                            m.put("oldIndex", oldIdx);
                            m.put("newIndex", newIdx);
                            m.put("used", used);
                            m.put("unitPrice", unitPrice);
                            m.put("amount", priceUsed);
                            totalAmount += priceUsed;
                        }

                        showList.add(m);
                    }

                    // Gửi tiện ích ra giao diện
                    req.setAttribute("utilityTypes", showList);

                    // Phụ phí
                    List<IncurredFee> fees = feeDAO.getFeesByRoomAndMonth(roomId, month);
                    BigDecimal totalFee = BigDecimal.ZERO;
                    for (IncurredFee f : fees) {
                        totalFee = totalFee.add(f.getAmount());
                    }

                    req.setAttribute("extraFee", fees.size() > 0 ? fees.get(0).getAmount() : "");
                    req.setAttribute("fees", fees);
                    totalAmount += totalFee.doubleValue();
                    req.setAttribute("totalAmount", totalAmount);
                }

                req.setAttribute("step", step != null ? step : "1");
                req.setAttribute("month", month);

                req.getRequestDispatcher("/admin/bill-step.jsp").forward(req, resp);
                return;
            }

            // --------- TẠO BILL TRUYỀN THỐNG (action=create) ----------
            if ("create".equals(action)) {
                // Có thể tái sử dụng phần cũ nếu muốn cho phép tạo hóa đơn truyền thống
                String selectedMonth = (month != null) ? month : LocalDate.now().toString().substring(0, 7);

                String blockId = blockIdStr;
                String roomId = roomIdStr;

                List<Block> blocks = blockDAO.getAllBlocks();
                req.setAttribute("blocks", blocks);

                List<Object[]> rooms = new ArrayList<>();
                if (blockId != null && !blockId.isEmpty()) {
                    rooms = roomDAO.getRoomIdNameByBlock(Integer.parseInt(blockId));
                }
                req.setAttribute("rooms", rooms);

                // 1. Lấy hợp đồng active (nên dùng 1 biến contract, không phải List)
                List<Contract> contracts = new ArrayList<>();
                if (roomId != null && !roomId.isEmpty()) {
                    Contract activeContract = contractDAO.getActiveContractOfRoomInMonth(Integer.parseInt(roomId), selectedMonth);
                    if (activeContract != null) {
                        contracts.add(activeContract);
                    }
                }

// 2. Tiện ích
                List<UtilityType> utilityTypes = utilityTypeDAO.getAll();
                // Tiện ích
                List<UtilityReading> readings;
                if (roomId != null && !roomId.isEmpty()) {
                    readings = readingDAO.getReadingsByRoomAndMonth(Integer.parseInt(roomId), selectedMonth);
                } else {
                    readings = new ArrayList<>();
                }

// 3. Phụ phí
                List<IncurredFee> fees;
                if (roomId != null && !roomId.isEmpty()) {
                    fees = feeDAO.getFeesByRoomAndMonth(Integer.parseInt(roomId), selectedMonth);
                } else {
                    fees = new ArrayList<>();
                }

                req.setAttribute("utilityTypes", utilityTypes);
                req.setAttribute("readings", readings);
                req.setAttribute("fees", fees);

                boolean billExists = (roomId != null && !roomId.isEmpty())
                        && billDAO.existsBillForRoomAndMonth(Integer.parseInt(roomId), selectedMonth);
                List<IncurredFeeType> feeTypeList = incurredFeeTypeDAO.getAll();

                req.setAttribute("selectedBlockId", blockId);
                req.setAttribute("selectedRoomId", roomId);
                req.setAttribute("selectedContractId", contractIdStr);
                req.setAttribute("selectedMonth", selectedMonth);
                req.setAttribute("utilityTypes", utilityTypes);
                req.setAttribute("readings", readings);
                req.setAttribute("feeTypeList", feeTypeList);
                req.setAttribute("fees", fees);
                req.setAttribute("billExists", billExists);

                Contract selectedContract = null;
                if (contractIdStr != null && !contractIdStr.isEmpty()) {
                    for (Contract c : contracts) {
                        if (String.valueOf(c.getContractId()).equals(contractIdStr)) {
                            selectedContract = c;
                            break;
                        }
                    }
                }
                req.setAttribute("selectedContract", selectedContract);

                req.getRequestDispatcher("/admin/bill-step.jsp").forward(req, resp);

                return;
            }
            if ("record".equals(action)) {
                int roomId = Integer.parseInt(req.getParameter("roomId"));
                String blockId = req.getParameter("blockId");

                Room room = roomDAO.getRoomById(roomId);
                List<Block> blocks = blockDAO.getAllBlocks();
                List<Object[]> rooms = roomDAO.getRoomIdNameByBlock(Integer.parseInt(blockId));
                List<Object[]> types = utilityTypeDAO.getAllTypeIdName(); // ✅ Đúng

                req.setAttribute("room", room);
                req.setAttribute("blocks", blocks);
                req.setAttribute("rooms", rooms); // ✅ cần cho dropdown room
                req.setAttribute("types", types); // ✅ cần cho dropdown utility type
                req.setAttribute("selectedBlockId", blockId);
                req.setAttribute("selectedRoomId", String.valueOf(roomId));
                req.setAttribute("selectedTypeId", null); // chưa chọn tiện ích
                req.setAttribute("oldIndex", null);
                req.setAttribute("readingMonth", month);

                req.getRequestDispatcher("/admin/utility-record.jsp").forward(req, resp);
                return;
            }

            if ("list".equals(action) || action == null) {
               PaymentConfirmationDAO confirmationDAO = new PaymentConfirmationDAO();
        List<Map<String, Object>> pendingProofs = confirmationDAO.getPendingProofs();
        req.setAttribute("pendingProofs", pendingProofs);
                String selectedMonth = req.getParameter("month");
                if (selectedMonth == null) {
                    selectedMonth = LocalDate.now().toString().substring(0, 7); // yyyy-MM
                }

                String success = req.getParameter("success");
                if (success != null) {
                    req.setAttribute("success", success);
                }

                //int monthNumber  = Integer.parseInt(selectedMonth.substring(5, 7));
                List<Map<String, Object>> billSummary = billDAO.getBillSummaryByMonth(selectedMonth);

                req.setAttribute("selectedMonth", selectedMonth);
                req.setAttribute("billSummary", billSummary);
                System.out.println(billSummary);
                BillFeedbackDAO feedbackDAO = new BillFeedbackDAO();
                List<BillFeedback> pendingFeedbacks = feedbackDAO.getAllPendingFeedbacks();
                req.setAttribute("pendingFeedbacks", pendingFeedbacks);
                req.getRequestDispatcher("/admin/bill-list.jsp").forward(req, resp);
                return;
            }// Add this block in doGet
    if ("viewProof".equals(action)) {
        try {
            int confId = Integer.parseInt(req.getParameter("confId"));
            PaymentConfirmationDAO dao = new PaymentConfirmationDAO();
            PaymentConfirmation conf = dao.getById(confId);
            if (conf != null && conf.getImageData() != null) {
                byte[] data = conf.getImageData();
            String contentType = "application/octet-stream"; // Fallback

            // Detect JPEG: FF D8 FF
            if (data.length >= 3 && data[0] == (byte)0xFF && data[1] == (byte)0xD8 && data[2] == (byte)0xFF) {
                contentType = "image/jpeg";
            }
            // Detect PNG: 89 50 4E 47 0D 0A 1A 0A
            else if (data.length >= 8 && data[0] == (byte)0x89 && data[1] == (byte)0x50 && data[2] == (byte)0x4E && 
                     data[3] == (byte)0x47 && data[4] == (byte)0x0D && data[5] == (byte)0x0A && 
                     data[6] == (byte)0x1A && data[7] == (byte)0x0A) {
                contentType = "image/png";
            }

            resp.setContentType(contentType);
            resp.setContentLength(data.length);
            resp.getOutputStream().write(data);
            resp.getOutputStream().flush();
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Image not found");
        }
    } catch (Exception e) {
        e.printStackTrace();
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error viewing image");
    }
    return;
    }

            if ("edit".equals(action)) {
                int billId = Integer.parseInt(req.getParameter("billId"));
                Bill bill = billDAO.getBillById(billId);
                BillDetail detail = detailDAO.getBillDetailById(billId);
                Contract contract = contractDAO.getContractWithRoomAndTenantByContractId(bill.getContractID());
                Room room = roomDAO.getRoomById(contract.getRoomId());

                String billMonth = bill.getBillDate().toLocalDate().toString().substring(0, 7);

                // ✅ Tính isLastMonth
                boolean isLastMonth = false;
                Date endDate = (Date) contract.getEndDate();
                if (endDate != null) {
                    LocalDate contractEndDate;

                    if (endDate instanceof java.sql.Date) {
                        contractEndDate = ((java.sql.Date) endDate).toLocalDate();
                    } else {
                        contractEndDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    }

                    String contractEndMonth = contractEndDate.toString().substring(0, 7);
                    isLastMonth = billMonth.equals(contractEndMonth);
                }

                req.setAttribute("isLastMonth", isLastMonth);
                req.setAttribute("selectedMonth", billMonth);
// ✅ 1. Load toàn bộ loại tiện ích (điện, nước, wifi, rác,...)
                List<UtilityType> utilityTypes = utilityTypeDAO.getAll();
                Map<Integer, UtilityType> utilityTypeMap = new HashMap<>();
                for (UtilityType ut : utilityTypes) {
                    utilityTypeMap.put(ut.getUtilityTypeID(), ut);
                }

// ✅ 2. Lấy readings từ DB theo phòng và tháng hóa đơn
                List<UtilityReading> dbReadings = readingDAO.getLatestReadingsByRoomAndMonth(room.getRoomID(), billMonth);
                Map<Integer, UtilityReading> readingMap = new HashMap<>();
                for (UtilityReading reading : dbReadings) {
                    readingMap.put(reading.getUtilityTypeID(), reading); // ghép theo loại tiện ích
                }

// ✅ 3. Tạo danh sách readings cuối cùng để hiển thị
                List<UtilityReading> finalReadings = new ArrayList<>();

                for (UtilityType ut : utilityTypes) {
                    UtilityReading ur = readingMap.get(ut.getUtilityTypeID());

                    if (ur != null) {
                        // ✅ Có dữ liệu trong DB -> dùng trực tiếp
                        finalReadings.add(ur);
                    } else if ("month".equalsIgnoreCase(ut.getUnit())) {
                        // ✅ Là tiện ích cố định như wifi/rác -> tạo bản ghi giả
                        UtilityReading dummy = new UtilityReading();
                        dummy.setUtilityTypeID(ut.getUtilityTypeID());
                        dummy.setRoomID(room.getRoomID());
                        dummy.setOldReading(BigDecimal.ZERO);
                        dummy.setNewReading(BigDecimal.ONE); // tính như đã dùng 1 đơn vị
                        dummy.setPriceUsed(ut.getUnitPrice()); // lấy đơn giá cố định
                        dummy.setChangedBy("default");
                        dummy.setReadingDate(Date.valueOf(bill.getBillDate().toLocalDate()));
                        finalReadings.add(dummy);
                    } else {
                        // ✅ Nếu là tiện ích theo số đo (điện/nước) mà không có -> báo lỗi hoặc bỏ qua
                        System.err.println("⚠️ Missing reading for utility type ID: " + ut.getUtilityTypeID() + " - " + ut.getUtilityName());
                    }
                }

                // Phụ phí
                List<IncurredFee> fees = feeDAO.getFeesByBillId(billId);
                List<IncurredFeeType> feeTypeList = incurredFeeTypeDAO.getAll();
                Map<Integer, BigDecimal> feeMap = new HashMap<>();
                for (IncurredFee fee : fees) {
                    feeMap.put(fee.getIncurredFeeTypeID(), fee.getAmount());
                }

                // Đưa vào request
                req.setAttribute("bill", bill);
                req.setAttribute("detail", detail);
                req.setAttribute("contract", contract);
                req.setAttribute("rooms", room);
                req.setAttribute("readings", finalReadings); // ✅ cập nhật readings hoàn chỉnh
                req.setAttribute("fees", fees);
                req.setAttribute("feeMap", feeMap);
                req.setAttribute("feeTypes", feeTypeList);
                req.setAttribute("utilityTypeMap", utilityTypeMap);
                req.setAttribute("editMode", true);

                req.getRequestDispatcher("/admin/bill-edit.jsp").forward(req, resp);
                return;
            }

            if ("view".equals(action)) {
                int billId = Integer.parseInt(req.getParameter("billId"));
                Bill bill = billDAO.getBillById(billId); // Use the updated getBillById method

                // Get contract and room details
                Contract contract = contractDAO.getContractWithRoomAndTenantByContractId(bill.getContractID());
                Room room = roomDAO.getRoomById(contract.getRoomId());

                String billMonth = bill.getBillDate().toLocalDate().toString().substring(0, 7);

                // Get utility readings from the Bill object (already populated by getBillById)
                List<UtilityReading> dbReadings = bill.getUtilityReadings();
                Map<Integer, UtilityReading> readingMap = new HashMap<>();
                for (UtilityReading ur : dbReadings) {
                    readingMap.put(ur.getUtilityTypeID(), ur);
                }

                // Get list of utility types
                List<UtilityType> utilityTypes = utilityTypeDAO.getAll();
                Map<Integer, UtilityType> utilityTypeMap = new HashMap<>();
                List<UtilityReading> finalReadings = new ArrayList<>();

                for (UtilityType ut : utilityTypes) {
                    utilityTypeMap.put(ut.getUtilityTypeID(), ut);

                    UtilityReading ur = readingMap.get(ut.getUtilityTypeID());
                    if (ur != null) {
                        finalReadings.add(ur);
                    } else {
                        // If monthly utility is not in DB, create a dummy reading for display
                        if ("month".equalsIgnoreCase(ut.getUnit())) {
                            UtilityReading dummy = new UtilityReading();
                            dummy.setUtilityTypeID(ut.getUtilityTypeID());
                            dummy.setRoomID(room.getRoomID());
                            dummy.setOldReading(BigDecimal.ZERO);
                            dummy.setNewReading(BigDecimal.ONE); // Assume used 1 unit
                            dummy.setPriceUsed(ut.getUnitPrice()); // Use correct unit price
                            dummy.setChangedBy("default");
                            dummy.setReadingDate(Date.valueOf(bill.getBillDate().toLocalDate()));
                            finalReadings.add(dummy);
                        }
                    }
                }

                // Get incurred fees
                List<IncurredFee> fees = feeDAO.getFeesByBillId(billId);

                // Get incurred fee types
                Map<Integer, IncurredFeeType> feeTypeMap = new HashMap<>();
                for (IncurredFeeType ft : incurredFeeTypeDAO.getAll()) {
                    feeTypeMap.put(ft.getIncurredFeeTypeID(), ft);
                }

                // Set request attributes
                req.setAttribute("bill", bill);
                req.setAttribute("contract", contract);
                req.setAttribute("room", room);
                req.setAttribute("readings", finalReadings); // Use merged readings
                req.setAttribute("fees", fees);
                req.setAttribute("utilityTypeMap", utilityTypeMap);
                req.setAttribute("feeTypeMap", feeTypeMap);

                // Check if billDate is in the last month of the contract
                boolean isLastMonth = false;
                if (contract.getEndDate() != null && bill.getBillDate() != null) {
                    YearMonth billYm = YearMonth.from(bill.getBillDate().toLocalDate());

                    LocalDate endDate;
                    if (contract.getEndDate() instanceof java.sql.Date) {
                        endDate = ((java.sql.Date) contract.getEndDate()).toLocalDate();
                    } else {
                        endDate = contract.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    }

                    YearMonth endYm = YearMonth.from(endDate);
                    isLastMonth = billYm.equals(endYm);
                }
                req.setAttribute("isLastMonth", isLastMonth);

                // Determine user role from session
                HttpSession session = req.getSession();
                String userType = (String) session.getAttribute("userType");
                String userRole = "";

                if ("Customer".equals(userType)) {
                    userRole = "customer";
                    // Load payment confirmations for customer view
                    PaymentConfirmationDAO confirmationDAO = new PaymentConfirmationDAO();
                    List<PaymentConfirmation> confirmations = confirmationDAO.getConfirmationsByBillId(billId);
                    req.setAttribute("confirmations", confirmations);
                } else if ("User".equals(userType)) {
                    Integer roleID = (Integer) session.getAttribute("roleID");
                    if (roleID != null) {
                        userRole = (roleID == 1) ? "admin" : "manager";  // Assume 1=Admin, adjust as needed
                    }
                }
                req.setAttribute("userRole", userRole);

                req.getRequestDispatcher("/admin/bill-view.jsp").forward(req, resp);
                return;
            } else if ("print".equals(action)) {
                int billId = Integer.parseInt(req.getParameter("billId"));

                Bill bill = billDAO.getBillById(billId);
                Contract contract = contractDAO.getContractWithRoomAndTenantByContractId(bill.getContractID());
                Room room = roomDAO.getRoomById(contract.getRoomId());

                String billMonth = bill.getBillDate().toLocalDate().toString().substring(0, 7);
                List<UtilityReading> dbReadings = readingDAO.getLatestReadingsByRoomAndMonth(room.getRoomID(), billMonth);

                Map<Integer, UtilityReading> readingMap = new HashMap<>();
                for (UtilityReading ur : dbReadings) {
                    readingMap.put(ur.getUtilityTypeID(), ur);
                }

                List<UtilityType> utilityTypes = utilityTypeDAO.getAll();
                Map<Integer, UtilityType> utilityTypeMap = new HashMap<>();
                List<UtilityReading> finalReadings = new ArrayList<>();
                for (UtilityType ut : utilityTypes) {
                    utilityTypeMap.put(ut.getUtilityTypeID(), ut);
                    UtilityReading ur = readingMap.get(ut.getUtilityTypeID());
                    if (ur != null) {
                        finalReadings.add(ur);
                    } else if ("month".equalsIgnoreCase(ut.getUnit())) {
                        UtilityReading dummy = new UtilityReading();
                        dummy.setUtilityTypeID(ut.getUtilityTypeID());
                        dummy.setRoomID(room.getRoomID());
                        dummy.setOldReading(BigDecimal.ZERO);
                        dummy.setNewReading(BigDecimal.ONE);
                        dummy.setPriceUsed(ut.getUnitPrice());
                        dummy.setChangedBy("default");
                        dummy.setReadingDate(Date.valueOf(bill.getBillDate().toLocalDate()));
                        finalReadings.add(dummy);
                    }
                }

                List<IncurredFee> incurredFees = feeDAO.getFeesByBillId(billId);
                Map<Integer, IncurredFeeType> feeTypeMap = new HashMap<>();
                for (IncurredFeeType ft : incurredFeeTypeDAO.getAll()) {
                    feeTypeMap.put(ft.getIncurredFeeTypeID(), ft);
                }

                boolean isLastMonth = false;
                if (contract.getEndDate() != null && bill.getBillDate() != null) {
                    YearMonth billYm = YearMonth.from(bill.getBillDate().toLocalDate());
                    LocalDate endDate = ((java.sql.Date) contract.getEndDate()).toLocalDate();
                    YearMonth endYm = YearMonth.from(endDate);
                    isLastMonth = billYm.equals(endYm);
                }

                BigDecimal deposit = contract.getDeposit() != null ? contract.getDeposit() : BigDecimal.ZERO;
                BigDecimal totalAmount = bill.getTotalAmount();
                BigDecimal amountDue = isLastMonth ? totalAmount.subtract(deposit) : totalAmount;

                req.setAttribute("bill", bill);
                req.setAttribute("contract", contract);
                req.setAttribute("room", room);
                req.setAttribute("readings", finalReadings);
                req.setAttribute("utilityTypeMap", utilityTypeMap);
                req.setAttribute("fees", incurredFees);
                req.setAttribute("feeTypeMap", feeTypeMap);
                req.setAttribute("amountDue", amountDue);
                req.setAttribute("isLastMonth", isLastMonth);

                req.getRequestDispatcher("/admin/bill-print.jsp").forward(req, resp);
                return;

            } else if ("cancel".equals(action)) {
                String billIdStr = req.getParameter("billId");
                String blockId = req.getParameter("blockId");

                if (billIdStr == null || billIdStr.isEmpty()) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu billId để hủy hóa đơn.");
                    return;
                }

                int billId = Integer.parseInt(billIdStr);
                if (blockId == null || blockId.isEmpty()) {
                    blockId = billDAO.getBlockIdByBillId(billId);
                }

                if (!billDAO.isBillSent(billId)) {
                    billDAO.deleteBillFully(billId);
                    req.getSession().setAttribute("success", "✅ Hủy hóa đơn thành công.");
                } else {
                    req.getSession().setAttribute("success", "❌ Hóa đơn đã gửi, không thể hủy.");
                }

                resp.sendRedirect(req.getContextPath() + "/admin/bill?action=list");
                return;
            }

            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Action không hợp lệ.");

        } catch (Exception e) {
            throw new ServletException("Error loading bills", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String month = req.getParameter("month");
        String contractIdStr = req.getParameter("contractId");
        String roomIdStr = req.getParameter("roomId");
        String action = req.getParameter("action");

        if ("edit".equals(action)) {
            try {
                int billId = Integer.parseInt(req.getParameter("billId"));
                float electricity = Float.parseFloat(req.getParameter("electricity"));
                float water = Float.parseFloat(req.getParameter("water"));
                float wifi = Float.parseFloat(req.getParameter("wifi"));
                float trash = Float.parseFloat(req.getParameter("trash"));
                String status = req.getParameter("status");

                Bill bill = billDAO.getBillById(billId);
                bill.setBillStatus(status);

                // ✅ Lấy Contract để truy xuất Room
                Contract contract = contractDAO.getContractById(bill.getContractID());
                Room room = roomDAO.getRoomById(contract.getRoomId());
                BigDecimal roomRent = BigDecimal.valueOf(room.getRentPrice());

                // ✅ Tính tổng tiền cơ bản
                BigDecimal totalBase = roomRent
                        .add(BigDecimal.valueOf(electricity))
                        .add(BigDecimal.valueOf(water))
                        .add(BigDecimal.valueOf(wifi))
                        .add(BigDecimal.valueOf(trash));
                bill.setTotalAmount(totalBase);
                billDAO.updateBill(bill);

                // ✅ Cập nhật UtilityReading (dữ liệu chỉ số)
                List<UtilityType> utilityTypes = utilityTypeDAO.getAll();
                int roomId = contract.getRoomId();
                String readingMonth = bill.getBillDate().toLocalDate().toString().substring(0, 7);
                Date readingDate = Date.valueOf(readingMonth + "-01");

                for (UtilityType ut : utilityTypes) {
                    BigDecimal oldReading = BigDecimal.ZERO;
                    BigDecimal newReading = BigDecimal.ONE;
                    BigDecimal priceUsed;

                    if (ut.isIndexedType()) {
                        String oldStr = req.getParameter("oldIndex_" + ut.getUtilityTypeID());
                        String newStr = req.getParameter("newIndex_" + ut.getUtilityTypeID());
                        if (oldStr == null || newStr == null) {
                            continue;
                        }

                        try {
                            oldReading = new BigDecimal(oldStr);
                            newReading = new BigDecimal(newStr);
                        } catch (Exception e) {
                            continue;
                        }

                        priceUsed = newReading.subtract(oldReading).multiply(ut.getUnitPrice());
                    } else {
                        priceUsed = ut.getUnitPrice(); // tiện ích theo tháng
                    }

                    UtilityReading existing = readingDAO.getReading(roomId, ut.getUtilityTypeID(), readingMonth);
                    if (existing != null) {
                        existing.setOldReading(oldReading);
                        existing.setNewReading(newReading);
                        existing.setPriceUsed(priceUsed);
                        existing.setChangedBy("admin");
                        existing.setUtilityReadingCreatedAt(new Timestamp(System.currentTimeMillis()));
                        readingDAO.updateReading(existing);
                    } else {
                        UtilityReading r = new UtilityReading();
                        r.setRoomID(roomId);
                        r.setUtilityTypeID(ut.getUtilityTypeID());
                        r.setReadingDate(readingDate);
                        r.setOldReading(oldReading);
                        r.setNewReading(newReading);
                        r.setPriceUsed(priceUsed);
                        r.setChangedBy("admin");
                        r.setUtilityReadingCreatedAt(new Timestamp(System.currentTimeMillis()));
                        readingDAO.insert(r);
                    }
                }

                // ✅ Tổng tiền sau khi cộng phụ phí
                BigDecimal total = totalBase;
                List<IncurredFeeType> feeTypes = incurredFeeTypeDAO.getAll();
                feeDAO.deleteFeesByBillId(billId);

                for (IncurredFeeType ft : feeTypes) {
                    String param = req.getParameter("extraFee_" + ft.getIncurredFeeTypeID());
                    if (param != null && !param.isEmpty()) {
                        try {
                            BigDecimal amount = new BigDecimal(param);
                            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                                IncurredFee fee = new IncurredFee();
                                fee.setBillID(billId);
                                fee.setIncurredFeeTypeID(ft.getIncurredFeeTypeID());
                                fee.setAmount(amount);
                                feeDAO.insertFee(fee);
                                total = total.add(amount);
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }

                // ✅ Trừ tiền cọc nếu là tháng cuối
                BigDecimal deposit = contract.getDeposit() != null ? contract.getDeposit() : BigDecimal.ZERO;
                total = total.subtract(deposit);
                bill.setTotalAmount(total);

                billDAO.updateBill(bill);

                resp.sendRedirect(req.getContextPath() + "/admin/bill?action=list&success=updated");
                return;

            } catch (Exception e) {
                throw new ServletException("Update bill failed", e);
            }
        } else if ("send".equals(action)) {
            try {
                int billId = Integer.parseInt(req.getParameter("billId"));

                // 1. Lấy hóa đơn
                Bill bill = billDAO.getBillById(billId);
                Contract contract = contractDAO.getContractWithRoomAndTenantByContractId(bill.getContractID());
                Room room = roomDAO.getRoomById(contract.getRoomId());
                TenantDAO tenantDAO = new TenantDAO();
                int customerId = tenantDAO.getCustomerIdByTenantId(contract.getTenantId());

                String billMonth = bill.getBillDate().toLocalDate().toString().substring(0, 7);

                // 2. Kiểm tra đã có tiện ích chưa
                List<UtilityReading> readings = bill.getUtilityReadings();
                if (readings == null || readings.isEmpty()) {
                    req.getSession().setAttribute("error", "❌ Hóa đơn chưa được lập đầy đủ (thiếu tiện ích). Vui lòng kiểm tra lại.");
                    resp.sendRedirect(req.getContextPath() + "/admin/bill?action=list");
                    return;
                }

                // 3. Kiểm tra đã gửi chưa
                if (billDAO.isBillSent(billId)) {
                    req.getSession().setAttribute("success", "📬 Hóa đơn đã được gửi trước đó.");
                    resp.sendRedirect(req.getContextPath() + "/admin/bill?action=list");
                    return;
                }

                // 4. Tính toán tổng tiền
                BigDecimal roomRent = bill.getRoomRent() != null ? bill.getRoomRent() : BigDecimal.ZERO;

                BigDecimal utilityTotal = BigDecimal.ZERO;
                for (UtilityReading ur : readings) {
                    if (ur.getPriceUsed() != null) {
                        utilityTotal = utilityTotal.add(ur.getPriceUsed());
                    }
                }

                List<IncurredFee> fees = feeDAO.getFeesByBillId(billId);
                BigDecimal feeTotal = BigDecimal.ZERO;
                for (IncurredFee fee : fees) {
                    if (fee.getAmount() != null) {
                        feeTotal = feeTotal.add(fee.getAmount());
                    }
                }

                BigDecimal deposit = contract.getDeposit() != null ? contract.getDeposit() : BigDecimal.ZERO;

                // 5. Kiểm tra tháng cuối cùng
                boolean isLastMonth = false;
                if (contract.getEndDate() != null && bill.getBillDate() != null) {
                    YearMonth billYm = YearMonth.from(bill.getBillDate().toLocalDate());
                    LocalDate endDate;
                    if (contract.getEndDate() instanceof java.sql.Date) {
                        endDate = ((java.sql.Date) contract.getEndDate()).toLocalDate();
                    } else {
                        endDate = contract.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    }
                    YearMonth endYm = YearMonth.from(endDate);
                    isLastMonth = billYm.equals(endYm);
                }

                BigDecimal totalAmount = roomRent.add(utilityTotal).add(feeTotal);
                BigDecimal dueAmount = isLastMonth ? totalAmount.subtract(deposit) : totalAmount;

                // ⚠️ Log thông tin để kiểm tra
                System.out.println("👉 Room rent: " + roomRent);
                System.out.println("👉 Utility total: " + utilityTotal);
                System.out.println("👉 Fee total: " + feeTotal);
                System.out.println("👉 Deposit: " + deposit);
                System.out.println("👉 Tổng tiền: " + totalAmount);
                System.out.println("👉 Phải thu: " + dueAmount);
                System.out.println("👉 Là tháng cuối?: " + isLastMonth);

                // 6. Gửi thông báo
                Notification noti = new Notification();
                noti.setCustomerID(customerId);

                noti.setTitle("📄 Hóa đơn mới tháng " + billMonth);
                String detailLink = String.format(
                        "<a href='http://localhost:8080/HomeNest/admin/bill?action=view&billId=%d&fromNotification=true' target='_blank'>Xem chi tiết hóa đơn</a>",
                        billId
                );

                noti.setMessage(String.format(
                        "Hóa đơn cho phòng %s đã được tạo. Tổng tiền phải thanh toán: %sđ. %s",
                        contract.getRoomNumber(),
                        dueAmount.toPlainString(),
                        detailLink
                ));

                noti.setRead(false);
                noti.setNotificationCreatedAt(new Timestamp(System.currentTimeMillis()));

                HttpSession session = req.getSession(false);
                Integer adminUserId = (session != null && session.getAttribute("userId") != null)
                        ? (Integer) session.getAttribute("userId")
                        : null;
                noti.setSentBy(adminUserId);

                NotificationDAO notificationDAO = new NotificationDAO();
                notificationDAO.insert(noti);

                // 7. Đánh dấu đã gửi
                billDAO.markBillAsSent(billId);

                // 8. Thông báo thành công
                req.getSession().setAttribute("success", "✅ Hóa đơn đã được gửi đến người thuê.");
                resp.sendRedirect(req.getContextPath() + "/admin/bill?action=list");
                return;

            } catch (Exception e) {
                e.printStackTrace();
                req.getSession().setAttribute("error", "❌ Lỗi khi gửi hóa đơn: " + e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/admin/bill?action=list");
                return;
            }
        } else if ("markPaid".equals(action)) {
            try {
                int billID = Integer.parseInt(req.getParameter("billID"));

                BillDAO billDAO = new BillDAO();

                boolean updated = billDAO.markBillAsPaid(billID);
                if (updated) {
                    req.getSession().setAttribute("success", "Bill marked as PAID successfully.");
                    resp.sendRedirect("bill?action=view&billId=" + billID);
                    return;
                } else {
                    req.getSession().setAttribute("error", "Failed to update bill status.");
                    resp.sendRedirect("bill?action=view&billId=" + billID);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                req.getSession().setAttribute("error", "An error occurred while updating bill status.");
                resp.sendRedirect("bill?action=list");
                return;
            }
        } else if ("uploadProof".equals(action)) {
            String billIdStr = req.getParameter("billId");
            int billId = Integer.parseInt(billIdStr);
            try {
                Part filePart = req.getPart("proofImage");
                if (filePart == null || filePart.getSize() <= 0) {
                    req.getSession().setAttribute("error", "❌ No file uploaded or file is empty.");
                    resp.sendRedirect(req.getContextPath() + "/admin/bill?action=view&billId=" + billId);
                    return;
                }

                try (InputStream inputStream = filePart.getInputStream();
                     ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) > -1) {
                        baos.write(buffer, 0, len);
                    }
                    baos.flush();
                    byte[] imageData = baos.toByteArray();

                    // Get bill and contract
                    Bill bill = billDAO.getBillById(billId);
                    if (bill == null) {
                        throw new ServletException("Bill not found for ID: " + billId);
                    }
                    Contract contract = contractDAO.getContractById(bill.getContractID());
                    if (contract == null) {
                        throw new ServletException("Contract not found for Bill ID: " + billId);
                    }

                    // Get tenantId from contract (fixed method name to standard camelCase)
                    int tenantId = contract.getTenantId();
                    if (tenantId <= 0) {
                        throw new ServletException("Invalid Tenant ID: " + tenantId);
                    }

                    // Get customerId for notification
                    TenantDAO tenantDAO = new TenantDAO();
                    int customerId = tenantDAO.getCustomerIdByTenantId(tenantId);
                    if (customerId <= 0) {
                        throw new ServletException("Customer not found for Tenant ID: " + tenantId);
                    }

                    // Get or create payment
                    PaymentDAO paymentDAO = new PaymentDAO();
                    int paymentId = paymentDAO.getOrCreatePaymentForBill(billId);
                    if (paymentId <= 0) {
                        throw new ServletException("Failed to get or create Payment for Bill ID: " + billId);
                    }

                    // Insert confirmation
                    PaymentConfirmation conf = new PaymentConfirmation();
                    conf.setPaymentID(paymentId);
                    conf.setTenantID(tenantId);
                    conf.setImageData(imageData);
                    conf.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    PaymentConfirmationDAO confirmationDAO = new PaymentConfirmationDAO();
                    int confirmationId = confirmationDAO.insert(conf);
                    if (confirmationId <= 0) {
                        throw new ServletException("Failed to insert PaymentConfirmation for Payment ID: " + paymentId);
                    }

                    // Create notification for customer
                    Notification noti = new Notification();
                    noti.setCustomerID(customerId);
                    noti.setTitle("📄 Payment Proof Uploaded");
                    noti.setMessage(String.format(
                        "Payment proof for bill #%d (Room %s) has been successfully uploaded. Awaiting admin approval.",
                        bill.getBillID(), contract.getRoomNumber()
                    ));
                    noti.setRead(false);
                    noti.setNotificationCreatedAt(new Timestamp(System.currentTimeMillis()));
                    HttpSession session = req.getSession(false);
                    Integer adminUserId = (session != null && session.getAttribute("userId") != null)
                        ? (Integer) session.getAttribute("userId")
                        : null;
                    noti.setSentBy(adminUserId); // Optional: null if sent by system
                    NotificationDAO notificationDAO = new NotificationDAO();
                    notificationDAO.insert(noti);

                    // Set success message and redirect back to bill view
                    req.getSession().setAttribute("success", "✅ Upload proof successful! Notification sent.");
                    resp.sendRedirect(req.getContextPath() + "/admin/bill?action=view&billId=" + billId);
                }
            } catch (Exception e) {
                e.printStackTrace();
                req.getSession().setAttribute("error", "❌ Upload failed: " + e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/admin/bill?action=view&billId=" + billId);
            }
            return;
        }
        // ✅ Fix: Nếu month là null, "null", hoặc rỗng → dùng tháng hiện tại
        if (month == null || month.trim().isEmpty() || "null".equalsIgnoreCase(month)) {
            month = LocalDate.now().toString().substring(0, 7); // yyyy-MM
        }

        try {
            if (contractIdStr == null || roomIdStr == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/bill?action=step&step=2&err=missing-info");
                return;
            }

            int contractId = Integer.parseInt(contractIdStr);
            int roomId = Integer.parseInt(roomIdStr);

            // ĐÃ FIX: kiểm tra bill có chưa
            if (billDAO.existsBillForRoomAndMonth(roomId, month)) {
                resp.sendRedirect(req.getContextPath() + "/admin/bill?action=create&err=exists"
                        + "&contractId=" + contractId + "&roomId=" + roomId + "&month=" + month);
                return;
            }

            // ĐÃ FIX: kiểm tra đủ chỉ số điện nước chưa
            List<UtilityType> utilityTypes = utilityTypeDAO.getAll();
            for (UtilityType ut : utilityTypes) {
                if (ut.isIndexedType()) {
                    UtilityReading ur = readingDAO.getReading(roomId, ut.getUtilityTypeID(), month);
                    if (ur == null) {
                        resp.sendRedirect(req.getContextPath() + "/admin/bill?action=step&step=2"
                                + "&err=missing-reading&typeId=" + ut.getUtilityTypeID()
                                + "&roomId=" + roomId
                                + "&month=" + month
                                + "&contractId=" + contractId);
                        return;
                    }
                }
            }

            Contract contract = contractDAO.getContractById(contractId);
            if (contract == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/bill?action=step&step=2"
                        + "&err=missing-contract"
                        + "&roomId=" + roomId
                        + "&month=" + month);
                return;
            }

            // ✅ Xử lý null cho startDate
            if (contract.getStartDate() == null) {
                throw new ServletException("Hợp đồng không có ngày bắt đầu");
            }
            LocalDate contractStart = toLocalDate(contract.getStartDate());

            // ✅ Xử lý null cho endDate
            LocalDate contractEnd = (contract.getEndDate() != null)
                    ? toLocalDate(contract.getEndDate())
                    : null;

            YearMonth ym = YearMonth.parse(month);
            LocalDate firstDay = ym.atDay(1);
            LocalDate lastDay = ym.atEndOfMonth();

            LocalDate fromDate = (contractStart.isAfter(firstDay)) ? contractStart : firstDay;
            LocalDate toDate = (contractEnd != null && contractEnd.isBefore(lastDay)) ? contractEnd : lastDay;

            int daysInMonth = ym.lengthOfMonth();
            int realDays = (int) ChronoUnit.DAYS.between(fromDate, toDate) + 1;

            // ✅ Xử lý null cho rentPrice
            BigDecimal rentPrice = (contract.getRoomRent() != null)
                    ? contract.getRoomRent()
                    : BigDecimal.ZERO;

            // ✅ Tính tiền thuê phòng theo ngày
            BigDecimal roomRent = rentPrice.multiply(BigDecimal.valueOf(realDays))
                    .divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP);

            // ✅ Lấy tiện ích và phụ phí
            List<UtilityReading> readings = readingDAO.getReadingsByRoomAndMonth(roomId, month);
            List<IncurredFee> fees = feeDAO.getFeesByRoomAndMonth(roomId, month);

            BigDecimal totalElectric = BigDecimal.ZERO;
            BigDecimal totalWater = BigDecimal.ZERO;
            BigDecimal totalWifi = BigDecimal.ZERO;

            for (UtilityReading ur : readings) {
                UtilityType ut = utilityTypeDAO.getById(ur.getUtilityTypeID());
                if (ut == null) {
                    continue; // tránh lỗi nếu tiện ích không tìm thấy
                }
                BigDecimal priceUsed = (ur.getPriceUsed() != null) ? ur.getPriceUsed() : BigDecimal.ZERO;
                String n = ut.getUtilityName().toLowerCase();

                if (n.contains("điện") || n.contains("electric")) {
                    totalElectric = totalElectric.add(priceUsed);
                } else if (n.contains("nước") || n.contains("water")) {
                    totalWater = totalWater.add(priceUsed);
                } else if (n.contains("wifi")) {
                    totalWifi = totalWifi.add(priceUsed);
                }
            }

            // ✅ Tính tổng hóa đơn ban đầu
            BigDecimal total = roomRent.add(totalElectric).add(totalWater).add(totalWifi);
            for (IncurredFee f : fees) {
                total = total.add((f.getAmount() != null) ? f.getAmount() : BigDecimal.ZERO);
            }

            // ✅ Trừ tiền cọc nếu là tháng cuối
            if (contractEnd != null && YearMonth.from(contractEnd).equals(ym)) {
                total = total.subtract((contract.getDeposit() != null)
                        ? contract.getDeposit()
                        : BigDecimal.ZERO);
            }

            // ✅ Ghi hóa đơn ban đầu
            Bill bill = new Bill();
            bill.setContractID(contractId);
            bill.setBillDate(new java.sql.Date(System.currentTimeMillis()));
            bill.setTotalAmount(total);
            bill.setBillStatus("Unpaid");

            int billId = billDAO.createBill(bill);

            // ✅ Ghi phụ phí phát sinh từ form
            for (IncurredFeeType feeType : incurredFeeTypeDAO.getAll()) {
                String paramName = "extraFee_" + feeType.getIncurredFeeTypeID();
                String val = req.getParameter(paramName);
                if (val != null && !val.isEmpty()) {
                    try {
                        BigDecimal amount = new BigDecimal(val);
                        if (amount.compareTo(BigDecimal.ZERO) > 0) {
                            IncurredFee fee = new IncurredFee();
                            fee.setBillID(billId);
                            fee.setIncurredFeeTypeID(feeType.getIncurredFeeTypeID());
                            fee.setAmount(amount);

                            feeDAO.insertFee(fee);
                            total = total.add(amount); // cộng thêm phụ phí vừa thêm
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            // ✅ Cập nhật tổng sau khi cộng thêm phụ phí mới
            bill.setTotalAmount(total);
            billDAO.updateBill(bill);

            resp.sendRedirect(req.getContextPath() + "/admin/bill?action=step&step=2&month=" + month
                    + "&roomId=" + roomId + "&contractId=" + contractId);

        } catch (Exception e) {
            throw new ServletException("Error generating bills", e);
        }

    }
}