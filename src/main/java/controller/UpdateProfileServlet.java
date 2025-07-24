/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.CustomerDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Customer;

/**
 *
 * @author ADMIN
 */
@WebServlet(name = "UpdateProfileServlet", urlPatterns = {"/update-profile"})
public class UpdateProfileServlet extends HttpServlet {

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
            out.println("<title>Servlet UpdateProfileServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UpdateProfileServlet at " + request.getContextPath() + "</h1>");
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
        processRequest(request, response);
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

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Retrieve form data
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String cccd = request.getParameter("cccd");
        String gender = request.getParameter("gender");
        String birthDate = request.getParameter("birthDate");
        String address = request.getParameter("address");
        String email = request.getParameter("email");

        // Store error messages
        StringBuilder errorMessage = new StringBuilder();

        // 1. Check for empty fields
        if (isEmptyOrNull(fullName)) {
            errorMessage.append("Full name cannot be empty.<br>");
        }
        if (isEmptyOrNull(phone)) {
            errorMessage.append("Phone number cannot be empty.<br>");
        }
        if (isEmptyOrNull(cccd)) {
            errorMessage.append("CCCD cannot be empty.<br>");
        }
        if (isEmptyOrNull(gender)) {
            errorMessage.append("Gender cannot be empty.<br>");
        }
        if (isEmptyOrNull(birthDate)) {
            errorMessage.append("Birth date cannot be empty.<br>");
        }
        if (isEmptyOrNull(address)) {
            errorMessage.append("Address cannot be empty.<br>");
        }
        if (isEmptyOrNull(email)) {
            errorMessage.append("Email cannot be empty.<br>");
        }

        // 2. Validate special characters
        if (!isValidName(fullName)) {
            errorMessage.append("Full name cannot contain special characters.<br>");
        }
        if (!isValidPhone(phone)) {
            errorMessage.append("Phone number is invalid (must contain only digits and be 10-11 characters long).<br>");
        }
        if (!isValidCCCD(cccd)) {
            errorMessage.append("CCCD is invalid (must contain only digits and be 9-12 characters long).<br>");
        }
        if (!isValidEmail(email)) {
            errorMessage.append("Email format is invalid.<br>");
        }

        // 3. Check if user is 18 or older
        if (birthDate != null && !birthDate.isEmpty()) {
            try {
                java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthDate);
                if (!isOver18(utilDate)) {
                    errorMessage.append("You must be 18 years or older.<br>");
                } else {
                    customer.setBirthDate(new java.sql.Date(utilDate.getTime()));
                }
            } catch (ParseException e) {
                errorMessage.append("Birth date format is invalid (yyyy-MM-dd).<br>");
                Logger.getLogger(UpdateProfileServlet.class.getName()).log(Level.SEVERE, null, e);
            }
        }
// If there are errors, forward back to the form with error messages
        if (errorMessage.length() > 0) {
            request.setAttribute("errorMessage", errorMessage.toString());
            request.getRequestDispatcher("/customer/view-profile.jsp").forward(request, response);
            return;
        }

        // Update customer object
        customer.setCustomerFullName(fullName);
        customer.setPhoneNumber(phone);
        customer.setCCCD(cccd);
        customer.setGender(gender);
        customer.setAddress(address);
        customer.setEmail(email);

        // Update database
        CustomerDAO dao = new CustomerDAO();
        try {
            dao.updateCustomer(customer);
        } catch (SQLException ex) {
            Logger.getLogger(UpdateProfileServlet.class.getName()).log(Level.SEVERE, null, ex);
            request.setAttribute("errorMessage", "Error updating data in the database.");
            request.getRequestDispatcher("/customer/view-profile.jsp").forward(request, response);
            return;
        }

        // Update session and redirect to profile page
        session.setAttribute("customer", customer);
        response.sendRedirect(request.getContextPath() + "/customer/view-profile");
    }

    // Check for empty or null fields
    private boolean isEmptyOrNull(String value) {
        return value == null || value.trim().isEmpty();
    }

    // Validate name (no special characters)
    private boolean isValidName(String name) {
        if (name == null) return false;
        return name.matches("^[a-zA-Z\\s]+$");
    }

    // Validate phone number (digits only, 10-11 characters)
    private boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return phone.matches("^[0-9]{10,11}$");
    }

    // Validate CCCD (digits only, 9-12 characters)
    private boolean isValidCCCD(String cccd) {
        if (cccd == null) return false;
        return cccd.matches("^[0-9]{9,12}$");
    }

    // Validate email format
    private boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    // Check if user is 18 or older
    private boolean isOver18(java.util.Date birthDate) {
        java.util.Calendar today = java.util.Calendar.getInstance();
        java.util.Calendar birth = java.util.Calendar.getInstance();
        birth.setTime(birthDate);
        int age = today.get(java.util.Calendar.YEAR) - birth.get(java.util.Calendar.YEAR);
        if (today.get(java.util.Calendar.DAY_OF_YEAR) < birth.get(java.util.Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age >= 18;
    }
       

    @Override
    public String getServletInfo() {
        return "Update profile information for customers.";
    }
}
 