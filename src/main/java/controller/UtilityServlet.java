package controller;

import dao.*;
import model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/admin/utility")
public class UtilityServlet extends HttpServlet {

    private BlockDAO blockDAO = new BlockDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private UtilityReadingDAO utilityReadingDAO = new UtilityReadingDAO();
    private UtilityTypeDAO dao = new UtilityTypeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/Login");
                return;
            }

            switch (action == null ? "list" : action) {
                case "editModal": {
                    // Chỉ Admin được chỉnh sửa tiện ích
                    if (currentUser.getRoleID() == 2) {
                        request.setAttribute("error", "❌ Manager không có quyền chỉnh sửa tiện ích.");
                        loadUtilityList(request);
                        request.getRequestDispatcher("/admin/utility-list.jsp").forward(request, response);
                        return;
                    }
                    int id = Integer.parseInt(request.getParameter("id"));
                    UtilityType editUtility = dao.getById(id);
                    Map<String, List<Object[]>> blockRoomMap = roomDAO.getRoomsGroupedByBlock();
                    List<Object[]> assignedRooms = roomDAO.getRoomsAppliedToUtility(id);
                    Set<Integer> assignedRoomIds = new HashSet<>();
                    for (Object[] r : assignedRooms) {
                        assignedRoomIds.add((Integer) r[0]);
                    }

                    for (Map.Entry<String, List<Object[]>> entry : blockRoomMap.entrySet()) {
                        List<Object[]> roomList = entry.getValue();
                        for (int i = 0; i < roomList.size(); i++) {
                            Object[] room = roomList.get(i);
                            Integer roomId = (Integer) room[0];
                            boolean isChecked = assignedRoomIds.contains(roomId);
                            if (room.length == 2) {
                                roomList.set(i, new Object[]{roomId, room[1], isChecked});
                            } else {
                                room[2] = isChecked;
                            }
                        }
                    }

                    request.setAttribute("utility", editUtility);
                    request.setAttribute("blockRoomMap", blockRoomMap);
                    request.getRequestDispatcher("/admin/utility-edit.jsp").forward(request, response);
                    break;
                }

                case "delete": {
                    // Chỉ Admin được xóa tiện ích
                    if (currentUser.getRoleID() == 2) {
                        request.setAttribute("error", "❌ Manager không có quyền xóa tiện ích.");
                        loadUtilityList(request);
                        request.getRequestDispatcher("/admin/utility-list.jsp").forward(request, response);
                        return;
                    }
                    int deleteId = Integer.parseInt(request.getParameter("id"));
                    utilityReadingDAO.deleteZeroReadingsByUtilityType(deleteId);
                    if (dao.isUtilityTypeInUse(deleteId)) {
                        request.setAttribute("error", "❌ This utility has been used and cannot be deleted.");
                    } else {
                        dao.delete(deleteId);
                        response.sendRedirect(request.getContextPath() + "/admin/utility?action=list");
                        return;
                    }
                    loadUtilityList(request);
                    request.getRequestDispatcher("/admin/utility-list.jsp").forward(request, response);
                    break;
                }

                case "history": {
                    List<UtilityHistoryView> history = new UtilityHistoryDAO().getHistory();
                    if (currentUser.getRoleID() == 2) {
                        // Lọc lịch sử theo block của Manager (nếu UtilityHistoryView có BlockID)
                        history = history.stream()
                                .filter(h -> h.getBlockID() == currentUser.getBlockID())
                                .collect(Collectors.toList());
                    }
                    request.setAttribute("historyList", history);
                    request.getRequestDispatcher("/admin/utility-history.jsp").forward(request, response);
                    break;
                }

               case "record": {
    String blockId = request.getParameter("blockId");
    String roomId = request.getParameter("roomId");
    String readingMonth = request.getParameter("readingMonth");
    if (readingMonth == null || readingMonth.isEmpty()) {
        readingMonth = LocalDate.now().toString().substring(0, 7);
    }

    List<Block> blocks;
    List<Room> rooms;

    // Giới hạn block và phòng cho Manager
    if (currentUser.getRoleID() == 2) {
        int managerBlockId = currentUser.getBlockID();
        blocks = new ArrayList<>();
        Block managerBlock = blockDAO.getBlockById(managerBlockId);
        if (managerBlock != null) {
            blocks.add(managerBlock);
        }
        rooms = roomDAO.getRoomsWithActiveContractsAndReadings(managerBlockId, readingMonth);
    } else {
        blocks = blockDAO.getAllBlocks();
        rooms = (blockId != null && !blockId.isEmpty())
                ? roomDAO.getRoomsWithActiveContractsAndReadings(Integer.parseInt(blockId), readingMonth)
                : new ArrayList<>();
    }

    List<UtilityType> utilityTypes = dao.getAll();
    Map<Integer, Double> oldIndexMap = new HashMap<>();
    Map<Integer, BigDecimal> effectivePriceMap = new HashMap<>();

    if (roomId != null && !roomId.trim().isEmpty()) {
        int rId = Integer.parseInt(roomId.trim());
        Room selectedRoom = roomDAO.getRoomById(rId);

        // Kiểm tra quyền truy cập phòng cho Manager
        if (currentUser.getRoleID() == 2 && selectedRoom.getBlockID() != currentUser.getBlockID()) {
            request.setAttribute("error", "❌ Bạn không có quyền truy cập phòng này.");
            request.setAttribute("blocks", blocks);
            request.setAttribute("rooms", rooms);
            request.setAttribute("utilityTypes", utilityTypes);
            request.setAttribute("readingMonth", readingMonth);
            request.setAttribute("selectedBlockId", blockId);
            request.setAttribute("selectedRoomId", roomId);
            request.getRequestDispatcher("/admin/utility-record.jsp").forward(request, response);
            return;
        }

        LocalDate readingDate = LocalDate.parse(readingMonth + "-01");
        for (UtilityType u : utilityTypes) {
            LocalDate prevMonth = readingDate.minusMonths(1);
            String prevMonthStr = prevMonth.toString().substring(0, 7);
            Double oldIndex = utilityReadingDAO.getLatestIndex(rId, u.getUtilityTypeID(), prevMonthStr);
            oldIndexMap.put(u.getUtilityTypeID(), oldIndex != null ? oldIndex : 0.0);

            // 🔥 Lấy giá áp dụng tại thời điểm đọc
            BigDecimal effectivePrice = utilityReadingDAO.getEffectivePrice(u.getUtilityTypeID(), readingDate);
            effectivePriceMap.put(u.getUtilityTypeID(), effectivePrice);
        }
    }

    request.setAttribute("blocks", blocks);
    request.setAttribute("rooms", rooms);
    request.setAttribute("utilityTypes", utilityTypes);
    request.setAttribute("oldIndexMap", oldIndexMap);
    request.setAttribute("effectivePriceMap", effectivePriceMap);
    request.setAttribute("readingMonth", readingMonth);
    request.setAttribute("selectedBlockId", blockId);
    request.setAttribute("selectedRoomId", roomId);
    request.getRequestDispatcher("/admin/utility-record.jsp").forward(request, response);
    break;
}

                case "list":
                default: {
                    loadUtilityList(request);
                    Map<String, List<Object[]>> blockRoomMap = (currentUser.getRoleID() == 2)
                            ? roomDAO.getRoomsGroupedByBlock(currentUser.getBlockID())
                            : roomDAO.getRoomsGroupedByBlock();
                    request.setAttribute("blockRoomMap", blockRoomMap);
                    request.getRequestDispatcher("/admin/utility-list.jsp").forward(request, response);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/Login");
                return;
            }

            if ("record".equals(action)) {
                String roomIdStr = request.getParameter("roomId");
                String readingMonth = request.getParameter("readingMonth");
                String blockId = request.getParameter("blockId");

                if (roomIdStr == null || roomIdStr.trim().isEmpty() || readingMonth == null || readingMonth.isEmpty()) {
                    request.setAttribute("error", "Thiếu thông tin phòng hoặc tháng.");
                    reloadRecordForm(request, response, currentUser);
                    return;
                }

                int roomId = Integer.parseInt(roomIdStr);
                Room selectedRoom = roomDAO.getRoomById(roomId);

                // Kiểm tra quyền truy cập phòng cho Manager
                if (currentUser.getRoleID() == 2 && selectedRoom.getBlockID() != currentUser.getBlockID()) {
                    request.setAttribute("error", "❌ Bạn không có quyền ghi chỉ số cho phòng này.");
                    reloadRecordForm(request, response, currentUser);
                    return;
                }

                List<UtilityType> utilityTypes = dao.getAll();

                if (utilityReadingDAO.hasReadingForMonth(roomId, readingMonth)) {
                    Map<Integer, Double> oldIndexMap = new HashMap<>();
                    for (UtilityType u : utilityTypes) {
                        double old = utilityReadingDAO.getLatestIndex(roomId, u.getUtilityTypeID(), readingMonth);
                        oldIndexMap.put(u.getUtilityTypeID(), old);
                    }
                    request.setAttribute("oldIndexMap", oldIndexMap);
                    request.setAttribute("error", "❌ Phòng này đã ghi chỉ số trong kỳ " + readingMonth + ", không thể ghi lại.");
                    reloadRecordForm(request, response, currentUser);
                    return;
                }

                for (UtilityType u : utilityTypes) {
                    String value = request.getParameter("new_" + u.getUtilityTypeID());
                    if (value != null && !value.trim().isEmpty()) {
                        try {
                            double newIndex = Double.parseDouble(value);
                            if (newIndex < 0) {
                                request.setAttribute("error", "❌ Chỉ số mới của " + u.getUtilityName() + " không được âm.");
                                reloadRecordForm(request, response, currentUser);
                                return;
                            }
                            Double oldIndex = utilityReadingDAO.getLatestIndex(roomId, u.getUtilityTypeID(), readingMonth);
                            if (oldIndex != null && newIndex <= oldIndex) {
                                request.setAttribute("error", "❌ Chỉ số mới của " + u.getUtilityName() + " phải lớn hơn chỉ số cũ (" + oldIndex + ").");
                                reloadRecordForm(request, response, currentUser);
                                return;
                            }
                            utilityReadingDAO.insertOrUpdate(roomId, u.getUtilityTypeID(), readingMonth, newIndex);
                        } catch (NumberFormatException e) {
                            request.setAttribute("error", "❌ Chỉ số mới của " + u.getUtilityName() + " không hợp lệ.");
                            reloadRecordForm(request, response, currentUser);
                            return;
                        }
                    }
                }

                response.sendRedirect(request.getContextPath() + "/admin/bill?step=2&action=step&blockId=" + blockId + "&roomId=" + roomId);
                return;
            }

            // Create or Update Utility (chỉ dành cho Admin)
            if (currentUser.getRoleID() == 2) {
                request.setAttribute("error", "❌ Manager không có quyền tạo hoặc cập nhật tiện ích.");
                reloadFormWithRoomData(request, response);
                return;
            }

            String name = request.getParameter("name") != null ? request.getParameter("name").trim() : "";
            String unit = request.getParameter("unit");
            String customUnit = request.getParameter("customUnit") != null ? request.getParameter("customUnit").trim() : "";
            String priceRaw = request.getParameter("price");

            if (name.length() < 3 || name.length() > 50 || !name.matches("^[\\w\\s\\-]+$") || name.matches("^\\d+$")) {
                request.setAttribute("error", "❌ Invalid utility name.");
                reloadFormWithRoomData(request, response);
                return;
            }

            if ("create".equals(action) && dao.isUtilityNameExists(name)) {
                request.setAttribute("error", "❌ Utility name already exists.");
                reloadFormWithRoomData(request, response);
                return;
            }

            if ("Other".equals(unit)) {
                unit = customUnit;
                if (unit.isEmpty() || unit.length() > 20 || !unit.matches("^[\\w\\s\\-]+$")) {
                    request.setAttribute("error", "❌ Invalid custom unit.");
                    reloadFormWithRoomData(request, response);
                    return;
                }
            }

            BigDecimal price;
            try {
                price = new BigDecimal(priceRaw);
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                request.setAttribute("error", "❌ Price must be a positive number.");
                reloadFormWithRoomData(request, response);
                return;
            }

            if ("create".equals(action)) {
                UtilityType newUtility = new UtilityType(0, name, price, unit);
                dao.insert(newUtility);
                int newId = dao.getLastInsertedId();
                String[] selectedRoomIds = request.getParameterValues("roomIds");
                if (selectedRoomIds != null) {
                    for (String rid : selectedRoomIds) {
                        utilityReadingDAO.assignUtilityToRoom(Integer.parseInt(rid), newId);
                    }
                }
            } else if ("update".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                UtilityType existing = dao.getById(id);
                BigDecimal oldPrice = existing.getUnitPrice();
                dao.update(new UtilityType(id, name, price, unit));
                if (oldPrice.compareTo(price) != 0) {
                    new UtilityHistoryDAO().insertHistory(id, existing.getUtilityName(),
                            oldPrice.doubleValue(), price.doubleValue(), currentUser.getUserFullName(), java.sql.Date.valueOf(LocalDate.now()));
                }
            }

            response.sendRedirect(request.getContextPath() + "/admin/utility?action=list");

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    private void loadUtilityList(HttpServletRequest request) throws Exception {
        List<UtilityType> all = dao.getAll();
        List<IncurredFeeType> feeList = new IncurredFeeTypeDAO().getAll();
        request.setAttribute("systemList", all);
        request.setAttribute("feeList", feeList);
    }

    private void reloadFormWithRoomData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        Map<String, List<Object[]>> blockRoomMap = (currentUser.getRoleID() == 2)
                ? roomDAO.getRoomsGroupedByBlock(currentUser.getBlockID())
                : roomDAO.getRoomsGroupedByBlock();
        request.setAttribute("blockRoomMap", blockRoomMap);
        request.getRequestDispatcher("/admin/create-utilitySystem.jsp").forward(request, response);
    }

    private void reloadRecordForm(HttpServletRequest request, HttpServletResponse response, User currentUser) throws ServletException, IOException, SQLException {
        String blockId = request.getParameter("blockId");
        String roomId = request.getParameter("roomId");
        String readingMonth = request.getParameter("readingMonth");
        if (readingMonth == null || readingMonth.isEmpty()) {
            readingMonth = LocalDate.now().toString().substring(0, 7);
        }

        List<Block> blocks;
        List<Room> rooms;

        if (currentUser.getRoleID() == 2) {
            int managerBlockId = currentUser.getBlockID();
            blocks = new ArrayList<>();
            Block managerBlock = blockDAO.getBlockById(managerBlockId);
            if (managerBlock != null) {
                blocks.add(managerBlock);
            }
            rooms = roomDAO.getRoomsWithActiveContractsAndReadings(managerBlockId, readingMonth);
        } else {
            blocks = blockDAO.getAllBlocks();
            rooms = (blockId != null && !blockId.isEmpty())
                    ? roomDAO.getRoomsWithActiveContractsAndReadings(Integer.parseInt(blockId), readingMonth)
                    : new ArrayList<>();
        }

        List<UtilityType> utilityTypes = dao.getAll();
        Map<Integer, Double> oldIndexMap = new HashMap<>();

        if (roomId != null && !roomId.trim().isEmpty()) {
            int rId = Integer.parseInt(roomId.trim());
            for (UtilityType u : utilityTypes) {
                LocalDate prevMonth = LocalDate.parse(readingMonth + "-01").minusMonths(1);
                String prevMonthStr = prevMonth.toString().substring(0, 7);
                Double oldIndex = utilityReadingDAO.getLatestIndex(rId, u.getUtilityTypeID(), prevMonthStr);
                oldIndexMap.put(u.getUtilityTypeID(), oldIndex != null ? oldIndex : 0.0);
            }
        }

        request.setAttribute("blocks", blocks);
        request.setAttribute("rooms", rooms);
        request.setAttribute("utilityTypes", utilityTypes);
        request.setAttribute("oldIndexMap", oldIndexMap);
        request.setAttribute("readingMonth", readingMonth);
        request.setAttribute("selectedBlockId", blockId);
        request.setAttribute("selectedRoomId", roomId);
        request.getRequestDispatcher("/admin/utility-record.jsp").forward(request, response);
    }
}