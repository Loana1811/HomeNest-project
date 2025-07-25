/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

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
import java.sql.Timestamp;
import model.UtilityReading;

/**
 *
 * @author kloane
 */
@WebServlet("/admin/edit-reading")
public class EditReadingServlet extends HttpServlet {

    private final UtilityReadingDAO dao = new UtilityReadingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("readingId"));
            UtilityReading ur = dao.getUtilityReadingById(id);
            if (ur == null) {
                throw new Exception("Không tìm thấy bản ghi.");
            }
            if (ur.isLocked()) {
                throw new Exception("Bản ghi đã chốt, không thể chỉnh sửa.");
            }
            req.setAttribute("reading", ur);
            req.getRequestDispatcher("/admin/edit-reading.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendError(403, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("readingId"));
            double newReading = Double.parseDouble(req.getParameter("newReading"));
            String reason = req.getParameter("reason");

            UtilityReading ur = dao.getUtilityReadingById(id);
            if (ur == null || ur.isLocked()) {
                throw new Exception("Không thể sửa bản ghi này.");
            }

            double oldReading = ur.getOldReading().doubleValue();
            double used = newReading - oldReading;

            // Cập nhật chỉ số mới và giá
            ur.setNewReading(BigDecimal.valueOf(newReading));
            ur.setPriceUsed(ur.getPriceUsed().multiply(BigDecimal.valueOf(used)));

            // Nếu bạn đã bỏ ChangedBy thì dòng này nên bỏ hoặc comment
            // ur.setChangedBy(" | Lý do: " + reason);
            dao.updateReading(ur);
            resp.sendRedirect(req.getContextPath() + "/admin/usage");

        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());

            // ❗️ Sửa lỗi NullPointerException tại JSP
            try {
                int id = Integer.parseInt(req.getParameter("readingId"));
                UtilityReading ur = dao.getUtilityReadingById(id);
                req.setAttribute("reading", ur);
            } catch (Exception ignored) {
            }

            req.getRequestDispatcher("/admin/edit-reading.jsp").forward(req, resp);
        }
    }

}
