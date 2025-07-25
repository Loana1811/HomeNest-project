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
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "new":
                request.setAttribute("block", new Block());
                request.setAttribute("action", "insert");
                request.getRequestDispatcher("/admin/add_block.jsp").forward(request, response);
                break;

            case "edit":
                try {
                int idEdit = Integer.parseInt(request.getParameter("id"));
                Block blockEdit = blockDAO.getBlockById(idEdit);
                if (blockEdit != null) {
                    request.setAttribute("block", blockEdit);
                    request.setAttribute("action", "update");
                    request.getRequestDispatcher("/admin/edit_block.jsp").forward(request, response);
                } else {
                    response.sendRedirect("blocks?action=list");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.sendRedirect("blocks?action=list");
            }
            break;

            case "delete":
                try {
                int idDelete = Integer.parseInt(request.getParameter("id"));
                blockDAO.deleteBlock(idDelete);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            response.sendRedirect("blocks?action=list");
            break;

            case "list":
            default:
                List<Block> blockList = blockDAO.getAllBlocks();
                request.setAttribute("blockList", blockList);
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

        try {
            if ("insert".equals(action)) {
                Block block = extractBlockFromRequest(request);

                // Validation
                if (block.getBlockName() == null || block.getBlockName().trim().isEmpty()) {
                    request.setAttribute("message", "Block name is required.");
                    request.setAttribute("alertType", "error");
                    forwardBackToForm(request, response, block, "insert");
                    return;
                }
                if (block.getMaxRooms() <= 0) {
                    request.setAttribute("message", "Max number of rooms must be a number greater than 0.");
                    request.setAttribute("alertType", "error");
                    forwardBackToForm(request, response, block, "insert");
                    return;
                }

                boolean success = blockDAO.addBlock(block);
                if (success) {
                    request.setAttribute("message", "Block added successfully.");
                    request.setAttribute("alertType", "success");
                } else {
                    request.setAttribute("message", "Block name already exists.");  
                    request.setAttribute("alertType", "error");
                    forwardBackToForm(request, response, block, "insert");
                    return;
                }

                response.sendRedirect("blocks?action=list");

            } else if ("update".equals(action)) {
                Block block = extractBlockFromRequest(request);
                blockDAO.updateBlock(block);
                response.sendRedirect("blocks?action=list");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "An error occurred: " + e.getMessage());
            request.setAttribute("alertType", "error");
            forwardBackToForm(request, response, null, "insert");
        }
    }

    private Block extractBlockFromRequest(HttpServletRequest request) {
        Block block = new Block();

        // 👇 Thêm phần này để lấy BlockID khi cập nhật
        try {
            String idStr = request.getParameter("id");
            if (idStr != null && !idStr.isEmpty()) {
                block.setBlockID(Integer.parseInt(idStr));
            }
        } catch (NumberFormatException e) {
            block.setBlockID(0); // hoặc xử lý khác nếu cần
        }

        block.setBlockName(request.getParameter("blockName"));

        try {
            String maxRoomsStr = request.getParameter("maxRooms");
            if (maxRoomsStr != null && !maxRoomsStr.isEmpty()) {
                block.setMaxRooms(Integer.parseInt(maxRoomsStr));
            } else {
                block.setMaxRooms(0);
            }
        } catch (NumberFormatException e) {
            block.setMaxRooms(0);
        }

        return block;
    }

    private void forwardBackToForm(HttpServletRequest request, HttpServletResponse response, Block block, String action)
            throws ServletException, IOException {
        if (block != null) {
            request.setAttribute("block", block);
        }
        request.setAttribute("action", action);
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
