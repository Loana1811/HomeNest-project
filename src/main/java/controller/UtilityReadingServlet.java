package controller;

import dao.BillDAO;
import dao.BlockDAO;
import dao.RoomDAO;
import dao.UtilityReadingDAO;
import dao.UtilityTypeDAO;
import model.Block;
import model.UtilityReading;
import model.UtilityType;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import model.Room;

/**
 * Ghi chỉ số tiện ích (nước, điện, wifi, rác...) theo phòng và theo tháng
 */
@WebServlet("/admin/record-reading")
public class UtilityReadingServlet extends HttpServlet {

    private final UtilityReadingDAO dao = new UtilityReadingDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final UtilityTypeDAO typeDAO = new UtilityTypeDAO();
    private final BlockDAO blockDAO = new BlockDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // ✅ Check đăng nhập và quyền
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/error.jsp");
            return;
        }

        model.User currentUser = (model.User) session.getAttribute("currentUser");
        if (currentUser.getRole() == null || currentUser.getRole().getRoleID() != 1) {
            resp.sendRedirect(req.getContextPath() + "/error.jsp");
            return;
        }
        try {
            String blockIdStr = req.getParameter("blockId");
            String roomIdStr = req.getParameter("roomId");
            String readingMonth = req.getParameter("readingMonth");

            String month = (readingMonth != null && !readingMonth.isEmpty())
                    ? readingMonth
                    : LocalDate.now().toString().substring(0, 7);

            List<Block> blocks = blockDAO.getAllBlocks();
            List<Room> roomList;

            // ✅ Lấy danh sách phòng dạng Room đầy đủ (không dùng Object[])
            if (blockIdStr != null && !blockIdStr.isEmpty()) {
                int blockId = Integer.parseInt(blockIdStr);
                List<Room> allRooms = roomDAO.getRoomsByBlockId(blockId);
                roomList = new ArrayList<>();

                for (Room r : allRooms) {
                    if (!dao.hasReadingForMonth(r.getRoomID(), month)) {
                        roomList.add(r);
                    }
                }
            } else {
                List<Room> allRooms = roomDAO.getAllRooms();
                roomList = new ArrayList<>();
                for (Room r : allRooms) {
                    if (!dao.hasReadingForMonth(r.getRoomID(), month)) {
                        roomList.add(r);
                    }
                }
            }

            List<UtilityType> utilityTypes = typeDAO.getAll();
            Map<Integer, Double> oldIndexMap = new HashMap<>();

            if (roomIdStr != null && !roomIdStr.isEmpty()) {
                int roomId = Integer.parseInt(roomIdStr);

                for (UtilityType ut : utilityTypes) {
                    double old = dao.getLatestIndex(roomId, ut.getUtilityTypeID(), month);
                    oldIndexMap.put(ut.getUtilityTypeID(), old);
                }

                req.setAttribute("selectedRoomId", roomIdStr);
                req.setAttribute("readingMonth", month);
            }

            req.setAttribute("blocks", blocks);
            req.setAttribute("rooms", roomList); // ✅ truyền List<Room>
            req.setAttribute("utilityTypes", utilityTypes);
            req.setAttribute("oldIndexMap", oldIndexMap);
            req.setAttribute("selectedBlockId", blockIdStr);

            req.getRequestDispatcher("/admin/utility-record.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

   @Override
protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

    // ✅ Kiểm tra đăng nhập và quyền
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("currentUser") == null) {
        resp.sendRedirect(req.getContextPath() + "/error.jsp");
        return;
    }

    model.User currentUser = (model.User) session.getAttribute("currentUser");
    int roleId = currentUser.getRole().getRoleID();

    if (roleId != 1 && roleId != 2) { // Chỉ Admin và Manager được phép
        resp.sendRedirect(req.getContextPath() + "/error.jsp");
        return;
    }

    // ✅ Chọn đường dẫn JSP phù hợp
    String jspPath = (roleId == 1) ? "/admin/utility-record.jsp" : "/manager/manager-utility.jsp";

    try {
        List<Block> blocks = blockDAO.getAllBlocks();
        List<Object[]> roomList = roomDAO.getAllRoomIdName();
        List<UtilityType> utilityTypes = typeDAO.getAll();
        Map<Integer, Double> oldIndexMap = new HashMap<>();

        req.setAttribute("blocks", blocks);
        req.setAttribute("rooms", roomList);
        req.setAttribute("utilityTypes", utilityTypes);

        String roomIdStr = req.getParameter("roomId");
        String readingMonth = req.getParameter("readingMonth");
        String blockId = req.getParameter("blockId");

        if (roomIdStr == null || readingMonth == null || roomIdStr.isEmpty() || readingMonth.isEmpty()) {
            req.setAttribute("error", "Thiếu thông tin phòng hoặc tháng.");
            req.getRequestDispatcher(jspPath).forward(req, resp);
            return;
        }

        int roomId = Integer.parseInt(roomIdStr);
        LocalDate firstOfMonth = LocalDate.parse(readingMonth + "-01");
        Date readingDate = Date.valueOf(firstOfMonth);

        Room room = roomDAO.getRoomById(roomId);

        req.setAttribute("selectedRoomId", roomIdStr);
        req.setAttribute("readingMonth", readingMonth);
        req.setAttribute("selectedBlockId", blockId);

        if (dao.hasReadingForMonth(roomId, readingMonth)) {
            for (UtilityType ut : utilityTypes) {
                double old = dao.getLatestIndex(roomId, ut.getUtilityTypeID(), readingMonth);
                oldIndexMap.put(ut.getUtilityTypeID(), old);
            }
            req.setAttribute("oldIndexMap", oldIndexMap);
            req.setAttribute("error", "❌ Phòng này đã ghi chỉ số trong kỳ " + readingMonth + ", không thể ghi lại.");
            req.getRequestDispatcher(jspPath).forward(req, resp);
            return;
        }

        for (UtilityType ut : utilityTypes) {
            double old = dao.getLatestIndex(roomId, ut.getUtilityTypeID(), readingMonth);
            oldIndexMap.put(ut.getUtilityTypeID(), old);
        }
        req.setAttribute("oldIndexMap", oldIndexMap);

        String[] typeIds = req.getParameterValues("typeIds");
        if (typeIds == null || typeIds.length == 0) {
            req.setAttribute("error", "Không có tiện ích nào được chọn.");
            req.getRequestDispatcher(jspPath).forward(req, resp);
            return;
        }

        for (String typeIdStr : typeIds) {
            int typeId = Integer.parseInt(typeIdStr);
            UtilityType type = typeDAO.getById(typeId);
            String utilityName = type.getUtilityName().toLowerCase();

            boolean isFree = (utilityName.contains("electricity") && room.getIsElectricityFree() == 0)
                    || (utilityName.contains("water") && room.getIsWaterFree() == 0)
                    || (utilityName.contains("wifi") && room.getIsWifiFree() == 0)
                    || (utilityName.contains("trash") && room.getIsTrashFree() == 0);

            if (isFree) {
                UtilityReading ur = new UtilityReading();
                ur.setRoomID(roomId);
                ur.setUtilityTypeID(typeId);
                ur.setOldReading(BigDecimal.ZERO);
                ur.setNewReading(BigDecimal.ZERO);
                ur.setPriceUsed(BigDecimal.ZERO);
                ur.setChangedBy(currentUser.getUserFullName());
                ur.setReadingDate(readingDate);
                dao.insertUtilityReading(ur);
                continue;
            }

            String newIndexStr = req.getParameter("new_" + typeId);
            if (newIndexStr == null || newIndexStr.isEmpty()) {
                continue;
            }

            BigDecimal newIndex = new BigDecimal(newIndexStr);
            double oldVal = oldIndexMap.get(typeId);
            BigDecimal oldIndex = BigDecimal.valueOf(oldVal);
            BigDecimal unitPrice = type.getUnitPrice() != null ? type.getUnitPrice() : BigDecimal.ZERO;

            if (newIndex.compareTo(oldIndex) <= 0) {
                req.setAttribute("error", "❌ Chỉ số mới của " + type.getUtilityName() + " phải lớn hơn chỉ số cũ.");
                req.getRequestDispatcher(jspPath).forward(req, resp);
                return;
            }

            BigDecimal priceUsed = newIndex.subtract(oldIndex).multiply(unitPrice);

            UtilityReading ur = new UtilityReading();
            ur.setRoomID(roomId);
            ur.setUtilityTypeID(typeId);
            ur.setOldReading(oldIndex);
            ur.setNewReading(newIndex);
            ur.setPriceUsed(priceUsed);
            ur.setChangedBy(currentUser.getUserFullName());
            ur.setReadingDate(readingDate);

            dao.insertUtilityReading(ur);
        }

        resp.sendRedirect(req.getContextPath() + "/admin/bill?action=step&step=1&blockId=" + blockId);

    } catch (Exception e) {
        e.printStackTrace();
        throw new ServletException(e);
    }
}

}
