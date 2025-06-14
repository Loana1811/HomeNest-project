/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.RoomDAO;
import dao.UtilityHistoryDAO;
import dao.UtilityTypeDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.UtilityHistoryView;
import model.UtilityType;

/**
 *
 * @author kloane
 */
@WebServlet("/admin/utility")
public class UtilityServlet extends HttpServlet {

    private UtilityTypeDAO dao = new UtilityTypeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            switch (action == null ? "list" : action) {
                case "create":
                    request.getRequestDispatcher("/admin/utility-create.jsp").forward(request, response);
                    break;
                case "edit":
                    int editId = Integer.parseInt(request.getParameter("id"));
                    request.setAttribute("utility", dao.getById(editId));

                    RoomDAO roomDAO = new RoomDAO();
                    List<Object[]> rooms = roomDAO.getRoomsAppliedToUtility(editId);
                    request.setAttribute("rooms", rooms); // để jsp dùng

                    request.getRequestDispatcher("/admin/utility-edit.jsp").forward(request, response);
                    break;

                case "delete":
                    int deleteId = Integer.parseInt(request.getParameter("id"));
                    dao.delete(deleteId);
                    response.sendRedirect("utility?action=list");
                    break;
                default: // list
                    List<UtilityType> utilities = dao.getAll();
                    request.setAttribute("utilities", utilities);
                    request.getRequestDispatcher("/admin/utility-list.jsp").forward(request, response);
                    break;

                case "history":
                    List<UtilityHistoryView> history = new UtilityHistoryDAO().getHistory();
                    request.setAttribute("historyList", history);
                    request.getRequestDispatcher("/admin/utility-history.jsp").forward(request, response);
                    break;

            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            String name = request.getParameter("name");
            double price = Double.parseDouble(request.getParameter("price"));
            String unit = request.getParameter("unit");

            if ("create".equals(action)) {
                dao.insert(new UtilityType(0, name, unit, price));
            } else if ("update".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                dao.update(new UtilityType(id, name, unit, price));
            }

            response.sendRedirect("utility?action=list");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
