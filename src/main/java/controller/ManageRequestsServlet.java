package controller;

import dao.CustomerDAO;
import dao.NotificationDAO;
import dao.RentalRequestDAO;
import dao.RoomDAO;
import dao.TenantDAO;
import dao.UserNotificationDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Customer;
import model.Notification;
import model.RentalRequest;
import model.Room;
import model.User;
import model.UserNotification;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import model.Tenant;

@WebServlet("/manager/manage-requests")
public class ManageRequestsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        User manager = (User) session.getAttribute("currentUser");

        if (manager.getRoleID() != 2) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        try {
            RentalRequestDAO requestDAO = new RentalRequestDAO();
            CustomerDAO customerDAO = new CustomerDAO();
            RoomDAO roomDAO = new RoomDAO();
            UserNotificationDAO userNotiDAO = new UserNotificationDAO();

            List<RentalRequest> requests = requestDAO.getRentalRequestsByBlockId(manager.getBlockID());
            List<Customer> customers = customerDAO.getAllCustomers();
            List<Room> rooms = roomDAO.getAllRooms();
            List<UserNotification> userNotifications = userNotiDAO.getUserNotificationsByUserId(manager.getUserID());

            request.setAttribute("requests", requests);
            request.setAttribute("customers", customers);
            request.setAttribute("rooms", rooms);
            request.setAttribute("userNotifications", userNotifications);

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

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        User manager = (User) session.getAttribute("currentUser");

        if (manager.getRoleID() != 2) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        try {
            RentalRequestDAO requestDAO = new RentalRequestDAO();
            RoomDAO roomDAO = new RoomDAO();
            NotificationDAO notiDAO = new NotificationDAO();
            UserNotificationDAO userNotiDAO = new UserNotificationDAO();
            TenantDAO tenantDAO = new TenantDAO();

            RentalRequest rental = requestDAO.getRequestById(requestId);
            System.out.println(rental.getCustomerID());
            if (rental == null || !"Pending".equalsIgnoreCase(rental.getRequestStatus())) {
                response.sendRedirect(request.getContextPath() + "/manager/manage-requests?error=notFoundOrAlreadyApproved");
                return;
            }

            rental.setApprovedBy(manager.getUserID());
            rental.setRequestStatus("Approved");
            rental.setApprovedDate(new java.sql.Date(System.currentTimeMillis()));
            requestDAO.updateRentalRequest(rental);

            // ✅ Kiểm tra nếu chưa là tenant thì mới thêm
            boolean isAlreadyTenant = tenantDAO.isCustomerAlreadyTenant(rental.getCustomerID());
            if (!isAlreadyTenant) {
                java.sql.Date joinDate = new java.sql.Date(System.currentTimeMillis());
                tenantDAO.addTenant(rental.getCustomerID(), joinDate);
            }

            // ❌ XÓA DÒNG: roomDAO.updateRoomStatus(rental.getRoomID(), "Occupied");
            Room room = roomDAO.getRoomById(rental.getRoomID());
            String roomInfo = (room != null) ? room.getRoomNumber() : ("ID " + rental.getRoomID());

            Notification noti = new Notification();
            noti.setCustomerID(rental.getCustomerID());
            noti.setTitle("Yêu cầu thuê phòng đã được duyệt");
            noti.setMessage("Yêu cầu thuê phòng " + roomInfo + " của bạn đã được phê duyệt.");
            noti.setSentBy(manager.getUserID());
            noti.setRead(false);
            noti.setNotificationCreatedAt(new Timestamp(System.currentTimeMillis()));
            notiDAO.insert(noti);

            // Gửi thông báo cho admin qua UserNotification
            Notification notifyAdmin = new Notification();
            Tenant tenant = tenantDAO.getTenantByCustomerId(rental.getCustomerID());
String tenantId = (tenant != null) ? String.valueOf(tenant.getTenantID()) : "N/A";

            notifyAdmin.setCustomerID(requestId); // Admin mặc định
            notifyAdmin.setTitle("Yêu cầu thuê mới được duyệt");
            Customer customers = new CustomerDAO().getCustomerById(rental.getCustomerID());
            System.out.println(customers.getCustomerFullName());
            String customerName = (customers != null) ? customers.getCustomerFullName() : ("ID " + rental.getCustomerID());
            String fullMessage = String.format(
                    "Khách hàng %s đã được phê duyệt là người thuê phòng số %s (Tenant ID: %s). Vui lòng tạo hợp đồng.",
                    customerName,
                    roomInfo,
                    tenantId
            );
            notifyAdmin.setMessage(fullMessage);

            notifyAdmin.setSentBy(manager.getUserID());
            notifyAdmin.setRead(false);
            notifyAdmin.setNotificationCreatedAt(new Timestamp(System.currentTimeMillis()));
            //userNotiDAO.createUserNotification(notifyAdmin);

            response.sendRedirect(request.getContextPath() + "/manager/manage-requests?success=approve");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/manager/manage-requests?error=serverError");
        }
    }
}
