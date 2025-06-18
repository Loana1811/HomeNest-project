/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.contract;

import DAO.RoomDAO;
import DAO.ContractDAO;
import DAO.TenantDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.util.List;
import Model.Contract;
import Model.Customer;
import Model.Room;
import Model.Tenant;

/**
 *
 * @author Admin
 */
@WebServlet(name = "ContractServlet", urlPatterns = {"/Contracts"})
public class ContractServlet extends HttpServlet {

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
        String action = request.getParameter("action");

        if (action == null || action.equals("list")) {
            ContractDAO contractDAO = new ContractDAO();
            List<Contract> contracts = contractDAO.getAllContracts();
            request.setAttribute("contracts", contracts);
            request.getRequestDispatcher("/admin/listContract.jsp").forward(request, response);

        } else if (action.equals("create")) {
            TenantDAO tenantDAO = new TenantDAO();
            RoomDAO roomDAO = new RoomDAO();

            List<Tenant> tenants = tenantDAO.getAllTenants();
            List<Room> rooms = roomDAO.getAvailableRooms();

            request.setAttribute("tenants", tenants);
            request.setAttribute("rooms", rooms);
            request.getRequestDispatcher("/admin/createContract.jsp").forward(request, response);

        } else if (action.equals("edit")) {
            int id = Integer.parseInt(request.getParameter("id"));

            ContractDAO contractDAO = new ContractDAO();
            Contract contract = contractDAO.getContractById(id);

            RoomDAO roomDAO = new RoomDAO();
            List<Room> rooms = roomDAO.getAvailableRoomsIncludingCurrent(contract.getRoomId());

            request.setAttribute("rooms", rooms);
            request.setAttribute("contract", contract);
            request.getRequestDispatcher("/admin/editContract.jsp").forward(request, response);

        } else if ("view".equals(action)) {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                response.sendRedirect("Contracts");
                return;
            }

            int contractId = Integer.parseInt(idParam);

            ContractDAO contractDAO = new ContractDAO();
            TenantDAO tenantDAO = new TenantDAO();
            RoomDAO roomDAO = new RoomDAO();

            Contract contract = contractDAO.getContractById(contractId);
            Tenant tenant = tenantDAO.getTenantById(contract.getTenantId());
            Room room = roomDAO.getRoomById(contract.getRoomId());

            // Lấy thông tin khách hàng từ CustomerID trong Tenant
            Customer customer = tenantDAO.getCustomerById(tenant.getCustomerID());

            request.setAttribute("contract", contract);
            request.setAttribute("tenant", tenant);
            request.setAttribute("room", room);
            request.setAttribute("customer", customer); // thêm dòng này

            request.getRequestDispatcher("/admin/viewDetail.jsp").forward(request, response);
        } else if ("history".equals(action)) {
            String tenantIdParam = request.getParameter("tenantId");

            if (tenantIdParam == null || tenantIdParam.trim().isEmpty()) {
                response.sendRedirect("Contracts?action=list");
                return;
            }

            int tenantId = Integer.parseInt(tenantIdParam);

            ContractDAO contractDAO = new ContractDAO();
            List<Contract> historyContracts = contractDAO.getContractHistoryByTenantId(tenantId);

            request.setAttribute("contracts", historyContracts);
            request.getRequestDispatcher("/admin/historyContract.jsp").forward(request, response);
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
        String action = request.getParameter("action");

        if ("create".equals(action)) {
            try {
                String tenantIdStr = request.getParameter("tenantId");
                String roomIdStr = request.getParameter("roomId");

                if (tenantIdStr == null || tenantIdStr.isEmpty() || roomIdStr == null || roomIdStr.isEmpty()) {
                    request.setAttribute("error", "Please select both Tenant and Room.");
                    // forward lại về form
                    request.getRequestDispatcher("/createContract.jsp").forward(request, response);
                    return;
                }

                int tenantId = Integer.parseInt(tenantIdStr);
                int roomId = Integer.parseInt(roomIdStr);

                Date startDate = Date.valueOf(request.getParameter("startDate"));
                String endDateStr = request.getParameter("endDate");
                Date endDate = (endDateStr == null || endDateStr.isEmpty()) ? null : Date.valueOf(endDateStr);

                ContractDAO contractDAO = new ContractDAO();
                boolean success = contractDAO.addContract(tenantId, roomId, startDate, endDate);

                if (success) {
                    response.sendRedirect(request.getContextPath() + "/Contracts?action=list");
                } else {
                    request.setAttribute("error", "Failed to create contract.");
                    doGet(request, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Invalid input or system error.");
                doGet(request, response);
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
                contract.setContractstatus(status);

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

            response.sendRedirect("Contracts");
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
