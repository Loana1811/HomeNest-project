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
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setInt(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal res = rs.getBigDecimal(1);
                    return (res != null) ? res : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }
}

