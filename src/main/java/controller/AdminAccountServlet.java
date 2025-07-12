package controller;

import dao.CustomerDAO;
import dao.UserDAO;
import dao.BlockDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Customer;
import model.User;
import model.Block;

@WebServlet("/admin/account")
public class AdminAccountServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final BlockDAO blockDAO = new BlockDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            // Tạo tài khoản mới
            if ("createManager".equals(action)) {
                List<Block> blockList = blockDAO.getAllBlocks();
                request.setAttribute("blockList", blockList);
                request.getRequestDispatcher("/admin/createManager.jsp").forward(request, response);
                return;
            }
            if ("createCustomer".equals(action)) {
                request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
                return;
            }

            // Xem chi tiết customer
            if ("viewDetail".equals(action)) {
                String customerIdStr = request.getParameter("customerID");
                if (customerIdStr == null) {
                    response.sendRedirect("error.jsp");
                    return;
                }
                int customerID = Integer.parseInt(customerIdStr);
                List<Map<String, Object>> detailList = customerDAO.getFullCustomerDetails(customerID);
                request.setAttribute("details", detailList);
                request.getRequestDispatcher("/admin/viewDetailsCustomer.jsp").forward(request, response);
                return;
            }
            if ("editUser".equals(action)) {
                String userIDStr = request.getParameter("userID");
                if (userIDStr == null) {
                    response.sendRedirect("error.jsp");
                    return;
                }
                int userID = Integer.parseInt(userIDStr);
                User user = userDAO.getUserById(userID);

                // Lấy danh sách block cho dropdown nếu user là Manager
                List<Block> blockList = null;
                if (user != null && user.getRole() != null && "Manager".equals(user.getRole().getRoleName())) {
                    blockList = blockDAO.getAllBlocks();
                }
                request.setAttribute("user", user);
                request.setAttribute("blockList", blockList);
                request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
                return;
            }

            // Sửa customer
            if ("editCustomer".equals(action)) {
                String customerIDStr = request.getParameter("customerID");
                if (customerIDStr == null) {
                    response.sendRedirect("error.jsp");
                    return;
                }
                int customerID = Integer.parseInt(customerIDStr);
                Customer customer = customerDAO.getCustomerById(customerID);
                request.setAttribute("customer", customer);
                request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
                return;
            }

            // Xoá account (user hoặc customer)
            if ("delete".equals(action)) {
                deleteAccount(request, response);
                return;
            }

            // Hiển thị danh sách tài khoản (không có action hoặc action khác)
            List<Customer> customerList = customerDAO.getAllCustomers();
            List<Integer> cannotDeleteCustomerIds = new ArrayList<>();
            for (Customer c : customerList) {
                if (customerDAO.hasContractOrTenant(c.getCustomerID())) {
                    cannotDeleteCustomerIds.add(c.getCustomerID());
                }
            }
            request.setAttribute("customerList", customerList);
            request.setAttribute("cannotDeleteCustomerIds", cannotDeleteCustomerIds);
            request.getRequestDispatcher("/admin/viewListAccount.jsp").forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("error", "System Error: " + ex.getMessage());
            request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("error.jsp");
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
                case "disable":
                    disableAccount(request, response);
                    break;
                default:
                    response.sendRedirect("error.jsp");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Database Error: " + e.getMessage());
            request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
        }
    }

    private void addAccount(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String roleIDStr = request.getParameter("roleID");

        if (fullName == null || fullName.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || phoneNumber == null || phoneNumber.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || confirmPassword == null || confirmPassword.trim().isEmpty()
                || roleIDStr == null || roleIDStr.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match.");
            request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
            return;
        }

        try {
            int roleID = Integer.parseInt(roleIDStr);

            if (roleID == 3) { // Customer
                Customer customer = new Customer();
                customer.setCustomerFullName(fullName.trim());
                customer.setEmail(email.trim());
                customer.setPhoneNumber(phoneNumber.trim());
                customer.setCustomerPassword(customerDAO.hashMD5(password));
                customer.setCCCD(null);
                customer.setGender(null);
                customer.setBirthDay(null);
                customer.setAddress(null);
                customer.setCustomerStatus("Potential");

                boolean success = customerDAO.createCustomer(customer);
                if (!success) {
                    request.setAttribute("error", "Failed to create customer account.");
                    request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
                    return;
                }
            } else { // Admin or Manager
                User user = new User();
                user.setUserFullName(fullName.trim());
                user.setEmail(email.trim());
                user.setPhoneNumber(phoneNumber.trim());
                user.setPassword(userDAO.hashMD5(password)); // Hash password!
                user.setRoleId(roleID);
                user.setUserStatus("Active");

                if (roleID == 2) { // Manager
                    String blockIDStr = request.getParameter("blockID");
                    if (blockIDStr != null && !blockIDStr.trim().isEmpty()) {
                        user.setBlockId(Integer.parseInt(blockIDStr));
                    }
                }
                boolean success = userDAO.addUser(user);
                if (!success) {
                    request.setAttribute("error", "Failed to create user account.");
                    request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
                    return;
                }
            }
            response.sendRedirect(request.getContextPath() + "/admin/account");
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid role ID format.");
            request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
        }
    }
    // Tạo Manager

    private void addManager(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String blockIDStr = request.getParameter("blockID");

        // Validate
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
        user.setRoleId(2); // Manager
        user.setUserStatus("Active");
        user.setBlockId(Integer.parseInt(blockIDStr));

        boolean success = userDAO.addUser(user);
        if (!success) {
            request.setAttribute("error", "Failed to create manager account.");
            List<Block> blockList = blockDAO.getAllBlocks();
            request.setAttribute("blockList", blockList);
            request.getRequestDispatcher("/admin/createManager.jsp").forward(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/viewListAccount");
    }

    // Tạo Customer
    private void addCustomer(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String birthDate = request.getParameter("birthDate");
        String cccd = request.getParameter("cccd");
        String gender = request.getParameter("gender");
        String address = request.getParameter("address");

        // Validate
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
        if (customerDAO.isEmailExists(email.trim())) {
            request.setAttribute("error", "Email already exists!");
            request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
            return;
        }
        if (customerDAO.isNameExists(fullName.trim())) {
            request.setAttribute("error", "Full name already exists!");
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
            customer.setBirthDay(Date.valueOf(birthDate.trim()));
        } else {
            customer.setBirthDay(null);
        }
        customer.setAddress(address != null && !address.trim().isEmpty() ? address.trim() : null);
        customer.setCustomerStatus("Active");

        if (birthDate != null && !birthDate.trim().isEmpty()) {
            customer.setBirthDay(Date.valueOf(birthDate.trim()));
        } else {
            customer.setBirthDay(null);
        }
        customer.setAddress(null);
        customer.setCustomerStatus("Active");

        boolean success = customerDAO.createCustomer(customer);
        if (!success) {
            request.setAttribute("error", "Failed to create customer account.");
            request.getRequestDispatcher("/admin/createCustomer.jsp").forward(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/viewListAccount");
    }

    private void deleteAccount(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        String userIDStr = request.getParameter("userID");
        String customerIDStr = request.getParameter("customerID");

        if ((userIDStr == null || userIDStr.trim().isEmpty())
                && (customerIDStr == null || customerIDStr.trim().isEmpty())) {
            request.setAttribute("error", "User ID or Customer ID is required for deletion.");
            request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
            return;
        }

        if (userIDStr != null && !userIDStr.trim().isEmpty()) {
            int userID = Integer.parseInt(userIDStr);
            boolean success = userDAO.deactivateUser(userID);
            if (!success) {
                request.setAttribute("error", "Failed to disable user.");
                request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
                return;
            }
        } else if (customerIDStr != null && !customerIDStr.trim().isEmpty()) {
            int customerID = Integer.parseInt(customerIDStr);
            if (customerDAO.hasContractOrTenant(customerID)) {
                request.setAttribute("error", "This customer has related contracts or has rented a room, cannot delete!");
                request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
                return;
            }
            Customer customer = customerDAO.getCustomerById(customerID);
            if (customer != null) {
                customer.setCustomerStatus("Inactive");
                boolean success = customerDAO.updateCustomer(customer);
                if (!success) {
                    request.setAttribute("error", "Failed to disable customer.");
                    request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
                    return;
                }
            } else {
                request.setAttribute("error", "Customer not found.");
                request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/account");
    }

    private void disableAccount(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        String userIDStr = request.getParameter("userID");
        String customerIDStr = request.getParameter("customerID");

        if ((userIDStr == null || userIDStr.trim().isEmpty())
                && (customerIDStr == null || customerIDStr.trim().isEmpty())) {
            request.setAttribute("error", "User ID or Customer ID is required for disabling.");
            request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
            return;
        }

        if (userIDStr != null && !userIDStr.trim().isEmpty()) {
            int userID = Integer.parseInt(userIDStr);
            User user = userDAO.getUserById(userID);
            if (user != null && user.getRole() != null && "Admin".equalsIgnoreCase(user.getRole().getRoleName())) {
                request.setAttribute("error", "You cannot disable an Admin account.");
                request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
                return;
            }
            boolean success = userDAO.deactivateUser(userID);
            if (!success) {
                request.setAttribute("error", "Failed to disable user.");
                request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
                return;
            }
        } else if (customerIDStr != null && !customerIDStr.trim().isEmpty()) {
            int customerID = Integer.parseInt(customerIDStr);
            if (customerDAO.hasContractOrTenant(customerID)) {
                request.setAttribute("error", "Cannot disable customer with active contracts or tenant records!");
                request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
                return;
            }
            boolean success = customerDAO.updateCustomerStatus(customerID, "Inactive");
            if (!success) {
                request.setAttribute("error", "Failed to disable customer.");
                request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/account");
    }

    private void editAccount(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        String userIDStr = request.getParameter("userID");
        String customerIDStr = request.getParameter("customerID");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String roleIDStr = request.getParameter("roleID");
        String status = request.getParameter("status");

        if (userIDStr == null && customerIDStr == null) {
            request.setAttribute("error", "User ID or Customer ID is required.");
            request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
            return;
        }

        if (fullName == null || fullName.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || phoneNumber == null || phoneNumber.trim().isEmpty()) {
            request.setAttribute("error", "Full name, email and phone number are required.");
            request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
            return;
        }

        if (userIDStr != null && !userIDStr.trim().isEmpty()) {
            int userID = Integer.parseInt(userIDStr);
            int roleID = Integer.parseInt(roleIDStr);

            User user = userDAO.getUserById(userID);
            if (user == null) {
                request.setAttribute("error", "User not found.");
                request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
                return;
            }
            if (user.getRole() != null && "Admin".equalsIgnoreCase(user.getRole().getRoleName())) {
                request.setAttribute("error", "Admin account cannot be edited.");
                request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
                return;
            }

            user.setUserFullName(fullName.trim());
            user.setEmail(email.trim());
            user.setPhoneNumber(phoneNumber.trim());
            user.setUserStatus(status != null ? status : "Active");
            user.setRoleId(roleID);

            if (roleID == 2) {
                String blockIDStr = request.getParameter("blockID");
                if (blockIDStr != null && !blockIDStr.trim().isEmpty()) {
                    user.setBlockId(Integer.parseInt(blockIDStr));
                } else {
                    user.setBlockId(null);
                }
            } else {
                user.setBlockId(null);
            }

            String newPassword = request.getParameter("password");
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                user.setPassword(userDAO.hashMD5(newPassword.trim()));
            }

            boolean success = userDAO.updateUser(user);
            if (!success) {
                request.setAttribute("error", "Failed to update user.");
                request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
                return;
            }
        } else if (customerIDStr != null && !customerIDStr.trim().isEmpty()) {
            int customerID = Integer.parseInt(customerIDStr);
            Customer customer = customerDAO.getCustomerById(customerID);

            if (customer == null) {
                request.setAttribute("error", "Customer not found.");
                request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
                return;
            }

            customer.setCustomerFullName(fullName.trim());
            customer.setEmail(email.trim());
            customer.setPhoneNumber(phoneNumber.trim());
            customer.setCustomerStatus(status != null ? status : "Potential");

            String cccd = request.getParameter("cccd");
            String gender = request.getParameter("gender");
            String birthDate = request.getParameter("birthDate");
            String address = request.getParameter("address");
            String customerPassword = request.getParameter("customerPassword");
            if (customerPassword != null && !customerPassword.trim().isEmpty()) {
                customer.setCustomerPassword(customerDAO.hashMD5(customerPassword.trim()));
            }

            if (cccd != null && !cccd.trim().isEmpty()) {
                customer.setCCCD(cccd.trim());
            }
            if (gender != null && !gender.trim().isEmpty()) {
                customer.setGender(gender.trim());
            }
            if (birthDate != null && !birthDate.trim().isEmpty()) {
                try {
                    Date sqlDate = Date.valueOf(birthDate.trim());
                    customer.setBirthDay(sqlDate);
                } catch (IllegalArgumentException e) {
                    System.out.println("Định dạng ngày không hợp lệ: " + birthDate);
                }
            }
            if (address != null && !address.trim().isEmpty()) {
                customer.setAddress(address.trim());
            }

            boolean success = customerDAO.updateCustomer(customer);
            if (!success) {
                request.setAttribute("error", "Failed to update customer.");
                request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + "/viewListAccount");
    }
}
