package controller;

import dao.CustomerDAO;
import dao.UserDAO;
import dao.BlockDAO;
import dao.NotificationDAO;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Customer;
import model.User;
import model.Block;
import utils.EmailConfig;

@WebServlet("/admin/account")
public class AdminAccountServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final BlockDAO blockDAO = new BlockDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private static final Logger LOGGER = Logger.getLogger(AdminAccountServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is Admin
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"Admin".equals(currentUser.getRole().getRoleName())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Default to list action
        }

        try {
            switch (action) {
                case "createManager":
                    List<Block> blockList = blockDAO.getAllBlocks();
                    request.setAttribute("blockList", blockList);
                    request.getRequestDispatcher("/admin/createManager.jsp").forward(request, response);
                    break;
                case "createCustomer":
                    request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
                    break;
                case "viewDetail":
                    String customerIdStr = request.getParameter("customerID");
                    if (customerIdStr == null) {
                        response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Customer ID is required.", "UTF-8"));
                        return;
                    }
                    int customerID = Integer.parseInt(customerIdStr);
                    List<Map<String, Object>> detailList = customerDAO.getFullCustomerDetails(customerID);
                    request.setAttribute("details", detailList);
                    request.getRequestDispatcher("/admin/viewDetailsCustomer.jsp").forward(request, response);
                    break;
                case "editUser":
                    String userIDStr = request.getParameter("userID");
                    if (userIDStr == null) {
                        response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("User ID is required.", "UTF-8"));
                        return;
                    }
                    int userID = Integer.parseInt(userIDStr);
                    User user = userDAO.getUserById(userID);
                    List<Block> blockListEdit = null;
                    if (user != null && user.getRole() != null && "Manager".equals(user.getRole().getRoleName())) {
                        blockListEdit = blockDAO.getAllBlocks();
                    }
                    request.setAttribute("user", user);
                    request.setAttribute("blockList", blockListEdit);
                    request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
                    break;
                case "editCustomer":
                    String customerIDStr = request.getParameter("customerID");
                    if (customerIDStr == null) {
                        response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Customer ID is required.", "UTF-8"));
                        return;
                    }
                    int customerIDEdit = Integer.parseInt(customerIDStr);
                    Customer customer = customerDAO.getCustomerById(customerIDEdit);
                    if (customer == null) {
                        response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Customer not found.", "UTF-8"));
                        return;
                    }
                    request.setAttribute("customer", customer);
                    request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
                    break;
                case "delete":
                case "deleteCustomer":
                    deleteAccount(request, response);
                    break;
                case "list":
                default:
                    displayAccountList(request, response);
                    break;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "System Error in doGet: " + action, ex);
            response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("System Error: " + ex.getMessage(), "UTF-8"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is Admin
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"Admin".equals(currentUser.getRole().getRoleName())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            LOGGER.warning("No action specified in doPost");
            response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("No action specified.", "UTF-8"));
            return;
        }

        try {
            switch (action) {
                case "addManager":
                    addManager(request, response);
                    break;
                case "addCustomer":
                    addCustomer(request, response);
                    break;
                case "edit":
                    editAccount(request, response);
                    break;
                case "delete":
                    deleteAccount(request, response);
                    break;
                case "disableUser":
                    disableUser(request, response);
                    break;
                case "disableCustomer":
                    disableCustomer(request, response);
                    break;
                default:
                    LOGGER.warning("Unknown action: " + action);
                    response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Unknown action: " + action, "UTF-8"));
                    break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in doPost for action: " + action, e);
            String errorMessage = "Database Error: " + e.getMessage();
            if ("edit".equals(action) && request.getParameter("customerID") != null) {
                preserveFormData(request);
                response.sendRedirect(request.getContextPath() + "/admin/account?action=editCustomer&customerID=" + request.getParameter("customerID") + "&error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
            }
        }
    }

    private void displayAccountList(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        LOGGER.info("Displaying account list");
        List<User> userList = userDAO.getAllUsers();
        List<Customer> customerList = customerDAO.getAllCustomers();
        List<Integer> cannotDeleteCustomerIds = new ArrayList<>();

        for (Customer c : customerList) {
            if (customerDAO.hasContractOrRentalRequest(c.getCustomerID())) {
                cannotDeleteCustomerIds.add(c.getCustomerID());
            }
        }

        int totalCustomers = customerDAO.getTotalCustomerCount();
        int activeCustomers = customerDAO.getActiveCustomerCount();
        int inactiveCustomers = customerDAO.getInactiveCustomerCount();

        request.setAttribute("userList", userList);
        request.setAttribute("customerList", customerList);
        request.setAttribute("cannotDeleteCustomerIds", cannotDeleteCustomerIds);
        request.setAttribute("totalCustomers", totalCustomers);
        request.setAttribute("activeCustomers", activeCustomers);
        request.setAttribute("inactiveCustomers", inactiveCustomers);
        request.getRequestDispatcher("/admin/viewListAccount.jsp").forward(request, response);
    }

    private void disableCustomer(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        String customerIDStr = request.getParameter("customerID");
        String reason = request.getParameter("reason");

        if (customerIDStr == null || customerIDStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Customer ID is required for disabling.", "UTF-8"));
            return;
        }
        if (reason == null || reason.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Reason is required for disabling.", "UTF-8"));
            return;
        }

        int customerID;
        try {
            customerID = Integer.parseInt(customerIDStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Invalid Customer ID format.", "UTF-8"));
            return;
        }

        Customer customer = customerDAO.getCustomerById(customerID);
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Customer not found.", "UTF-8"));
            return;
        }

        if (customerDAO.hasContractOrRentalRequest(customerID)) {
            response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Cannot disable customer with active contract or rental request.", "UTF-8"));
            return;
        }

        boolean success = customerDAO.updateCustomerStatus(customerID, "Inactive");
        if (!success) {
            response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Failed to disable customer.", "UTF-8"));
            return;
        }

        String email = customer.getEmail();
        String subject = "Your Account Has Been Disabled";
        String message = "Dear " + customer.getCustomerFullName() + ",\n\n"
                + "We regret to inform you that your account has been disabled.\n\n"
                + "Reason: " + reason.trim() + "\n\n"
                + "If you have any questions, please contact our support team at support@homenest.com.\n\n"
                + "Best regards,\nHomeNest Team";

        if (email == null || email.trim().isEmpty() || !isValidEmail(email)) {
            customerDAO.logEmail(customerID, null, email != null ? email : "N/A", subject, message, "Failed", "No valid email address provided.");
            response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Customer disabled, but no valid email address found to send notification.", "UTF-8"));
        } else {
            try {
                sendDisableEmail(email, customer.getCustomerFullName(), reason.trim());
                customerDAO.logEmail(customerID, null, email, subject, message, "Sent", null);
                response.sendRedirect(request.getContextPath() + "/admin/account?success=" + java.net.URLEncoder.encode("Customer disabled and email logged successfully.", "UTF-8"));
            } catch (MessagingException e) {
                LOGGER.log(Level.SEVERE, "Failed to send email for customer " + customerID, e);
                customerDAO.logEmail(customerID, null, email, subject, message, "Failed", e.getMessage());
                response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Customer disabled, but failed to send email: " + e.getMessage(), "UTF-8"));
            }
        }
    }

    private void sendDisableEmail(String recipientEmail, String customerName, String reason) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", EmailConfig.SMTP_HOST);
        props.put("mail.smtp.port", EmailConfig.SMTP_PORT);
        props.put("mail.debug", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailConfig.SENDER_EMAIL, EmailConfig.SENDER_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EmailConfig.SENDER_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Your Account Has Been Disabled");

        String htmlContent = "<html><body><h2>Dear " + customerName + ",</h2>"
                + "<p>We regret to inform you that your account has been disabled.</p>"
                + "<p><strong>Reason:</strong> " + reason + "</p>"
                + "<p>If you have any questions, please contact our support team at <a href='mailto:support@homenest.com'>support@homenest.com</a>.</p>"
                + "<p>Best regards,<br>HomeNest Team</p></body></html>";
        message.setContent(htmlContent, "text/html; charset=UTF-8");

        Transport.send(message);
    }

    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(regex);
    }

    private void deleteAccount(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        String userIDStr = request.getParameter("userID");
        String customerIDStr = request.getParameter("customerID");

        if ((userIDStr == null || userIDStr.trim().isEmpty()) && (customerIDStr == null || customerIDStr.trim().isEmpty())) {
            response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("User ID or Customer ID is required to delete.", "UTF-8"));
            return;
        }

        if (userIDStr != null && !userIDStr.trim().isEmpty()) {
            int userID = Integer.parseInt(userIDStr);
            boolean success = userDAO.deleteUser(userID);
            if (!success) {
                response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Failed to delete user.", "UTF-8"));
                return;
            }
        } else if (customerIDStr != null && !customerIDStr.trim().isEmpty()) {
            int customerID = Integer.parseInt(customerIDStr);
            if (customerDAO.hasContractOrRentalRequest(customerID)) {
                response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Cannot delete customer with active contract or rental request.", "UTF-8"));
                return;
            }
            boolean success = customerDAO.deleteCustomer(customerID);
            if (!success) {
                response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Failed to delete customer.", "UTF-8"));
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/account");
    }

    private void addManager(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String blockIDStr = request.getParameter("blockID");

        if (fullName == null || fullName.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || phoneNumber == null || phoneNumber.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || confirmPassword == null || confirmPassword.trim().isEmpty()
                || blockIDStr == null || blockIDStr.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            List<Block> blockList = blockDAO.getAllBlocks();
            request.setAttribute("blockList", blockList);
            request.getRequestDispatcher("/admin/createManager.jsp").forward(request, response);
            return;
        }
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match.");
            List<Block> blockList = blockDAO.getAllBlocks();
            request.setAttribute("blockList", blockList);
            request.getRequestDispatcher("/admin/createManager.jsp").forward(request, response);
            return;
        }
        if (!phoneNumber.matches("\\d{10,11}")) {
            request.setAttribute("error", "Phone number must be 10-11 digits.");
            List<Block> blockList = blockDAO.getAllBlocks();
            request.setAttribute("blockList", blockList);
            request.getRequestDispatcher("/admin/createManager.jsp").forward(request, response);
            return;
        }
        if (userDAO.isEmailExists(email.trim())) {
            request.setAttribute("error", "Email already exists!");
            List<Block> blockList = blockDAO.getAllBlocks();
            request.setAttribute("blockList", blockList);
            request.getRequestDispatcher("/admin/createManager.jsp").forward(request, response);
            return;
        }
        if (userDAO.isNameExists(fullName.trim())) {
            request.setAttribute("error", "Full name already exists!");
            List<Block> blockList = blockDAO.getAllBlocks();
            request.setAttribute("blockList", blockList);
            request.getRequestDispatcher("/admin/createManager.jsp").forward(request, response);
            return;
        }

        User user = new User();
        user.setUserFullName(fullName.trim());
        user.setEmail(email.trim());
        user.setPhoneNumber(phoneNumber.trim());
        user.setPassword(userDAO.hashMD5(password));
        user.setRoleID(2); // Manager
        user.setUserStatus("Active");
        user.setBlockID(Integer.parseInt(blockIDStr));

        boolean success = userDAO.addUser(user);
        if (!success) {
            request.setAttribute("error", "Failed to create manager account.");
            List<Block> blockList = blockDAO.getAllBlocks();
            request.setAttribute("blockList", blockList);
            request.getRequestDispatcher("/admin/createManager.jsp").forward(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/admin/account");
    }

    public void addCustomer(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String birthDate = request.getParameter("birthDate");
        String cccd = request.getParameter("cccd");
        String gender = request.getParameter("gender");
        String address = request.getParameter("address");

        if (fullName == null || fullName.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || phoneNumber == null || phoneNumber.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || confirmPassword == null || confirmPassword.trim().isEmpty()
                || birthDate == null || birthDate.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
            return;
        }
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match.");
            request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
            return;
        }
        if (!phoneNumber.matches("\\d{10,11}")) {
            request.setAttribute("error", "Phone number must be 10-11 digits.");
            request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
            return;
        }
        if (cccd != null && !cccd.trim().isEmpty() && !cccd.matches("\\d{12}")) {
            request.setAttribute("error", "CCCD must be 12 digits if provided.");
            request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
            return;
        }
        if (customerDAO.isEmailExists(email.trim())) {
            request.setAttribute("error", "Email already exists!");
            request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
            return;
        }
        if (customerDAO.isPhoneNumberExists(phoneNumber.trim())) {
            request.setAttribute("error", "Phone number already exists!");
            request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
            return;
        }
        if (cccd != null && !cccd.trim().isEmpty() && customerDAO.isCCCDExists(cccd.trim())) {
            request.setAttribute("error", "CCCD already exists!");
            request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
            return;
        }

        Customer customer = new Customer();
        customer.setCustomerFullName(fullName.trim());
        customer.setEmail(email.trim());
        customer.setPhoneNumber(phoneNumber.trim());
        customer.setCustomerPassword(customerDAO.hashMD5(password));
        customer.setCCCD(cccd != null && !cccd.trim().isEmpty() ? cccd.trim() : null);
        customer.setGender(gender != null && !gender.trim().isEmpty() ? gender.trim() : null);

        if (birthDate != null && !birthDate.trim().isEmpty()) {
            try {
                customer.setBirthDate(Date.valueOf(birthDate.trim()));
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.SEVERE, "Invalid date format: " + birthDate, e);
                request.setAttribute("error", "Invalid date format for birth date.");
                request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
                return;
            }
        } else {
            customer.setBirthDate(null);
        }
        customer.setAddress(address != null && !address.trim().isEmpty() ? address.trim() : null);
        customer.setCustomerStatus("Active");

        boolean success = customerDAO.createCustomer(customer);
        if (!success) {
            request.setAttribute("error", "Failed to create customer account.");
            request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/admin/account");
    }

    private void disableUser(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        String userIDStr = request.getParameter("userID");
        String reason = request.getParameter("reason");

        if (userIDStr == null || userIDStr.trim().isEmpty()) {
            request.setAttribute("error", "User ID is required for disabling.");
            displayAccountList(request, response);
            return;
        }

        if (reason == null || reason.trim().isEmpty()) {
            request.setAttribute("error", "Reason is required for disabling.");
            displayAccountList(request, response);
            return;
        }

        int userID = Integer.parseInt(userIDStr);
        User user = userDAO.getUserById(userID);

        if (user == null) {
            request.setAttribute("error", "User not found.");
            displayAccountList(request, response);
            return;
        }

        if (user.getRole() != null && "Admin".equalsIgnoreCase(user.getRole().getRoleName())) {
            request.setAttribute("error", "You cannot disable an Admin account.");
            displayAccountList(request, response);
            return;
        }

        boolean success = userDAO.deactivateUser(userID);
        if (!success) {
            request.setAttribute("error", "Failed to disable user.");
            displayAccountList(request, response);
            return;
        }

        String email = user.getEmail();
        if (email != null && !email.trim().isEmpty() && isValidEmail(email)) {
            try {
                sendDisableEmail(email, user.getUserFullName(), reason.trim());
                customerDAO.logEmail(0, userID, email, "Your Account Has Been Disabled", "Your account has been disabled. Reason: " + reason, "Sent", null);
                request.setAttribute("success", "User disabled and email sent successfully.");
            } catch (MessagingException e) {
                LOGGER.log(Level.SEVERE, "Failed to send email for user " + userID, e);
                customerDAO.logEmail(0, userID, email, "Your Account Has Been Disabled", "Your account has been disabled. Reason: " + reason, "Failed", e.getMessage());
                request.setAttribute("error", "User disabled but failed to send email: " + e.getMessage());
            }
        } else {
            request.setAttribute("error", "User disabled but no valid email address found.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/account");
    }

    public void editAccount(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        String userIDStr = request.getParameter("userID");
        String customerIDStr = request.getParameter("customerID");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String roleIDStr = request.getParameter("roleID");
        String status = request.getParameter("status");
        String reason = request.getParameter("reason");

        // Validate required fields
        if (userIDStr == null && customerIDStr == null) {
            request.setAttribute("error", "User ID or Customer ID is required.");
            preserveFormData(request);
            request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
            return;
        }

        if (fullName == null || fullName.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || phoneNumber == null || phoneNumber.trim().isEmpty()) {
            request.setAttribute("error", "Full name, email, and phone number are required.");
            preserveFormData(request);
            request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
            return;
        }

        if (!phoneNumber.matches("\\d{10,11}")) {
            request.setAttribute("error", "Phone number must be 10-11 digits.");
            preserveFormData(request);
            request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
            return;
        }

        if (status == null || status.trim().isEmpty()) {
            request.setAttribute("error", "Status is required.");
            preserveFormData(request);
            request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
            return;
        }

        if (userIDStr != null && !userIDStr.trim().isEmpty()) {
            // Handle User Edit
            int userID = Integer.parseInt(userIDStr);
            int roleID = Integer.parseInt(roleIDStr);

            User user = userDAO.getUserById(userID);
            if (user == null) {
                request.setAttribute("error", "User not found.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
                return;
            }
            if (user.getRole() != null && "Admin".equalsIgnoreCase(user.getRole().getRoleName())) {
                request.setAttribute("error", "Admin account cannot be edited.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
                return;
            }

            if (userDAO.isEmailExists(email.trim()) && !email.trim().equals(user.getEmail())) {
                request.setAttribute("error", "Email already exists.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
                return;
            }

            String originalStatus = user.getUserStatus();
            user.setUserFullName(fullName.trim());
            user.setEmail(email.trim());
            user.setPhoneNumber(phoneNumber.trim());
            user.setUserStatus(status.trim());
            user.setRoleID(roleID);

            if (roleID == 2) { // Manager role
                String blockIDStr = request.getParameter("blockID");
                if (blockIDStr == null || blockIDStr.trim().isEmpty()) {
                    request.setAttribute("error", "Block is required for Manager.");
                    preserveFormData(request);
                    request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
                    return;
                }
                user.setBlockID(Integer.parseInt(blockIDStr));
            } else {
                user.setBlockID(null);
            }

            boolean success = userDAO.updateUser(user);
            if (!success) {
                request.setAttribute("error", "Failed to update user.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
                return;
            }

            // Send email if status changed
            if (!status.equals(originalStatus) && reason != null && !reason.trim().isEmpty()) {
                String emailSubject = status.equals("Inactive") ? "Your Account Has Been Disabled" : "Your Account Has Been Activated";
                String emailMessage = status.equals("Inactive")
                        ? "Dear " + user.getUserFullName() + ",\n\nYour account has been disabled.\n\nReason: " + reason
                        + "\n\nIf you have any questions, please contact our support team at support@homenest.com.\n\nBest regards,\nHomeNest Team"
                        : "Dear " + user.getUserFullName() + ",\n\nYour account has been activated.\n\nReason: " + reason
                        + "\n\nWelcome back! If you have any questions, please contact our support team at support@homenest.com.\n\nBest regards,\nHomeNest Team";
                if (email != null && !email.trim().isEmpty() && isValidEmail(email)) {
                    try {
                        sendStatusChangeEmail(email, user.getUserFullName(), status, reason);
                        customerDAO.logEmail(0, userID, email, emailSubject, emailMessage, "Sent", null);
                        request.setAttribute("success", "User updated and email sent successfully.");
                    } catch (MessagingException e) {
                        LOGGER.log(Level.SEVERE, "Failed to send email for user " + userID, e);
                        customerDAO.logEmail(0, userID, email, emailSubject, emailMessage, "Failed", e.getMessage());
                        request.setAttribute("error", "User updated but failed to send email: " + e.getMessage());
                    }
                } else {
                    customerDAO.logEmail(0, userID, email != null ? email : "N/A", emailSubject, emailMessage, "Failed", "Invalid or missing email address.");
                    request.setAttribute("error", "User updated but no valid email address found to send notification.");
                }
            } else if (!status.equals(originalStatus)) {
                request.setAttribute("error", "Reason is required for status change.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
                return;
            }
            request.setAttribute("success", "User updated successfully.");
            response.sendRedirect(request.getContextPath() + "/admin/account");
        } else if (customerIDStr != null && !customerIDStr.trim().isEmpty()) {
            // Handle Customer Edit
            int customerID = Integer.parseInt(customerIDStr);
            Customer customer = customerDAO.getCustomerById(customerID);

            if (customer == null) {
                request.setAttribute("error", "Customer not found.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
                return;
            }

            if (customerDAO.isEmailExists(email.trim()) && !email.trim().equals(customer.getEmail())) {
                request.setAttribute("error", "Email already exists.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
                return;
            }
            if (customerDAO.isPhoneNumberExists(phoneNumber.trim()) && !phoneNumber.trim().equals(customer.getPhoneNumber())) {
                request.setAttribute("error", "Phone number already exists.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
                return;
            }

            String cccd = request.getParameter("cccd");
            if (cccd != null && !cccd.trim().isEmpty() && !cccd.matches("\\d{12}")) {
                request.setAttribute("error", "CCCD must be 12 digits if provided.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
                return;
            }
            if (cccd != null && !cccd.trim().isEmpty() && customerDAO.isCCCDExists(cccd.trim()) && !cccd.trim().equals(customer.getCCCD())) {
                request.setAttribute("error", "CCCD already exists.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
                return;
            }

            String originalStatus = customer.getCustomerStatus();
            customer.setCustomerFullName(fullName.trim());
            customer.setEmail(email.trim());
            customer.setPhoneNumber(phoneNumber.trim());
            customer.setCustomerStatus(status.trim());

            String gender = request.getParameter("gender");
            String birthDate = request.getParameter("birthDate");
            String address = request.getParameter("address");

            if (cccd != null && !cccd.trim().isEmpty()) {
                customer.setCCCD(cccd.trim());
            } else {
                customer.setCCCD(null);
            }
            if (gender != null && !gender.trim().isEmpty()) {
                customer.setGender(gender.trim());
            } else {
                customer.setGender(null);
            }
            if (birthDate != null && !birthDate.trim().isEmpty()) {
                try {
                    Date sqlDate = Date.valueOf(birthDate.trim());
                    customer.setBirthDate(sqlDate);
                } catch (IllegalArgumentException e) {
                    LOGGER.log(Level.SEVERE, "Invalid date format: " + birthDate, e);
                    request.setAttribute("error", "Invalid date format for birth date.");
                    preserveFormData(request);
                    request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
                    return;
                }
            } else {
                customer.setBirthDate(null);
            }
            if (address != null && !address.trim().isEmpty()) {
                customer.setAddress(address.trim());
            } else {
                customer.setAddress(null);
            }

            boolean success = customerDAO.updateCustomer(customer);
            if (!success) {
                request.setAttribute("error", "Failed to update customer.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
                return;
            }

            // Send email if status changed
            if (!status.equals(originalStatus) && reason != null && !reason.trim().isEmpty()) {
                String emailSubject = status.equals("Inactive") ? "Your Account Has Been Disabled" : "Your Account Has Been Activated";
                String emailMessage = status.equals("Inactive")
                        ? "Dear " + customer.getCustomerFullName() + ",\n\nYour account has been disabled.\n\nReason: " + reason
                        + "\n\nIf you have any questions, please contact our support team at support@homenest.com.\n\nBest regards,\nHomeNest Team"
                        : "Dear " + customer.getCustomerFullName() + ",\n\nYour account has been activated.\n\nReason: " + reason
                        + "\n\nWelcome back! If you have any questions, please contact our support team at support@homenest.com.\n\nBest regards,\nHomeNest Team";
                if (email != null && !email.trim().isEmpty() && isValidEmail(email)) {
                    try {
                        sendStatusChangeEmail(email, customer.getCustomerFullName(), status, reason);
                        customerDAO.logEmail(customerID, null, email, emailSubject, emailMessage, "Sent", null);
                        request.setAttribute("success", "Customer updated and email sent successfully.");
                    } catch (MessagingException e) {
                        LOGGER.log(Level.SEVERE, "Failed to send email for customer " + customerID, e);
                        customerDAO.logEmail(customerID, null, email, emailSubject, emailMessage, "Failed", e.getMessage());
                        request.setAttribute("error", "Customer updated but failed to send email: " + e.getMessage());
                    }
                } else {
                    customerDAO.logEmail(customerID, null, email != null ? email : "N/A", emailSubject, emailMessage, "Failed", "Invalid or missing email address.");
                    request.setAttribute("error", "Customer updated but no valid email address found to send notification.");
                }
            } else if (!status.equals(originalStatus)) {
                request.setAttribute("error", "Reason is required for status change.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
                return;
            }
            request.setAttribute("success", "Customer updated successfully.");
            response.sendRedirect(request.getContextPath() + "/admin/account");
        }
    }

    public void sendStatusChangeEmail(String recipientEmail, String name, String status, String reason) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", EmailConfig.SMTP_HOST);
        props.put("mail.smtp.port", EmailConfig.SMTP_PORT);
        props.put("mail.debug", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailConfig.SENDER_EMAIL, EmailConfig.SENDER_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EmailConfig.SENDER_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject(status.equals("Inactive") ? "Your Account Has Been Disabled" : "Your Account Has Been Activated");

        String htmlContent = "<html><body><h2>Dear " + name + ",</h2>"
                + "<p>We would like to inform you that your account has been " + (status.equals("Inactive") ? "disabled" : "activated") + ".</p>"
                + "<p><strong>Reason:</strong> " + reason + "</p>"
                + "<p>If you have any questions, please contact our support team at <a href='mailto:support@homenest.com'>support@homenest.com</a>.</p>"
                + "<p>Best regards,<br>HomeNest Team</p></body></html>";
        message.setContent(htmlContent, "text/html; charset=UTF-8");

        Transport.send(message);
    }

    private void preserveFormData(HttpServletRequest request) {
        request.setAttribute("formFullName", request.getParameter("fullName"));
        request.setAttribute("formEmail", request.getParameter("email"));
        request.setAttribute("formPhoneNumber", request.getParameter("phoneNumber"));
        request.setAttribute("formCCCD", request.getParameter("cccd"));
        request.setAttribute("formGender", request.getParameter("gender"));
        request.setAttribute("formBirthDate", request.getParameter("birthDate"));
        request.setAttribute("formAddress", request.getParameter("address"));
        request.setAttribute("formStatus", request.getParameter("status"));
        request.setAttribute("formReason", request.getParameter("reason"));
        // Loại bỏ gọi lại getCustomerById để tránh vòng lặp
        // if (request.getParameter("customerID") != null) {
        //     try {
        //         int customerID = Integer.parseInt(request.getParameter("customerID"));
        //         Customer customer = customerDAO.getCustomerById(customerID);
        //         request.setAttribute("customer", customer);
        //     } catch (NumberFormatException | SQLException e) {
        //         LOGGER.log(Level.SEVERE, "Error preserving customer data", e);
        //     }
        // }
    }

}
