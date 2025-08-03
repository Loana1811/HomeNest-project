/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.RoomDAO;
import dao.ContractDAO;
import dao.CustomerDAO;
import dao.NotificationDAO;
import dao.TenantDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Contract;
import model.Customer;
import model.Notification;
import model.Room;
import model.Tenant;

/**
 *
 * @author Admin
 */
@WebServlet(name = "ContractServlet", urlPatterns = {"/Contracts"})
public class ContractServlet extends HttpServlet {

    private ContractDAO contractDAO = new ContractDAO(); // hoặc dùng setter để inject

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
            out.println("<title>Servlet ContractServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ContractServlet at " + request.getContextPath() + "</h1>");
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

        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        ContractDAO contractDAO = new ContractDAO();

        // ✅ Phân quyền truy cập: ADMIN / MANAGER
        if (session.getAttribute("idUser") != null) {
            int roleID = (int) session.getAttribute("roleID"); // bạn có thể dùng roleName thay thế

            // Các chức năng của ADMIN và MANAGER
            if (action == null || action.equals("list")) {
                List<Contract> contracts = contractDAO.getAllContracts();
                request.setAttribute("contracts", contracts);
                request.getRequestDispatcher("/admin/listContract.jsp").forward(request, response);
            } else if (action.equals("create")) {
                TenantDAO tenantDAO = new TenantDAO();
                RoomDAO roomDAO = new RoomDAO();
                List<Tenant> tenants = tenantDAO.getAllTenants();
                List<Room> rooms = roomDAO.getAllRooms();
                request.setAttribute("tenants", tenants);
                request.setAttribute("rooms", rooms);
                request.getRequestDispatcher("/admin/createContract.jsp").forward(request, response);
            } else if (action.equals("edit")) {
                String idParam = request.getParameter("id");
                if (idParam == null || idParam.trim().isEmpty()) {
                    response.sendRedirect("Contracts?action=list");
                    return;
                }
                int id = Integer.parseInt(idParam);
                Contract contract = contractDAO.getContractById(id);
                RoomDAO roomDAO = new RoomDAO();
                List<Room> rooms = roomDAO.getAllRooms();
                request.setAttribute("rooms", rooms);
                request.setAttribute("contract", contract);
                request.getRequestDispatcher("/admin/editContract.jsp").forward(request, response);
            } else if (action.equals("view")) {
                String idParam = request.getParameter("id");
                if (idParam == null || idParam.trim().isEmpty()) {
                    response.sendRedirect("Contracts");
                    return;
                }

                int contractId = Integer.parseInt(idParam);
                TenantDAO tenantDAO = new TenantDAO();
                RoomDAO roomDAO = new RoomDAO();
                CustomerDAO customerDAO = new CustomerDAO();

                Contract contract = contractDAO.getContractById(contractId);
                Tenant tenant = tenantDAO.getTenantById(contract.getTenantId());
                Room room = roomDAO.getRoomById(contract.getRoomId());
                Customer customer = null;
                try {
                    customer = customerDAO.getCustomerById(tenant.getCustomerID());
                } catch (SQLException ex) {
                    Logger.getLogger(ContractServlet.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (contract == null || tenant == null || room == null || customer == null) {
                    request.setAttribute("error", "Dữ liệu hợp đồng không đầy đủ.");
                    response.sendRedirect("Contracts");
                    return;
                }

                request.setAttribute("contract", contract);
                request.setAttribute("tenant", tenant);
                request.setAttribute("room", room);
                request.setAttribute("customer", customer);
                request.getRequestDispatcher("/admin/viewDetail.jsp").forward(request, response);
            } else if (action.equals("history")) {
                String tenantIdParam = request.getParameter("tenantId");
                if (tenantIdParam == null || tenantIdParam.trim().isEmpty()) {
                    response.sendRedirect("Contracts?action=list");
                    return;
                }

                int tenantId = Integer.parseInt(tenantIdParam);
                List<Contract> historyContracts = contractDAO.getContractHistoryByTenantId(tenantId);
                request.setAttribute("contracts", historyContracts);
                request.getRequestDispatcher("/admin/historyContract.jsp").forward(request, response);
            } else if ("viewDetail".equals(action)) {
                int contractId = Integer.parseInt(request.getParameter("id"));
                Contract contract = contractDAO.getContractById(contractId);
                request.setAttribute("contract", contract);
                request.getRequestDispatcher("/customer/viewContracts.jsp").forward(request, response);
            } else if (action.equals("createFromRequest")) {
                String customerIdParam = request.getParameter("customerId");
                String roomIdParam = request.getParameter("roomId");

                if (customerIdParam == null || roomIdParam == null) {
                    response.sendRedirect("Contracts?action=list");
                    return;
                }

                try {
                    int customerId = Integer.parseInt(customerIdParam);
                    int roomId = Integer.parseInt(roomIdParam);

                    TenantDAO tenantDAO = new TenantDAO();
                    RoomDAO roomDAO = new RoomDAO();
                    CustomerDAO customerDAO = new CustomerDAO();

                    Tenant tenant = tenantDAO.getTenantByCustomerId(customerId);
                    Room room = roomDAO.getRoomById(roomId);
                    Customer customer = customerDAO.getCustomerById(customerId);

                    if (tenant == null || room == null || customer == null) {
                        request.setAttribute("error", "Không thể tạo hợp đồng do thiếu thông tin.");
                        response.sendRedirect("Contracts?action=list");
                        return;
                    }

                    request.setAttribute("tenant", tenant);
                    request.setAttribute("room", room);
                    request.setAttribute("customer", customer);
                    request.getRequestDispatcher("/admin/createContract.jsp").forward(request, response);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect("Contracts?action=list");
                }
            } else {
                response.sendRedirect("Contracts?action=list");
            }

        } // ✅ Phân quyền: CUSTOMER
        else if (session.getAttribute("idCustomer") != null) {
            int customerId = (int) session.getAttribute("idCustomer");
            String idParam = request.getParameter("id");

            if (session.getAttribute("idCustomer") != null) {

                System.out.println("Customer ID in session: " + customerId);
            } else if (session.getAttribute("idUser") != null) {
                System.out.println("User ID in session: " + session.getAttribute("idUser"));
            } else {
                System.out.println("No user or customer logged in.");
            }

            if ("viewCustomer".equals(action)) {
                try {
                    int customerIdOrContractId = Integer.parseInt(idParam);
                    ContractDAO contractDAOs = new ContractDAO();
                    TenantDAO tenantDAO = new TenantDAO();
                    CustomerDAO customerDAO = new CustomerDAO();

                    int tenantId = -1;
                    if (request.getParameter("contractId") != null) {
                        int contractId = Integer.parseInt(request.getParameter("contractId"));
                        Contract contract = contractDAOs.getContractById(contractId);
                        if (contract != null) {
                            tenantId = contract.getTenantId();
                            Tenant tenant = tenantDAO.getTenantById(tenantId);
                            if (tenant != null) {
                                customerId = tenant.getCustomerID();
                            }
                        }
                    } else {
                        Tenant tenant = tenantDAO.getTenantByCustomerId(customerId);
                        if (tenant != null) {
                            tenantId = tenant.getTenantID();
                        }
                    }

                    if (tenantId == -1) {
                        request.setAttribute("error", "Không tìm thấy khách hàng liên quan.");
                        request.getRequestDispatcher("/customer/viewContracts.jsp").forward(request, response);
                        return;
                    }

                    List<Contract> contracts = contractDAOs.getContractsByCustomerId(customerId);
                    Customer customer = new CustomerDAO().getCustomerById(customerId);
                    request.setAttribute("customer", customer);
                    request.setAttribute("contracts", contracts);

                    if (request.getParameter("contractId") != null) {
                        int contractId = Integer.parseInt(request.getParameter("contractId"));
                        request.setAttribute("highlightContractId", contractId);
                    } else if (contracts != null && !contracts.isEmpty()) {
                        request.setAttribute("highlightContractId", contracts.get(0).getContractId());
                    }

                } catch (Exception e) {
                    request.setAttribute("error", "Lỗi xử lý: " + e.getMessage());
                }

                request.getRequestDispatcher("/customer/viewContracts.jsp").forward(request, response);
            } else if ("viewHistoryCustomer".equals(action)) {
                try {
                    TenantDAO tenantDAO = new TenantDAO();
                    Tenant tenant = tenantDAO.getTenantByCustomerId(customerId);
                    if (tenant == null) {
                        request.setAttribute("error", "Không tìm thấy người thuê.");
                        request.getRequestDispatcher("/customer/viewHistoryContract.jsp").forward(request, response);
                        return;
                    }

                    int tenantId = tenant.getTenantID();
                    List<Contract> historyContracts = contractDAO.getContractHistoryByTenantId(tenantId);
                    Customer customer = new CustomerDAO().getCustomerById(customerId);

                    request.setAttribute("contracts", historyContracts);
                    request.setAttribute("customer", customer);
                    request.setAttribute("tenant", tenant);
                    request.getRequestDispatcher("/customer/viewHistoryContract.jsp").forward(request, response);
                } catch (SQLException ex) {
                    Logger.getLogger(ContractServlet.class.getName()).log(Level.SEVERE, null, ex);
                    request.setAttribute("error", "Không lấy được lịch sử hợp đồng.");
                    request.getRequestDispatcher("/customer/viewHistoryContract.jsp").forward(request, response);
                }
            } else if ("accept".equals(action) || "reject".equals(action)) {
                int contractId = Integer.parseInt(request.getParameter("id"));
                String newStatus = "accept".equals(action) ? "Active" : "Terminated";

                try {
                    boolean updated = contractDAO.updateContractStatus(contractId, newStatus);

                    if (updated) {
                        Contract contract = contractDAO.getContractById(contractId);
                        RoomDAO roomDAO = new RoomDAO();
                        if ("accept".equals(action)) {
                            roomDAO.updateRoomStatus(contract.getRoomId(), "Occupied");
                            request.setAttribute("message", "Hợp đồng đã được chấp nhận.");
                        } else {
                            request.setAttribute("message", "Hợp đồng đã bị từ chối.");
                        }

                        // Gửi thông báo
                        Notification noti = new Notification();
                        Tenant tenant = new TenantDAO().getTenantById(contract.getTenantId());
                        noti.setCustomerID(tenant.getCustomerID());
                        noti.setTitle("Hợp đồng #" + contractId + " đã được " + ("accept".equals(action) ? "chấp nhận" : "từ chối"));
                        noti.setMessage(noti.getTitle());
                        noti.setSentBy(1);
                        noti.setRead(false);
                        noti.setNotificationCreatedAt(new Timestamp(System.currentTimeMillis()));
                        new NotificationDAO().insertNotification(noti);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("error", "Không thể cập nhật hợp đồng.");
                }

                request.getRequestDispatcher("/customer/viewContracts.jsp").forward(request, response);
            }

        } else {
            // ❌ Không đăng nhập → Redirect đến trang login
            response.sendRedirect("Login");
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
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("create".equals(action)) {
            try {
                String tenantIdStr = request.getParameter("tenantId");
                String roomIdStr = request.getParameter("roomId");
                String startDateStr = request.getParameter("startDate");
                String endDateStr = request.getParameter("endDate");

                if (tenantIdStr == null || tenantIdStr.isEmpty()
                        || roomIdStr == null || roomIdStr.isEmpty()
                        || startDateStr == null || startDateStr.isEmpty()
                        || endDateStr == null || endDateStr.isEmpty()) {

                    request.setAttribute("error", "Please fill in all required fields.");
                    loadContractFormData(request);
                    request.getRequestDispatcher("/admin/createContract.jsp").forward(request, response);
                    return;
                }

                int tenantId = Integer.parseInt(tenantIdStr);
                int roomId = Integer.parseInt(roomIdStr);
                Date startDate = Date.valueOf(startDateStr);
                Date endDate = Date.valueOf(endDateStr);

                if (startDate.after(endDate)) {
                    request.setAttribute("error", "Start date cannot be after end date.");
                    loadContractFormData(request);
                    request.getRequestDispatcher("/admin/createContract.jsp").forward(request, response);
                    return;
                }

                long diffDays = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
                if (diffDays < 30) {
                    request.setAttribute("error", "The contract must be at least 30 days long.");
                    loadContractFormData(request);
                    request.getRequestDispatcher("/admin/createContract.jsp").forward(request, response);
                    return;
                }

                int contractId = this.contractDAO.addContract(tenantId, roomId, startDate, endDate);
                if (contractId != -1) {
                    // Gửi thông báo tại đây
                    Room room = new RoomDAO().getRoomById(roomId);
                    Tenant tenant = new TenantDAO().getTenantById(tenantId);

                    if (room != null && tenant != null) {
                        Notification noti = new Notification();
                        noti.setCustomerID(tenant.getCustomerID());
                        noti.setTitle("Hợp đồng mới đang chờ phản hồi");

                        String detailLink = String.format(
                                "<a href='http://localhost:8080/HomeNest/Contracts?action=viewCustomer&id=%d&contractId=%d' target='_blank'>Xem chi tiết hợp đồng</a>",
                                tenant.getCustomerID(),
                                contractId
                        );
                        noti.setMessage(String.format(
                                "Hợp đồng thuê phòng %s (Giá: %sđ, Diện tích: %.1fm², Vị trí: %s, Block: %d) đã được tạo. "
                                + "Vui lòng phản hồi trong vòng 3 ngày. %s",
                                room.getRoomNumber(),
                                room.getRentPrice(),
                                room.getArea(),
                                room.getLocation(),
                                room.getBlockID(),
                                detailLink
                        ));

                        noti.setSentBy(1);
                        noti.setRead(false);
                        noti.setNotificationCreatedAt(new Timestamp(System.currentTimeMillis()));

                        new NotificationDAO().insertNotification(noti);
                    }

                    response.sendRedirect(request.getContextPath() + "/Contracts?action=list");
                } else {
                    request.setAttribute("error", "Failed to create contract. Please try again.");
                    loadContractFormData(request);
                    request.getRequestDispatcher("/admin/createContract.jsp").forward(request, response);
                }

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Invalid input or system error.");
                loadContractFormData(request);
                request.getRequestDispatcher("/admin/createContract.jsp").forward(request, response);
            }
        } else if ("update".equals(action)) {
            try {
                int contractId = Integer.parseInt(request.getParameter("contractId"));
                int tenantId = Integer.parseInt(request.getParameter("tenantId"));
                int roomId = Integer.parseInt(request.getParameter("roomId"));
                Date startDate = Date.valueOf(request.getParameter("startDate"));
                String endDateStr = request.getParameter("endDate");
                String status = request.getParameter("status");
                Date endDate = (endDateStr == null || endDateStr.isEmpty()) ? null : Date.valueOf(endDateStr);

                Contract contract = new Contract();
                contract.setContractId(contractId);
                contract.setTenantId(tenantId);
                contract.setRoomId(roomId);
                contract.setStartDate(startDate);
                contract.setEndDate(endDate);
                contract.setContractStatus(status);

                ContractDAO contractDAO = new ContractDAO();
                contractDAO.updateContract(contract);

                response.sendRedirect(request.getContextPath() + "/Contracts?action=list");

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Invalid input or system error.");
                doGet(request, response);
            }
        } else if ("delete".equals(action)) {
            String contractIdStr = request.getParameter("contractId");

            if (contractIdStr == null || contractIdStr.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing contractId for delete action.");
                return;
            }

            int contractId = Integer.parseInt(contractIdStr);

            ContractDAO contractDAO = new ContractDAO();
            boolean deleted = contractDAO.deleteContract(contractId);

            if (!deleted) {
                // Đặt thông báo lỗi vào session
                request.getSession().setAttribute("deleteError", "❌ Không thể xóa hợp đồng đã bắt đầu hoặc đã kết thúc.");
            }

            response.sendRedirect("Contracts");
        }

    }

    private void loadContractFormData(HttpServletRequest request) throws ServletException {
        try {
            TenantDAO tenantDAO = new TenantDAO();
            RoomDAO roomDAO = new RoomDAO();
            request.setAttribute("tenants", tenantDAO.getAllTenants());
            request.setAttribute("rooms", roomDAO.getAllRooms()); // Ensure RoomDAO.getAllRooms() fetches all required fields
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to load form data.");
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
