/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.NotificationDAO;
import model.User;
import dao.RentalRequestDAO;
import dao.RoomDAO;
import dao.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import model.Customer;
import model.Notification;
import model.RentalRequest;
import model.Room;
//nguoi dung gui 
/**
 *
 * @author ADMIN
 */
@WebServlet("/request-room")
public class RentalRequestServlet extends HttpServlet {

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
            out.println("<title>Servlet RentalRequestServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RentalRequestServlet at " + request.getContextPath() + "</h1>");
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
        response.sendRedirect("customer/room-list"); // Hoặc hiển thị form gửi yêu cầu nếu bạn muốn
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
        try {
            HttpSession session = request.getSession();
            Customer currentCustomer = (Customer) session.getAttribute("customer");

            if (currentCustomer == null) {
                response.sendRedirect("Login");
                return;
            }

            int roomId = Integer.parseInt(request.getParameter("roomId"));
            int customerId = currentCustomer.getCustomerID();

            RentalRequestDAO requestDAO = new RentalRequestDAO();

            if (requestDAO.existsPendingRequest(customerId, roomId)) {
                response.sendRedirect("customer/room-detail?id=" + roomId + "&error=already_requested");
                return;
            }

            RentalRequest rentalRequest = new RentalRequest();
            rentalRequest.setCustomerID(customerId);
            rentalRequest.setRoomID(roomId);
            rentalRequest.setRequestDate(new Date(System.currentTimeMillis()));
            rentalRequest.setRequestStatus("Pending");
            rentalRequest.setApprovedBy(null);
            rentalRequest.setApprovedDate(null);

            requestDAO.insertRentalRequest(rentalRequest);

            RoomDAO roomDAO = new RoomDAO();
            Room room = roomDAO.getRoomById(roomId);
            int blockId = room.getBlockID();

            UserDAO userDAO = new UserDAO();
            List<User> managers = userDAO.getManagersByBlockId(blockId); // ✅ lấy danh sách managers

            if (managers != null && !managers.isEmpty()) {
                NotificationDAO notiDAO = new NotificationDAO();

                String title = "Yêu cầu thuê phòng mới";
                String message = "Khách hàng " + currentCustomer.getCustomerFullName()
                        + " đã gửi yêu cầu thuê phòng \"" + room.getRoomNumber() + "\".";

                for (User manager : managers) {
                    if (!notiDAO.existsSimilarNotification(manager.getUserID(), title, message)) {
                        Notification notification = new Notification();
                        notification.setCustomerID(manager.getUserID());
                        notification.setTitle(title);
                        notification.setMessage(message);
                        notification.setRead(false);
                        notification.setNotificationCreatedAt(new Timestamp(System.currentTimeMillis()));
                        notification.setSentBy(null); // hoặc nếu là Customer gửi thì để null

                        notiDAO.insertNotification(notification);
                    }
                }
            }

            response.sendRedirect("customer/room-detail?id=" + roomId + "&success=true");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp?message=error_sending_request");
        }
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
