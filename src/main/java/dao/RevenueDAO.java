/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author Admin
 */
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import model.Revenue;
import utils.DBContext;

public class RevenueDAO extends DBContext {

    // Lấy toàn bộ danh sách phiếu thu
    public List<Revenue> getAll() {
        List<Revenue> list = new ArrayList<>();
        String sql = "SELECT r.RevenueID, r.RevenueName, r.Amount, r.RevenueDate, r.Payer, r.Notes, "
                + "c.CategoryName, r.RevenueCategoryID "
                + "FROM Revenues r "
                + "JOIN RevenueCategories c ON r.RevenueCategoryID = c.RevenueCategoryID "
                + "ORDER BY r.RevenueDate DESC";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Revenue r = new Revenue();
                r.setRevenueID(rs.getInt("RevenueID"));
                r.setRevenueName(rs.getString("RevenueName"));
                r.setAmount(rs.getDouble("Amount"));
                r.setRevenueDate(rs.getDate("RevenueDate").toLocalDate());
                r.setPayer(rs.getString("Payer"));
                r.setNotes(rs.getString("Notes"));
                r.setCategoryName(rs.getString("CategoryName"));
                r.setRevenueCategoryID(rs.getInt("RevenueCategoryID"));
                list.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Thêm phiếu thu
    public boolean addRevenue(Revenue revenue) {
        String sql = "INSERT INTO Revenues (RevenueName, Amount, RevenueDate, Payer, Notes, RevenueCategoryID) VALUES (?, ?, ?, ?, ?, ?)";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, revenue.getRevenueName());
            ps.setDouble(2, revenue.getAmount());
            ps.setDate(3, java.sql.Date.valueOf(revenue.getRevenueDate()));
            ps.setString(4, revenue.getPayer());
            ps.setString(5, revenue.getNotes());
            ps.setInt(6, revenue.getRevenueCategoryID());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa phiếu thu theo ID
    public boolean deleteRevenue(int id) {
        String sql = "DELETE FROM Revenues WHERE RevenueID = ?";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy phiếu thu theo ID
    public Revenue getRevenueById(int id) {
        String sql = "SELECT r.RevenueID, r.RevenueName, r.Amount, r.RevenueDate, r.Payer, r.Notes, "
                + "c.CategoryName, r.RevenueCategoryID "
                + "FROM Revenues r "
                + "JOIN RevenueCategories c ON r.RevenueCategoryID = c.RevenueCategoryID "
                + "WHERE r.RevenueID = ?";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Revenue r = new Revenue();
                    r.setRevenueID(rs.getInt("RevenueID"));
                    r.setRevenueName(rs.getString("RevenueName"));
                    r.setAmount(rs.getDouble("Amount"));
                    r.setRevenueDate(rs.getDate("RevenueDate").toLocalDate());

                    r.setPayer(rs.getString("Payer"));
                    r.setNotes(rs.getString("Notes"));
                    r.setCategoryName(rs.getString("CategoryName"));
                    r.setRevenueCategoryID(rs.getInt("RevenueCategoryID"));
                    return r;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Revenue> getRevenuesBetween(LocalDate startDate, LocalDate endDate) {
        List<Revenue> list = new ArrayList<>();

        String sql = "SELECT r.RevenueID, r.RevenueName, r.Amount, r.RevenueDate, r.Payer, r.Notes, "
                + "c.CategoryName, r.RevenueCategoryID "
                + "FROM Revenues r "
                + "JOIN RevenueCategories c ON r.RevenueCategoryID = c.RevenueCategoryID "
                + "WHERE r.RevenueDate BETWEEN ? AND ? "
                + "ORDER BY r.RevenueDate DESC";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate));

            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Revenue r = new Revenue();
                    r.setRevenueID(rs.getInt("RevenueID"));
                    r.setRevenueName(rs.getString("RevenueName"));
                    r.setAmount(rs.getDouble("Amount"));
                    r.setRevenueDate(rs.getDate("RevenueDate").toLocalDate());
                    r.setPayer(rs.getString("Payer"));
                    r.setNotes(rs.getString("Notes"));
                    r.setCategoryName(rs.getString("CategoryName"));
                    r.setRevenueCategoryID(rs.getInt("RevenueCategoryID"));

                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean createRevenueForPaidBill(int billId, int revenueCategoryId) {
        String sql = "INSERT INTO Revenues (RevenueName, Amount, RevenueDate, Payer, Notes, RevenueCategoryID, BillID) "
                + "SELECT ?, (ISNULL(b.TotalAmount, 0) + ISNULL(r.RentPrice, 0)), ?, ?, ?, ?, ? "
                + "FROM Bills b "
                + "JOIN Contracts c ON b.ContractID = c.ContractID "
                + "JOIN Rooms r ON c.RoomID = r.RoomID "
                + "WHERE b.BillID = ? AND UPPER(TRIM(b.BillStatus)) IN ('PAID', 'ĐÃ THANH TOÁN')";

        try ( Connection conn = getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) {
                System.err.println("Failed to get database connection");
                return false;
            }

            String revenueName = "Thanh toán hóa đơn " + billId;
            LocalDate revenueDate = LocalDate.now();
            String payer = "Người thuê"; // Có thể cải tiến để lấy từ Tenants
            String notes = "Phiếu thu tự động cho thanh toán hóa đơn";

            stmt.setString(1, revenueName);
            stmt.setDate(2, java.sql.Date.valueOf(revenueDate));
            stmt.setString(3, payer);
            stmt.setString(4, notes);
            stmt.setInt(5, revenueCategoryId); // Mặc định là 1
            stmt.setInt(6, billId);
            stmt.setInt(7, billId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.err.println("No rows inserted for billId: " + billId + ". Check BillStatus or related data.");
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("SQLException in createRevenueForPaidBill: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsRevenueForBill(int billId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Revenues WHERE BillID = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

}
