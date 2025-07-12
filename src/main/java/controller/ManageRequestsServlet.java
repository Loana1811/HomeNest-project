package controller;

import dao.CustomerDAO;
import dao.NotificationDAO;
import dao.RentalRequestDAO;
import dao.RoomDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Customer;
import model.Notification;
import model.RentalRequest;
import model.Room;
import model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@WebServlet("/manager/manage-requests")
public class ManageRequestsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("manager") == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        User manager = (User) session.getAttribute("manager");

        if (manager.getRoleId() != 2 || manager.getBlockId() == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        try {
            RentalRequestDAO requestDAO = new RentalRequestDAO();
            CustomerDAO customerDAO = new CustomerDAO();
            RoomDAO roomDAO = new RoomDAO();

            // ✅ lấy các yêu cầu theo block mà manager đang phụ trách
            List<RentalRequest> requests = requestDAO.getRentalRequestsByBlockId(manager.getBlockId());

            List<Customer> customers = customerDAO.getAllCustomers();
            List<Room> rooms = roomDAO.getAllRooms();

            request.setAttribute("requests", requests);
            request.setAttribute("customers", customers);
            request.setAttribute("rooms", rooms);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải danh sách yêu cầu thuê.");
        }

        request.getRequestDispatcher("/manager/manage-requests.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String requestIdStr = request.getParameter("requestId");

        if (!"approve".equals(action) || requestIdStr == null || !requestIdStr.matches("\\d+")) {
            response.sendRedirect(request.getContextPath() + "/manager/manage-requests?error=invalidRequest");
            return;
        }

        int requestId = Integer.parseInt(requestIdStr);

        try {
            RentalRequestDAO requestDAO = new RentalRequestDAO();
            RoomDAO roomDAO = new RoomDAO();
            NotificationDAO notiDAO = new NotificationDAO();

            RentalRequest rental = requestDAO.getRequestById(requestId);
            if (rental == null || !"Pending".equalsIgnoreCase(rental.getRequestStatus())) {
                response.sendRedirect(request.getContextPath() + "/manager/manage-requests?error=notFoundOrAlreadyApproved");
                return;
            }

            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("manager") == null) {
                response.sendRedirect(request.getContextPath() + "/Login");
                return;
            }

            User manager = (User) session.getAttribute("manager");

            if (manager.getRoleId() != 2) {
                response.sendRedirect(request.getContextPath() + "/Login");
                return;
            }

            // ✅ Cập nhật yêu cầu thuê
            rental.setApprovedBy(manager.getUserId());
            rental.setRequestStatus("Approved");
            rental.setApprovedDate(new java.sql.Date(System.currentTimeMillis()));
            requestDAO.updateRentalRequest(rental);

            // ✅ Cập nhật trạng thái phòng
            roomDAO.updateRoomStatus(rental.getRoomID(), "Occupied");

            // ✅ Lấy số phòng để hiển thị trong thông báo
            Room room = roomDAO.getRoomById(rental.getRoomID());
            String roomInfo = (room != null) ? room.getRoomNumber() : ("ID " + rental.getRoomID());

            // ✅ Gửi thông báo
            Notification noti = new Notification();
            noti.setCustomerID(rental.getCustomerID());
            noti.setTitle("Yêu cầu thuê phòng đã được duyệt");
            noti.setMessage("Yêu cầu thuê phòng " + roomInfo + " của bạn đã được phê duyệt.");
            noti.setSentBy(String.valueOf(manager.getUserId()));

            noti.setIsRead(false);
            noti.setNotificationCreateAt(new Timestamp(System.currentTimeMillis()));
            System.out.println("🟢 Chuẩn bị insert notification cho CustomerID = " + rental.getCustomerID());

            notiDAO.insertNotification(noti);

            // ✅ Redirect về trang quản lý với thông báo thành công
            response.sendRedirect(request.getContextPath() + "/manager/manage-requests?success=approve");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/manager/manage-requests?error=serverError");
        }
    }
}
