package controller;

import dao.IncurredFeeTypeDAO;
import dao.UtilityHistoryDAO;
import model.IncurredFeeType;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;

@WebServlet("/admin/feeType")
public class IncurredFeeTypeServlet extends HttpServlet {

    private IncurredFeeTypeDAO feeTypeDAO = new IncurredFeeTypeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        try {
            if ("editModal".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                IncurredFeeType feeType = feeTypeDAO.getById(id);
                req.setAttribute("feeType", feeType);
                req.getRequestDispatcher("/admin/incurredFeeType-edit.jsp").forward(req, resp);
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                feeTypeDAO.delete(id);
                resp.sendRedirect(req.getContextPath() + "/admin/utility?action=list");
            } else {
                List<IncurredFeeType> feeList = feeTypeDAO.getAll();
                req.setAttribute("feeList", feeList);
                req.getRequestDispatcher("/admin/utility-list.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Error: " + e.getMessage());
            req.getRequestDispatcher("/admin/utility-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        try {
            if ("create".equals(action)) {
                String name = req.getParameter("feeName");
                String amountStr = req.getParameter("defaultAmount");

                BigDecimal amount = new BigDecimal(amountStr.replace(",", ""));

                IncurredFeeType feeType = new IncurredFeeType(0, name, amount);
                feeTypeDAO.insert(feeType);
                resp.sendRedirect(req.getContextPath() + "/admin/utility?action=list");

          } else if  ("update".equals(action)) {
            int id = Integer.parseInt(req.getParameter("feeTypeId"));
            String name = req.getParameter("feeName");
            String amountStr = req.getParameter("defaultAmount");

            // Validate tên
            if (name == null || name.trim().length() < 3 || name.trim().matches("^\\d+$")) {
                IncurredFeeType old = feeTypeDAO.getById(id);
                req.setAttribute("feeType", old);
                req.setAttribute("error", "❌ Invalid fee name.");
                req.getRequestDispatcher("/admin/incurredFeeType-edit.jsp").forward(req, resp);
                return;
            }

            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr.replace(",", ""));
                if (amount.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                IncurredFeeType old = feeTypeDAO.getById(id);
                req.setAttribute("feeType", old);
                req.setAttribute("error", "❌ Amount must be non-negative.");
                req.getRequestDispatcher("/admin/incurredFeeType-edit.jsp").forward(req, resp);
                return;
            }

            // Get old data
            IncurredFeeType old = feeTypeDAO.getById(id);

            // 👇 Thêm dòng này để đảm bảo insert được gọi
            System.out.println("🛠 So sánh giá: OLD = " + old.getDefaultAmount() + " | NEW = " + amount);

            // If giá thay đổi → insert vào bảng lịch sử
            if (old.getDefaultAmount().compareTo(amount) != 0) {
                new UtilityHistoryDAO().insertHistoryForIncurredFee(
                        id,
                        name,
                        old.getDefaultAmount().doubleValue(),
                        amount.doubleValue(),
                        "admin", // có thể sửa thành lấy từ session
                        new java.sql.Date(System.currentTimeMillis())
                );
                System.out.println("✅ Đã insert lịch sử cho incurred fee: " + name);
            }

            // Cập nhật bảng chính
            IncurredFeeType feeType = new IncurredFeeType(id, name, amount);
            feeTypeDAO.update(feeType);

            resp.sendRedirect(req.getContextPath() + "/admin/utility?action=list");
        }

    } catch (Exception e) {
        e.printStackTrace();
        resp.sendRedirect(req.getContextPath() + "/admin/utility?action=list&error=" + URLEncoder.encode("Error: " + e.getMessage(), "UTF-8"));
    }

    }
}
        
