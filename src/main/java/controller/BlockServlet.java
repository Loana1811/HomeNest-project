/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.BlockDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.Block;

/**
 *
 * @author Admin
 */
@WebServlet("/admin/blocks")
public class BlockServlet extends HttpServlet {

    BlockDAO blockDAO = new BlockDAO();

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
            out.println("<title>Servlet BlockServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet BlockServlet at " + request.getContextPath() + "</h1>");
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
        if (action == null) action = "list";

        switch (action) {
            case "new":
                request.setAttribute("block", new Block());
                request.setAttribute("action", "insert");
                request.getRequestDispatcher("/admin/add_block.jsp").forward(request, response);
                break;

            case "list":
            default:
                List<Block> blocks = blockDAO.getAllBlocks();
                request.setAttribute("blockList", blocks);
                request.getRequestDispatcher("/admin/list_blocks.jsp").forward(request, response);
                break;
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

        if ("insert".equals(action)) {
            Block block = extractBlockFromRequest(request);

            if (block.getBlockName() == null || block.getBlockName().trim().isEmpty()) {
                request.setAttribute("message", "Block name is required.");
                request.setAttribute("alertType", "error");
                forwardBackToForm(request, response, block);
                return;
            }

            if (blockDAO.isBlockNameExists(block.getBlockName())) {
                request.setAttribute("message", "Block name already exists.");
                request.setAttribute("alertType", "error");
                forwardBackToForm(request, response, block);
                return;
            }

            boolean success = blockDAO.addBlock(block);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/blocks?action=list");
            } else {
                request.setAttribute("message", "Failed to insert block. Please try again.");
                request.setAttribute("alertType", "error");
                forwardBackToForm(request, response, block);
            }
        }
    }

    private Block extractBlockFromRequest(HttpServletRequest request) {
        String name = request.getParameter("blockName");
        int roomCount = parseIntOrDefault(request.getParameter("roomCount"), 0);
        int availableRooms = roomCount; // mặc định available = roomCount
        String status = "Available"; // hoặc lấy từ form nếu có

        Block block = new Block();
        block.setBlockName(name);
        block.setRoomCount(roomCount);
        block.setAvailableRooms(availableRooms);
        block.setBlockStatus(status);

        return block;
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private void forwardBackToForm(HttpServletRequest request, HttpServletResponse response, Block block)
            throws ServletException, IOException {
        request.setAttribute("block", block);
        request.setAttribute("action", "insert");
        request.getRequestDispatcher("/admin/add_block.jsp").forward(request, response);
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
