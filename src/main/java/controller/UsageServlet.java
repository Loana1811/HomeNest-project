/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.UtilityReadingDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import model.UtilityUsageView;

@WebServlet("/admin/usage")
public class UsageServlet extends HttpServlet {

    private final UtilityReadingDAO dao = new UtilityReadingDAO();

 @Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
    try {
        String roomFilter = req.getParameter("room");  // từ input
        String monthFilter = req.getParameter("month");  // yyyy-MM

        List<UtilityUsageView> usages = dao.getAllUsages(); // tất cả usage

        if (roomFilter != null && !roomFilter.isEmpty()) {
            usages = usages.stream()
                .filter(u -> u.getRoomNumber().toLowerCase().contains(roomFilter.toLowerCase()))
                .collect(Collectors.toList());
        }

        if (monthFilter != null && !monthFilter.isEmpty()) {
            usages = usages.stream()
                .filter(u -> u.getReadingDate().toString().startsWith(monthFilter)) // yyyy-MM-DD
                .collect(Collectors.toList());
        }

        // Group lại theo block
        Map<String, List<UtilityUsageView>> grouped = new LinkedHashMap<>();
        for (UtilityUsageView u : usages) {
            String block = u.getBlockName();
            grouped.computeIfAbsent(block, k -> new ArrayList<>()).add(u);
        }

        req.setAttribute("groupedUsages", grouped);
        req.getRequestDispatcher("/admin/utility-usage-list.jsp").forward(req, resp);

    } catch (SQLException e) {
        throw new ServletException(e);
    }
}
}
