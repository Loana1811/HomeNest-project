/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.BlockDAO;
import dao.CategoryDAO;
import dao.RoomDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import model.Block;
import model.Room;
import utils.DBContext;

/**
 *
 * @author Admin
 */
@WebServlet(name = "RoomServlet", urlPatterns = {"/admin/rooms"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 15 // 15MB
)
public class RoomServlet extends HttpServlet {

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
            out.println("<title>Servlet RoomServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RoomServlet at " + request.getContextPath() + "</h1>");
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

        try  {
            RoomDAO roomDAO = new RoomDAO();
            BlockDAO blockDAO = new BlockDAO();
            CategoryDAO categoryDAO = new CategoryDAO();

            switch (action) {
                case "new":
                    request.setAttribute("room", new Room());
                    request.setAttribute("blockList", blockDAO.getAllBlocks());
                    request.setAttribute("categoryList", categoryDAO.getAllCategories());
                    request.setAttribute("action", "insert");
                    request.getRequestDispatcher("/admin/create_room.jsp").forward(request, response);
                    break;

                case "edit":
                    int editId = Integer.parseInt(request.getParameter("id"));
                    Room room = roomDAO.getRoomByIds(editId);
                    request.setAttribute("room", room);
                    request.setAttribute("blockList", blockDAO.getAllBlocks());
                    request.setAttribute("categoryList", categoryDAO.getAllCategories());
                    request.setAttribute("action", "update");
                    request.getRequestDispatcher("/admin/edit_room.jsp").forward(request, response);
                    break;

                case "delete":
                    int deleteId = Integer.parseInt(request.getParameter("id"));
                    Room deletedRoom = roomDAO.getRoomByIds(deleteId);
                    if (deletedRoom != null) {
                        roomDAO.deleteRoom(deleteId);
                        blockDAO.updateRoomCount(deletedRoom.getBlockID());
                    } else {
                        request.setAttribute("errorMessage", "Room not found.");
                        request.getRequestDispatcher("error.jsp").forward(request, response);
                        return;
                    }
                    response.sendRedirect("rooms?action=list");
                    break;
                case "search":
                    String roomName = request.getParameter("roomName");
                    List<Room> searchResult = roomDAO.searchByRoomName(roomName);

                    request.setAttribute("list", searchResult);
                    request.setAttribute("blockList", blockDAO.getAllBlocks());
                    request.setAttribute("categoryList", categoryDAO.getAllCategories());
                    request.setAttribute("roomName", roomName); // để giữ lại giá trị trong ô tìm kiếm
                    request.getRequestDispatcher("/admin/list_rooms.jsp").forward(request, response);
                    break;
                case "list":
                default:
                    String selectedCategoryID = request.getParameter("categoryID");
                    String selectedBlockID = request.getParameter("blockID");

                    List<Room> roomList = roomDAO.filterRoomsByCategoryAndBlock(selectedCategoryID, selectedBlockID);
                    request.setAttribute("list", roomList);
                    request.setAttribute("blockList", blockDAO.getAllBlocks());
                    request.setAttribute("categoryList", categoryDAO.getAllCategories());
                    request.setAttribute("categoryID", selectedCategoryID);
                    request.setAttribute("blockID", selectedBlockID);
                    request.getRequestDispatcher("/admin/list_rooms.jsp").forward(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error handling request: " + e.getMessage());
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
        Room room = extractRoomFromRequest(request);
        Integer blockID = room.getBlockID();

        try  {
            RoomDAO roomDAO = new RoomDAO();
            BlockDAO blockDAO = new BlockDAO();
            CategoryDAO categoryDAO = new CategoryDAO();

            if ("insert".equals(action)) {
                if (roomDAO.isRoomNumberExists(room.getRoomNumber())) {
                    request.setAttribute("error", "Room number already exists.");
                    request.setAttribute("room", room);
request.setAttribute("blockList", blockDAO.getAllBlocks());
                    request.setAttribute("categoryList", categoryDAO.getAllCategories());
                    request.getRequestDispatcher("/admin/create_room.jsp").forward(request, response);
                    return;
                }

                if (blockID != null) {
                    Block block = blockDAO.getBlockById(blockID);
                    if (block != null && block.getRoomCount() >= block.getMaxRooms()) {
                        request.setAttribute("error", "This block is already full. Please choose another block.");
                        request.setAttribute("room", room);
                        request.setAttribute("blockList", blockDAO.getAllBlocks());
                        request.setAttribute("categoryList", categoryDAO.getAllCategories());
                        request.getRequestDispatcher("/admin/create_room.jsp").forward(request, response);
                        return;
                    }
                }

                roomDAO.addRoom(room);
                blockDAO.updateRoomCount(blockID);
                response.sendRedirect("rooms?action=list");
            } else if ("edit".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                room.setRoomID(id);

                if (roomDAO.isRoomNumberExistsForOther(room.getRoomNumber(), id)) {
                    request.setAttribute("error", "Room number already exists.");
                    request.setAttribute("room", room);
                    request.setAttribute("blockList", blockDAO.getAllBlocks());
                    request.setAttribute("categoryList", categoryDAO.getAllCategories());
                    request.setAttribute("action", "update");
                    request.getRequestDispatcher("/admin/edit_room.jsp").forward(request, response);
                    return;
                }

                roomDAO.updateRoom(room);

                response.sendRedirect("rooms?action=list");
            } else if ("delete".equals(action)) {
                String id = request.getParameter("id");
                try {
                    int roomID = Integer.parseInt(id);
                    roomDAO.deleteRoom(roomID);
                    response.sendRedirect("rooms?action=list");
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("error", "Delete failed: " + e.getMessage());
                    request.getRequestDispatcher("list_rooms.jsp").forward(request, response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error handling request: " + e.getMessage());
        }
    }

    private Room extractRoomFromRequest(HttpServletRequest request) {
        String roomNumber = request.getParameter("roomNumber");
double rentPrice = Double.parseDouble(request.getParameter("rentPrice"));
        String areaStr = request.getParameter("area");
        Double area = (areaStr == null || areaStr.isEmpty()) ? 0.0 : Double.parseDouble(areaStr);
        String location = request.getParameter("location");
        String status = request.getParameter("status");

        Integer blockID = (request.getParameter("blockID") == null || request.getParameter("blockID").isEmpty()) ? null : Integer.parseInt(request.getParameter("blockID"));
        Integer categoryID = (request.getParameter("categoryID") == null || request.getParameter("categoryID").isEmpty()) ? null : Integer.parseInt(request.getParameter("categoryID"));

        String[] highlightsArr = request.getParameterValues("highlights");
        String highlights = (highlightsArr != null) ? String.join(",", highlightsArr) : null;

        String description = request.getParameter("description");
        Timestamp postedDate = new Timestamp(System.currentTimeMillis());

        // Xử lý ảnh
        // Xử lý ảnh upload vào thư mục /uploads/
        String existingImagePath = request.getParameter("existingImagePath");
        String imagePath = existingImagePath; // fallback nếu không có ảnh mới

        try {
            Part imagePart = request.getPart("image");
            if (imagePart != null && imagePart.getSize() > 0) {
                String fileName = extractFileName(imagePart);

                // Nếu muốn tránh trùng tên file, có thể thêm thời gian hoặc UUID
                String uniqueFileName = System.currentTimeMillis() + "_" + fileName;

                String uploadDir = getServletContext().getRealPath("/uploads");
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String fullPath = uploadDir + File.separator + uniqueFileName;
                imagePart.write(fullPath);

                // Đây là đường dẫn sẽ lưu trong DB (không có dấu "/" đầu tiên!)
                imagePath = "uploads/" + uniqueFileName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Room(0, roomNumber, (float) rentPrice, area.floatValue(), location, status,
                blockID, categoryID, highlights, imagePath, description, new java.sql.Date(postedDate.getTime()));
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String token : contentDisp.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 2, token.length() - 1);
            }
        }
        return null;
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
