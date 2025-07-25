/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.CategoryDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import model.Category;
import utils.DBContext;

/**
 *
 * @author Admin
 */
@WebServlet("/admin/category")
public class CategoryServlet extends HttpServlet {
    
    private static final int PAGE_SIZE = 14;


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
            out.println("<title>Servlet CategoryServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CategoryServlet at " + request.getContextPath() + "</h1>");
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
        Connection conn = null;

        try {
            DBContext db = new DBContext();
            conn = db.getConnection();
            CategoryDAO dao = new CategoryDAO();

            if ("edit".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Category category = dao.getCategoryById(id);
                request.setAttribute("category", category);
                request.getRequestDispatcher("/admin/category-edit.jsp").forward(request, response);

            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                dao.deleteCategory(id);
                response.sendRedirect("category?action=list");

            } else if ("create".equals(action)) {
                request.getRequestDispatcher("/admin/category-create.jsp").forward(request, response);

            } else { // action = list or null
                int page = 1;
                if (request.getParameter("page") != null) {
                    try {
                        page = Integer.parseInt(request.getParameter("page"));
                    } catch (NumberFormatException e) {
                        page = 1;
                    }
                }

                List<Category> list = dao.getCategoriesWithPaging(page, PAGE_SIZE);
                int totalCategories = dao.getTotalCategories();
                int totalPages = (int) Math.ceil((double) totalCategories / PAGE_SIZE);

                request.setAttribute("categoryList", list);
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);

                request.getRequestDispatcher("/admin/category-list.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            request.getRequestDispatcher("/admin/error.jsp").forward(request, response);

        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
        Connection conn = null;

        try {
            DBContext db = new DBContext();
            conn = db.getConnection();
            CategoryDAO dao = new CategoryDAO();

            if ("create".equals(action)) {
                String name = request.getParameter("categoryName");
                String desc = request.getParameter("description");

                Category category = new Category(name, desc);
                boolean success = dao.insertCategory(category);

                if (success) {
                    response.sendRedirect(request.getContextPath() + "/admin/category?action=list");
                } else {
                    request.setAttribute("error", "Failed to create category.");
                    request.getRequestDispatcher("/admin/category-create.jsp").forward(request, response);
                }

            } else if ("update".equals(action)) {
                int id = Integer.parseInt(request.getParameter("categoryID"));
                String name = request.getParameter("categoryName");
                String desc = request.getParameter("description");

                dao.updateCategory(id, name, desc);
                response.sendRedirect(request.getContextPath() + "/admin/category?action=list");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            request.getRequestDispatcher("/admin/error.jsp").forward(request, response);

        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
