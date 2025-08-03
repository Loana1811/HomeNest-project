package controller;

import dao.CustomerDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Customer;

@WebServlet(name = "UpdateProfileServlet", urlPatterns = {"/update-profile"})
public class UpdateProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        if (customer.getBirthDate() != null) {
            String valueDOB = new SimpleDateFormat("yyyy-MM-dd").format(customer.getBirthDate());
            request.setAttribute("valueDOB", valueDOB);
        }

        request.getRequestDispatcher("/customer/view-profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        // Lấy customerId từ form (hidden input)
        String customerIdStr = request.getParameter("customerId");
        if (customerIdStr != null && !customerIdStr.trim().isEmpty()) {
            try {
                int customerId = Integer.parseInt(customerIdStr);
                customer.setCustomerID(customerId);
            } catch (NumberFormatException e) {
                Logger.getLogger(UpdateProfileServlet.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String cccd = request.getParameter("cccd");
        String gender = request.getParameter("gender");
        String birthDate = request.getParameter("birthDate");
        String address = request.getParameter("address");
        String email = request.getParameter("email");

        StringBuilder errorMessage = new StringBuilder();

        if (isEmptyOrNull(fullName)) {
            errorMessage.append("Họ và tên không được để trống.<br>");
        }
        if (isEmptyOrNull(phone)) {
            errorMessage.append("Số điện thoại không được để trống.<br>");
        }
        if (isEmptyOrNull(cccd)) {
            errorMessage.append("CCCD không được để trống.<br>");
        }
        if (isEmptyOrNull(gender)) {
            errorMessage.append("Giới tính không được để trống.<br>");
        }
        if (isEmptyOrNull(birthDate)) {
            errorMessage.append("Ngày sinh không được để trống.<br>");
        }
        if (isEmptyOrNull(address)) {
            errorMessage.append("Địa chỉ không được để trống.<br>");
        }
        if (isEmptyOrNull(email)) {
            errorMessage.append("Email không được để trống.<br>");
        }

        if (!isValidName(fullName)) {
            errorMessage.append("Họ và tên không được chứa ký tự đặc biệt.<br>");
        }
        if (!isValidPhone(phone)) {
            errorMessage.append("Số điện thoại không hợp lệ (chỉ gồm chữ số và dài 10 ký tự).<br>");
        }
        if (!isValidCCCD(cccd)) {
            errorMessage.append("CCCD không hợp lệ (chỉ gồm chữ số và dài 12 ký tự).<br>");
        }
        if (!isValidEmail(email)) {
            errorMessage.append("Định dạng email không hợp lệ.<br>");
        }
        if (!isValidAddress(address)) {
            errorMessage.append("Địa chỉ không được chứa ký tự không hợp lệ.<br>");
        }

        String formattedBirthDate = birthDate;

        if (birthDate != null && !birthDate.isEmpty()) {
            try {
                java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthDate);
                if (!isOver18(utilDate)) {
                    errorMessage.append("Bạn phải đủ 18 tuổi trở lên.<br>");
                } else {
                    customer.setBirthDate(new java.sql.Date(utilDate.getTime()));
                    formattedBirthDate = new SimpleDateFormat("yyyy-MM-dd").format(utilDate);
                }
            } catch (ParseException e) {
                errorMessage.append("Định dạng ngày sinh không hợp lệ (yyyy-MM-dd).<br>");
                Logger.getLogger(UpdateProfileServlet.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        if (errorMessage.length() > 0) {
            request.setAttribute("errorMessage", errorMessage.toString());

            request.setAttribute("tempFullName", fullName);
            request.setAttribute("tempPhone", phone);
            request.setAttribute("tempCCCD", cccd);
            request.setAttribute("tempGender", gender);
            request.setAttribute("tempBirthDate", formattedBirthDate);
            request.setAttribute("tempAddress", address);
            request.setAttribute("tempEmail", email);
            request.setAttribute("activeTab", "edit");

            request.getRequestDispatcher("/customer/view-profile.jsp").forward(request, response);
            return;
        }

        customer.setCustomerFullName(fullName);
        customer.setPhoneNumber(phone);
        customer.setCCCD(cccd);
        customer.setGender(gender);
        customer.setAddress(address);
        customer.setEmail(email);

        CustomerDAO dao = new CustomerDAO();
        try {
            boolean updated = dao.updateCustomer(customer);
            if (!updated) {
                request.setAttribute("errorMessage", "Không thể cập nhật hồ sơ.");
                request.getRequestDispatcher("/customer/view-profile.jsp").forward(request, response);
                return;
            }
        } catch (SQLException ex) {
            Logger.getLogger(UpdateProfileServlet.class.getName()).log(Level.SEVERE, null, ex);
            request.setAttribute("errorMessage", "Lỗi cơ sở dữ liệu khi cập nhật.");
            request.getRequestDispatcher("/customer/view-profile.jsp").forward(request, response);
            return;
        }

        session.setAttribute("customer", customer);
        response.sendRedirect(request.getContextPath() + "/customer/view-profile.jsp?success=Cập nhật thành công");
    }

    private boolean isEmptyOrNull(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isValidName(String name) {
        return name != null && name.matches("^[a-zA-Z\\s]+$");
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^\\d{10}$");
    }

    private boolean isValidCCCD(String cccd) {
        return cccd != null && cccd.matches("^\\d{12}$");
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.(com|net|org|edu|gov|vn|edu\\.vn)$");
    }

    private boolean isValidAddress(String address) {
        return address != null && address.matches("^[a-zA-Z0-9\\s,.-/]+$");
    }

    private boolean isOver18(java.util.Date birthDate) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.YEAR, -18);
        java.util.Date date18YearsAgo = cal.getTime();
        return birthDate.before(date18YearsAgo);
    }


    @Override
    public String getServletInfo() {
        return "Update profile information for customers.";
    }
}
