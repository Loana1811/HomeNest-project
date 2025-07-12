/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.BlockDAO;
import dao.CategoryDAO;
import dao.NotificationDAO;
import dao.RoomDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import model.Block;
import model.Category;
import model.Notification;
import model.Room;

/**
 *
 * @author Admin
 */
@WebServlet("/customer/room-list")
public class RoomListServlet extends HttpServlet {

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
            out.println("<title>Servlet RoomListServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RoomListServlet at " + request.getContextPath() + "</h1>");
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
        Connection conn = null;

        try {
            RoomDAO roomDAO = new RoomDAO();
            CategoryDAO categoryDAO = new CategoryDAO();
            BlockDAO blockDAO = new BlockDAO();

            String categoryParam = request.getParameter("category");
            String blockParam = request.getParameter("block");
            String statusParam = request.getParameter("status");
            String minPriceParam = request.getParameter("minPrice");
            String maxPriceParam = request.getParameter("maxPrice");
            String minAreaParam = request.getParameter("minArea");
            String maxAreaParam = request.getParameter("maxArea");

            Integer categoryId = parseInteger(categoryParam);
            String block = isEmpty(blockParam) ? null : blockParam;
            String status = isEmpty(statusParam) ? null : statusParam;
            Double minPrice = parseDouble(minPriceParam);
            Double maxPrice = parseDouble(maxPriceParam);
            Double minArea = parseDouble(minAreaParam);
            Double maxArea = parseDouble(maxAreaParam);

            int pageSize = 6;
            String pageParam = request.getParameter("page");
            int currentPage = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
            if (currentPage < 1) {
                currentPage = 1;
            }

            int totalRooms = roomDAO.countFilteredRooms(categoryId, block, status, minPrice, maxPrice, minArea, maxArea);
            int totalPages = (int) Math.ceil((double) totalRooms / pageSize);
            if (currentPage > totalPages && totalPages > 0) {
                currentPage = totalPages;
            }

            List<Room> pagedRooms = roomDAO.filterRooms(categoryId, block, status, minPrice, maxPrice, minArea, maxArea, currentPage, pageSize);
            List<Category> categoryList = categoryDAO.getAllCategories();
            List<Block> blockList = blockDAO.getAllBlocks();
            List<String> locationList = roomDAO.getAllLocations();

            // ===== LẤY THÔNG BÁO CỦA KHÁCH HÀNG =====
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("customer") != null) {
                model.Customer customer = (model.Customer) session.getAttribute("customer");
                int customerId = customer.getCustomerID();
                NotificationDAO notificationDAO = new NotificationDAO();
                List<Notification> notifications = notificationDAO.getNotificationsByCustomer(customerId);

                request.setAttribute("notifications", notifications);
            }

            // ===== GỬI DỮ LIỆU SANG JSP =====
            request.setAttribute("roomList", pagedRooms);
            request.setAttribute("categoryList", categoryList);
            request.setAttribute("blockList", blockList);
            request.setAttribute("locationList", locationList);

            request.setAttribute("selectedCategory", categoryId);
            request.setAttribute("selectedBlock", block);
            request.setAttribute("selectedStatus", status);
            request.setAttribute("minPrice", minPrice);
            request.setAttribute("maxPrice", maxPrice);
            request.setAttribute("minArea", minArea);
            request.setAttribute("maxArea", maxArea);

            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);

            request.getRequestDispatcher("/customer/room-list.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi xử lý dữ liệu phòng.");
            }
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        try {
            if (isEmpty(value)) {
                return null;
            }
            return Double.parseDouble(value.trim());
        } catch (Exception e) {
            return null;
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
