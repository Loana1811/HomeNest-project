/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import utils.GoogleUtils;
import utils.GoogleUser;
import DAO.UserDAO;
import Model.User;
import jakarta.servlet.annotation.WebServlet;
import java.io.PrintWriter;

/**
 *
 * @author Admin
 */
@WebServlet(name = "LoginGoogleServlet", urlPatterns = {"/login-google"})
public class LoginGoogleServlet extends HttpServlet {

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
            out.println("<title>Servlet LoginGoogleServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginGoogleServlet at " + request.getContextPath() + "</h1>");
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String code = req.getParameter("code");

        if (code == null || code.isEmpty()) {
            resp.sendRedirect("login.jsp");
            return;
        }

        try {
            // B1: Lấy access token từ Google
            String accessToken = GoogleUtils.getToken(code);

            // B2: Lấy thông tin người dùng Google
            GoogleUser googleUser = GoogleUtils.getUserInfo(accessToken);

            // B3: Kiểm tra người dùng đã tồn tại trong DB chưa
            UserDAO dao = new UserDAO();
            User user = dao.getUserByEmail(googleUser.getEmail());

            if (user == null) {
                // B4: Nếu chưa có thì tạo user mới
                user = new User();
                user.setEmail(googleUser.getEmail());
                user.setUserFullName(googleUser.getName());
                user.setRoleId(2); // mặc định là User với RoleID = 2
                dao.insertUserFromGoogle(user);

                // lấy lại user sau khi insert để có ID và roleName
                user = dao.getUserByEmail(googleUser.getEmail());
            }

            // B5: Lưu user vào session
            HttpSession session = req.getSession();
            session.setAttribute("user", user);

            // B6: Điều hướng đến trang chính
            resp.sendRedirect(req.getContextPath() + "/Contracts");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("error.jsp");
        }
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
        processRequest(request, response);
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
