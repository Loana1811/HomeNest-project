package controller;

import dao.CustomerDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Customer;

@WebServlet(name = "ChangePasswordServlet", urlPatterns = {"/customer/change-password"})
public class ChangePasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (role == null || !"customer".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/error.jsp");
            return;
        }

        request.getRequestDispatcher("/customer/change-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

 

        Customer customer = (Customer) session.getAttribute("customer");

        // Lấy dữ liệu từ form
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        CustomerDAO dao = new CustomerDAO();
        String hashedCurrent = dao.hashMD5(currentPassword);
        String hashedNew = dao.hashMD5(newPassword);

        // Kiểm tra mật khẩu hiện tại có đúng không
        if (!hashedCurrent.equals(customer.getCustomerPassword())) {
            request.setAttribute("error", "Mật khẩu hiện tại không chính xác.");
            request.getRequestDispatcher("/customer/change-password.jsp").forward(request, response);
            return;
        }

        // Kiểm tra xác nhận mật khẩu mới
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu mới không khớp.");
            request.getRequestDispatcher("/customer/change-password.jsp").forward(request, response);
            return;
        }

        // Kiểm tra định dạng mật khẩu mới
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9\\s])[A-Za-z\\d[^\\s]]{8,20}$";
        if (!newPassword.matches(passwordPattern)) {
            request.setAttribute("error", "Mật khẩu mới phải từ 8–20 ký tự, có chữ hoa, chữ thường, số, ký tự đặc biệt và không chứa khoảng trắng.");
            request.getRequestDispatcher("/customer/change-password.jsp").forward(request, response);
            return;
        }

        // Cập nhật mật khẩu mới
        try {
            dao.updatePassword(customer.getEmail(), hashedNew);
            customer.setCustomerPassword(hashedNew);
            session.setAttribute("customer", customer);
            request.setAttribute("success", "✅ Đổi mật khẩu thành công!");
            request.getRequestDispatcher("/customer/change-password.jsp").forward(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(ChangePasswordServlet.class.getName()).log(Level.SEVERE, null, ex);
            request.setAttribute("error", "❌ Có lỗi xảy ra khi thay đổi mật khẩu.");
            request.getRequestDispatcher("/customer/change-password.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Xử lý thay đổi mật khẩu cho người dùng";
    }
}
