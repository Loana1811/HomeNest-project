package controller.roomlist;

import dao.CategoryDAO;
import dao.RoomDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import model.Category;
import model.Room;
import utils.DBContext;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
@WebServlet("/customer/room-list")
public class RoomListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Connection conn = null;

        try {
            conn = new DBContext().getConnection();
            RoomDAO roomDAO = new RoomDAO(conn);
            CategoryDAO categoryDAO = new CategoryDAO(conn);

            // Lấy tham số lọc
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

            // ===== PHÂN TRANG =====
            int pageSize = 6;
            String pageParam = request.getParameter("page");
            int currentPage = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
            if (currentPage < 1) {
                currentPage = 1;
            }

            // Đếm tổng số phòng sau khi lọc
            int totalRooms = roomDAO.countFilteredRooms(categoryId, block, status, minPrice, maxPrice, minArea, maxArea);
            int totalPages = (int) Math.ceil((double) totalRooms / pageSize);
            if (currentPage > totalPages && totalPages > 0) {
                currentPage = totalPages;
            }

            // Lấy danh sách phòng theo trang hiện tại
            List<Room> pagedRooms = roomDAO.filterRooms(categoryId, block, status, minPrice, maxPrice, minArea, maxArea, currentPage, pageSize);

            // Lấy danh sách danh mục
            List<Category> categoryList = categoryDAO.getAllCategories();

            // Gửi dữ liệu sang JSP
            request.setAttribute("roomList", pagedRooms);
            request.setAttribute("categoryList", categoryList);

            // Các tham số lọc
            request.setAttribute("selectedCategory", categoryId);
            request.setAttribute("selectedBlock", block);
            request.setAttribute("selectedStatus", status);
            request.setAttribute("minPrice", minPrice);
            request.setAttribute("maxPrice", maxPrice);
            request.setAttribute("minArea", minArea);
            request.setAttribute("maxArea", maxArea);

            // Thông tin phân trang
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);

            request.getRequestDispatcher("/customer/room-list.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi xử lý dữ liệu phòng.");
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        try {
            if (isEmpty(value)) {
                return null; // Tránh parse giá trị rỗng
            }
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
