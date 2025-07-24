package controller;

import dao.ReportDAO;
import model.Report;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "CustomerReportServlet", urlPatterns = {"/CustomerReport"})
public class CustomerReportServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer customerID = (Integer) session.getAttribute("idCustomer");
        String userType = (String) session.getAttribute("userType");

        // Kiểm tra đăng nhập với vai trò Customer
        if (customerID == null || !"Customer".equals(userType)) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        ReportDAO reportDAO = new ReportDAO();
        try {
            // Lấy danh sách báo cáo của khách hàng
            List<Report> reports = reportDAO.getReportsByCustomer(customerID);
            request.setAttribute("reports", reports);

            // Lấy danh sách hợp đồng còn hiệu lực
            List<Map<String, Object>> activeContracts = reportDAO.getActiveNonExpiredRoomsAndContracts(customerID);
            request.setAttribute("roomsAndContracts", activeContracts);

            // Xác định hành động
            String action = request.getParameter("action");
            if ("create".equals(action) && !activeContracts.isEmpty()) {
                request.getRequestDispatcher("/customer/createReport.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/customer/reportList.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            request.getSession().setAttribute("error", "Lỗi cơ sở dữ liệu khi lấy dữ liệu báo cáo.");
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/customer/dashboard.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer customerID = (Integer) session.getAttribute("idCustomer");
        String userType = (String) session.getAttribute("userType");

        if (customerID == null || !"Customer".equals(userType)) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        ReportDAO reportDAO = new ReportDAO();
        try {
            int roomID = Integer.parseInt(request.getParameter("roomID"));
            int contractID = Integer.parseInt(request.getParameter("contractID"));
            String issueDescription = request.getParameter("issueDescription");

            // Xác thực phòng và hợp đồng
            boolean isValid = reportDAO.validateRoomAndContract(customerID, roomID, contractID);
            if (!isValid) {
                request.getSession().setAttribute("error", "Phòng hoặc hợp đồng không hợp lệ.");
                response.sendRedirect("CustomerReport?action=create");
                return;
            }

            // Tạo báo cáo mới
            Report report = new Report();
            report.setCustomerID(customerID);
            report.setRoomID(roomID);
            report.setContractID(contractID);
            report.setIssueDescription(issueDescription);
            report.setReportStatus("Pending");

            boolean success = reportDAO.addReport(report);
            if (success) {
                request.getSession().setAttribute("message", "Tạo báo cáo thành công!");
            } else {
                request.getSession().setAttribute("error", "Tạo báo cáo thất bại. Vui lòng thử lại.");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "Định dạng đầu vào không hợp lệ.");
            e.printStackTrace();
        } catch (SQLException e) {
            request.getSession().setAttribute("error", "Lỗi cơ sở dữ liệu khi tạo báo cáo.");
            e.printStackTrace();
        }

        response.sendRedirect("CustomerReport");
    }

    @Override
    public String getServletInfo() {
        return "Servlet xử lý các thao tác báo cáo của khách hàng";
    }
}