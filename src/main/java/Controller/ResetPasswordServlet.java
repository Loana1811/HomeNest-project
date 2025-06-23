package Controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
import DAO.UserDAO;
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
            if (token == null || !new UserDAO().isValidToken(token)) {
                request.setAttribute("error", "Token không hợp lệ hoặc hết hạn.");
            } else {
                request.setAttribute("token", token);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ResetPasswordServlet.class.getName()).log(Level.SEVERE, null, ex);
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
        } else {
            UserDAO dao = new UserDAO();
            String email = null;
            try {
                email = dao.getEmailByToken(token);
            } catch (SQLException ex) {
                Logger.getLogger(ResetPasswordServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (email != null) {
                try {
                    dao.updatePassword(email, dao.hashMd5(password));
                } catch (SQLException ex) {
                    Logger.getLogger(ResetPasswordServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    dao.deleteToken(token);
                } catch (SQLException ex) {
                    Logger.getLogger(ResetPasswordServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                request.setAttribute("success", "Mật khẩu đã được thay đổi.");
            } else {
                request.setAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            }
        }
        request.getRequestDispatcher("reset_password.jsp").forward(request, response);
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
