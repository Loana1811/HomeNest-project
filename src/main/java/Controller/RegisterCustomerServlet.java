/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.CustomerDAO;
import model.Customer;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

/**
 *
 * @author Admin
 */
@WebServlet(name = "RegisterCustomerServlet", urlPatterns = {"/RegisterCustomer"})
public class RegisterCustomerServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet RegisterCustomerServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RegisterCustomerServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("register_customer.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String name = request.getParameter("fullname");
        String phone = request.getParameter("phone");
        String cccd = request.getParameter("cccd");
        String gender = request.getParameter("gender");
        String birth = request.getParameter("birthdate");
        String address = request.getParameter("address");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirm");

        String error = null;

        Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9])\\S{8,20}$");
        Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
        Pattern PHONE_PATTERN = Pattern.compile("^0\\d{9}$");
        Pattern CCCD_PATTERN = Pattern.compile("^\\d{12}$");

        if (!password.equals(confirm)) {
            error = "Mật khẩu xác nhận không khớp.";
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            error = "Mật khẩu phải từ 8–20 ký tự, gồm chữ hoa, thường, số, ký tự đặc biệt và không chứa khoảng trắng.";
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            error = "Email không hợp lệ.";
        } else if (!PHONE_PATTERN.matcher(phone).matches()) {
            error = "Số điện thoại không hợp lệ (phải gồm 10 số, bắt đầu bằng 0).";
        } else if (!CCCD_PATTERN.matcher(cccd).matches()) {
            error = "CCCD phải đúng 12 chữ số.";
        } else {
            try {
                LocalDate birthDate = LocalDate.parse(birth);
                LocalDate today = LocalDate.now();

                if (birthDate.isAfter(today)) {
                    error = "Ngày sinh không được ở tương lai.";
                } else if (Period.between(birthDate, today).getYears() < 16) {
                    error = "Bạn phải từ 16 tuổi trở lên để đăng ký.";
                }
            } catch (Exception e) {
                error = "Ngày sinh không hợp lệ.";
            }
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.getRequestDispatcher("register_customer.jsp").forward(request, response);
            return;
        }

        // Tạo đối tượng Customer
        Customer customer = new Customer();
        customer.setCustomerFullName(name);
        customer.setPhoneNumber(phone);
        customer.setCCCD(cccd);
        customer.setGender(gender);
        customer.setBirthDate(Date.valueOf(birth));
        customer.setAddress(address);
        customer.setEmail(email);
        customer.setCustomerPassword(password);

        CustomerDAO dao = new CustomerDAO();

        try {
            if (dao.isDuplicate(customer)) {
                request.setAttribute("error", "Email, số điện thoại hoặc CCCD đã tồn tại.");
                request.getRequestDispatcher("register_customer.jsp").forward(request, response);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi kiểm tra dữ liệu trùng.");
            request.getRequestDispatcher("register_customer.jsp").forward(request, response);
            return;
        }

        boolean success = dao.insertCustomer(customer);

        if (success) {
            request.setAttribute("success", "Đăng ký khách hàng thành công!");
        } else {
            request.setAttribute("error", "Đăng ký thất bại.");
        }

        request.getRequestDispatcher("register_customer.jsp").forward(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
