/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.UserDAO;
import dao.CustomerDAO;
import model.User;
import model.Customer;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/Login"})
public class LoginServlet extends HttpServlet {

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
            out.println("<title>Servlet LoginServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginServlet at " + request.getContextPath() + "</h1>");
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
        request.getRequestDispatcher("Login.jsp").forward(request, response);
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

    String email = request.getParameter("_username");
    String password = request.getParameter("_password");
    String remember = request.getParameter("remember");

    if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
        request.setAttribute("error", "Vui lòng nhập đầy đủ email và mật khẩu.");
        request.getRequestDispatcher("Login.jsp").forward(request, response);
        return;
    }

    HttpSession session = request.getSession();

    try {
        // 🔍 Bước 1: kiểm tra người dùng trong bảng Users
        UserDAO userDao = new UserDAO();
        User user = userDao.loginUser(email, password); // ✅ Truyền password gốc

        if (user != null) {
            int role = user.getRoleId();

            // ✅ Thiết lập session theo Role
            if (role == 1) {
                session.setAttribute("admin", user);
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else if (role == 2) {
                session.setAttribute("manager", user);
                response.sendRedirect(request.getContextPath() + "/manager/manage-requests");
            } else {
                request.setAttribute("error", "Tài khoản không có quyền truy cập hệ thống.");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
                return;
            }

            // ✅ Remember Me
            if ("on".equals(remember)) {
                Cookie usernameCookie = new Cookie("username", email);
                usernameCookie.setMaxAge(30 * 24 * 60 * 60); // 30 ngày
                usernameCookie.setHttpOnly(true);
                usernameCookie.setPath("/");
                response.addCookie(usernameCookie);
            }

            return;
        }

        // 🔍 Bước 2: nếu không phải User, kiểm tra trong bảng Customers
        CustomerDAO customerDao = new CustomerDAO();
        String hashedPassword = CustomerDAO.hashMd5(password); // ✅ Khách cần mã hóa vì DAO không tự mã hóa
        Customer customer = customerDao.checkLogin(email, hashedPassword);

        if (customer != null) {
            session.setAttribute("customer", customer);
            session.setAttribute("customerId", customer.getCustomerID());

            if ("on".equals(remember)) {
                Cookie usernameCookie = new Cookie("username", email);
                usernameCookie.setMaxAge(30 * 24 * 60 * 60);
                usernameCookie.setHttpOnly(true);
                usernameCookie.setPath("/");
                response.addCookie(usernameCookie);
            }

            response.sendRedirect(request.getContextPath() + "/customer/room-list");
            return;
        }

        request.setAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
        request.getRequestDispatcher("Login.jsp").forward(request, response);

    } catch (SQLException e) {
        e.printStackTrace();
        request.setAttribute("error", "Đã xảy ra lỗi trong quá trình đăng nhập.");
        request.getRequestDispatcher("Login.jsp").forward(request, response);
    }
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
