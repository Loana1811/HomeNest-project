package controller;

import dao.UserNotificationDAO;
import dao.UserDAO;
import dao.NotificationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.UserNotification;
import model.Notification;
import model.Customer;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ManagerNotification", urlPatterns = {"/manager/notification"})
public class ManagerNotification extends HttpServlet {

    private final UserNotificationDAO userNotificationDAO = new UserNotificationDAO();
    private final UserDAO userDAO = new UserDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private static final Logger LOGGER = Logger.getLogger(ManagerNotification.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("idUser");
        Integer roleID = (Integer) session.getAttribute("roleID");

        // Log session attributes and request parameter
        String idManagerParam = request.getParameter("idManager");
        LOGGER.info("doGet - Session: idUser=" + userId + ", roleID=" + roleID + ", idManagerParam=" + idManagerParam);

        // Fallback to idManager parameter if session idUser is null (less secure, for debugging)
        if (userId == null && idManagerParam != null && !idManagerParam.isEmpty()) {
            try {
                userId = Integer.parseInt(idManagerParam);
                session.setAttribute("idUser", userId); // Restore session attribute
                LOGGER.info("Restored userId from idManagerParam: " + userId);
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid idManagerParam: " + idManagerParam);
            }
        }

        if (userId == null || roleID == null || roleID != 2) { // Only managers (roleID = 2)
            LOGGER.warning("Unauthorized access to manager notification, userId: " + userId + ", roleID=" + roleID);
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if ("createNotification".equals(action)) {
            handleCreateForm(request, response, userId);
            return;
        } else if ("deleteNotification".equals(action)) {
            try {
                deleteNotification(request, response, userId);
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Database error in delete for userId: " + userId, ex);
                request.setAttribute("error", "Failed to delete notification: " + ex.getMessage());
                request.getRequestDispatcher("/manager/viewNotification.jsp").forward(request, response);
            }
            return;
        }

        try {
            // Get the manager
            User manager = userDAO.getUserById(userId);
            if (manager == null || manager.getBlockID() == null) {
                LOGGER.warning("Manager not found or no block assigned for userId: " + userId);
                request.setAttribute("error", "Manager has no assigned block.");
                request.getRequestDispatcher("/managerDashboard.jsp?idManager=" + userId).forward(request, response);
                return;
            }
            Integer blockId = manager.getBlockID();
            LOGGER.info("Fetching notifications for manager userId: " + userId + ", blockId: " + blockId);

            // Fetch manager notifications (for this manager)
            List<UserNotification> managerNotifications = userNotificationDAO.getUserNotificationsByUserId(userId);
            LOGGER.info("Found " + managerNotifications.size() + " manager notifications");

            // Fetch customer notifications sent by this manager in their block
            List<Notification> allNotifications = notificationDAO.getNotificationsByBlock(blockId);
            List<Notification> customerNotifications = new ArrayList<Notification>();
            for (Notification n : allNotifications) {
                if (n.getSentBy() != null && n.getSentBy().equals(userId)) {
                    customerNotifications.add(n);
                }
            }
            LOGGER.info("Found " + customerNotifications.size() + " customer notifications sent by userId: " + userId);

            // Create userMap for sender lookup
            Map<Integer, User> userMap = new HashMap<>();
            for (UserNotification un : managerNotifications) {
                if (un.getSentBy() != null) {
                    User sender = userDAO.getUserById(un.getSentBy());
                    if (sender != null) {
                        userMap.put(un.getSentBy(), sender);
                    }
                }
            }
            for (Notification n : customerNotifications) {
                if (n.getSentBy() != null) {
                    User sender = userDAO.getUserById(n.getSentBy());
                    if (sender != null) {
                        userMap.put(n.getSentBy(), sender);
                    }
                }
            }

            // Create customerMap for customer lookup
            Map<Integer, Customer> customerMap = new HashMap<>();
            for (Notification n : customerNotifications) {
                Customer customer = userDAO.getCustomerById(n.getCustomerID());
                if (customer != null) {
                    customerMap.put(n.getCustomerID(), customer);
                }
            }

            request.setAttribute("managerNotifications", managerNotifications);
            request.setAttribute("customerNotifications", customerNotifications);
            request.setAttribute("manager", manager);
            request.setAttribute("userMap", userMap);
            request.setAttribute("customerMap", customerMap);
            request.setAttribute("idUser", userId);

            request.getRequestDispatcher("/manager/viewNotification.jsp").forward(request, response);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database error fetching notifications for userId: " + userId, ex);
            request.setAttribute("error", "Failed to load notifications: " + ex.getMessage());
            request.getRequestDispatcher("/manager/viewNotification.jsp").forward(request, response);
        }
    }

    private void handleCreateForm(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws ServletException, IOException {
        try {
            User manager = userDAO.getUserById(userId);
            if (manager == null || manager.getBlockID() == null) {
                LOGGER.warning("No block assigned");
                request.setAttribute("error", "Manager has no assigned block.");
                request.getRequestDispatcher("/manager/viewNotification.jsp").forward(request, response);
                return;
            }
            int blockId = manager.getBlockID();
            List<Customer> customers = userDAO.getCustomersByBlock(blockId);
            request.setAttribute("customers", customers);
            request.getRequestDispatcher("/manager/managerCreateNotification.jsp").forward(request, response);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database error", ex);
            request.setAttribute("error", "Failed to load customers.");
            request.getRequestDispatcher("/manager/managerCreateNotification.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("idUser");
        Integer roleID = (Integer) session.getAttribute("roleID");

        // Log session attributes for POST request
        LOGGER.info("doPost - Session: idUser=" + userId + ", roleID=" + roleID);

        if (userId == null || roleID == null || roleID != 2) {
            LOGGER.warning("Unauthorized access to manager notification post, userId: " + userId + ", roleID=" + roleID);
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            if ("createNotification".equals(action)) {
                createNotification(request, response, userId);
            } else if ("editNotification".equals(action)) {
                editNotification(request, response, userId);
            } else {
                response.sendRedirect(request.getContextPath() + "/manager/notification?idUser=" + userId);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database error in doPost for userId: " + userId, ex);
            request.setAttribute("error", "Database Error: " + ex.getMessage());
            request.getRequestDispatcher("/manager/viewNotification.jsp").forward(request, response);
        }
    }

    private void createNotification(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws SQLException, ServletException, IOException {
        String title = request.getParameter("title");
        String message = request.getParameter("message");
        String[] customerIdsStr = request.getParameterValues("customerIds");

        if (title == null || title.trim().isEmpty() || message == null || message.trim().isEmpty() || customerIdsStr == null || customerIdsStr.length == 0) {
            request.setAttribute("error", "Title, message, and at least one customer are required.");
            handleCreateForm(request, response, userId);
            return;
        }

        List<Integer> customerIds = new ArrayList<>();
        for (String str : customerIdsStr) {
            try {
                customerIds.add(Integer.parseInt(str));
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid customer ID: " + str);
            }
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());

        for (int custId : customerIds) {
            Notification notification = new Notification();
            notification.setCustomerID(custId);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setRead(false);
            notification.setNotificationCreatedAt(now);
            notification.setSentBy(userId);
            notificationDAO.insert(notification);
        }

        response.sendRedirect(request.getContextPath() + "/manager/notification?idUser=" + userId);
    }

    private void editNotification(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws SQLException, ServletException, IOException {
        String notificationIDStr = request.getParameter("notificationID");
        String title = request.getParameter("title");
        String message = request.getParameter("message");

        if (notificationIDStr == null || notificationIDStr.trim().isEmpty() ||
            title == null || title.trim().isEmpty() ||
            message == null || message.trim().isEmpty()) {
            LOGGER.warning("Invalid input for editing notification, userId: " + userId);
            request.setAttribute("error", "Notification ID, title, and message are required.");
            request.getRequestDispatcher("/manager/viewNotification.jsp").forward(request, response);
            return;
        }

        int notificationID = Integer.parseInt(notificationIDStr);
        Notification notification = notificationDAO.getNotificationById(notificationID);

        if (notification == null) {
            LOGGER.warning("Notification not found for ID: " + notificationID + ", userId: " + userId);
            request.setAttribute("error", "Notification not found.");
            request.getRequestDispatcher("/manager/viewNotification.jsp").forward(request, response);
            return;
        }

        // Check if the notification was sent by this manager
        if (notification.getSentBy() == null || !notification.getSentBy().equals(userId)) {
            LOGGER.warning("Attempt to edit notification not sent by userId: " + userId);
            request.setAttribute("error", "Cannot edit notification not sent by you.");
            request.getRequestDispatcher("/manager/viewNotification.jsp").forward(request, response);
            return;
        }

        notification.setTitle(title.trim());
        notification.setMessage(message.trim());

        boolean success = notificationDAO.updateNotification(notification);
        if (success) {
            LOGGER.info("Notification updated successfully, ID: " + notificationID + ", userId: " + userId);
            request.setAttribute("success", "Notification updated successfully.");
        } else {
            LOGGER.warning("Failed to update notification, ID: " + notificationID + ", userId: " + userId);
            request.setAttribute("error", "Failed to update notification.");
        }
        response.sendRedirect(request.getContextPath() + "/manager/notification?idUser=" + userId);
    }

    private void deleteNotification(HttpServletRequest request, HttpServletResponse response, Integer userId)
            throws SQLException, ServletException, IOException {
        String notificationIDStr = request.getParameter("notificationID");
        if (notificationIDStr == null || notificationIDStr.trim().isEmpty()) {
            LOGGER.warning("Invalid notification ID for deletion, userId: " + userId);
            request.setAttribute("error", "Notification ID is required.");
            request.getRequestDispatcher("/manager/viewNotification.jsp").forward(request, response);
            return;
        }

        int notificationID = Integer.parseInt(notificationIDStr);
        Notification notification = notificationDAO.getNotificationById(notificationID);

        if (notification == null) {
            LOGGER.warning("Notification not found for ID: " + notificationID + ", userId: " + userId);
            request.setAttribute("error", "Notification not found.");
            request.getRequestDispatcher("/manager/viewNotification.jsp").forward(request, response);
            return;
        }

        // Check if the notification was sent by this manager
        if (notification.getSentBy() == null || !notification.getSentBy().equals(userId)) {
            LOGGER.warning("Attempt to delete notification not sent by userId: " + userId);
            request.setAttribute("error", "Cannot delete notification not sent by you.");
            request.getRequestDispatcher("/manager/viewNotification.jsp").forward(request, response);
            return;
        }

        boolean success = notificationDAO.deleteNotification(notificationID);
        if (success) {
            LOGGER.info("Notification deleted successfully, ID: " + notificationID + ", userId: " + userId);
            request.setAttribute("success", "Notification deleted successfully.");
        } else {
            LOGGER.warning("Failed to delete notification, ID: " + notificationID + ", userId: " + userId);
            request.setAttribute("error", "Failed to delete notification.");
        }
        response.sendRedirect(request.getContextPath() + "/manager/notification?idUser=" + userId);
    }

    @Override
    public String getServletInfo() {
        return "Servlet to manage notifications for managers";
    }
}