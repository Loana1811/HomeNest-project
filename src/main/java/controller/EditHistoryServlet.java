/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dao.UtilityDAO;
import dao.UtilityReadingDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.util.List;
import model.UtilityReading;

/**
 *
 * @author kloane
 */
@WebServlet("/admin/edit-history")
public class EditHistoryServlet extends HttpServlet {

    private UtilityDAO utilityDAO;

    @Override
    public void init() {
        utilityDAO = new UtilityDAO(); // Giả sử bạn đã có DAO này
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int roomId = Integer.parseInt(request.getParameter("roomId"));
        int utilityTypeId = Integer.parseInt(request.getParameter("utilityTypeId"));
        Date readingDate = Date.valueOf(request.getParameter("date"));

        //List<UtilityReading> historyList = utilityDAO.getReadingHistory(roomId, utilityTypeId, readingDate);

       // request.setAttribute("historyList", historyList);
        request.getRequestDispatcher("/admin/edit-history.jsp").forward(request, response);
    }
}
