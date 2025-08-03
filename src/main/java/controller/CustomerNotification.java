package controller;

import dao.NotificationDAO;
import dao.ReportDAO;
import model.Notification;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Report;
@WebServlet(name = "CustomerNotification", urlPatterns = {"/customer/notification"})
public class CustomerNotification extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String ctx = request.getContextPath();

        if (session == null || session.getAttribute("idCustomer") == null
                || !"customer".equalsIgnoreCase((String) session.getAttribute("userType"))) {
            response.sendRedirect(ctx + "/error.jsp");
            return;
        }

        Integer customerID = (Integer) session.getAttribute("idCustomer");

        try {
            // Lấy thông báo
            NotificationDAO notificationDAO = new NotificationDAO();
            List<Notification> notifications = notificationDAO.getNotificationsByCustomer(customerID);
            request.setAttribute("notifications", notifications);

            // Lấy hợp đồng hợp lệ
            ReportDAO reportDAO = new ReportDAO();
            List<Map<String, Object>> activeContracts = reportDAO.getActiveNonExpiredRoomsAndContracts(customerID);
            request.setAttribute("roomsAndContracts", activeContracts);

            // Lấy báo cáo đã gửi
            List<Report> reports = reportDAO.getReportsByCustomer(customerID);
            request.setAttribute("reports", reports);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("roomsAndContracts", null);
            request.setAttribute("reports", null);
            request.setAttribute("notifications", null);
        }

        // Sau khi đã set đầy đủ, forward tới trang viewNotification.jsp
        request.getRequestDispatcher("/customer/viewNotification.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

