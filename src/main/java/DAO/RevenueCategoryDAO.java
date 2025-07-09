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
import model.RevenueCategory;
import utils.DBContext;

/**
 *
 * @author Admin
 */


public class RevenueCategoryDAO extends DBContext {

    // Lấy danh sách danh mục thu
    public List<RevenueCategory> getRevenueCategories() throws SQLException {
        List<RevenueCategory> list = new ArrayList<>();
        String sql = "SELECT RevenueCategoryID, CategoryName FROM RevenueCategories ORDER BY RevenueCategoryID";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new RevenueCategory(rs.getInt("RevenueCategoryID"), rs.getString("CategoryName")));
            }
        }
        return list;
    }

    // Thêm danh mục thu, trả về true nếu thành công
    public boolean addRevenueCategory(String name) {
        String sql = "INSERT INTO RevenueCategories (CategoryName) VALUES (?)";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}