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
            switch (action == null ? "list" : action) {
                case "editModal": {
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
                    int deleteId = Integer.parseInt(request.getParameter("id"));
                    utilityReadingDAO.deleteZeroReadingsByUtilityType(deleteId);
                    if (dao.isUtilityTypeInUse(deleteId)) {
                        request.setAttribute("error", "❌ This utility has been used and cannot be deleted.");
                    } else {
                        dao.delete(deleteId);
                        response.sendRedirect("/admin/utility?action=list");
                        return;
                    }
                    loadUtilityList(request);
                    request.getRequestDispatcher("/admin/utility-list.jsp").forward(request, response);
                    break;
                }

                case "history": {
                    List<UtilityHistoryView> history = new UtilityHistoryDAO().getHistory();
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

                    List<Block> blocks = blockDAO.getAllBlocks();
                    List<Object[]> rooms = blockId != null ? roomDAO.getRoomsWithActiveContractsByBlock(Integer.parseInt(blockId)) : null;
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
                    break;
                }

                case "list":
                default: {
                    loadUtilityList(request);
                    Map<String, List<Object[]>> blockRoomMap = roomDAO.getRoomsGroupedByBlock();
                    request.setAttribute("blockRoomMap", blockRoomMap);
                    request.getRequestDispatcher("/admin/utility-list.jsp").forward(request, response);
                    break;
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("record".equals(action)) {
                int roomId = Integer.parseInt(request.getParameter("roomId"));
                String readingMonth = request.getParameter("readingMonth");
                String blockId = request.getParameter("blockId");
                List<UtilityType> utilityTypes = dao.getAll();

                for (UtilityType u : utilityTypes) {
                    String value = request.getParameter("new_" + u.getUtilityTypeID());
                    if (value != null && !value.trim().isEmpty()) {
                        double newIndex = Double.parseDouble(value);
                        utilityReadingDAO.insertOrUpdate(roomId, u.getUtilityTypeID(), readingMonth, newIndex);
                    }
                }

                response.sendRedirect(request.getContextPath() + "/admin/bill?step=2&action=step&blockId=" + blockId + "&roomId=" + roomId);
                return;
            }

            // Create or Update Utility
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
                            oldPrice.doubleValue(), price.doubleValue(), "admin", java.sql.Date.valueOf(LocalDate.now()));
                }
            }

            response.sendRedirect("utility?action=list");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void loadUtilityList(HttpServletRequest request) throws Exception {
    List<UtilityType> all = dao.getAll();
    List<IncurredFeeType> feeList = new IncurredFeeTypeDAO().getAll(); // 👈 Thêm dòng này

    request.setAttribute("systemList", all);
    request.setAttribute("feeList", feeList); // 👈 Và dòng này
}


    private void reloadFormWithRoomData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        Map<String, List<Object[]>> blockRoomMap = roomDAO.getRoomsGroupedByBlock();
        request.setAttribute("blockRoomMap", blockRoomMap);
        request.getRequestDispatcher("/admin/create-utilitySystem.jsp").forward(request, response);
    }
}
