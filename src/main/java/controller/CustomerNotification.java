package controller;

import dao.NotificationDAO;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "CustomerNotification", urlPatterns = {"/customer/notification"})
public class CustomerNotification extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer customerID = (Integer) session.getAttribute("idCustomer");
        String userType = (String) session.getAttribute("userType");

   
        NotificationDAO notificationDAO = new NotificationDAO();
        // Lấy danh sách thông báo cho khách hàng
        List<Notification> notifications = null;
        try {
            notifications = notificationDAO.getNotificationsByCustomer(customerID);
        } catch (SQLException ex) {
            Logger.getLogger(CustomerNotification.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("notifications", notifications);
        // Chuyển tiếp đến viewNotification.jsp
        request.getRequestDispatcher("/customer/viewNotification.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Có thể thêm logic để xử lý các hành động như đánh dấu thông báo đã đọc
        doGet(request, response); // Tạm thời gọi doGet để xử lý
    }

    @Override
    public String getServletInfo() {
        return "Servlet xử lý các thao tác thông báo của khách hàng";
    }
}