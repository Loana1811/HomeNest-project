/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller.roomlist;

import dao.RoomDAO;
import model.Room;
import utils.DBContext;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet("/customer/room-detail")
public class RoomDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu ID phòng.");
            return;
        }

        try (Connection conn = new DBContext().getConnection()) {
            RoomDAO roomDAO = new RoomDAO(conn);
            int roomId = Integer.parseInt(idParam);
            Room room = roomDAO.getRoomById(roomId);

            if (room == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy phòng.");
                return;
            }

            // ✅ Thêm phần lấy danh sách phòng nổi bật
            List<Room> featuredRooms = roomDAO.getFeaturedRooms();  // Đảm bảo RoomDAO có hàm này

            // ✅ Đưa dữ liệu lên request
            request.setAttribute("room", room);
            request.setAttribute("featuredRooms", featuredRooms);

            // ✅ Forward về JSP hiển thị chi tiết
            request.getRequestDispatcher("/customer/room-detail.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi lấy thông tin phòng.");
        }
    }
}
