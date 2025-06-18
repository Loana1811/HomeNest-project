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
import java.util.ArrayList;
import java.util.List;
import model.Block;
import model.UtilityReading;

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
            List<Object[]> rooms = roomDAO.getAllRoomIdName();
            List<Object[]> types = typeDAO.getAllTypeIdName();
            List<Block> blocks = new BlockDAO().getAllBlocks();
            req.setAttribute("blocks", blocks);

            String blockIdStr = req.getParameter("blockId");
            req.setAttribute("selectedBlockId", blockIdStr);

            List<Object[]> room = new ArrayList<>();
            if (blockIdStr != null && !blockIdStr.isEmpty()) {
                int blockId = Integer.parseInt(blockIdStr);
                room = roomDAO.getRoomIdNameByBlock(blockId);
            }
            req.setAttribute("rooms", room);

            req.setAttribute("rooms", room);
            req.setAttribute("types", types);

            String roomIdStr = req.getParameter("roomId");
            String typeIdStr = req.getParameter("typeId");

            req.setAttribute("selectedRoomId", roomIdStr);
            req.setAttribute("selectedTypeId", typeIdStr);

            if (roomIdStr != null && !roomIdStr.isEmpty()
                    && typeIdStr != null && !typeIdStr.isEmpty()) {
                int roomId = Integer.parseInt(roomIdStr);
                int typeId = Integer.parseInt(typeIdStr);
                double oldIndex = dao.getLatestIndex(roomId, typeId);
                req.setAttribute("oldIndex", oldIndex);
            }

            req.getRequestDispatcher("/admin/utility-record.jsp")
                    .forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int roomId = Integer.parseInt(req.getParameter("roomId"));
            int typeId = Integer.parseInt(req.getParameter("typeId"));
            BigDecimal newIndex = new BigDecimal(req.getParameter("newIndex"));
            BigDecimal oldIndex = BigDecimal.valueOf(dao.getLatestIndex(roomId, typeId));
            BigDecimal unitPrice = typeDAO.getById(typeId).getUnitPrice();

            if (newIndex.compareTo(oldIndex) <= 0) {
                req.setAttribute("error", "❌ New index must be greater than the old index!");
                req.setAttribute("rooms", roomDAO.getAllRoomIdName());
                req.setAttribute("types", typeDAO.getAllTypeIdName());
                req.setAttribute("selectedRoomId", String.valueOf(roomId));
                req.setAttribute("selectedTypeId", String.valueOf(typeId));
                req.setAttribute("oldIndex", oldIndex.doubleValue());
                req.getRequestDispatcher("/admin/utility-record.jsp").forward(req, resp);
                return;
            }

            BigDecimal priceUsed = newIndex.subtract(oldIndex).multiply(unitPrice);

            UtilityReading ur = new UtilityReading(
                    0,
                    roomId,
                    typeId,
                    oldIndex,
                    newIndex,
                    priceUsed,
                    "admin",
                    new java.sql.Date(System.currentTimeMillis())
            );
            dao.insert(ur);

            ur.setReadingDate(new java.sql.Date(System.currentTimeMillis()));
            resp.sendRedirect(req.getContextPath() + "/admin/record-reading");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

}
