/*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.RevenueCategory;
import utils.DBContext;

/**
 *
 * @author Admin
 */
public class RevenueCategoryDAO extends DBContext {


    public int getOrCreateRevenueCategoryId(Connection conn, String categoryName) throws SQLException {
        String selectSql = "SELECT RevenueCategoryID FROM RevenueCategories WHERE CategoryName = ?";
        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setString(1, categoryName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("RevenueCategoryID");
                }
            }
        }

        // Không tìm thấy => thêm mới
        String insertSql = "INSERT INTO RevenueCategories (CategoryName) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categoryName);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Không thể tạo danh mục thu mới: " + categoryName);
    }

 
    public boolean addRevenueCategory(String name) {
        String sql = "INSERT INTO RevenueCategories (CategoryName) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<RevenueCategory> getRevenueCategories(Connection conn) throws SQLException {
        List<RevenueCategory> categories = new ArrayList<>();
        String sql = "SELECT RevenueCategoryID, CategoryName FROM RevenueCategories ORDER BY RevenueCategoryID";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categories.add(new RevenueCategory(
                        rs.getInt("RevenueCategoryID"),
                        rs.getString("CategoryName")
                ));
            }
        }

        return categories;
    }

    public List<RevenueCategory> getRevenueCategories() throws SQLException {
        try (Connection conn = getConnection()) {
            return getRevenueCategories(conn);
        }
    }
}