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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
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
        if ("room-image".equals(action)) {
            // Lấy id phòng từ request
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Room ID is required");
                return;
            }
            try {
                int roomId = Integer.parseInt(idStr);
                RoomDAO roomDAO = new RoomDAO();
                Room room = roomDAO.getRoomByIds(roomId);
                
                if (room != null && room.getImagePath() != null && room.getImagePath().length > 0) {
                    // Thiết lập content type phù hợp với ảnh, ví dụ "image/jpeg"
                    response.setContentType("image/jpeg");
                    // Gửi mảng byte ảnh về client
                    response.getOutputStream().write(room.getImagePath());
                } else {
                    // Nếu không có ảnh, trả về ảnh mặc định (redirect hoặc trả lỗi 404)
                    response.sendRedirect(request.getContextPath() + "/uploads/default.jpg");
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Room ID");
            }
            return; // Dừng xử lý tiếp
        }

        // Phần xử lý các action khác như cũ
        if (action == null) {
            action = "list";
        }
        
        try {
            RoomDAO roomDAO = new RoomDAO();
            BlockDAO blockDAO = new BlockDAO();
            
            switch (action) {
                case "new":
                    request.setAttribute("room", new Room());
                    request.setAttribute("blockList", blockDAO.getAllBlocks());
                    request.setAttribute("action", "insert");
                    request.getRequestDispatcher("/admin/create_room.jsp").forward(request, response);
                    break;
                case "view":
                    int viewId = 0;
                    try {
                        viewId = Integer.parseInt(request.getParameter("id"));
                    } catch (NumberFormatException ex) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid room ID");
                        return;
                    }
                    
                    Room viewRoom = roomDAO.getRoomByIds(viewId);
                    if (viewRoom == null) {
                        request.setAttribute("error", "Room not found with ID: " + viewId);
                        response.sendRedirect("rooms?action=list");
                        return;
                    }
                    
                    request.setAttribute("room", viewRoom);
                    request.setAttribute("blockList", blockDAO.getAllBlocks());
                    request.getRequestDispatcher("/admin/view_room.jsp").forward(request, response);
                    break;
                case "delete":
                    String id = request.getParameter("id");
                    try {
                        int roomID = Integer.parseInt(id);
                        Room deletedRoom = roomDAO.getRoomByIds(roomID);
                        roomDAO.deleteRoom(roomID);
                        // Optionally cập nhật block (nếu cần)
                        // if (deletedRoom != null && deletedRoom.getBlockID() != null) {
                        //     blockDAO.updateRoomCount(deletedRoom.getBlockID());
                        // }
                        response.sendRedirect("rooms?action=list");
                    } catch (Exception e) {
                        e.printStackTrace();
                        request.setAttribute("error", "Delete failed: " + e.getMessage());
                        request.getRequestDispatcher("/admin/list_rooms.jsp").forward(request, response);
                    }
                    break;
                
                case "edit":
                    int editId = 0;
                    try {
                        editId = Integer.parseInt(request.getParameter("id"));
                    } catch (NumberFormatException ex) {
                        // Nếu id không hợp lệ, trả về lỗi hoặc chuyển hướng
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid room ID");
                        return;
                    }
                    
                    Room room = roomDAO.getRoomByIds(editId);
                    if (room == null) {
                        // Nếu không tìm thấy phòng, trả về lỗi hoặc chuyển hướng
                        request.setAttribute("error", "Room not found with ID: " + editId);
                        request.getRequestDispatcher("/admin/list_rooms.jsp").forward(request, response);
                        return;
                    }
                    
                    request.setAttribute("room", room);
                    request.setAttribute("blockList", blockDAO.getAllBlocks());
                    request.setAttribute("action", "update");
                    request.getRequestDispatcher("/admin/edit_room.jsp").forward(request, response);
                    break;
                
                case "search":
                    String roomName = request.getParameter("roomName");
                    List<Room> searchResult = roomDAO.searchByRoomName(roomName);
                    request.setAttribute("list", searchResult);
                    request.setAttribute("blockList", blockDAO.getAllBlocks());
                    request.setAttribute("roomName", roomName);
                    request.getRequestDispatcher("/admin/list_rooms.jsp").forward(request, response);
                    break;
                case "list":
                    String blockIDParam = request.getParameter("blockID");
                    
                    List<Room> roomList;
                    if (blockIDParam != null && !blockIDParam.isEmpty()) {
                        try {
                            int blockID = Integer.parseInt(blockIDParam);
                            roomList = roomDAO.getRoomsByBlock(blockIDParam);
                            request.setAttribute("blockID", blockIDParam); // để giữ selected trong dropdown
                        } catch (NumberFormatException e) {
                            // Nếu không phải số, fallback về danh sách tất cả
                            roomList = roomDAO.getAllRooms();
                        }
                    } else {
                        roomList = roomDAO.getAllRooms();
                    }
                    
                    request.setAttribute("list", roomList);
                    request.setAttribute("blockList", blockDAO.getAllBlocks());
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
        
        try {
            RoomDAO roomDAO = new RoomDAO();
            BlockDAO blockDAO = new BlockDAO();
            CategoryDAO categoryDAO = new CategoryDAO();
            
            if ("insert".equals(action)) {
                if (roomDAO.isRoomNumberExists(room.getRoomNumber())) {
                    request.setAttribute("error", "Room number already exists.");
                    request.setAttribute("room", room);
                    request.setAttribute("blockList", blockDAO.getAllBlocks());
                    request.getRequestDispatcher("/admin/create_room.jsp").forward(request, response);
                    return;
                }

                // ❌ BỎ CHECK maxRooms VÌ KHÔNG CÒN FIELD NÀY
                // ✅ Nếu bạn muốn limit số phòng sau này, thêm logic mới (nhưng hiện tại thì bỏ)
                roomDAO.addRoom(room);
//                blockDAO.updateRoomCount(blockID);
                response.sendRedirect("rooms?action=list");
                
            } else if ("edit".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Room existingRoom = roomDAO.getRoomByIds(id);
                Room roomFromForm = extractRoomFromRequest(request);

                // Giữ lại ảnh cũ nếu không upload ảnh mới
                if (roomFromForm.getImagePath() == null) {
                    roomFromForm.setImagePath(existingRoom.getImagePath());
                }
                
                roomFromForm.setRoomID(id);

                // Kiểm tra trùng số phòng
                if (roomDAO.isRoomNumberExistsForOther(roomFromForm.getRoomNumber(), id)) {
                    request.setAttribute("error", "Room number already exists.");
                    request.setAttribute("room", roomFromForm);
                    request.setAttribute("blockList", blockDAO.getAllBlocks());
                    request.setAttribute("action", "update");
                    request.getRequestDispatcher("/admin/edit_room.jsp").forward(request, response);
                    return;
                }
                
                roomDAO.updateRoom(roomFromForm);
                blockDAO.updateAvailableRooms(roomFromForm.getBlockID());
                response.sendRedirect("rooms?action=list");
                
            } else if ("delete".equals(action)) {
                String id = request.getParameter("id");
                try {
                    int roomID = Integer.parseInt(id);
                    Room deletedRoom = roomDAO.getRoomByIds(roomID);
                    roomDAO.deleteRoom(roomID);
//                    if (deletedRoom != null && deletedRoom.getBlockID() != null) {
//                        blockDAO.updateRoomCount(deletedRoom.getBlockID());
//                    }
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
        
        Integer blockID = (request.getParameter("blockID") == null || request.getParameter("blockID").isEmpty())
                ? null : Integer.parseInt(request.getParameter("blockID"));

        // ✅ Checkbox
        boolean isElectricityFree = request.getParameter("isElectricityFree") != null;
        boolean isWaterFree = request.getParameter("isWaterFree") != null;
        boolean isWifiFree = request.getParameter("isWifiFree") != null;
        boolean isTrashFree = request.getParameter("isTrashFree") != null;
        
        String description = request.getParameter("description");
        Timestamp postedDate = new Timestamp(System.currentTimeMillis());

       
        byte[] imageBytes = null;
        try {
            Part imagePart = request.getPart("image");
            if (imagePart != null && imagePart.getSize() > 0) {
                InputStream inputStream = imagePart.getInputStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int nRead;
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                imageBytes = buffer.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return new Room(
                0,
                roomNumber,
                rentPrice,
                area.floatValue(),
                location,
                status,
                blockID,
                imageBytes, // ✅ Lưu ảnh dạng byte[]
                description,
                new java.sql.Date(postedDate.getTime()),
                null, // activeContractCode
                false, // hasRecord
                false, // hasBill
                isTrashFree ? 1 : 0,
                isWifiFree ? 1 : 0,
                isWaterFree ? 1 : 0,
                isElectricityFree ? 1 : 0
        );
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
