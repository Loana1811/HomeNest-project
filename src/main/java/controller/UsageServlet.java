// === File: UsageServlet.java ===
package controller;

import dao.ContractDAO;
import dao.UtilityReadingDAO;
import dao.UtilityTypeDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import model.Contract;
import model.User;
import model.UtilityType;
import model.UtilityUsageView;

@WebServlet("/admin/usage")
public class UsageServlet extends HttpServlet {
    private final UtilityReadingDAO readingDAO = new UtilityReadingDAO();
    private final ContractDAO contractDAO = new ContractDAO();
    private final UtilityTypeDAO utilityTypeDAO = new UtilityTypeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            User currentUser = (User) req.getSession().getAttribute("currentUser");
            if (currentUser == null) {
                resp.sendRedirect(req.getContextPath() + "/Login");
                return;
            }

            String roomFilter = req.getParameter("roomFilter");
            String monthFilter = req.getParameter("readingMonth");
            req.setAttribute("readingMonth", monthFilter);

            List<UtilityUsageView> usages = readingDAO.getAllUsages();

            if (roomFilter != null && !roomFilter.isEmpty()) {
                usages = usages.stream()
                        .filter(u -> u.getRoomNumber().toLowerCase().contains(roomFilter.toLowerCase()))
                        .collect(Collectors.toList());
            }

            if (monthFilter != null && !monthFilter.isEmpty()) {
                usages = usages.stream()
                        .filter(u -> u.getReadingDate().toString().startsWith(monthFilter))
                        .collect(Collectors.toList());
            }

            // Gợi ý ngày bắt đầu theo hợp đồng cho room đầu tiên (nếu có)
            if (!usages.isEmpty()) {
                int roomId = usages.get(0).getRoomId();
                Contract contract = contractDAO.getActiveContractByRoom(roomId);
                if (contract != null) {
                    Date contractStart = contract.getStartDate();
                    req.setAttribute("contractStartDate", contractStart);
                }
            }

            Map<String, List<UtilityUsageView>> grouped = new LinkedHashMap<>();
            for (UtilityUsageView u : usages) {
                grouped.computeIfAbsent(u.getBlockName(), k -> new ArrayList<>()).add(u);
            }
            req.setAttribute("groupedUsages", grouped);

            int roleId = currentUser.getRole().getRoleID();
            if (roleId == 1) {
                req.getRequestDispatcher("/admin/utility-usage-list.jsp").forward(req, resp);
            } else if (roleId == 2) {
                req.getRequestDispatcher("/manager/utility-usage-list.jsp").forward(req, resp);
            } else {
                resp.sendRedirect(req.getContextPath() + "/unauthorized.jsp");
            }

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
