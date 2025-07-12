package controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
import dao.CustomerDAO;
import dao.PasswordResetDAO;
import dao.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {

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
            out.println("<title>Servlet ResetPasswordServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ResetPasswordServlet at " + request.getContextPath() + "</h1>");
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

        System.out.println("👉 ResetPasswordServlet được gọi");

        String token = request.getParameter("token");

        try {
            PasswordResetDAO resetDAO = new PasswordResetDAO();

            if (token == null || !resetDAO.isValidToken(token)) {
                request.setAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            } else {
                request.setAttribute("token", token); // gửi token để form giữ lại
            }

        } catch (SQLException ex) {
            Logger.getLogger(ResetPasswordServlet.class.getName()).log(Level.SEVERE, null, ex);
            request.setAttribute("error", "Lỗi hệ thống khi xác minh token.");
        }

        request.getRequestDispatcher("reset_password.jsp").forward(request, response);
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
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirm");

        if (!password.equals(confirm)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("reset_password.jsp").forward(request, response);
            return;
        }

        try {
            PasswordResetDAO resetDAO = new PasswordResetDAO();
            String email = resetDAO.getEmailByToken(token);

            if (email == null) {
                request.setAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
                request.getRequestDispatcher("reset_password.jsp").forward(request, response);
                return;
            }

            String hashedPassword = UserDAO.hashMD5(password); // dùng chung hàm hash

            // Cập nhật mật khẩu trong bảng tương ứng
            UserDAO userDAO = new UserDAO();
            if (userDAO.getUserByEmail(email) != null) {
                userDAO.updatePassword(email, hashedPassword);
            } else {
                CustomerDAO customerDAO = new CustomerDAO();
                if (customerDAO.getCustomerByEmail(email) != null) {
                    customerDAO.updatePassword(email, hashedPassword);
                } else {
                    request.setAttribute("error", "Email không tồn tại.");
                    request.getRequestDispatcher("reset_password.jsp").forward(request, response);
                    return;
                }
            }

            resetDAO.deleteToken(token);
            request.setAttribute("success", "Mật khẩu đã được thay đổi.");

        } catch (SQLException ex) {
            Logger.getLogger(ResetPasswordServlet.class.getName()).log(Level.SEVERE, null, ex);
            request.setAttribute("error", "Lỗi hệ thống.");
        }

        request.getRequestDispatcher("Login.jsp").forward(request, response);
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
