/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.IncurredFeeTypeDAO;
import dao.UtilityTypeDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;
import model.IncurredFeeType;
import model.UtilityType;
import utils.DBContext;

/**
 *
 * @author kloane
 */
@WebServlet("/admin/feeType")
public class IncurredFeeTypeServlet extends HttpServlet {

    private IncurredFeeTypeDAO feeTypeDAO = new IncurredFeeTypeDAO();
    private UtilityTypeDAO utilityTypeDAO = new UtilityTypeDAO();

   

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
            // List all
            List<UtilityType> systemList = utilityTypeDAO.getAll();
            List<IncurredFeeType> feeList = feeTypeDAO.getAll();

            req.setAttribute("systemList", systemList);
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
            } else if ("update".equals(action)) {
                int id = Integer.parseInt(req.getParameter("feeTypeId"));
                String name = req.getParameter("feeName");
                String amountStr = req.getParameter("defaultAmount");
                BigDecimal amount = new BigDecimal(amountStr.replace(",", ""));
                IncurredFeeType feeType = new IncurredFeeType(id, name, amount);
                feeTypeDAO.update(feeType);

            }
//            resp.sendRedirect(req.getContextPath() + "/admin/utility?action=list");
        } catch (Exception e) {
//            req.setAttribute("error", "Error: " + e.getMessage());
            // Sau khi thêm/sửa/xóa
            resp.sendRedirect(req.getContextPath() + "/admin/utility?action=list&error=" + URLEncoder.encode("Error: " + e.getMessage(), "UTF-8"));

        }
    }
}
