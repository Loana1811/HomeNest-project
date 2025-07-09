/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.ExpenseCategoryDAO;
import dao.ExpenseDAO;
import dao.RevenueCategoryDAO;
import dao.StatisticalDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import model.Expense;
import model.ExpenseCategory;
import model.Revenue;
import model.RevenueCategory;
import dao.RevenueDAO;
import java.io.ByteArrayOutputStream;

/**
 *
 * @author Admin
 */
@WebServlet("/admin/statistical")
public class StatisticalServlet extends HttpServlet {

    private final ExpenseCategoryDAO expenseCategoryDAO = new ExpenseCategoryDAO();
    private final RevenueCategoryDAO revenueCategoryDAO = new RevenueCategoryDAO();
    private final StatisticalDAO statisticalDAO = new StatisticalDAO();
    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private final RevenueDAO revenueDAO = new RevenueDAO();

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
            out.println("<title>Servlet StatisticalServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet StatisticalServlet at " + request.getContextPath() + "</h1>");
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
        try {
            List<ExpenseCategory> expenseCategories = expenseCategoryDAO.getExpenseCategories();
            List<RevenueCategory> revenueCategories = revenueCategoryDAO.getRevenueCategories();

            List<Expense> expenseItems = expenseDAO.getAll();
            List<Revenue> revenueItems = revenueDAO.getAll();

            List<Object> transactions = statisticalDAO.getAllTransactionsMerged();
            BigDecimal totalDebt = statisticalDAO.getTotalDebt();

            handleStatistical(request);

            request.setAttribute("expenseCategories", expenseCategories);
            request.setAttribute("revenueCategories", revenueCategories);
            request.setAttribute("expenseItems", expenseItems);
            request.setAttribute("revenueItems", revenueItems);
            request.setAttribute("transactions", transactions);
            request.setAttribute("totalDebt", totalDebt);

            request.getRequestDispatcher("/admin/statistical.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Lỗi hệ thống: " + e.getMessage());
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String target = request.getParameter("target");   // expenseCategory | revenueCategory | expenseItem | revenueItem
        String action = request.getParameter("action");   // add

        response.setContentType("application/json;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {

            // Thêm danh mục chi
            if ("expenseCategory".equals(target) && "add".equals(action)) {
                String name = request.getParameter("name");
                if (name != null && !name.trim().isEmpty()) {
                    boolean success = expenseCategoryDAO.addExpenseCategory(name.trim());
                    if (success) {
                        out.print("{\"status\":\"success\"}");
                    } else {
                        response.setStatus(500);
                        out.print("{\"status\":\"error\",\"message\":\"Không thể thêm danh mục chi\"}");
                    }
                } else {
                    response.setStatus(400);
                    out.print("{\"status\":\"error\",\"message\":\"Tên danh mục không hợp lệ\"}");
                }
                return;
            }

            // Thêm danh mục thu
            if ("revenueCategory".equals(target) && "add".equals(action)) {
                String name = request.getParameter("name");
                if (name != null && !name.trim().isEmpty()) {
                    boolean success = revenueCategoryDAO.addRevenueCategory(name.trim());
                    if (success) {
                        out.print("{\"status\":\"success\"}");
                    } else {
                        response.setStatus(500);
                        out.print("{\"status\":\"error\",\"message\":\"Không thể thêm danh mục thu\"}");
                    }
                } else {
                    response.setStatus(400);
                    out.print("{\"status\":\"error\",\"message\":\"Tên danh mục không hợp lệ\"}");
                }
                return;
            }

            // Thêm phiếu thu
            if ("revenueItem".equals(target) && "add".equals(action)) {
                try {
                    Revenue revenue = new Revenue();
                    revenue.setRevenueName(request.getParameter("revenueName"));
                    revenue.setAmount(Double.parseDouble(request.getParameter("amount")));
                    revenue.setRevenueCategoryID(Integer.parseInt(request.getParameter("revenueCategoryID")));
                    revenue.setRevenueDate(java.sql.Date.valueOf(request.getParameter("revenueDate")).toLocalDate());
                    revenue.setPayer(request.getParameter("payer"));
                    revenue.setNotes(request.getParameter("notes"));

                    boolean success = revenueDAO.addRevenue(revenue);
                    if (success) {
                        out.print("{\"status\":\"success\"}");
                    } else {
                        response.setStatus(500);
                        out.print("{\"status\":\"error\",\"message\":\"Không thể thêm phiếu thu\"}");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    response.setStatus(500);
                    out.print("{\"status\":\"error\",\"message\":\"Lỗi hệ thống hoặc dữ liệu không hợp lệ\"}");
                }
                return;
            }

            // Thêm phiếu chi
            if ("expenseItem".equals(target) && "add".equals(action)) {
                try {
                    Expense expense = new Expense();
                    expense.setExpenseName(request.getParameter("expenseName"));
                    expense.setAmount(Double.parseDouble(request.getParameter("amount")));
                    expense.setExpenseDate(request.getParameter("expenseDate")); // String yyyy-MM-dd
                    expense.setPayer(request.getParameter("payer"));
                    expense.setNotes(request.getParameter("notes"));
                    int categoryId = Integer.parseInt(request.getParameter("expenseCategoryID"));

                    boolean success = expenseDAO.addExpenseItem(expense, categoryId);
                    if (success) {
                        out.print("{\"status\":\"success\"}");
                    } else {
                        response.setStatus(500);
                        out.print("{\"status\":\"error\",\"message\":\"Không thể thêm phiếu chi\"}");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    response.setStatus(500);
                    out.print("{\"status\":\"error\",\"message\":\"Lỗi hệ thống hoặc dữ liệu không hợp lệ\"}");
                }
                return;
            }

            response.setStatus(400);
            out.print("{\"status\":\"error\",\"message\":\"Dữ liệu không hợp lệ\"}");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Lỗi hệ thống: " + e.getMessage());
        }

    }

    private void handleStatistical(HttpServletRequest request) throws Exception {
        String type = request.getParameter("type");
        String valueParam = request.getParameter("value");
        String yearParam = request.getParameter("year");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");

        if (type == null || type.isEmpty()) {
            type = "month";
        }

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        switch (type) {
            case "month":
                int month = (valueParam == null || valueParam.isEmpty()) ? LocalDate.now().getMonthValue() : Integer.parseInt(valueParam);
                int year = (yearParam == null || yearParam.isEmpty()) ? LocalDate.now().getYear() : Integer.parseInt(yearParam);
                totalIncome = statisticalDAO.getIncomeByMonth(month, year);
                totalExpense = statisticalDAO.getExpenseByMonth(month, year);
                request.setAttribute("month", month);
                request.setAttribute("year", year);
                break;

            case "quarter":
                int quarter = (valueParam == null || valueParam.isEmpty())
                        ? (LocalDate.now().getMonthValue() - 1) / 3 + 1
                        : Integer.parseInt(valueParam);
                year = (yearParam == null || yearParam.isEmpty()) ? LocalDate.now().getYear() : Integer.parseInt(yearParam);
                totalIncome = statisticalDAO.getIncomeByQuarter(quarter, year);
                totalExpense = statisticalDAO.getExpenseByQuarter(quarter, year);
                request.setAttribute("quarter", quarter);
                request.setAttribute("year", year);
                break;

            case "year":
                year = (yearParam == null || yearParam.isEmpty()) ? LocalDate.now().getYear() : Integer.parseInt(yearParam);
                totalIncome = statisticalDAO.getIncomeByYear(year);
                totalExpense = statisticalDAO.getExpenseByYear(year);
                request.setAttribute("year", year);
                break;

            case "range":
                if (fromDate == null || fromDate.isEmpty() || toDate == null || toDate.isEmpty()) {
                    LocalDate now = LocalDate.now();
                    toDate = now.toString();
                    fromDate = now.minusDays(6).toString();
                }
                totalIncome = statisticalDAO.getIncomeByDateRange(fromDate, toDate);
                totalExpense = statisticalDAO.getExpenseByDateRange(fromDate, toDate);
                request.setAttribute("fromDate", fromDate);
                request.setAttribute("toDate", toDate);
                break;

            default:
                throw new IllegalArgumentException("Kiểu thống kê không hợp lệ: " + type);
        }

        BigDecimal profit = totalIncome.subtract(totalExpense);

        request.setAttribute("type", type);
        request.setAttribute("totalIncome", totalIncome);
        request.setAttribute("totalExpense", totalExpense);
        request.setAttribute("profit", profit);

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
