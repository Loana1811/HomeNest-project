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
import model.UtilityUsageView;

@WebServlet("/admin/usage")
public class UsageServlet extends HttpServlet {

    private final UtilityReadingDAO dao = new UtilityReadingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<UtilityUsageView> usages = dao.getAllUsages();

            // ✅ Group by BlockName
            Map<String, List<UtilityUsageView>> grouped = new LinkedHashMap<>();
            for (UtilityUsageView u : usages) {
                String block = u.getBlockName(); // đảm bảo đã có BlockName trong DAO
                if (!grouped.containsKey(block)) {
                    grouped.put(block, new ArrayList<UtilityUsageView>());
                }
                grouped.get(block).add(u);
            }

            req.setAttribute("groupedUsages", grouped);
            req.getRequestDispatcher("/admin/record-usage.jsp").forward(req, resp);


        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
