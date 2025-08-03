/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import static com.microsoft.sqlserver.jdbc.StringUtils.isNumeric;
import com.sun.rowset.internal.Row;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

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
        HttpSession session = request.getSession(false);
        Integer roleId = (session != null) ? (Integer) session.getAttribute("roleID") : null;
        if (roleId == null || roleId != 1) {
            response.sendRedirect(request.getContextPath() + "/error.jsp");
            return;
        }

        try {
            // Lấy tham số lọc
            String filterType = request.getParameter("filterType");
            if (filterType == null || filterType.isEmpty()) {
                filterType = "all";
            }

            String filterCategoryId = request.getParameter("filterCategoryId"); // danh mục thu
            if (filterCategoryId == null || filterCategoryId.isEmpty()) {
                filterCategoryId = "all";
            }

            String filterExpenseCategoryId = request.getParameter("filterExpenseCategoryId"); // danh mục chi
            if (filterExpenseCategoryId == null || filterExpenseCategoryId.isEmpty()) {
                filterExpenseCategoryId = "all";
            }

            // Nếu chưa chọn loại nhưng đã lọc danh mục → tự xác định loại
            if ("all".equalsIgnoreCase(filterType)) {
                if (!"all".equalsIgnoreCase(filterCategoryId) && "all".equalsIgnoreCase(filterExpenseCategoryId)) {
                    filterType = "income"; // chỉ lọc danh mục thu
                } else if ("all".equalsIgnoreCase(filterCategoryId) && !"all".equalsIgnoreCase(filterExpenseCategoryId)) {
                    filterType = "expense"; // chỉ lọc danh mục chi
                }
            }

            // Xử lý khoảng thời gian thống kê
            Map<String, LocalDate> range = handleStatistical(request);
            LocalDate startDate = range.get("start");
            LocalDate endDate = range.get("end");

            // Lấy danh mục thu và chi để hiển thị filter
            List<ExpenseCategory> expenseCategories = expenseCategoryDAO.getExpenseCategories();
            List<RevenueCategory> revenueCategories = revenueCategoryDAO.getRevenueCategories();

            // Danh sách chứa phiếu thu và chi được lọc
            List<Expense> expenseItems = new ArrayList<>();
            List<Revenue> revenueItems = new ArrayList<>();

            switch (filterType.toLowerCase()) {
                case "income":
                    // Lọc phiếu thu theo danh mục thu
                    List<Revenue> allRevenues = revenueDAO.getRevenuesBetween(startDate, endDate);
                    for (Revenue r : allRevenues) {
                        if ("all".equalsIgnoreCase(filterCategoryId)
                                || r.getRevenueCategoryID() == Integer.parseInt(filterCategoryId)) {
                            revenueItems.add(r);
                        }
                    }
                    break;

                case "expense":
                    // Lọc phiếu chi theo danh mục chi
                    List<Expense> allExpenses = expenseDAO.getExpensesBetween(startDate, endDate);
                    for (Expense e : allExpenses) {
                        if ("all".equalsIgnoreCase(filterExpenseCategoryId)
                                || e.getExpenseCategoryID() == Integer.parseInt(filterExpenseCategoryId)) {
                            expenseItems.add(e);
                        }
                    }
                    break;
                case "word":
                case "excel":
                    // Lọc phiếu chi
                    List<Expense> exportExpenses = expenseDAO.getExpensesBetween(startDate, endDate);
                    for (Expense e : exportExpenses) {
                        if ("all".equalsIgnoreCase(filterExpenseCategoryId)
                                || (isNumeric(filterExpenseCategoryId)
                                && e.getExpenseCategoryID() == Integer.parseInt(filterExpenseCategoryId))) {
                            expenseItems.add(e);
                        }
                    }

                    // Lọc phiếu thu
                    List<Revenue> exportRevenues = revenueDAO.getRevenuesBetween(startDate, endDate);
                    for (Revenue r : exportRevenues) {
                        if ("all".equalsIgnoreCase(filterCategoryId)
                                || (isNumeric(filterCategoryId)
                                && r.getRevenueCategoryID() == Integer.parseInt(filterCategoryId))) {
                            revenueItems.add(r);
                        }
                    }

                    if ("word".equalsIgnoreCase(filterType)) {
                        exportToWord(expenseItems, revenueItems, response);
                    } else {
                        exportToExcel(expenseItems, revenueItems, response);
                    }
                    return;

                case "all":
                default:
                    // Lọc phiếu chi
                    List<Expense> expenses = expenseDAO.getExpensesBetween(startDate, endDate);
                    for (Expense e : expenses) {
                        if ("all".equalsIgnoreCase(filterExpenseCategoryId)
                                || (isNumeric(filterExpenseCategoryId)
                                && e.getExpenseCategoryID() == Integer.parseInt(filterExpenseCategoryId))) {
                            expenseItems.add(e);
                        }
                    }

                    // Lọc phiếu thu
                    List<Revenue> revenues = revenueDAO.getRevenuesBetween(startDate, endDate);
                    for (Revenue r : revenues) {
                        if ("all".equalsIgnoreCase(filterCategoryId)
                                || (isNumeric(filterCategoryId)
                                && r.getRevenueCategoryID() == Integer.parseInt(filterCategoryId))) {
                            revenueItems.add(r);
                        }
                    }
                    break;
            }

            // Giao dịch tổng hợp
            List<Object> transactions = statisticalDAO.getFilteredTransactions(
                    startDate, endDate,
                    filterType,
                    filterCategoryId,
                    filterExpenseCategoryId
            );

            // Gán thuộc tính danh mục thu/chi để hiển thị đúng
            if ("income".equalsIgnoreCase(filterType)) {
                request.setAttribute("revenueCategories", revenueCategories);
                request.setAttribute("expenseCategories", new ArrayList<>());
            } else if ("expense".equalsIgnoreCase(filterType)) {
                request.setAttribute("revenueCategories", new ArrayList<>());
                request.setAttribute("expenseCategories", expenseCategories);
            } else {
                request.setAttribute("revenueCategories", revenueCategories);
                request.setAttribute("expenseCategories", expenseCategories);
            }
            boolean showRevenueCategory = !"all".equalsIgnoreCase(filterCategoryId);
            boolean showExpenseCategory = !"all".equalsIgnoreCase(filterExpenseCategoryId);

            // Gán dữ liệu cho JSP
            request.setAttribute("filterType", filterType);
            request.setAttribute("expenseItems", expenseItems);
            request.setAttribute("revenueItems", revenueItems);
            request.setAttribute("transactions", transactions);
            request.setAttribute("totalDebt", statisticalDAO.getTotalDebt());
            request.setAttribute("showRevenueCategory", showRevenueCategory);
            request.setAttribute("showExpenseCategory", showExpenseCategory);

            // Chuyển tiếp đến JSP
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
        HttpSession session = request.getSession(false);
        Integer roleId = (session != null) ? (Integer) session.getAttribute("roleID") : null;
        if (roleId == null || roleId != 1) {
            response.sendRedirect(request.getContextPath() + "/error.jsp");
            return;
        }

        String target = request.getParameter("target");   // expenseCategory | revenueCategory | expenseItem | revenueItem
        String action = request.getParameter("action");   // add

        response.setContentType("application/json;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
// Thêm danh mục chi
            if ("expenseCategory".equals(target) && "add".equals(action)) {
                String name = request.getParameter("name");
                if (name != null && !name.trim().isEmpty() && isValidCategoryName(name.trim())) {
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
                if (name != null && !name.trim().isEmpty() && isValidCategoryName(name.trim())) {
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
                    String revenueName = request.getParameter("revenueName");
                    String amountStr = request.getParameter("amount");
                    String categoryIdStr = request.getParameter("revenueCategoryID");
                    String dateStr = request.getParameter("revenueDate");

                    if (revenueName == null || revenueName.trim().length() < 2) {
                        response.setStatus(400);
                        out.print("{\"status\":\"error\",\"message\":\"Tên phiếu thu phải có ít nhất 2 ký tự\"}");
                        return;
                    }

                    double amount;
                    try {
                        amount = Double.parseDouble(amountStr);
                        if (amount <= 0) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException e) {
                        response.setStatus(400);
                        out.print("{\"status\":\"error\",\"message\":\"Số tiền phải là số dương hợp lệ\"}");
                        return;
                    }

                    int categoryId = Integer.parseInt(categoryIdStr);

                    LocalDate revenueDate = LocalDate.parse(dateStr);
                    if (revenueDate.isAfter(LocalDate.now())) {
                        response.setStatus(400);
                        out.print("{\"status\":\"error\",\"message\":\"Ngày thu không được lớn hơn ngày hiện tại\"}");
                        return;
                    }

                    Revenue revenue = new Revenue();
                    revenue.setRevenueName(revenueName.trim());
                    revenue.setAmount(amount);
                    revenue.setRevenueCategoryID(categoryId);
                    revenue.setRevenueDate(revenueDate);

                    String payer = request.getParameter("payer");
                    if (payer != null && payer.length() > 100) {
                        payer = payer.substring(0, 100);
                    }
                    revenue.setPayer(payer);

                    String notes = request.getParameter("notes");
                    if (notes != null && notes.length() > 255) {
                        notes = notes.substring(0, 255);
                    }
                    revenue.setNotes(notes);

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
                    String expenseName = request.getParameter("expenseName");
                    String amountStr = request.getParameter("amount");
                    String categoryIdStr = request.getParameter("expenseCategoryID");
                    String dateStr = request.getParameter("expenseDate");

                    if (expenseName == null || expenseName.trim().length() < 2) {
                        response.setStatus(400);
                        out.print("{\"status\":\"error\",\"message\":\"Tên phiếu chi phải có ít nhất 2 ký tự\"}");
                        return;
                    }

                    double amount;
                    try {
                        amount = Double.parseDouble(amountStr);
                        if (amount <= 0) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException e) {
                        response.setStatus(400);
                        out.print("{\"status\":\"error\",\"message\":\"Số tiền phải là số dương hợp lệ\"}");
                        return;
                    }

                    int categoryId = Integer.parseInt(categoryIdStr);

                    LocalDate expenseDate = LocalDate.parse(dateStr);
                    if (expenseDate.isAfter(LocalDate.now())) {
                        response.setStatus(400);
                        out.print("{\"status\":\"error\",\"message\":\"Ngày chi không được lớn hơn ngày hiện tại\"}");
                        return;
                    }

                    Expense expense = new Expense();
                    expense.setExpenseName(expenseName.trim());
                    expense.setAmount(amount);
                    expense.setExpenseCategoryID(categoryId);
                    expense.setExpenseDate(expenseDate);

                    String payer = request.getParameter("payer");
                    if (payer != null && payer.length() > 100) {
                        payer = payer.substring(0, 100);
                    }
                    expense.setPayer(payer);

                    String notes = request.getParameter("notes");
                    if (notes != null && notes.length() > 255) {
                        notes = notes.substring(0, 255);
                    }
                    expense.setNotes(notes);

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

        }

    }

    private boolean isValidCategoryName(String name) {
        // Chỉ cho phép chữ cái (kể cả tiếng Việt) và khoảng trắng
        return name != null && name.matches("^[\\p{L}\\s]+$");
    }

    private Map<String, LocalDate> handleStatistical(HttpServletRequest request) throws Exception {
        String type = request.getParameter("type");
        String valueParam = request.getParameter("value");
        String yearParam = request.getParameter("year");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");

        if (type == null || type.isEmpty()) {
            type = "month"; // default
        }
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        LocalDate startDate = null;
        LocalDate endDate = null;

        switch (type) {
            case "month": {
                int month = (valueParam != null && !valueParam.isEmpty())
                        ? Integer.parseInt(valueParam)
                        : LocalDate.now().getMonthValue();

                int year = (yearParam != null && !yearParam.isEmpty())
                        ? Integer.parseInt(yearParam)
                        : LocalDate.now().getYear();

                startDate = LocalDate.of(year, month, 1);
                endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

                request.setAttribute("selectedMonth", month);
                request.setAttribute("selectedYear", year);

                totalIncome = statisticalDAO.getIncomeByMonth(month, year);
                totalExpense = statisticalDAO.getExpenseByMonth(month, year);
                break;
            }

            case "quarter": {
                int quarter = (valueParam != null && !valueParam.isEmpty())
                        ? Integer.parseInt(valueParam)
                        : (LocalDate.now().getMonthValue() - 1) / 3 + 1;

                int year = (yearParam != null && !yearParam.isEmpty())
                        ? Integer.parseInt(yearParam)
                        : LocalDate.now().getYear();

                int startMonth = (quarter - 1) * 3 + 1;
                startDate = LocalDate.of(year, startMonth, 1);
                endDate = startDate.plusMonths(2).withDayOfMonth(startDate.plusMonths(2).lengthOfMonth());

                request.setAttribute("quarter", quarter);
                request.setAttribute("year", year);

                totalIncome = statisticalDAO.getIncomeByQuarter(quarter, year);
                totalExpense = statisticalDAO.getExpenseByQuarter(quarter, year);
                break;
            }

            case "year": {
                int year = (yearParam != null && !yearParam.isEmpty())
                        ? Integer.parseInt(yearParam)
                        : LocalDate.now().getYear();

                startDate = LocalDate.of(year, 1, 1);
                endDate = LocalDate.of(year, 12, 31);

                request.setAttribute("year", year);

                totalIncome = statisticalDAO.getIncomeByYear(year);
                totalExpense = statisticalDAO.getExpenseByYear(year);
                break;
            }

            case "range": {
                if (fromDate == null || fromDate.isEmpty() || toDate == null || toDate.isEmpty()) {
                    LocalDate now = LocalDate.now();
                    fromDate = now.minusDays(6).toString();
                    toDate = now.toString();
                }
                startDate = LocalDate.parse(fromDate);
                endDate = LocalDate.parse(toDate);
                request.setAttribute("fromDate", fromDate);
                request.setAttribute("toDate", toDate);

                totalIncome = statisticalDAO.getIncomeByDateRange(fromDate, toDate);
                totalExpense = statisticalDAO.getExpenseByDateRange(fromDate, toDate);
                break;
            }

            default:
                throw new IllegalArgumentException("Loại thống kê không hợp lệ: " + type);
        }

        BigDecimal profit = totalIncome.subtract(totalExpense);
        request.setAttribute("type", type);
        request.setAttribute("totalIncome", totalIncome);
        request.setAttribute("totalExpense", totalExpense);
        request.setAttribute("profit", profit);

// ✅ Giữ lại `value` cho form GET (month/quarter/year navigation)
        if (type.equals("month") || type.equals("quarter") || type.equals("year")) {
            request.setAttribute("value", valueParam);
        }

        Map<String, LocalDate> map = new HashMap<>();
        map.put("start", startDate);
        map.put("end", endDate);
        return map;
    }

    public void exportToWord(List<Expense> expenses, List<Revenue> revenues, HttpServletResponse response) throws IOException {
        XWPFDocument document = new XWPFDocument();

        // Title
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = title.createRun();
        run.setText("BÁO CÁO THU CHI");
        run.setBold(true);
        run.setFontSize(18);

        // Revenue Table
        if (!revenues.isEmpty()) {
            document.createParagraph().createRun().setText("Danh sách phiếu thu:");
            XWPFTable table = document.createTable();

            // Header
            XWPFTableRow header = table.getRow(0);
            header.getCell(0).setText("Tên thu");
            header.addNewTableCell().setText("Số tiền");
            header.addNewTableCell().setText("Người nộp");
            header.addNewTableCell().setText("Ngày");
            header.addNewTableCell().setText("Ghi chú");

            for (Revenue r : revenues) {
                XWPFTableRow row = table.createRow();
                row.getCell(0).setText(r.getRevenueName());
                row.getCell(1).setText(String.valueOf(r.getAmount()));
                row.getCell(2).setText(r.getPayer());
                row.getCell(3).setText(String.valueOf(r.getRevenueDate()));
                row.getCell(4).setText(r.getNotes());
            }
        }

        // Expense Table
        if (!expenses.isEmpty()) {
            document.createParagraph().createRun().addBreak();
            document.createParagraph().createRun().setText("Danh sách phiếu chi:");
            XWPFTable table = document.createTable();

            // Header
            XWPFTableRow header = table.getRow(0);
            header.getCell(0).setText("Tên chi");
            header.addNewTableCell().setText("Số tiền");
            header.addNewTableCell().setText("Người chi");
            header.addNewTableCell().setText("Ngày");
            header.addNewTableCell().setText("Ghi chú");

            for (Expense e : expenses) {
                XWPFTableRow row = table.createRow();
                row.getCell(0).setText(e.getExpenseName());
                row.getCell(1).setText(String.valueOf(e.getAmount()));
                row.getCell(2).setText(e.getPayer());
                row.getCell(3).setText(String.valueOf(e.getExpenseDate()));
                row.getCell(4).setText(e.getNotes());
            }
        }

        // Export
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=baocao-thuchi.docx");

        document.write(response.getOutputStream());
        document.close();
    }

    public void exportToExcel(List<Expense> expenses, List<Revenue> revenues, HttpServletResponse response) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Revenue Sheet
        if (!revenues.isEmpty()) {
            XSSFSheet sheet = workbook.createSheet("Phiếu Thu");
            int rowIndex = 0;

            // Header
            XSSFRow header = sheet.createRow(rowIndex++);
            header.createCell(0).setCellValue("Tên thu");
            header.createCell(1).setCellValue("Số tiền");
            header.createCell(2).setCellValue("Người nộp");
            header.createCell(3).setCellValue("Ngày");
            header.createCell(4).setCellValue("Ghi chú");

            for (Revenue r : revenues) {
                XSSFRow row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(r.getRevenueName());
                row.createCell(1).setCellValue(r.getAmount());
                row.createCell(2).setCellValue(r.getPayer());
                row.createCell(3).setCellValue(r.getRevenueDate().toString());
                row.createCell(4).setCellValue(r.getNotes());
            }
        }

        // Expense Sheet
        if (!expenses.isEmpty()) {
            XSSFSheet sheet = workbook.createSheet("Phiếu Chi");
            int rowIndex = 0;

            // Header
            XSSFRow header = sheet.createRow(rowIndex++);
            header.createCell(0).setCellValue("Tên chi");
            header.createCell(1).setCellValue("Số tiền");
            header.createCell(2).setCellValue("Người chi");
            header.createCell(3).setCellValue("Ngày");
            header.createCell(4).setCellValue("Ghi chú");

            for (Expense e : expenses) {
                XSSFRow row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(e.getExpenseName());
                row.createCell(1).setCellValue(e.getAmount());
                row.createCell(2).setCellValue(e.getPayer());
                row.createCell(3).setCellValue(e.getExpenseDate().toString());
                row.createCell(4).setCellValue(e.getNotes());
            }
        }

        // Export
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=baocao-thuchi.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
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
