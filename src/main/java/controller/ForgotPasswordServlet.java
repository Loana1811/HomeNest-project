/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.CustomerDAO;
import dao.PasswordResetDAO;
import dao.UserDAO;
import model.Customer;
import model.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.MailUtils;

/**
 *
 * @author Admin
 */
@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot_password"})
public class ForgotPasswordServlet extends HttpServlet {

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
            out.println("<title>Servlet ForgotPasswordServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ForgotPasswordServlet at " + request.getContextPath() + "</h1>");
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
        response.sendRedirect("forgot_password.jsp");
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
        String email = request.getParameter("email");
        String token = UUID.randomUUID().toString();

        if (email == null || email.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập email.");
            request.getRequestDispatcher("forgot_password.jsp").forward(request, response);
            return;
        }

        boolean found = false;

        try {
            PasswordResetDAO resetDAO = new PasswordResetDAO();

            // 1. Kiểm tra trong bảng Users
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByEmail(email);

            if (user != null) {
                resetDAO.saveResetToken(email, token);
                sendResetEmail(request, email, token);
                request.setAttribute("success", "Đã gửi link đặt lại mật khẩu đến email người dùng.");
                found = true;
            } else {
                // 2. Nếu không có trong Users, kiểm tra trong Customers
                CustomerDAO customerDAO = new CustomerDAO();
                Customer customer = customerDAO.getCustomerByEmail(email);

                if (customer != null) {
                    resetDAO.saveResetToken(email, token);
                    sendResetEmail(request, email, token);
                    request.setAttribute("success", "Đã gửi link đặt lại mật khẩu đến email khách hàng.");
                    found = true;
                }
            }

            if (!found) {
                request.setAttribute("error", "Email không tồn tại trong hệ thống.");
            }

        } catch (SQLException ex) {
            Logger.getLogger(ForgotPasswordServlet.class.getName()).log(Level.SEVERE, null, ex);
            request.setAttribute("error", "Lỗi hệ thống.");
        }

        request.getRequestDispatcher("forgot_password.jsp").forward(request, response);
    }

   private void sendResetEmail(HttpServletRequest request, String email, String token) {
    String baseURL = request.getScheme() + "://" + request.getServerName()
            + ":" + request.getServerPort() + request.getContextPath();
    String resetLink = baseURL + "/reset-password?token=" + token;

    String subject = "Password Reset Request";

    String content = "<div style=\"font-family:Segoe UI, Tahoma, sans-serif; max-width:600px; margin:auto; padding:20px; "
                   + "border:1px solid #ddd; border-radius:10px; background-color:#f9f9f9;\">"
                   + "<h2 style=\"color:rgb(0,128,128);\">Reset Your Password</h2>"
                   + "<p>Hello,</p>"
                   + "<p>We received a request to reset your password. To proceed, please click the button below:</p>"
                   + "<p style=\"text-align:center; margin: 20px 0;\">"
                   + "<a href=\"" + resetLink + "\" "
                   + "style=\"display:inline-block; padding:12px 20px; background-color:rgb(0,128,128); color:white; "
                   + "text-decoration:none; border-radius:5px; font-weight:bold;\">Reset Password</a></p>"
                   + "<p>If the button doesn't work, you can also use the following link:</p>"
                   + "<p style=\"word-wrap:break-word;\"><a href=\"" + resetLink + "\">" + resetLink + "</a></p>"
                   + "<hr style=\"margin:20px 0;\">"
                   + "<p style=\"color:#666; font-size:14px;\">"
                   + "⚠️ This link is valid for a limited time only and can be used once. "
                   + "Do not share this link with anyone for security reasons."
                   + "</p>"
                   + "<p style=\"font-size:13px; color:#999;\">If you did not request a password reset, please ignore this email.</p>"
                   + "</div>";

    MailUtils.send(email, subject, content);
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
