package controller;

import dao.CustomerDAO;
import dao.NotificationDAO;
import dao.UserNotificationDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Customer;
import model.Notification;
import model.User;
import model.UserNotification;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "AdminNotificationServlet", urlPatterns = {"/admin/notification"})
public class AdminNotificationServlet extends HttpServlet {

    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final UserNotificationDAO userNotificationDAO = new UserNotificationDAO();
    private final UserDAO userDAO = new UserDAO();
    private static final Logger LOGGER = Logger.getLogger(AdminNotificationServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Integer roleID = (Integer) session.getAttribute("roleID");
        Integer userId = (Integer) session.getAttribute("idUser");
        LOGGER.info("doGet - Action: " + action + ", roleID: " + roleID + ", idUser: " + userId);

        if (userId == null) {
            LOGGER.warning("Session invalid or expired, no idUser found, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            if ("viewNotifications".equals(action) || action == null) {
                viewNotifications(request, response, userId);
            } else if ("deleteNotification".equals(action)) {
                deleteNotification(request, response, userId);
            } else if ("createNotification".equals(action)) {
                if (roleID == null || (roleID != 1 && roleID != 2)) {
                    LOGGER.warning("Unauthorized access to createNotification, roleID: " + roleID);
                    response.sendRedirect(request.getContextPath() + "/login.jsp");
                    return;
                }
                try {
                    List<Customer> customerList = customerDAO.getAllCustomers();
                    List<User> managerList = userDAO.getUsersByRole("Manager");
                    request.setAttribute("customerList", customerList);
                    request.setAttribute("managerList", managerList);
                    request.setAttribute("idUser", userId);
                    request.getRequestDispatcher("/admin/createNotification.jsp").forward(request, response);
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Database error fetching customer/manager list", ex);
                    request.setAttribute("error", "Failed to load data for notification creation.");
                    response.sendRedirect("error.jsp?idUser=" + userId);
                }
            } else if ("viewUserNotifications".equals(action)) {
                viewUserNotifications(request, response, userId);
            } else if ("deleteUserNotification".equals(action)) {
                deleteUserNotification(request, response, userId);
            } else {
                response.sendRedirect("error.jsp?idUser=" + userId);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database error in doGet", ex);
            request.setAttribute("error", "Database Error: " + ex.getMessage());
            try {
                viewNotifications(request, response, userId);
            } catch (SQLException ex1) {
                LOGGER.log(Level.SEVERE, "Failed to fallback to viewNotifications", ex1);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Integer roleID = (Integer) session.getAttribute("roleID");
        Integer userId = (Integer) session.getAttribute("idUser");
        LOGGER.info("doPost - Action: " + action + ", roleID: " + roleID + ", idUser: " + userId);

        if (userId == null) {
            LOGGER.warning("Session invalid or expired, no idUser found, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            if ("editNotification".equals(action)) {
                editNotification(request, response, userId);
            } else if ("createNotification".equals(action)) {
                if (roleID == null || (roleID != 1 && roleID != 2)) {
                    LOGGER.warning("Unauthorized access to createNotification, roleID: " + roleID);
                    response.sendRedirect(request.getContextPath() + "/login.jsp");
                    return;
                }
                createNotification(request, response, userId);
            } else if ("createUserNotification".equals(action)) {
                createUserNotification(request, response, userId);
            } else if ("editUserNotification".equals(action)) {
                editUserNotification(request, response, userId);
            } else {
                response.sendRedirect("error.jsp?idUser=" + userId);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database error in doPost", ex);
            request.setAttribute("error", "Database Error: " + ex.getMessage());
            try {
                viewNotifications(request, response, userId);
            } catch (SQLException ex1) {
                LOGGER.log(Level.SEVERE, "Failed to fallback to viewNotifications", ex1);
            }
        }
    }

    private void createNotification(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws SQLException, ServletException, IOException {
        String title = request.getParameter("title");
        String message = request.getParameter("message");
        String[] customerIds = request.getParameterValues("customerIds");
        String[] managerIds = request.getParameterValues("managerIds");
        boolean selectAllCustomers = request.getParameter("selectAllCustomers") != null;
        boolean selectAllManagers = request.getParameter("selectAllManagers") != null;

        if (title == null || title.trim().isEmpty() || message == null || message.trim().isEmpty()) {
            request.setAttribute("error", "Title and message are required.");
            List<Customer> customerList = customerDAO.getAllCustomers();
            List<User> managerList = userDAO.getUsersByRole("Manager");
            request.setAttribute("customerList", customerList);
            request.setAttribute("managerList", managerList);
            request.setAttribute("idUser", userId);
            request.getRequestDispatcher("/admin/createNotification.jsp").forward(request, response);
            return;
        }

        Integer sentBy = (Integer) request.getSession().getAttribute("idUser");
        if (sentBy == null) {
            LOGGER.warning("No sender ID (idUser) found in session");
            request.setAttribute("error", "Session error: Please log in again to send notifications.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());

        // Handle customer notifications
        if (selectAllCustomers || (customerIds == null || customerIds.length == 0)) {
            List<Customer> allCustomers = customerDAO.getAllCustomers();
            for (Customer customer : allCustomers) {
                Notification notification = new Notification(0, customer.getCustomerID(), title.trim(), message.trim(), false, currentTime, sentBy);
                notificationDAO.createNotification(notification);
            }
        } else {
            for (String customerId : customerIds) {
                int customerID = Integer.parseInt(customerId.trim());
                Notification notification = new Notification(0, customerID, title.trim(), message.trim(), false, currentTime, sentBy);
                notificationDAO.createNotification(notification);
            }
        }

        // Handle manager notifications
        if (selectAllManagers || (managerIds == null || managerIds.length == 0)) {
            List<User> allManagers = userDAO.getUsersByRole("Manager");
            for (User manager : allManagers) {
                UserNotification notification = new UserNotification(0, manager.getUserID(), title.trim(), message.trim(), false, currentTime, sentBy);
                userNotificationDAO.createUserNotification(notification);
            }
        } else {
            for (String managerId : managerIds) {
                int userID = Integer.parseInt(managerId.trim());
                UserNotification notification = new UserNotification(0, userID, title.trim(), message.trim(), false, currentTime, sentBy);
                userNotificationDAO.createUserNotification(notification);
            }
        }

        request.setAttribute("success", "Notification(s) created successfully for customers and managers.");
        response.sendRedirect(request.getContextPath() + "/admin/notification?action=viewNotifications&idUser=" + userId);
    }

    private void createUserNotification(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws SQLException, ServletException, IOException {
        String userTitle = request.getParameter("userTitle");
        String userMessage = request.getParameter("userMessage");
        String[] managerIds = request.getParameterValues("managerIds");
        boolean selectAll = request.getParameter("selectAll") != null;

        if (userTitle == null || userTitle.trim().isEmpty() || userMessage == null || userMessage.trim().isEmpty()) {
            request.setAttribute("error", "Title and message are required.");
            List<User> managerList = userDAO.getUsersByRole("Manager");
            request.setAttribute("managerList", managerList);
            request.setAttribute("idUser", userId);
            request.getRequestDispatcher("/admin/createUserNotification.jsp").forward(request, response);
            return;
        }

        Integer sentBy = (Integer) request.getSession().getAttribute("idUser");
        if (sentBy == null) {
            LOGGER.warning("No sender ID (idUser) found in session");
            request.setAttribute("error", "Session error: Please log in again to send notifications.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());

        if (selectAll || (managerIds == null || managerIds.length == 0)) {
            List<User> allManagers = userDAO.getUsersByRole("Manager");
            for (User manager : allManagers) {
                UserNotification notification = new UserNotification(0, manager.getUserID(), userTitle.trim(), userMessage.trim(), false, currentTime, sentBy);
                userNotificationDAO.createUserNotification(notification);
            }
        } else {
            for (String managerId : managerIds) {
                int userID = Integer.parseInt(managerId.trim());
                UserNotification notification = new UserNotification(0, userID, userTitle.trim(), userMessage.trim(), false, currentTime, sentBy);
                userNotificationDAO.createUserNotification(notification);
            }
        }

        request.setAttribute("success", "User Notification(s) changed successfully.");
        response.sendRedirect(request.getContextPath() + "/admin/notification?action=viewUserNotifications&idUser=" + userId);
    }

    private void viewNotifications(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws SQLException, ServletException, IOException {
        String search = request.getParameter("search");
        List<Notification> notificationList;
        List<UserNotification> userNotificationList;

        try {
            if (search != null && !search.trim().isEmpty()) {
                notificationList = notificationDAO.searchNotifications(search.trim());
                userNotificationList = userNotificationDAO.searchUserNotifications(search.trim());
            } else {
                notificationList = notificationDAO.getAllNotifications();
                userNotificationList = userNotificationDAO.getAllUserNotifications();
            }

            // Gỡ lỗi: In thông tin notificationList
            System.out.println("Kích thước notificationList: " + notificationList.size());
            for (Notification n : notificationList) {
                System.out.println("Notification ID: " + n.getNotificationID() + ", isRead: " + n.isRead());
            }

            // Gỡ lỗi: In thông tin userNotificationList
            System.out.println("Kích thước userNotificationList: " + userNotificationList.size());
            for (UserNotification un : userNotificationList) {
                System.out.println("UserNotification ID: " + un.getUserNotificationID() + ", isRead: " + un.isRead());
            }

            Map<Integer, Customer> customerMap = new HashMap<>();
            List<Customer> customers = customerDAO.getAllCustomers();
            for (Customer customer : customers) {
                customerMap.put(customer.getCustomerID(), customer);
            }
            request.setAttribute("customerMap", customerMap);

            Map<Integer, User> userMap = new HashMap<>();
            List<User> users = userDAO.getAllUsers();
            for (User user : users) {
                userMap.put(user.getUserID(), user);
            }
            request.setAttribute("userMap", userMap);

            request.setAttribute("notificationList", notificationList);
            request.setAttribute("userNotificationList", userNotificationList);
            request.setAttribute("idUser", userId);
            request.getRequestDispatcher("/admin/viewNotifications.jsp").forward(request, response);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy thông báo", ex);
            throw ex;
        }
    }

    private void viewUserNotifications(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws SQLException, ServletException, IOException {
        String search = request.getParameter("search");
        List<UserNotification> userNotificationList;
        try {
            if (search != null && !search.trim().isEmpty()) {
                userNotificationList = userNotificationDAO.searchUserNotifications(search.trim());
            } else {
                userNotificationList = userNotificationDAO.getAllUserNotifications();
            }

            Map<Integer, User> userMap = new HashMap<>();
            List<User> users = userDAO.getAllUsers();
            for (User user : users) {
                userMap.put(user.getUserID(), user);
            }
            request.setAttribute("userMap", userMap);
            request.setAttribute("userNotificationList", userNotificationList);
            request.setAttribute("idUser", userId);
            request.getRequestDispatcher("/admin/viewUserNotifications.jsp").forward(request, response);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error fetching user notifications", ex);
            throw ex;
        }
    }

    private void editNotification(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws SQLException, ServletException, IOException {
        String notificationIDStr = request.getParameter("notificationID");
        String title = request.getParameter("title");
        String message = request.getParameter("message");

        if (notificationIDStr == null || notificationIDStr.trim().isEmpty()
                || title == null || title.trim().isEmpty()
                || message == null || message.trim().isEmpty()) {
            request.setAttribute("error", "Notification ID, title, and message are required.");
            viewNotifications(request, response, userId);
            return;
        }

        int notificationID = Integer.parseInt(notificationIDStr);
        Notification notification = notificationDAO.getNotificationById(notificationID);

        if (notification == null) {
            request.setAttribute("error", "Notification not found.");
            viewNotifications(request, response, userId);
            return;
        }

        notification.setTitle(title.trim());
        notification.setMessage(message.trim());

        boolean success = notificationDAO.updateNotification(notification);
        if (!success) {
            request.setAttribute("error", "Failed to update notification.");
            viewNotifications(request, response, userId);
            return;
        }

        request.setAttribute("success", "Notification updated successfully.");
        viewNotifications(request, response, userId);
    }

    private void editUserNotification(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws SQLException, ServletException, IOException {
        String userNotificationIDStr = request.getParameter("userNotificationID");
        String userTitle = request.getParameter("userTitle");
        String userMessage = request.getParameter("userMessage");

        if (userNotificationIDStr == null || userNotificationIDStr.trim().isEmpty()
                || userTitle == null || userTitle.trim().isEmpty()
                || userMessage == null || userMessage.trim().isEmpty()) {
            request.setAttribute("error", "Notification ID, title, and message are required.");
            viewUserNotifications(request, response, userId);
            return;
        }

        int userNotificationID = Integer.parseInt(userNotificationIDStr);
        UserNotification notification = userNotificationDAO.getUserNotificationById(userNotificationID);

        if (notification == null) {
            request.setAttribute("error", "User Notification not found.");
            viewUserNotifications(request, response, userId);
            return;
        }

        notification.setUserTitle(userTitle.trim());
        notification.setUserMessage(userMessage.trim());

        boolean success = userNotificationDAO.updateUserNotification(notification);
        if (!success) {
            request.setAttribute("error", "Failed to update user notification.");
            viewUserNotifications(request, response, userId);
            return;
        }

        request.setAttribute("success", "User Notification updated successfully.");
        viewUserNotifications(request, response, userId);
    }

    private void deleteNotification(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws SQLException, ServletException, IOException {
        String notificationIDStr = request.getParameter("notificationID");

        if (notificationIDStr == null || notificationIDStr.trim().isEmpty()) {
            request.setAttribute("error", "Notification ID is required for deletion.");
            viewNotifications(request, response, userId);
            return;
        }

        int notificationID = Integer.parseInt(notificationIDStr);
        boolean success = notificationDAO.deleteNotification(notificationID);
        if (success) {
            request.setAttribute("success", "Notification deleted successfully.");
        } else {
            request.setAttribute("error", "Failed to delete notification.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/notification?action=viewNotifications&idUser=" + userId);
    }

    private void deleteUserNotification(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws SQLException, ServletException, IOException {
        String userNotificationIDStr = request.getParameter("userNotificationID");

        if (userNotificationIDStr == null || userNotificationIDStr.trim().isEmpty()) {
            request.setAttribute("error", "User Notification ID is required for deletion.");
            viewUserNotifications(request, response, userId);
            return;
        }

        int userNotificationID = Integer.parseInt(userNotificationIDStr);
        boolean success = userNotificationDAO.deleteUserNotification(userNotificationID);
        if (success) {
            request.setAttribute("success", "User Notification deleted successfully.");
        } else {
            request.setAttribute("error", "Failed to delete user notification.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/notification?action=viewNotifications&idUser=" + userId);
    }

    @Override
    public String getServletInfo() {
        return "Servlet to manage notifications for admin and managers";
    }
}
