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

        String username = request.getParameter("_username");
    String password = request.getParameter("_password");
    String remember = request.getParameter("remember");

    System.out.println("Login attempt: username=" + username);

    CustomerDAO customerDAO = new CustomerDAO();
    UserDAO userDAO = new UserDAO();
    HttpSession session = request.getSession();

    try {
        // Try authenticating as a Customer
        Customer customer = customerDAO.checkLogin(username, password);
        if (customer != null) {
            session.setAttribute("idCustomer", customer.getCustomerID());
            session.setAttribute("userType", "Customer");
            session.setAttribute("customer", customer);
            System.out.println("Customer logged in: idCustomer=" + customer.getCustomerID());
            response.sendRedirect(request.getContextPath() + "/customer/room-list");
            return;
        }

        // Try authenticating as a User
        User user = userDAO.login(username, password);
        if (user != null) {
            session.setAttribute("idUser", user.getUserID());
            session.setAttribute("userType", "User");
            session.setAttribute("currentUser", user);
            session.setAttribute("roleID", user.getRoleID());
            System.out.println("User logged in: idUser=" + user.getUserID() + ", role=" + user.getRole().getRoleName());
            String roleName = user.getRole().getRoleName();
            if ("Admin".equals(roleName)) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp?idUser=" + user.getUserID());
            } else if ("Manager".equals(roleName)) {
                response.sendRedirect(request.getContextPath() + "/manager/dashboard.jsp?idManager=" + user.getUserID());
            } else {
                request.setAttribute("error", "Invalid user role.");
                request.getRequestDispatcher("/Login.jsp").forward(request, response);
            }
            return;
        }

        // Login failed
        System.out.println("Login failed for username: " + username);
        request.setAttribute("error", "Invalid email/phone number or password.");
        request.getRequestDispatcher("/Login.jsp").forward(request, response);

    } catch (SQLException e) {
        System.out.println("SQLException during login: " + e.getMessage());
        e.printStackTrace();
        request.setAttribute("error", "An error occurred during login. Please try again.");
        request.getRequestDispatcher("/Login.jsp").forward(request, response);
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
