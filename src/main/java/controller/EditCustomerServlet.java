package controller;

import dao.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Customer;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/editCustomer")
public class EditCustomerServlet extends HttpServlet {

    private final CustomerDAO customerDAO = new CustomerDAO();

   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String ctx = request.getContextPath();
        if (session == null || session.getAttribute("idUser") == null) {
            response.sendRedirect(ctx + "/error.jsp");
            return;
        }

        String userType = (String) session.getAttribute("userType");
        String roleName = (String) session.getAttribute("roleName");

        if (!"User".equalsIgnoreCase(userType) || !"Admin".equalsIgnoreCase(roleName)) {
            response.sendRedirect(ctx + "/error.jsp");
            return;
        }

        String customerIDStr = request.getParameter("customerID");

        try {
            if (customerIDStr != null && !customerIDStr.trim().isEmpty()) {
                int customerID = Integer.parseInt(customerIDStr);
                Customer customer = customerDAO.getCustomerById(customerID);

                if (customer != null) {
                    request.setAttribute("customer", customer);
                    request.getRequestDispatcher("/admin/editCustomer.jsp").forward(request, response);
                    return;
                }
            }

            request.setAttribute("error", "Customer not found.");
            request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
        } catch (SQLException | NumberFormatException e) {
            request.setAttribute("error", "Error loading customer: " + e.getMessage());
            request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
        }
    }

}
