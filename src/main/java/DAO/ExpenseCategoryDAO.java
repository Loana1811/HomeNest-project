/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.ExpenseCategory;
import utils.DBContext;

/**
 *
 * @author Admin
 */

public class ExpenseCategoryDAO extends DBContext {

    // Lấy danh sách danh mục chi
    public List<ExpenseCategory> getExpenseCategories() throws SQLException {
        List<ExpenseCategory> list = new ArrayList<>();
        String sql = "SELECT ExpenseCategoryID, CategoryName, CreatedAt FROM ExpenseCategories ORDER BY ExpenseCategoryID";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ExpenseCategory cat = new ExpenseCategory(
                        rs.getInt("ExpenseCategoryID"),
                        rs.getString("CategoryName"),
                        rs.getTimestamp("CreatedAt")
                );
                list.add(cat);
            }
        }
        return list;
    }

    // Thêm danh mục chi, trả về true nếu thành công
    public boolean addExpenseCategory(String name) {
        String sql = "INSERT INTO ExpenseCategories (CategoryName) VALUES (?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}