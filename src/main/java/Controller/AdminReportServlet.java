package controller;

import dao.ReportDAO;
import dao.UserDAO;
import model.Report;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "AdminReportServlet", urlPatterns = {"/adminReport"})
public class AdminReportServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idUser") == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        Integer userID = (Integer) session.getAttribute("idUser");
        UserDAO userDAO = new UserDAO();
        try {
            User user = userDAO.getUserById(userID);
            if (user == null) {
                session.setAttribute("error", "Unauthorized access.");
                response.sendRedirect(request.getContextPath() + "/Login");
                return;
            }

            ReportDAO reportDAO = new ReportDAO();
            String statusFilter = request.getParameter("statusFilter");
            List<Report> reports;

            if (user.getRole().getRoleID() == 1 && userID == 1) { // Admin with UserID = 1
                // Admins see all reports
                reports = statusFilter != null && !statusFilter.isEmpty()
                        ? reportDAO.getReportsByStatus(statusFilter)
                        : reportDAO.getDetailedReports();
                request.setAttribute("reports", reports);
                request.setAttribute("currentFilter", statusFilter);
                request.getRequestDispatcher("/admin/reportList.jsp").forward(request, response);
            } else if (user.getRole().getRoleID() == 2) { // Manager
                Integer blockID = user.getBlockID();
                if (blockID == null) {
                    session.setAttribute("error", "Manager has no assigned block.");
                    response.sendRedirect(request.getContextPath() + "/manager/dashboard.jsp");
                    return;
                }
                // Managers see only reports for their block
                reports = statusFilter != null && !statusFilter.isEmpty()
                        ? reportDAO.getManagerReportsByStatus(statusFilter, blockID)
                        : reportDAO.getManagerDetailedReports(blockID);
                request.setAttribute("reports", reports);
                request.setAttribute("currentFilter", statusFilter);
                request.getRequestDispatcher("/manager/reportListManager.jsp").forward(request, response);
            } else {
                session.setAttribute("error", "Invalid user role.");
                response.sendRedirect(request.getContextPath() + "/Login");
                return;
            }
        } catch (SQLException e) {
            session.setAttribute("error", "Database error occurred while fetching reports: " + e.getMessage());
            Logger.getLogger(AdminReportServlet.class.getName()).log(Level.SEVERE, "Database error", e);
            response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idUser") == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        Integer userID = (Integer) session.getAttribute("idUser");
        UserDAO userDAO = new UserDAO();
        try {
            User user = userDAO.getUserById(userID);
            if (user == null) {
                session.setAttribute("error", "Unauthorized access.");
                response.sendRedirect(request.getContextPath() + "/Login");
                return;
            }

            int resolvedBy = (user.getRole().getRoleID() == 1) ? 1 : 2; // 1 = Admin, 2 = Manager
            String action = request.getParameter("action");
            if ("resolve".equals(action)) {
                try {
                    int reportID = Integer.parseInt(request.getParameter("reportID"));
                    String status = request.getParameter("reportStatus");
                    ReportDAO reportDAO = new ReportDAO();

                    // Validate block ownership for managers
                    if (user.getRole().getRoleID() == 2) {
                        Integer blockID = user.getBlockID();
                        if (blockID == null || !reportDAO.isReportInManagerBlock(reportID, blockID)) {
                            session.setAttribute("error", "You are not authorized to resolve this report.");
                            response.sendRedirect("adminReport");
                            return;
                        }
                    }

                    boolean success = reportDAO.updateReportStatus(reportID, status, resolvedBy);
                    if (success) {
                        session.setAttribute("message", "Report updated successfully!");
                    } else {
                        session.setAttribute("error", "Failed to update report. Please try again.");
                    }
                } catch (NumberFormatException e) {
                    session.setAttribute("error", "Invalid input: " + e.getMessage());
                    Logger.getLogger(AdminReportServlet.class.getName()).log(Level.WARNING, "Invalid input", e);
                } catch (SQLException e) {
                    session.setAttribute("error", "Database error occurred while updating report: " + e.getMessage());
                    Logger.getLogger(AdminReportServlet.class.getName()).log(Level.SEVERE, "Database error", e);
                }
            }

            response.sendRedirect("adminReport");
        } catch (SQLException e) {
            session.setAttribute("error", "Database error occurred while verifying user: " + e.getMessage());
            Logger.getLogger(AdminReportServlet.class.getName()).log(Level.SEVERE, "User verification error", e);
            response.sendRedirect(request.getContextPath() + "/Login");
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet for handling admin and manager report operations";
    }
}