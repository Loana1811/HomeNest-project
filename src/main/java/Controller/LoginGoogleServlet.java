/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.CustomerDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import utils.GoogleUtils;
import utils.GoogleUser;
import dao.UserDAO;
import model.Customer;
import model.User;
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

            // B3: Kiểm tra người dùng đã tồn tại trong bảng Customers chưa
            CustomerDAO dao = new CustomerDAO();
            Customer customer = dao.getCustomerByEmail(googleUser.getEmail());

            if (customer == null) {
                // B4: Nếu chưa có thì tạo customer mới
                customer = new Customer();
                customer.setEmail(googleUser.getEmail());
                customer.setCustomerFullName(googleUser.getName());
                customer.setCustomerPassword(CustomerDAO.hashMd5("GOOGLE_AUTH")); // Mật khẩu giả định
                customer.setCustomerStatus("Potential");

                System.out.println("Tên: " + customer.getCustomerFullName());
                System.out.println("Email: " + customer.getEmail());
                System.out.println("Mật khẩu: " + customer.getCustomerPassword());
                System.out.println("Trạng thái: " + customer.getCustomerStatus());
                
                dao.insertCustomerFromGoogle(customer);

                // Lấy lại thông tin sau khi insert để có ID
                customer = dao.getCustomerByEmail(googleUser.getEmail());
            }

            // B5: Lưu vào session
            HttpSession session = req.getSession();
            session.setAttribute("customer", customer);
            session.setAttribute("customerId", customer.getCustomerID());

            // B6: Điều hướng
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
