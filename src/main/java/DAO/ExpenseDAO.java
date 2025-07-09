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
import model.Expense;
import java.sql.*;
import java.util.*;
import utils.DBContext;

public class ExpenseDAO extends DBContext {

    // Lấy tất cả phiếu chi
    public List<Expense> getAll() {
        List<Expense> list = new ArrayList<>();
        String sql = "SELECT e.ExpenseID, e.ExpenseName, e.Amount, e.ExpenseDate, e.Payer, e.Notes, "
                + "c.CategoryName "
                + "FROM Expenses e "
                + "JOIN ExpenseCategories c ON e.ExpenseCategoryID = c.ExpenseCategoryID";

        try (
                 Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Expense e = new Expense();
                e.setExpenseID(rs.getInt("ExpenseID"));
                e.setExpenseName(rs.getString("ExpenseName")); // dùng đúng tên cột Title
                e.setAmount(rs.getDouble("Amount"));
                e.setExpenseDate(rs.getDate("ExpenseDate").toString());
                e.setPayer(rs.getString("Payer"));     // dùng đúng tên cột Spender
                e.setNotes(rs.getString("Notes"));
                e.setCategoryName(rs.getString("CategoryName"));
                list.add(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Thêm mới phiếu chi
    public boolean addExpenseItem(Expense expense, int categoryId) {
        String sql = "INSERT INTO Expenses (ExpenseName, Amount, ExpenseCategoryID, ExpenseDate, Payer, Notes) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (
                 Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, expense.getExpenseName()); // -> Title
            ps.setDouble(2, expense.getAmount());
            ps.setInt(3, categoryId);
            ps.setDate(4, java.sql.Date.valueOf(expense.getExpenseDate()));
            ps.setString(5, expense.getPayer());       // -> Spender
            ps.setString(6, expense.getNotes());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}