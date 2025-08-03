package controller;

import dao.BillFeedbackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/RejectFeedbackServlet")
public class RejectFeedbackServlet extends HttpServlet {

    private final BillFeedbackDAO feedbackDAO = new BillFeedbackDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer roleId = (session != null) ? (Integer) session.getAttribute("roleID") : null;

        if (roleId == null || (roleId != 1 && roleId != 2)) {
            response.sendRedirect(request.getContextPath() + "/error.jsp");
            return;
        }

        int feedbackId = Integer.parseInt(request.getParameter("feedbackId"));

        try {
            feedbackDAO.updateStatus(feedbackId, "Rejected");
            response.sendRedirect("manage-feedback.jsp?rejected=1");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("manage-feedback.jsp?error=sql");
        }
    }
}
