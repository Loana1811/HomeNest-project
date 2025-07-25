/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.DashboardDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import model.User;

/**
 *
 * @author kloane
 */
@WebServlet("/admin/dashboard")
public class DashboardServlet extends HttpServlet {

    private DashboardDAO dao = new DashboardDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get the session
        HttpSession session = req.getSession(false); // Don't create a new session

        // Check if user is logged in
        if (session == null || session.getAttribute("userType") == null) {
            req.setAttribute("error", "Please log in to access the admin dashboard.");
            resp.sendRedirect(req.getContextPath() + "/Login");
            return;
        }

        // Check if the user is an Admin
        String userType = (String) session.getAttribute("userType");
        Integer roleID = (Integer) session.getAttribute("roleID");
        if (!"User".equals(userType) || roleID == null) {
            req.setAttribute("error", "Unauthorized access. Admin privileges required.");
            resp.sendRedirect(req.getContextPath() + "/Login");
            return;
        }

        // Verify Admin role
        UserDAO userDAO = new UserDAO();
        try {
            User user = userDAO.getUserById((Integer) session.getAttribute("idUser"));
            if (user == null || !"Admin".equals(user.getRole().getRoleName())) {
                req.setAttribute("error", "Unauthorized access. Admin privileges required.");
                resp.sendRedirect(req.getContextPath() + "/Login");
                return;
            }

            // User is authorized, proceed with dashboard data
            req.setAttribute("totalRooms", dao.countRooms());
            req.setAttribute("totalTenants", dao.countTenants());
            req.setAttribute("unpaidBills", dao.countUnpaidBills());
            req.setAttribute("recentReadings", dao.countReadingsLast30Days());
            req.setAttribute("electricityData", dao.getDailyElectricityUsageLastWeek());
            req.getRequestDispatcher("/admin/dashboard.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Database error occurred", e);
        } catch (Exception e) {
            throw new ServletException("An error occurred while accessing the dashboard", e);
        }
    }
}
