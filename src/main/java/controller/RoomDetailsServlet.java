/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.RentalRequestDAO;
import dao.RoomDAO;
import dao.UtilityDAO;
import dao.UtilityTypeDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
import java.util.List;
import model.Customer;
import model.Room;
import model.UtilityType;
import utils.DBContext;

/**
 *
 * @author Admin
 */
@WebServlet("/customer/room-detail")
public class RoomDetailsServlet extends HttpServlet {

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
            out.println("<title>Servlet RoomDetailsServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RoomDetailsServlet at " + request.getContextPath() + "</h1>");
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

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu ID phòng.");
            return;
        }

        try {
            int roomId = Integer.parseInt(idParam);
            RoomDAO roomDAO = new RoomDAO();
            Room room = roomDAO.getRoomById(roomId);
            UtilityDAO utilityDAO = new UtilityDAO();

            if (room == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy phòng.");
                return;
            }

            List<Room> featuredRooms = roomDAO.getFeaturedRooms();
            request.setAttribute("room", room);
            request.setAttribute("featuredRooms", featuredRooms);
            List<UtilityType> utilityTypes = utilityDAO.getAllUtilityTypes();
            request.setAttribute("utilityTypes", utilityTypes);

            // ✅ Kiểm tra nếu khách đã gửi yêu cầu rồi thì không hiện nút "Gửi"
            HttpSession session = request.getSession();
            Customer currentCustomer = (Customer) session.getAttribute("customer");

            boolean alreadyRequested = false;
            boolean showSuccess = false;

            if (currentCustomer != null) {
                if ("true".equals(request.getParameter("success"))) {
                    alreadyRequested = true;
                    showSuccess = true;
                } else {
                    RentalRequestDAO rentalRequestDAO = new RentalRequestDAO();
                    alreadyRequested = rentalRequestDAO.existsPendingRequest(currentCustomer.getCustomerID(), roomId);
                }
            }

            request.setAttribute("alreadyRequested", alreadyRequested);
            request.setAttribute("showSuccess", showSuccess);

            request.getRequestDispatcher("/customer/room-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID phòng không hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi lấy thông tin phòng.");
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
        return "Hiển thị thông tin chi tiết phòng và kiểm tra đã gửi yêu cầu thuê chưa";
    }
}
