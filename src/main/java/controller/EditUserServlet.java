package controller;

import dao.UserDAO;
import dao.BlockDAO;            // THÊM DÒNG NÀY
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.Block;            // THÊM DÒNG NÀY

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;         // THÊM DÒNG NÀY

@WebServlet("/admin/editUser")
public class EditUserServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

   @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // ✅ Check quyền truy cập
        HttpSession session = request.getSession(false);
        String ctx = request.getContextPath();
        if (session == null || session.getAttribute("idUser") == null) {
            response.sendRedirect(ctx + "/error.jsp");
            return;
        }
        String userType = (String) session.getAttribute("userType");
        String roleName = (String) session.getAttribute("roleName");
        if (!"User".equalsIgnoreCase(userType) || !"Admin".equalsIgnoreCase(roleName)) {
            response.sendRedirect(ctx + "/error.jsp");
            return;
        }

        // ✅ Xử lý logic chỉnh sửa user
        String userIDStr = request.getParameter("userID");

        try {
            if (userIDStr != null && !userIDStr.trim().isEmpty()) {
                int userID = Integer.parseInt(userIDStr);
                User user = userDAO.getUserById(userID);

                if (user != null) {
                    List<Block> blockList = new BlockDAO().getAllBlocks();
                    request.setAttribute("user", user);
                    request.setAttribute("blockList", blockList);
                    request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
                    return;
                }
            }

            request.setAttribute("error", "User not found.");
            request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
        } catch (SQLException | NumberFormatException e) {
            request.setAttribute("error", "Error loading user: " + e.getMessage());
            request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
        }
    }
}
