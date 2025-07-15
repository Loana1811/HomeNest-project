/*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author Admin
 */
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Expense;
import model.Revenue;
import utils.DBContext;

public class StatisticalDAO extends DBContext {

    private final RevenueDAO revenueDAO = new RevenueDAO();
    private final ExpenseDAO expenseDAO = new ExpenseDAO();

    // Lấy tổng thu theo tháng
    public BigDecimal getIncomeByMonth(int month, int year) throws SQLException {
        String sql = "SELECT ISNULL(SUM(Amount), 0) FROM Revenues WHERE MONTH(RevenueDate) = ? AND YEAR(RevenueDate) = ?";
        return getTotal(sql, month, year);
    }

    // Tổng thu theo quý
    public BigDecimal getIncomeByQuarter(int quarter, int year) throws SQLException {
        String sql = "SELECT ISNULL(SUM(Amount), 0) FROM Revenues WHERE DATEPART(QUARTER, RevenueDate) = ? AND YEAR(RevenueDate) = ?";
        return getTotal(sql, quarter, year);
    }

    // Tổng thu theo năm
    public BigDecimal getIncomeByYear(int year) throws SQLException {
        String sql = "SELECT ISNULL(SUM(Amount), 0) FROM Revenues WHERE YEAR(RevenueDate) = ?";
        return getTotal(sql, year);
    }

    // Tổng chi theo tháng
    public BigDecimal getExpenseByMonth(int month, int year) throws SQLException {
        String sql = "SELECT ISNULL(SUM(Amount), 0) FROM Expenses WHERE MONTH(ExpenseDate) = ? AND YEAR(ExpenseDate) = ?";
        return getTotal(sql, month, year);
    }

    // Tổng chi theo quý
    public BigDecimal getExpenseByQuarter(int quarter, int year) throws SQLException {
        String sql = "SELECT ISNULL(SUM(Amount), 0) FROM Expenses WHERE DATEPART(QUARTER, ExpenseDate) = ? AND YEAR(ExpenseDate) = ?";
        return getTotal(sql, quarter, year);
    }

    // Tổng chi theo năm
    public BigDecimal getExpenseByYear(int year) throws SQLException {
        String sql = "SELECT ISNULL(SUM(Amount), 0) FROM Expenses WHERE YEAR(ExpenseDate) = ?";
        return getTotal(sql, year);
    }

    // Hàm helper xử lý param động
    private BigDecimal getTotal(String sql, int... params) throws SQLException {
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setInt(i + 1, params[i]);
            }

            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal res = rs.getBigDecimal(1);
                    return (res != null) ? res : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }
    // StatisticalDAO.java

    public BigDecimal getIncomeByDateRange(String fromDate, String toDate) throws Exception {
        String sql = "SELECT SUM(Amount) FROM Revenues WHERE RevenueDate BETWEEN ? AND ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fromDate);
            ps.setString(2, toDate);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1) != null ? rs.getBigDecimal(1) : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getExpenseByDateRange(String fromDate, String toDate) throws Exception {
        String sql = "SELECT SUM(Amount) FROM Expenses WHERE ExpenseDate BETWEEN ? AND ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fromDate);
            ps.setString(2, toDate);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1) != null ? rs.getBigDecimal(1) : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalDebt() throws SQLException {
        String sql = "SELECT ISNULL(SUM(TotalAmount), 0) FROM Bills WHERE BillStatus = 'Unpaid'";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1) != null ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    //    public boolean addExpenseItem(String title, BigDecimal amount, int categoryId, Date expenseDate, String spender, String notes) {
    //        String sql = "INSERT INTO Expenses (Title, Amount, ExpenseCategoryID, ExpenseDate, Spender, Notes) VALUES (?, ?, ?, ?, ?, ?)";
    //        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
    //            ps.setString(1, title);
    //            ps.setBigDecimal(2, amount);
    //            ps.setInt(3, categoryId);
    //            ps.setDate(4, expenseDate);
    //            ps.setString(5, spender);
    //            ps.setString(6, notes);
    //            return ps.executeUpdate() > 0;
    //        } catch (SQLException e) {
    //            e.printStackTrace();
    //            return false;
    //        }
    //    }
    // Lợi nhuận theo tháng
    public BigDecimal getProfitByMonth(int month, int year) throws SQLException {
        return getIncomeByMonth(month, year).subtract(getExpenseByMonth(month, year));
    }

    // Lợi nhuận theo quý
    public BigDecimal getProfitByQuarter(int quarter, int year) throws SQLException {
        return getIncomeByQuarter(quarter, year).subtract(getExpenseByQuarter(quarter, year));
    }

    // Lợi nhuận theo năm
    public BigDecimal getProfitByYear(int year) throws SQLException {
        return getIncomeByYear(year).subtract(getExpenseByYear(year));
    }

    // Lợi nhuận theo khoảng ngày
    public BigDecimal getProfitByDateRange(String fromDate, String toDate) throws Exception {
        return getIncomeByDateRange(fromDate, toDate).subtract(getExpenseByDateRange(fromDate, toDate));
    }

    public boolean addRevenueItem(int categoryId, BigDecimal amount, Date revenueDate, String notes) {
        String sql = "INSERT INTO Revenues (CategoryID, Amount, RevenueDate, Notes) VALUES (?, ?, ?, ?)";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.setBigDecimal(2, amount);
            ps.setDate(3, new java.sql.Date(revenueDate.getTime()));
            ps.setString(4, notes);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Object> getAllTransactionsMerged() throws Exception {
        List<Object> transactions = new ArrayList<>();

        // Lấy tất cả phiếu thu
        List<Revenue> revenues = new RevenueDAO().getAll();
        for (Revenue r : revenues) {
            Map<String, Object> t = new HashMap<>();
            t.put("type", "income");
            t.put("title", r.getRevenueName());
            t.put("amount", r.getAmount());
            t.put("categoryName", r.getCategoryName());
            t.put("date", r.getRevenueDate());
            t.put("payer", r.getPayer());
            t.put("notes", r.getNotes());
            transactions.add(t);
        }

        // Lấy tất cả phiếu chi
        List<Expense> expenses = new ExpenseDAO().getAll();
        for (Expense e : expenses) {
            Map<String, Object> t = new HashMap<>();
            t.put("type", "expense");
            t.put("title", e.getExpenseName());
            t.put("amount", e.getAmount());
            t.put("categoryName", e.getCategoryName());
            t.put("date", e.getExpenseDate());
            t.put("payer", e.getPayer());
            t.put("notes", e.getNotes());
            transactions.add(t);
        }

        return transactions;
    }

    public List<Object> getAllTransactionsMerged(LocalDate startDate, LocalDate endDate) throws Exception {
        List<Object> transactions = new ArrayList<>();
        RevenueDAO revenueDAO = new RevenueDAO();
        ExpenseDAO expenseDAO = new ExpenseDAO();

        // Lọc phiếu thu trong khoảng thời gian
        List<Revenue> revenues = revenueDAO.getRevenuesBetween(startDate, endDate);

        for (Revenue r : revenues) {
            Map<String, Object> t = new HashMap<>();
            t.put("type", "income");
            t.put("title", r.getRevenueName());
            t.put("amount", r.getAmount());
            t.put("categoryName", r.getCategoryName());
            t.put("date", r.getRevenueDate());
            t.put("payer", r.getPayer());
            t.put("notes", r.getNotes());
            transactions.add(t);
        }

        // Lọc phiếu chi trong khoảng thời gian
        List<Expense> expenses = expenseDAO.getExpensesBetween(startDate, endDate);
        for (Expense e : expenses) {
            Map<String, Object> t = new HashMap<>();
            t.put("type", "expense");
            t.put("title", e.getExpenseName());
            t.put("amount", e.getAmount());
            t.put("categoryName", e.getCategoryName());
            t.put("date", e.getExpenseDate());
            t.put("payer", e.getPayer());
            t.put("notes", e.getNotes());
            transactions.add(t);
        }

        return transactions;
    }
    // Trong StatisticalDAO

    public List<Revenue> getRevenuesBetween(LocalDate start, LocalDate end) throws Exception {
        return revenueDAO.getRevenuesBetween(start, end);
    }

    public List<Expense> getExpensesBetween(LocalDate start, LocalDate end) throws Exception {
        return expenseDAO.getExpensesBetween(start, end);
    }

    public List<Object> getFilteredTransactions(
            LocalDate start, LocalDate end,
            String filterType,
            String revenueCategoryId,
            String expenseCategoryId
    ) throws Exception {
        List<Object> transactions = new ArrayList<>();

        if ("income".equalsIgnoreCase(filterType)) {
            // Chỉ lấy phiếu thu, lọc theo danh mục thu nếu có
            List<Revenue> revenues = getRevenuesBetween(start, end);
            for (Revenue r : revenues) {
                if (!"all".equalsIgnoreCase(revenueCategoryId)) {
                    try {
                        int catId = Integer.parseInt(revenueCategoryId);
                        if (r.getRevenueCategoryID() != catId) {
                            continue;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
                Map<String, Object> t = new HashMap<>();
                t.put("type", "income");
                t.put("title", r.getRevenueName());
                t.put("amount", r.getAmount());
                t.put("categoryName", r.getCategoryName());
                t.put("date", r.getRevenueDate());
                t.put("payer", r.getPayer());
                t.put("notes", r.getNotes());
                transactions.add(t);
            }
        } else if ("expense".equalsIgnoreCase(filterType)) {
            // Chỉ lấy phiếu chi, lọc theo danh mục chi nếu có
            List<Expense> expenses = getExpensesBetween(start, end);
            for (Expense e : expenses) {
                if (!"all".equalsIgnoreCase(expenseCategoryId)) {
                    try {
                        int catId = Integer.parseInt(expenseCategoryId);
                        if (e.getExpenseCategoryID() != catId) {
                            continue;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
                Map<String, Object> t = new HashMap<>();
                t.put("type", "expense");
                t.put("title", e.getExpenseName());
                t.put("amount", e.getAmount());
                t.put("categoryName", e.getCategoryName());
                t.put("date", e.getExpenseDate());
                t.put("payer", e.getPayer());
                t.put("notes", e.getNotes());
                transactions.add(t);
            }
        } else {
            // filterType = all, lấy cả thu và chi với lọc danh mục tương ứng
            // Thu
            List<Revenue> revenues = getRevenuesBetween(start, end);
            for (Revenue r : revenues) {
                if (!"all".equalsIgnoreCase(revenueCategoryId)) {
                    try {
                        int catId = Integer.parseInt(revenueCategoryId);
                        if (r.getRevenueCategoryID() != catId) {
                            continue;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
                Map<String, Object> t = new HashMap<>();
                t.put("type", "income");
                t.put("title", r.getRevenueName());
                t.put("amount", r.getAmount());
                t.put("categoryName", r.getCategoryName());
                t.put("date", r.getRevenueDate());
                t.put("payer", r.getPayer());
                t.put("notes", r.getNotes());
                transactions.add(t);
            }

            // Chi
            List<Expense> expenses = getExpensesBetween(start, end);
            for (Expense e : expenses) {
                if (!"all".equalsIgnoreCase(expenseCategoryId)) {
                    try {
                        int catId = Integer.parseInt(expenseCategoryId);
                        if (e.getExpenseCategoryID() != catId) {
                            continue;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
                Map<String, Object> t = new HashMap<>();
                t.put("type", "expense");
                t.put("title", e.getExpenseName());
                t.put("amount", e.getAmount());
                t.put("categoryName", e.getCategoryName());
                t.put("date", e.getExpenseDate());
                t.put("payer", e.getPayer());
                t.put("notes", e.getNotes());
                transactions.add(t);
            }
        }

        Collections.sort(transactions, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                LocalDate d1 = (LocalDate) ((Map<String, Object>) o1).get("date");
                LocalDate d2 = (LocalDate) ((Map<String, Object>) o2).get("date");
                return d2.compareTo(d1);
            }
        });

        return transactions;
    }

}
