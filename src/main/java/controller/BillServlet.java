package controller;

import dao.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@WebServlet("/admin/bill")
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
                    Room room = roomDAO.getRoomById(roomId);
                    req.setAttribute("room", room);

// Hợp đồng active
                    Contract activeContract = contractDAO.getActiveContractOfRoomInMonth(roomId, month);
                    if (activeContract == null) {
                        resp.sendRedirect(req.getContextPath() + "/admin/bill?step=1&action=step&blockId=" + blockIdStr + "&err=missing-contract");
                        return;
                    }

                    req.setAttribute("activeContract", activeContract);

// Lấy danh sách tiện ích và các chỉ số đã ghi
                    List<UtilityReading> readings = readingDAO.getReadingsByRoomAndMonth(roomId, month);
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
                        m.put("unitPrice", ut.getUnitPrice().doubleValue());

                        if ("month".equalsIgnoreCase(ut.getUnit())) {
                            // Gói cước cố định (wifi/trash)
                            m.put("oldIndex", null);
                            m.put("newIndex", null);
                            m.put("used", 1.0);
                            m.put("amount", ut.getUnitPrice().doubleValue());
                        } else {
                            UtilityReading ur = readingMap.get(ut.getUtilityTypeID());
                            double oldIdx = ur != null ? ur.getOldReading().doubleValue() : 0;
                            double newIdx = ur != null ? ur.getNewReading().doubleValue() : 0;
                            double used = newIdx - oldIdx;
                            double amount = used * ut.getUnitPrice().doubleValue();

                            m.put("oldIndex", oldIdx);
                            m.put("newIndex", newIdx);
                            m.put("used", used);
                            m.put("amount", amount);
                        }

                        totalAmount += (Double) m.get("amount");
                        showList.add(m);
                    }

                    req.setAttribute("utilityTypes", showList);
                    req.setAttribute("totalAmount", totalAmount);

// Phụ phí
                    List<IncurredFee> fees = feeDAO.getFeesByRoomAndMonth(roomId, month);
                    req.setAttribute("extraFee", fees.size() > 0 ? fees.get(0).getAmount() : "");
                    BigDecimal totalFee = BigDecimal.ZERO;
                    for (IncurredFee f : fees) {
                        totalFee = totalFee.add(f.getAmount());
                    }
                    totalAmount += totalFee.doubleValue();

                    req.setAttribute("fees", fees); // để hiển thị
                    req.setAttribute("totalAmount", totalAmount); // CẬP NHẬT

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

                // --- Tiện ích đã ghi/tháng ---
//                List<UtilityType> utilityTypes = utilityTypeDAO.getAll();
//                List<UtilityReading> readings = (roomIdStr != null && !roomIdStr.isEmpty())
//                        ? readingDAO.getReadingsByRoomAndMonth(Integer.parseInt(roomIdStr), selectedMonth)
//                        : Collections.<UtilityReading>emptyList();
//
//                // --- Phụ phí đã nhập ---
//                List<IncurredFee> fees = (roomIdStr != null && !roomIdStr.isEmpty())
//                        ? feeDAO.getFeesByRoomAndMonth(Integer.parseInt(roomIdStr), selectedMonth)
//                        : Collections.<IncurredFee>emptyList();
//
//                // --- Kiểm tra đã có bill chưa ---
//                boolean billExists = (roomIdStr != null && !roomIdStr.isEmpty())
//                        && billDAO.existsBillForRoomAndMonth(Integer.parseInt(roomIdStr), selectedMonth);
//                List<IncurredFeeType> feeTypeList = incurredFeeTypeDAO.getAll();
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

            // --------- DANH SÁCH BILL (action=list hoặc mặc định) ---------
//            if ("list".equals(action) || action == null) {
//                String thisMonth = LocalDate.now().toString().substring(0, 7); // yyyy-MM
//                List<Contract> contracts = contractDAO.getAllContracts();
//
//                Map<Integer, Bill> contractToBillMap = new HashMap<>();
//                Map<Integer, BillDetail> billDetails = new HashMap<>();
//                Map<Integer, List<IncurredFee>> billToFees = new HashMap<>();
//                Map<Integer, List<UtilityReading>> contractToReadings = new HashMap<>();
//
//                List<UtilityType> utilityTypes = utilityTypeDAO.getAll();
//                Map<Integer, UtilityType> utilityTypeMap = new HashMap<>();
//                for (UtilityType ut : utilityTypes) {
//                    utilityTypeMap.put(ut.getUtilityTypeID(), ut);
//                }
//
//                for (Contract c : contracts) {
//                    Bill bill = billDAO.getLatestBillByContract(c.getContractId());
//                    if (bill != null) {
//                        int billId = bill.getBillID();
//                        contractToBillMap.put(c.getContractId(), bill);
//                        billDetails.put(billId, detailDAO.getBillDetailById(billId));
//                        billToFees.put(billId, feeDAO.getFeesByBillId(billId));
//
//                        List<UtilityReading> readings = readingDAO.getReadingsByRoomAndMonth(c.getRoomId(), thisMonth);
//                        contractToReadings.put(c.getContractId(), readings);
//                    }
//                }
//
//                req.setAttribute("contracts", contracts);
//                req.setAttribute("contractToBillMap", contractToBillMap);
//                req.setAttribute("billDetails", billDetails);
//                req.setAttribute("billToFees", billToFees);
//                req.setAttribute("billToReadings", contractToReadings);
//                req.setAttribute("utilityTypeMap", utilityTypeMap);
//
//                req.getRequestDispatcher("/admin/bill-list.jsp").forward(req, resp);
//
//                return;
//            }
//if ("list".equals(action) || action == null) {
//    List<Bill> bills = billDAO.getAllBills(); // ✅ Lấy toàn bộ hóa đơn
//
//    Map<Integer, Contract> contractMap = new HashMap<>();
//    Map<Integer, BillDetail> billDetails = new HashMap<>();
//    Map<Integer, List<IncurredFee>> billToFees = new HashMap<>();
//    Map<Integer, List<UtilityReading>> contractToReadings = new HashMap<>();
//    Map<Integer, UtilityType> utilityTypeMap = new HashMap<>();
//
//    for (Contract c : contractDAO.getAllContracts()) {
//        contractMap.put(c.getContractId(), c);
//    }
//
//    for (Bill b : bills) {
//        int billId = b.getBillID();
//        Contract c = contractMap.get(b.getContractID());
//
//        billDetails.put(billId, detailDAO.getBillDetailById(billId));
//        billToFees.put(billId, feeDAO.getFeesByBillId(billId));
//
//        // lấy reading theo room và tháng của bill
//        String months = b.getBillDate().toLocalDate().toString().substring(0, 7);
//        if (c != null) {
//            List<UtilityReading> readings = readingDAO.getReadingsByRoomAndMonth(c.getRoomId(), months);
//            contractToReadings.put(c.getContractId(), readings);
//        }
//    }
//
//    for (UtilityType ut : utilityTypeDAO.getAll()) {
//        utilityTypeMap.put(ut.getUtilityTypeID(), ut);
//    }
//
//    req.setAttribute("bills", bills);
//    req.setAttribute("contractMap", contractMap);
//    req.setAttribute("billDetails", billDetails);
//    req.setAttribute("billToFees", billToFees);
//    req.setAttribute("billToReadings", contractToReadings); // để khớp với JSP
//
//    req.setAttribute("utilityTypeMap", utilityTypeMap);
//req.setAttribute("success", "Hóa đơn đã được lưu thành công!");
//    req.getRequestDispatcher("/admin/bill-list.jsp").forward(req, resp);
//}
            if ("list".equals(action) || action == null) {
                String selectedMonth = req.getParameter("month");
                if (selectedMonth == null) {
                    selectedMonth = LocalDate.now().toString().substring(0, 7);
                }

                String success = req.getParameter("success"); // ✅ NEW
                if (success != null) {
                    req.setAttribute("success", success); // ✅ NEW
                }

                List<Map<String, Object>> billSummary = billDAO.getBillSummaryByMonth(selectedMonth);

                req.setAttribute("selectedMonth", selectedMonth);
                req.setAttribute("billSummary", billSummary);
                req.getRequestDispatcher("/admin/bill-list.jsp").forward(req, resp);
                return; // ✅ nên có return ở đây
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

                // ✅ Dữ liệu readings từ DB
                List<UtilityReading> dbReadings = readingDAO.getLatestReadingsByRoomAndMonth(room.getRoomID(), billMonth);
                Map<Integer, UtilityReading> readingMap = new HashMap<>();
                for (UtilityReading ur : dbReadings) {
                    readingMap.put(ur.getUtilityTypeID(), ur);
                }

                // ✅ Load tất cả loại tiện ích
                List<UtilityType> utilityTypes = utilityTypeDAO.getAll();
                Map<Integer, UtilityType> utilityTypeMap = new HashMap<>();
                List<UtilityReading> finalReadings = new ArrayList<>();

                for (UtilityType ut : utilityTypes) {
                    utilityTypeMap.put(ut.getUtilityTypeID(), ut);
                    UtilityReading ur = readingMap.get(ut.getUtilityTypeID());
                    if (ur != null) {
                        finalReadings.add(ur); // đã có dữ liệu
                    } else if ("month".equalsIgnoreCase(ut.getUnit())) {
                        // ✅ Nếu là tiện ích cố định (wifi, rác), tự tạo bản ghi hiển thị
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
                Bill bill = billDAO.getBillById(billId);
                BillDetail detail = detailDAO.getBillDetailById(billId);

                Contract contract = contractDAO.getContractWithRoomAndTenantByContractId(bill.getContractID());
                Room room = roomDAO.getRoomById(contract.getRoomId());

                String billMonth = bill.getBillDate().toLocalDate().toString().substring(0, 7);

                // Lấy readings thực tế từ DB
                List<UtilityReading> dbReadings = readingDAO.getLatestReadingsByRoomAndMonth(room.getRoomID(), billMonth);
                Map<Integer, UtilityReading> readingMap = new HashMap<>();
                for (UtilityReading ur : dbReadings) {
                    readingMap.put(ur.getUtilityTypeID(), ur);
                }

                // Lấy danh sách tiện ích cố định
                List<UtilityType> utilityTypes = utilityTypeDAO.getAll();
                Map<Integer, UtilityType> utilityTypeMap = new HashMap<>();
                List<UtilityReading> finalReadings = new ArrayList<>();

                for (UtilityType ut : utilityTypes) {
                    utilityTypeMap.put(ut.getUtilityTypeID(), ut);

                    UtilityReading ur = readingMap.get(ut.getUtilityTypeID());
                    if (ur != null) {
                        finalReadings.add(ur);
                    } else {
                        // Nếu tiện ích theo tháng mà không có trong DB, tạo giả để hiển thị
                        if ("month".equalsIgnoreCase(ut.getUnit())) {
                            UtilityReading dummy = new UtilityReading();
                            dummy.setUtilityTypeID(ut.getUtilityTypeID());
                            dummy.setRoomID(room.getRoomID());
                            dummy.setOldReading(BigDecimal.ZERO);
                            dummy.setNewReading(BigDecimal.ONE); // đã dùng 1
                            dummy.setPriceUsed(ut.getUnitPrice()); // lấy đúng đơn giá
                            dummy.setChangedBy("default");
                            dummy.setReadingDate(Date.valueOf(bill.getBillDate().toLocalDate()));
                            finalReadings.add(dummy);
                        }
                    }
                }

                List<IncurredFee> fees = feeDAO.getFeesByBillId(billId);

                Map<Integer, IncurredFeeType> feeTypeMap = new HashMap<>();
                for (IncurredFeeType ft : incurredFeeTypeDAO.getAll()) {
                    feeTypeMap.put(ft.getIncurredFeeTypeID(), ft);
                }

                req.setAttribute("bill", bill);
                req.setAttribute("detail", detail);
                req.setAttribute("contract", contract);
                req.setAttribute("room", room);
                req.setAttribute("readings", finalReadings); // dùng readings đã merge
                req.setAttribute("fees", fees);
                req.setAttribute("utilityTypeMap", utilityTypeMap);
                req.setAttribute("feeTypeMap", feeTypeMap);
// ✅ Kiểm tra nếu billDate thuộc tháng cuối của hợp đồng
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

                req.getRequestDispatcher("/admin/bill-view.jsp").forward(req, resp);
                return;
            } else if ("print".equals(action)) {
                int billId = Integer.parseInt(req.getParameter("billId"));

                Bill bill = billDAO.getBillById(billId);
                BillDetail detail = detailDAO.getBillDetailById(billId);
                Contract contract = contractDAO.getContractWithRoomAndTenantByContractId(bill.getContractID());
                Room room = roomDAO.getRoomById(contract.getRoomId());

                // Lấy tiện ích
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
                        // Nếu là tiện ích tháng như rác, wifi thì tạo reading ảo
                        UtilityReading dummy = new UtilityReading();
                        dummy.setUtilityTypeID(ut.getUtilityTypeID());
                        dummy.setRoomID(room.getRoomID());
                        dummy.setOldReading(BigDecimal.ZERO);
                        dummy.setNewReading(BigDecimal.ONE); // đã dùng 1 đơn vị
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

                // ✅ Kiểm tra nếu là tháng cuối
                boolean isLastMonth = false;
                if (contract.getEndDate() != null && bill.getBillDate() != null) {
                    YearMonth billYm = YearMonth.from(bill.getBillDate().toLocalDate());
                    LocalDate endDate = contract.getEndDate().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    YearMonth endYm = YearMonth.from(endDate);

                    isLastMonth = billYm.equals(endYm);
                }

                // ✅ Tính số tiền phải thu (có thể trừ cọc nếu là tháng cuối)
                BigDecimal deposit = contract.getDeposit() != null ? contract.getDeposit() : BigDecimal.ZERO;
                BigDecimal totalAmount = bill.getTotalAmount();

                BigDecimal amountDue = isLastMonth
                        ? totalAmount.subtract(deposit)
                        : totalAmount;

                // Gửi data sang JSP
                req.setAttribute("bill", bill);
                req.setAttribute("detail", detail);
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
                BillDAO dao = new BillDAO();

                if (blockId == null || blockId.isEmpty()) {
                    blockId = dao.getBlockIdByBillId(billId); // ✅ fallback
                }

                if (!dao.isBillSent(billId)) {
                    dao.deleteBillFully(billId);
                    req.getSession().setAttribute("success", "✅ Hủy hóa đơn thành công.");
                } else {
                    req.getSession().setAttribute("success", "❌ Hóa đơn đã gửi, không thể hủy.");
                }

                resp.sendRedirect(req.getContextPath() + "/admin/bill?action=list");

                return;
            }

            // --------- Action không hợp lệ ---------
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Action không hợp lệ.");
        } catch (ServletException | IOException | NumberFormatException | SQLException e) {
            throw new ServletException("Error loading bills", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String month = req.getParameter("month");
        String contractIdStr = req.getParameter("contractId");
        String roomIdStr = req.getParameter("roomId");

        if ("edit".equals(req.getParameter("action"))) {
            try {
                int billId = Integer.parseInt(req.getParameter("billId"));
                float roomRent = Float.parseFloat(req.getParameter("roomRent"));
                float electricity = Float.parseFloat(req.getParameter("electricity"));
                float water = Float.parseFloat(req.getParameter("water"));
                float wifi = Float.parseFloat(req.getParameter("wifi"));
                float trash = Float.parseFloat(req.getParameter("trash"));
                String status = req.getParameter("status");

                Bill bill = billDAO.getBillById(billId);
                bill.setBillStatus(status);
                BigDecimal totalBase = BigDecimal.valueOf(roomRent)
                        .add(BigDecimal.valueOf(electricity))
                        .add(BigDecimal.valueOf(water))
                        .add(BigDecimal.valueOf(wifi))
                        .add(BigDecimal.valueOf(trash));

                bill.setTotalAmount(totalBase);
// sẽ cập nhật lại sau khi cộng phụ phí
                billDAO.updateBill(bill);

                BillDetail detail = detailDAO.getBillDetailById(billId);
                detail.setRoomrent(roomRent);
                detail.setElectricityCost(electricity);
                detail.setWaterCost(water);
                detail.setWifiCost(wifi);
//        detail.setTrashCost(trash); // ✳️ nếu có cột này
                detailDAO.updateBillDetail(detail);

                // ✅ Cập nhật chỉ số tiện ích
                List<UtilityType> utilityTypes = utilityTypeDAO.getAll();
                Contract contract = contractDAO.getContractById(bill.getContractID());
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
                        priceUsed = ut.getUnitPrice(); // tiện ích theo tháng: mặc định 1 đơn vị
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

                // ✅ Cập nhật phụ phí
                BigDecimal total = BigDecimal.ZERO
                        .add(BigDecimal.valueOf(roomRent))
                        .add(BigDecimal.valueOf(electricity))
                        .add(BigDecimal.valueOf(water))
                        .add(BigDecimal.valueOf(wifi))
                        .add(BigDecimal.valueOf(trash));

                List<IncurredFeeType> feeTypes = incurredFeeTypeDAO.getAll();
                feeDAO.deleteFeesByBillId(billId); // xóa cũ để cập nhật lại

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

                // Trừ tiền cọc nếu là tháng cuối
                BigDecimal deposit = contract.getDeposit() != null ? contract.getDeposit() : BigDecimal.ZERO;
                total = total.subtract(deposit);

                bill.setTotalAmount(total);

                billDAO.updateBill(bill);

                resp.sendRedirect(req.getContextPath() + "/admin/bill?action=list&success=updated");
                return;

            } catch (Exception e) {
                throw new ServletException("Update bill failed", e);
            }
        }
        if ("send".equals(req.getParameter("action"))) {
            try {
                int billId = Integer.parseInt(req.getParameter("billId"));
// 1. Lấy hóa đơn
                Bill bill = billDAO.getBillById(billId);
                Contract contract = contractDAO.getContractWithRoomAndTenantByContractId(bill.getContractID());

// 2. Kiểm tra đã lập đủ tiện ích chưa (utility readings)
                String billMonth = bill.getBillDate().toLocalDate().toString().substring(0, 7);
                List<UtilityReading> readings = readingDAO.getLatestReadingsByRoomAndMonth(contract.getRoomId(), billMonth);

                if (readings == null || readings.isEmpty()) {
                    req.getSession().setAttribute("error", "❌ Hóa đơn chưa được lập đầy đủ (thiếu tiện ích). Vui lòng kiểm tra lại.");
                    resp.sendRedirect(req.getContextPath() + "/admin/bill?action=list");
                    return;
                }

//                // 1. Lấy thông tin bill và hợp đồng
//                Bill bill = billDAO.getBillById(billId);
//                Contract contract = contractDAO.getContractWithRoomAndTenantByContractId(bill.getContractID());
                // 2. Kiểm tra đã gửi chưa
                if (billDAO.isBillSent(billId)) {
                    req.getSession().setAttribute("success", "📬 Hóa đơn đã được gửi trước đó.");
                    resp.sendRedirect(req.getContextPath() + "/admin/bill?action=list");
                    return;
                }

                // 3. Gửi thông báo
//                String billMonth = bill.getBillDate().toLocalDate().toString().substring(0, 7);
                Notification noti = new Notification();
                noti.setCustomerID(contract.getTenantId());  // vì tenantId đã là customerID

                noti.setTitle("📄 Hóa đơn mới tháng " + billMonth);
                noti.setMessage("Hóa đơn cho phòng " + contract.getRoomNumber()
                        + " đã được tạo. Tổng tiền: " + bill.getTotalAmount().toPlainString() + "đ.");
                noti.setRead(false);
                noti.setNotificationCreatedAt(new Timestamp(System.currentTimeMillis()));

                HttpSession session = req.getSession(false);
                Integer adminUserId = (session != null && session.getAttribute("userId") != null)
                        ? (Integer) session.getAttribute("userId")
                        : null;

                noti.setSentBy(adminUserId);

                NotificationDAO notificationDAO = new NotificationDAO();
                notificationDAO.insert(noti);

                // 4. Đánh dấu đã gửi
                billDAO.markBillAsSent(billId);

                // 5. Thông báo & chuyển hướng
                req.getSession().setAttribute("success", "✅ Hóa đơn đã được gửi đến người thuê.");
                resp.sendRedirect(req.getContextPath() + "/admin/bill?action=list");
                return;

            } catch (Exception e) {
                e.printStackTrace();
                req.getSession().setAttribute("error", "❌ Lỗi khi gửi hóa đơn: " + e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/admin/bill?action=list");
                return;
            }
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

            LocalDate contractStart = toLocalDate(contract.getStartDate());
            LocalDate contractEnd = toLocalDate(contract.getEndDate());

            YearMonth ym = YearMonth.parse(month);
            LocalDate firstDay = ym.atDay(1);
            LocalDate lastDay = ym.atEndOfMonth();

            LocalDate fromDate = (contractStart.isAfter(firstDay)) ? contractStart : firstDay;
            LocalDate toDate = (contractEnd != null && contractEnd.isBefore(lastDay)) ? contractEnd : lastDay;

            int daysInMonth = ym.lengthOfMonth();
            int realDays = (int) ChronoUnit.DAYS.between(fromDate, toDate) + 1;
            float rentPrice = contract.getRoomRent();
            float roomRent = rentPrice * realDays / daysInMonth;

            // Lấy tiện ích, phụ phí
            List<UtilityReading> readings = readingDAO.getReadingsByRoomAndMonth(roomId, month);
            List<IncurredFee> fees = feeDAO.getFeesByRoomAndMonth(roomId, month);

            float totalElectric = 0, totalWater = 0, totalWifi = 0;
            for (UtilityReading ur : readings) {
                UtilityType ut = utilityTypeDAO.getById(ur.getUtilityTypeID());
                BigDecimal priceUsed = ur.getPriceUsed();
                float price = (priceUsed != null) ? priceUsed.floatValue() : 0f;
                String n = ut.getUtilityName().toLowerCase();
                if (n.contains("điện") || n.contains("electric")) {
                    totalElectric += price;
                } else if (n.contains("nước") || n.contains("water")) {
                    totalWater += price;
                } else if (n.contains("wifi")) {
                    totalWifi += price;
                }
            }

            BigDecimal total = BigDecimal.valueOf(roomRent + totalElectric + totalWater + totalWifi);
            for (IncurredFee f : fees) {
                total = total.add(f.getAmount());
            }

            // Nếu là tháng cuối (hợp đồng kết thúc trong tháng này) thì trừ tiền cọc
            if (contractEnd != null && YearMonth.from(contractEnd).equals(ym)) {
                total = total.subtract(contract.getDeposit() != null
                        ? contract.getDeposit()
                        : BigDecimal.ZERO);
            }

            // Ghi bill
            Bill bill = new Bill();
            bill.setContractID(contractId);
            bill.setBillDate(new java.sql.Date(System.currentTimeMillis()));
            bill.setTotalAmount(total);

            bill.setBillStatus("Unpaid");
            int billId = billDAO.createBill(bill);

            BillDetail detail = new BillDetail();
            detail.setBillID(billId);
            detail.setRoomrent(roomRent);
            detail.setElectricityCost(totalElectric);
            detail.setWaterCost(totalWater);
            detail.setWifiCost(totalWifi);
            detailDAO.insertBillDetail(detail);

            // Gán tên phụ phí cho JSP (nếu dùng)
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

                            feeDAO.insertFee(fee); // ✅ Thêm dòng này

                            total = total.add(amount);
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

// ✅ Sau khi cộng tất cả, mới update tổng bill
            bill.setTotalAmount(total);

            billDAO.updateBill(bill);

            resp.sendRedirect(req.getContextPath() + "/admin/bill?action=list");

        } catch (Exception e) {
            throw new ServletException("Error generating bills", e);
        }
    }
}
