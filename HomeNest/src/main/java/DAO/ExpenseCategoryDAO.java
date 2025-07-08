/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
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

    public void addExpenseCategory(String name) throws SQLException {
        String sql = "INSERT INTO ExpenseCategories (CategoryName) VALUES (?)";
        try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
        }
    }

    public List<ExpenseCategory> getExpenseCategories() throws SQLException {
        List<ExpenseCategory> list = new ArrayList<>();
        String sql = "SELECT ExpenseCategoryID, CategoryName FROM ExpenseCategories";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new ExpenseCategory(rs.getInt(1), rs.getString(2)));
            }
        }

        return list;
    }

}
