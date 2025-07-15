package controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
import dao.BlockDAO;
import dao.RoomDAO;
import dao.UtilityReadingDAO;
import dao.UtilityTypeDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Block;
import model.UtilityReading;
import model.UtilityType;

/**
 *
 * @author kloane
 */
@WebServlet("/admin/record-reading")
public class UtilityReadingServlet extends HttpServlet {

    private UtilityReadingDAO dao = new UtilityReadingDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private UtilityTypeDAO typeDAO = new UtilityTypeDAO();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String blockIdStr = req.getParameter("blockId");
            String roomIdStr = req.getParameter("roomId");
            String readingMonth = req.getParameter("readingMonth");

            // Danh sách block, phòng, tiện ích (mặc định)
            List<Block> blocks = new BlockDAO().getAllBlocks();
            List<Object[]> roomList = new ArrayList<>();
            if (blockIdStr != null && !blockIdStr.isEmpty()) {
                int blockId = Integer.parseInt(blockIdStr);
                roomList = roomDAO.getRoomIdNameByBlock(blockId);
            } else {
                roomList = roomDAO.getAllRoomIdName();
            }

            List<UtilityType> utilityTypes = typeDAO.getAll();
            Map<Integer, Double> oldIndexMap = new HashMap<>();

            // Nếu đã chọn phòng thì lấy chỉ số cũ từng tiện ích
            if (roomIdStr != null && !roomIdStr.isEmpty()) {
                int roomId = Integer.parseInt(roomIdStr);
                String month = (readingMonth != null && !readingMonth.isEmpty())
                        ? readingMonth
                        : LocalDate.now().toString().substring(0, 7);

                for (UtilityType ut : utilityTypes) {
                    double old = dao.getLatestIndex(roomId, ut.getUtilityTypeID(), month);

                    oldIndexMap.put(ut.getUtilityTypeID(), old);
                }

                req.setAttribute("selectedRoomId", roomIdStr);
                req.setAttribute("readingMonth", month);
            }

            // Set các thuộc tính cho JSP
            req.setAttribute("blocks", blocks);
            req.setAttribute("rooms", roomList);
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
        try {
            // Load lại dữ liệu cho JSP (dù có lỗi hay không)
            List<Block> blocks = new BlockDAO().getAllBlocks();
            List<Object[]> roomList = roomDAO.getAllRoomIdName();
            List<UtilityType> utilityTypes = typeDAO.getAll();
            Map<Integer, Double> oldIndexMap = new HashMap<>();

            req.setAttribute("blocks", blocks);
            req.setAttribute("rooms", roomList);
            req.setAttribute("utilityTypes", utilityTypes);

            String roomIdStr = req.getParameter("roomId");
            String readingMonth = req.getParameter("readingMonth");
            String blockId = req.getParameter("blockId"); // để redirect lại sau khi submit

            if (roomIdStr == null || readingMonth == null || roomIdStr.isEmpty() || readingMonth.isEmpty()) {
                req.setAttribute("error", "Thiếu thông tin phòng hoặc tháng.");
                req.getRequestDispatcher("/admin/utility-record.jsp").forward(req, resp);
                return;
            }

            int roomId = Integer.parseInt(roomIdStr);
            LocalDate firstOfMonth = LocalDate.parse(readingMonth + "-01");
            Date readingDate = Date.valueOf(firstOfMonth);

            req.setAttribute("selectedRoomId", roomIdStr);
            req.setAttribute("readingMonth", readingMonth);
            req.setAttribute("selectedBlockId", blockId);

            // lấy chỉ số cũ cho tất cả tiện ích
            for (UtilityType ut : utilityTypes) {
                double old = dao.getLatestIndex(roomId, ut.getUtilityTypeID(), readingMonth);
                oldIndexMap.put(ut.getUtilityTypeID(), old);
            }
            req.setAttribute("oldIndexMap", oldIndexMap);

            String[] typeIds = req.getParameterValues("typeIds");
            if (typeIds == null || typeIds.length == 0) {
                req.setAttribute("error", "Không có tiện ích nào được chọn.");
                req.getRequestDispatcher("/admin/utility-record.jsp").forward(req, resp);
                return;
            }

            for (String typeIdStr : typeIds) {
                int typeId = Integer.parseInt(typeIdStr);
                String newIndexStr = req.getParameter("new_" + typeId);
                if (newIndexStr == null || newIndexStr.isEmpty()) {
                    continue;
                }

                BigDecimal newIndex = new BigDecimal(newIndexStr);
                double oldVal = oldIndexMap.get(typeId);
                BigDecimal oldIndex = BigDecimal.valueOf(oldVal);
                BigDecimal unitPrice = typeDAO.getById(typeId).getUnitPrice();

                if (unitPrice == null) {
                    unitPrice = BigDecimal.ZERO;
                }

                if (newIndex.compareTo(oldIndex) <= 0) {
                    req.setAttribute("error", "❌ Chỉ số mới của " + typeDAO.getById(typeId).getUtilityName() + " phải lớn hơn chỉ số cũ.");
                    req.getRequestDispatcher("/admin/utility-record.jsp").forward(req, resp);
                    return;
                }

                BigDecimal priceUsed = newIndex.subtract(oldIndex).multiply(unitPrice);

                UtilityReading ur = new UtilityReading();
                ur.setRoomID(roomId);
                ur.setUtilityTypeID(typeId);
                ur.setOldReading(oldIndex);
                ur.setNewReading(newIndex);
                ur.setPriceUsed(priceUsed);
                ur.setChangedBy("admin");
                ur.setReadingDate(readingDate);

                dao.insertUtilityReading(ur);
            }

            // ✅ Ghi xong, redirect về bước 1 tạo hóa đơn
            resp.sendRedirect(req.getContextPath() + "/admin/bill?action=step&step=1&blockId=" + blockId);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

}
