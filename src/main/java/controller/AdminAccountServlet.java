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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is Admin
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"Admin".equals(currentUser.getRole().getRoleName())) {
            response.sendRedirect(request.getContextPath() + "/Login");
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
                    System.out.println("User fetched: " + (user != null ? user.getUserFullName() : "null"));
                    List<Block> blockListEdit = null;
                    if (user != null && user.getRole() != null && "Manager".equals(user.getRole().getRoleName())) {
                        blockListEdit = blockDAO.getAllBlocks();
                        System.out.println("Block list size: " + (blockListEdit != null ? blockListEdit.size() : 0));
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
            System.out.println("System Error in doGet: " + action + " - " + ex.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("System Error: " + ex.getMessage(), "UTF-8"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is Admin
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"Admin".equals(currentUser.getRole().getRoleName())) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            System.out.println("No action specified in doPost");
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
                    System.out.println("Unknown action: " + action);
                    response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Unknown action: " + action, "UTF-8"));
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error in doPost for action: " + action + " - " + e.getMessage());
            String errorMessage = "Database Error: " + e.getMessage();
            if ("edit".equals(action) && request.getParameter("customerID") != null) {
                preserveFormData(request);
                response.sendRedirect(request.getContextPath() + "/admin/account?action=editCustomer&customerID=" + request.getParameter("customerID") + "&error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
            } else if ("edit".equals(action) && request.getParameter("userID") != null) {
                preserveFormData(request);
                response.sendRedirect(request.getContextPath() + "/admin/account?action=editUser&userID=" + request.getParameter("userID") + "&error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
            }
        }
    }

    private void displayAccountList(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        System.out.println("Displaying account list");
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

        boolean success = customerDAO.updateCustomerStatus(customerID, "Disable");
        if (!success) {
            response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Failed to disable customer.", "UTF-8"));
            return;
        }

        String email = customer.getEmail();
        String subject = "Tài Khoản Của Bạn Đã Bị Vô Hiệu Hóa";
        String message = "Kính gửi " + customer.getCustomerFullName() + ",\n\n"
                + "Chúng tôi rất tiếc phải thông báo rằng tài khoản của bạn đã bị vô hiệu hóa.\n\n"
                + "Lý do: " + reason.trim() + "\n\n"
                + "Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với đội ngũ hỗ trợ của chúng tôi tại support@homenest.com.\n\n"
                + "Trân trọng,\nĐội Ngũ HomeNest";

        if (email == null || email.trim().isEmpty() || !isValidEmail(email)) {
            customerDAO.logEmail(customerID, null, email != null ? email : "N/A", subject, message, "Failed", "No valid email address provided.");
            response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Khách hàng đã bị vô hiệu hóa, nhưng không tìm thấy địa chỉ email hợp lệ để gửi thông báo.", "UTF-8"));
        } else {
            try {
                sendDisableEmail(email, customer.getCustomerFullName(), reason.trim());
                customerDAO.logEmail(customerID, null, email, subject, message, "Sent", null);
                response.sendRedirect(request.getContextPath() + "/admin/account?success=" + java.net.URLEncoder.encode("Khách hàng đã bị vô hiệu hóa và email đã được ghi nhận thành công.", "UTF-8"));
            } catch (MessagingException e) {
                System.out.println("Không thể gửi email cho khách hàng " + customerID + " - " + e.getMessage());
                customerDAO.logEmail(customerID, null, email, subject, message, "Failed", e.getMessage());
                response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Khách hàng đã bị vô hiệu hóa, nhưng không thể gửi email: " + e.getMessage(), "UTF-8"));
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
        message.setSubject("Tài Khoản Của Bạn Đã Bị Vô Hiệu Hóa");

        String htmlContent = "<html><body><h2>Kính gửi " + customerName + ",</h2>"
                + "<p>Chúng tôi rất tiếc phải thông báo rằng tài khoản của bạn đã bị vô hiệu hóa.</p>"
                + "<p><strong>Lý do:</strong> " + reason + "</p>"
                + "<p>Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với đội ngũ hỗ trợ của chúng tôi tại <a href='mailto:support@homenest.com'>support@homenest.com</a>.</p>"
                + "<p>Trân trọng,<br>Đội Ngũ HomeNest</p></body></html>";
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
            response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("User ID hoặc Customer ID là bắt buộc để xóa.", "UTF-8"));
            return;
        }

        if (userIDStr != null && !userIDStr.trim().isEmpty()) {
            int userID = Integer.parseInt(userIDStr);
            boolean success = userDAO.deleteUser(userID);
            if (!success) {
                response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Không thể xóa người dùng.", "UTF-8"));
                return;
            }
        } else if (customerIDStr != null && !customerIDStr.trim().isEmpty()) {
            int customerID = Integer.parseInt(customerIDStr);
            if (customerDAO.hasContractOrRentalRequest(customerID)) {
                response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Không thể xóa khách hàng có hợp đồng hoặc yêu cầu thuê đang hoạt động.", "UTF-8"));
                return;
            }
            boolean success = customerDAO.deleteCustomer(customerID);
            if (!success) {
                response.sendRedirect(request.getContextPath() + "/admin/account?error=" + java.net.URLEncoder.encode("Không thể xóa khách hàng.", "UTF-8"));
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

        request.setAttribute("formFullName", fullName);
        request.setAttribute("formEmail", email);
        request.setAttribute("formPhoneNumber", phoneNumber);
        request.setAttribute("formPassword", password);
        request.setAttribute("formConfirmPassword", confirmPassword);
        request.setAttribute("formBlockID", blockIDStr);

        if (fullName == null || fullName.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || phoneNumber == null || phoneNumber.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || confirmPassword == null || confirmPassword.trim().isEmpty()
                || blockIDStr == null || blockIDStr.trim().isEmpty()) {
            request.setAttribute("error", "Tất cả các trường là bắt buộc.");
            List<Block> blockList = blockDAO.getAllBlocks();
            request.setAttribute("blockList", blockList);
            request.getRequestDispatcher("/admin/createManager.jsp").forward(request, response);
            return;
        }
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu không khớp.");
            List<Block> blockList = blockDAO.getAllBlocks();
            request.setAttribute("blockList", blockList);
            request.getRequestDispatcher("/admin/createManager.jsp").forward(request, response);
            return;
        }
        if (!phoneNumber.matches("\\d{10}")) { // Chỉ kiểm tra 10 chữ số, tiền tố đã kiểm tra ở client-side
            request.setAttribute("error", "Số điện thoại phải có đúng 10 chữ số.");
            List<Block> blockList = blockDAO.getAllBlocks();
            request.setAttribute("blockList", blockList);
            request.getRequestDispatcher("/admin/createManager.jsp").forward(request, response);
            return;
        }
        if (userDAO.isEmailExists(email.trim())) {
            request.setAttribute("error", "Email đã tồn tại!");
            List<Block> blockList = blockDAO.getAllBlocks();
            request.setAttribute("blockList", blockList);
            request.getRequestDispatcher("/admin/createManager.jsp").forward(request, response);
            return;
        }

        int blockID = Integer.parseInt(blockIDStr);
        if (userDAO.hasActiveManager(blockID)) {
            request.setAttribute("error", "Khu này đã có quản lý đang hoạt động. Vui lòng vô hiệu hóa quản lý hiện tại trước.");
            List<Block> blockList = blockDAO.getAllBlocks();
            request.setAttribute("blockList", blockList);
            request.getRequestDispatcher("/admin/createManager.jsp").forward(request, response);
            return;
        }

        User user = new User();
        user.setUserFullName(fullName.trim());
        user.setEmail(email.trim());
        user.setPhoneNumber(phoneNumber.trim());

        user.setPassword(password);
        user.setRoleID(2);
        user.setUserStatus("Active");
        user.setBlockID(Integer.parseInt(blockIDStr));

        boolean success = userDAO.addUser(user);
        if (!success) {
            request.setAttribute("error", "Không thể tạo tài khoản quản lý.");
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

        // Lưu dữ liệu đã nhập vào request
        request.setAttribute("formFullName", fullName);
        request.setAttribute("formEmail", email);
        request.setAttribute("formPhoneNumber", phoneNumber);
        request.setAttribute("formPassword", password);
        request.setAttribute("formConfirmPassword", confirmPassword);
        request.setAttribute("formBirthDate", birthDate);
        request.setAttribute("formCCCD", cccd);
        request.setAttribute("formGender", gender);
        request.setAttribute("formAddress", address);

        if (fullName == null || fullName.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || phoneNumber == null || phoneNumber.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || confirmPassword == null || confirmPassword.trim().isEmpty()
                || birthDate == null || birthDate.trim().isEmpty()) {
            request.setAttribute("error", "Tất cả các trường là bắt buộc.");
            request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
            return;
        }
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu không khớp.");
            request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
            return;
        }
        if (!phoneNumber.matches("\\d{10}")) { // Chỉ kiểm tra 10 chữ số, tiền tố đã kiểm tra ở client-side
            request.setAttribute("error", "Số điện thoại phải có đúng 10 chữ số.");
            request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
            return;
        }
        if (cccd != null && !cccd.trim().isEmpty() && !cccd.matches("\\d{12}")) {
            request.setAttribute("error", "CCCD phải có 12 chữ số nếu được cung cấp.");
            request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
            return;
        }
        if (customerDAO.isEmailExists(email.trim())) {
            request.setAttribute("error", "Email đã tồn tại!");
            request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
            return;
        }
        if (customerDAO.isPhoneNumberExists(phoneNumber.trim())) {
            request.setAttribute("error", "Số điện thoại đã tồn tại!");
            request.setAttribute("formFullName", fullName); // Lưu lại fullName
            request.setAttribute("formEmail", email); // Lưu lại email
            request.setAttribute("formPhoneNumber", phoneNumber); // Lưu lại phoneNumber
            request.setAttribute("formCCCD", cccd); // Lưu lại cccd
            request.setAttribute("formGender", gender); // Lưu lại gender
            request.setAttribute("formBirthDate", birthDate); // Lưu lại birthDate
            request.setAttribute("formAddress", address); // Lưu lại address
            request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response); // Forward với dữ liệu
            return;
        }
        if (cccd != null && !cccd.trim().isEmpty() && customerDAO.isCCCDExists(cccd.trim())) {
            request.setAttribute("error", "CCCD đã tồn tại!");
            request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
            return;
        }

        Customer customer = new Customer();
        customer.setCustomerFullName(fullName.trim());
        customer.setEmail(email.trim());
        customer.setPhoneNumber(phoneNumber.trim());
        customer.setCustomerPassword(password);
        customer.setCCCD(cccd != null && !cccd.trim().isEmpty() ? cccd.trim() : null);
        customer.setGender(gender != null && !gender.trim().isEmpty() ? gender.trim() : null);

        if (birthDate != null && !birthDate.trim().isEmpty()) {
            try {
                customer.setBirthDate(Date.valueOf(birthDate.trim()));
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid date format: " + birthDate + " - " + e.getMessage());
                request.setAttribute("error", "Định dạng ngày sinh không hợp lệ.");
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
            request.setAttribute("error", "Không thể tạo tài khoản khách hàng.");
            request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/admin/account");
    }

// ... (phần còn lại của file giữ nguyên)
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
                customerDAO.logEmail(0, userID, email, "Tài Khoản Của Bạn Đã Bị Vô Hiệu Hóa", "Tài khoản của bạn đã bị vô hiệu hóa. Lý do: " + reason, "Sent", null);
                request.setAttribute("success", "Người dùng đã bị vô hiệu hóa và email đã được gửi thành công.");
            } catch (MessagingException e) {
                System.out.println("Không thể gửi email cho người dùng " + userID + " - " + e.getMessage());
                customerDAO.logEmail(0, userID, email, "Tài Khoản Của Bạn Đã Bị Vô Hiệu Hóa", "Tài khoản của bạn đã bị vô hiệu hóa. Lý do: " + reason, "Failed", e.getMessage());
                request.setAttribute("error", "Người dùng đã bị vô hiệu hóa nhưng không thể gửi email: " + e.getMessage());
            }
        } else {
            request.setAttribute("error", "Người dùng đã bị vô hiệu hóa nhưng không tìm thấy địa chỉ email hợp lệ.");
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

        // Lưu dữ liệu đã nhập vào request
        request.setAttribute("formFullName", fullName);
        request.setAttribute("formEmail", email);
        request.setAttribute("formPhoneNumber", phoneNumber);
        request.setAttribute("formRoleID", roleIDStr);
        request.setAttribute("formStatus", status);
        request.setAttribute("formReason", reason);
        if (userIDStr != null && !userIDStr.trim().isEmpty()) {
            String blockIDStr = request.getParameter("blockID");
            request.setAttribute("formBlockID", blockIDStr);
        }

        // Validate required fields
        if (userIDStr == null && customerIDStr == null) {
            request.setAttribute("error", "User ID hoặc Customer ID là bắt buộc.");
            preserveFormData(request);
            request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
            return;
        }

        if (fullName == null || fullName.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || phoneNumber == null || phoneNumber.trim().isEmpty()) {
            request.setAttribute("error", "Họ và tên, email, và số điện thoại là bắt buộc.");
            preserveFormData(request);
            request.getRequestDispatcher(userIDStr != null ? "/admin/editUser.jsp" : "/admin/editCustomer.jsp").forward(request, response);
            return;
        }

        if (!phoneNumber.matches("\\d{10}")) { // Chỉ kiểm tra 10 chữ số
            request.setAttribute("error", "Số điện thoại phải có đúng 10 chữ số.");
            preserveFormData(request);
            request.getRequestDispatcher(userIDStr != null ? "/admin/editUser.jsp" : "/admin/editCustomer.jsp").forward(request, response);
            return;
        }

        if (status == null || status.trim().isEmpty()) {
            request.setAttribute("error", "Trạng thái là bắt buộc.");
            preserveFormData(request);
            request.getRequestDispatcher(userIDStr != null ? "/admin/editUser.jsp" : "/admin/editCustomer.jsp").forward(request, response);
            return;
        }

        if (userIDStr != null && !userIDStr.trim().isEmpty()) {
            // Handle User Edit
            int userID = Integer.parseInt(userIDStr);
            int roleID = Integer.parseInt(roleIDStr);

            User user = userDAO.getUserById(userID);
            if (user == null) {
                request.setAttribute("error", "Người dùng không tìm thấy.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
                return;
            }
            if (user.getRole() != null && "Admin".equalsIgnoreCase(user.getRole().getRoleName())) {
                request.setAttribute("error", "Tài khoản Admin không thể chỉnh sửa.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
                return;
            }

            if (userDAO.isEmailExists(email.trim()) && !email.trim().equals(user.getEmail())) {
                request.setAttribute("error", "Email đã tồn tại.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
                return;
            }

            String originalStatus = user.getUserStatus();
            Integer originalBlockID = user.getBlockID();
            user.setUserFullName(fullName.trim());
            user.setEmail(email.trim());
            user.setPhoneNumber(phoneNumber.trim());
            user.setUserStatus(status.trim());
            user.setRoleID(roleID);

            if (roleID == 2) { // Manager role
                String blockIDStr = request.getParameter("blockID");
                if (blockIDStr == null || blockIDStr.trim().isEmpty()) {
                    request.setAttribute("error", "Khu là bắt buộc cho Quản Lý.");
                    preserveFormData(request);
                    request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
                    return;
                }
                int newBlockID = Integer.parseInt(blockIDStr);
                user.setBlockID(newBlockID);

                // Check if changing block and new block has active manager
                if (originalBlockID == null || originalBlockID != newBlockID) {
                    if (userDAO.hasActiveManager(newBlockID)) {
                        request.setAttribute("error", "Khu đã chọn đã có quản lý đang hoạt động. Không thể phân công.");
                        preserveFormData(request);
                        request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
                        return;
                    }
                }
            } else {
                user.setBlockID(null);
            }

            // Check if activating a disabled a manager and block has active manager
            if (status.equals("Active") && originalStatus.equals("Disable") && roleID == 2) {
                if (userDAO.hasActiveManager(user.getBlockID())) {
                    request.setAttribute("error", "Không thể kích hoạt quản lý này vì khu đã có quản lý đang hoạt động.");
                    preserveFormData(request);
                    request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
                    return;
                }
            }

            boolean success = userDAO.updateUser(user);
            if (!success) {
                request.setAttribute("error", "Không thể cập nhật người dùng.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
                return;
            }

            // Send email if status changed
            if (!status.equals(originalStatus) && reason != null && !reason.trim().isEmpty()) {
                String emailSubject = status.equals("Disable") ? "Tài Khoản Của Bạn Đã Bị Vô Hiệu Hóa" : "Tài Khoản Của Bạn Đã Được Kích Hoạt";
                String emailMessage = status.equals("Disable")
                        ? "Kính gửi " + user.getUserFullName() + ",\n\nTài khoản của bạn đã bị vô hiệu hóa.\n\nLý do: " + reason
                        + "\n\nNếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với đội ngũ hỗ trợ của chúng tôi tại support@homenest.com.\n\nTrân trọng,\nĐội Ngũ HomeNest"
                        : "Kính gửi " + user.getUserFullName() + ",\n\nTài khoản của bạn đã được kích hoạt.\n\nLý do: " + reason
                        + "\n\nChào mừng trở lại! Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với đội ngũ hỗ trợ của chúng tôi tại support@homenest.com.\n\nTrân trọng,\nĐội Ngũ HomeNest";
                if (email != null && !email.trim().isEmpty() && isValidEmail(email)) {
                    try {
                        sendStatusChangeEmail(email, user.getUserFullName(), status, reason);
                        customerDAO.logEmail(0, userID, email, emailSubject, emailMessage, "Sent", null);
                        request.setAttribute("success", "Người dùng đã được cập nhật và email đã gửi thành công.");
                    } catch (MessagingException e) {
                        System.out.println("Không thể gửi email cho người dùng " + userID + " - " + e.getMessage());
                        customerDAO.logEmail(0, userID, email, emailSubject, emailMessage, "Failed", e.getMessage());
                        request.setAttribute("error", "Người dùng đã được cập nhật nhưng không thể gửi email: " + e.getMessage());
                    }
                } else {
                    customerDAO.logEmail(0, userID, email != null ? email : "N/A", emailSubject, emailMessage, "Failed", "Địa chỉ email không hợp lệ hoặc bị thiếu.");
                    request.setAttribute("error", "Người dùng đã được cập nhật nhưng không tìm thấy địa chỉ email hợp lệ để gửi thông báo.");
                }
            } else if (!status.equals(originalStatus)) {
                request.setAttribute("error", "Lý do là bắt buộc cho việc thay đổi trạng thái.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
                return;
            }
            request.setAttribute("success", "Người dùng đã được cập nhật thành công.");
            response.sendRedirect(request.getContextPath() + "/admin/account");
        } else if (customerIDStr != null && !customerIDStr.trim().isEmpty()) {
            // Handle Customer Edit
            int customerID = Integer.parseInt(customerIDStr);
            Customer customer = customerDAO.getCustomerById(customerID);

            if (customer == null) {
                request.setAttribute("error", "Khách hàng không tìm thấy.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
                return;
            }

            if (customerDAO.isEmailExists(email.trim()) && !email.trim().equals(customer.getEmail())) {
                request.setAttribute("error", "Email đã tồn tại.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
                return;
            }
            if (customerDAO.isPhoneNumberExists(phoneNumber.trim()) && !phoneNumber.trim().equals(customer.getPhoneNumber())) {
                request.setAttribute("error", "Số điện thoại đã tồn tại.");
                preserveFormData(request); // Lưu tất cả dữ liệu khi số điện thoại đã tồn tại
                request.setAttribute("customer", customer); // Giữ lại customer object để hiển thị dữ liệu gốc
                request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
                return;
            }

            String cccd = request.getParameter("cccd");
            if (cccd != null && !cccd.trim().isEmpty() && !cccd.matches("\\d{12}")) {
                request.setAttribute("error", "CCCD phải có 12 chữ số nếu được cung cấp.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
                return;
            }
            if (cccd != null && !cccd.trim().isEmpty() && customerDAO.isCCCDExists(cccd.trim()) && !cccd.trim().equals(customer.getCCCD())) {
                request.setAttribute("error", "CCCD đã tồn tại.");
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
                    System.out.println("Định dạng ngày không hợp lệ: " + birthDate + " - " + e.getMessage());
                    request.setAttribute("error", "Định dạng ngày sinh không hợp lệ.");
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

            // Kiểm tra nếu thay đổi trạng thái sang Disable và khách hàng còn contract hoặc rental request
            if ("Disable".equals(status.trim()) && !"Disable".equals(originalStatus)) {
                if (customerDAO.hasContractOrRentalRequest(customerID)) {
                    request.setAttribute("error", "Cannot disable customer with active contract or rental request.");
                    preserveFormData(request);
                    request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
                    return;
                }
            }

            boolean success = customerDAO.updateCustomer(customer);
            if (!success) {
                request.setAttribute("error", "Không thể cập nhật khách hàng.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
                return;
            }

            // Send email if status changed
            if (!status.equals(originalStatus) && reason != null && !reason.trim().isEmpty()) {
                String emailSubject = status.equals("Disable") ? "Tài Khoản Của Bạn Đã Bị Vô Hiệu Hóa" : "Tài Khoản Của Bạn Đã Được Kích Hoạt";
                String emailMessage = status.equals("Disable")
                        ? "Kính gửi " + customer.getCustomerFullName() + ",\n\nTài khoản của bạn đã bị vô hiệu hóa.\n\nLý do: " + reason
                        + "\n\nNếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với đội ngũ hỗ trợ của chúng tôi tại support@homenest.com.\n\nTrân trọng,\nĐội Ngũ HomeNest"
                        : "Kính gửi " + customer.getCustomerFullName() + ",\n\nTài khoản của bạn đã được kích hoạt.\n\nLý do: " + reason
                        + "\n\nChào mừng trở lại! Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với đội ngũ hỗ trợ của chúng tôi tại support@homenest.com.\n\nTrân trọng,\nĐội Ngũ HomeNest";
                if (email != null && !email.trim().isEmpty() && isValidEmail(email)) {
                    try {
                        sendStatusChangeEmail(email, customer.getCustomerFullName(), status, reason);
                        customerDAO.logEmail(customerID, null, email, emailSubject, emailMessage, "Sent", null);
                        request.setAttribute("success", "Khách hàng đã được cập nhật và email đã gửi thành công.");
                    } catch (MessagingException e) {
                        System.out.println("Không thể gửi email cho khách hàng " + customerID + " - " + e.getMessage());
                        customerDAO.logEmail(customerID, null, email, emailSubject, emailMessage, "Failed", e.getMessage());
                        request.setAttribute("error", "Khách hàng đã được cập nhật nhưng không thể gửi email: " + e.getMessage());
                    }
                } else {
                    customerDAO.logEmail(customerID, null, email != null ? email : "N/A", emailSubject, emailMessage, "Failed", "Địa chỉ email không hợp lệ hoặc bị thiếu.");
                    request.setAttribute("error", "Khách hàng đã được cập nhật nhưng không tìm thấy địa chỉ email hợp lệ để gửi thông báo.");
                }
            } else if (!status.equals(originalStatus)) {
                request.setAttribute("error", "Lý do là bắt buộc cho việc thay đổi trạng thái.");
                preserveFormData(request);
                request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
                return;
            }
            request.setAttribute("success", "Khách hàng đã được cập nhật thành công.");
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
        message.setSubject(status.equals("Disable") ? "Tài Khoản Của Bạn Đã Bị Vô Hiệu Hóa" : "Tài Khoản Của Bạn Đã Được Kích Hoạt");

        String htmlContent = "<html><body><h2>Kính gửi " + name + ",</h2>"
                + "<p>Chúng tôi muốn thông báo rằng tài khoản của bạn đã bị " + (status.equals("Disable") ? "vô hiệu hóa" : "kích hoạt") + ".</p>"
                + "<p><strong>Lý do:</strong> " + reason + "</p>"
                + "<p>Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với đội ngũ hỗ trợ của chúng tôi tại <a href='mailto:support@homenest.com'>support@homenest.com</a>.</p>"
                + "<p>Trân trọng,<br>Đội Ngũ HomeNest</p></body></html>";
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
        //         System.out.println("Error preserving customer data - " + e.getMessage());
        //     }
        // }
    }
}
