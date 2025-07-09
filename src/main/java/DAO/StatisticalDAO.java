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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Expense;
import model.Revenue;
import utils.DBContext;

public class StatisticalDAO extends DBContext {

    // Lấy tổng thu theo tháng
    public BigDecimal getIncomeByMonth(int month, int year) throws SQLException {
        String sql = "SELECT ISNULL(SUM(TotalAmount), 0) FROM Bills WHERE BillStatus = 'Paid' AND MONTH(BillDate) = ? AND YEAR(BillDate) = ?";
        return getTotal(sql, month, year);
    }

    // Lấy tổng thu theo quý
    public BigDecimal getIncomeByQuarter(int quarter, int year) throws SQLException {
        String sql = "SELECT ISNULL(SUM(TotalAmount), 0) FROM Bills WHERE BillStatus = 'Paid' AND DATEPART(QUARTER, BillDate) = ? AND YEAR(BillDate) = ?";
        return getTotal(sql, quarter, year);
    }

    // Lấy tổng thu theo năm
    public BigDecimal getIncomeByYear(int year) throws SQLException {
        String sql = "SELECT ISNULL(SUM(TotalAmount), 0) FROM Bills WHERE BillStatus = 'Paid' AND YEAR(BillDate) = ?";
        return getTotal(sql, year);
    }

    // Lấy tổng chi theo tháng
    public BigDecimal getExpenseByMonth(int month, int year) throws SQLException {
        String sql = "SELECT ISNULL(SUM(Amount), 0) FROM Expenses WHERE MONTH(ExpenseDate) = ? AND YEAR(ExpenseDate) = ?";
        return getTotal(sql, month, year);
    }

    // Lấy tổng chi theo quý
    public BigDecimal getExpenseByQuarter(int quarter, int year) throws SQLException {
        String sql = "SELECT ISNULL(SUM(Amount), 0) FROM Expenses WHERE DATEPART(QUARTER, ExpenseDate) = ? AND YEAR(ExpenseDate) = ?";
        return getTotal(sql, quarter, year);
    }

    // Lấy tổng chi theo năm
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

}
