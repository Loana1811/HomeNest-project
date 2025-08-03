package dao;

import model.PaymentConfirmation;
import utils.DBContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentConfirmationDAO extends DBContext {

    // Insert a new confirmation and return the generated ID
    public int insert(PaymentConfirmation conf) {
        String sql = "INSERT INTO PaymentConfirmations (PaymentID, TenantID, ImageData, CreatedAt) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, conf.getPaymentID());
            ps.setInt(2, conf.getTenantID());
            ps.setBytes(3, conf.getImageData());
            ps.setTimestamp(4, conf.getCreatedAt());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Error
    }

    // Get confirmations by BillID (join with Payments)
    public List<PaymentConfirmation> getConfirmationsByBillId(int billId) {
        List<PaymentConfirmation> list = new ArrayList<>();
        String sql = "SELECT pc.* FROM PaymentConfirmations pc "
                + "JOIN Payments p ON pc.PaymentID = p.PaymentID "
                + "WHERE p.BillID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PaymentConfirmation pc = new PaymentConfirmation();
                pc.setConfirmationID(rs.getInt("ConfirmationID"));
                pc.setPaymentID(rs.getInt("PaymentID"));
                pc.setTenantID(rs.getInt("TenantID"));
                pc.setImageData(rs.getBytes("ImageData"));
                pc.setCreatedAt(rs.getTimestamp("CreatedAt"));
                list.add(pc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Delete confirmation by ID
    public void deleteConfirmation(int confirmationId) {
        String sql = "DELETE FROM PaymentConfirmations WHERE ConfirmationID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, confirmationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public PaymentConfirmation getById(int confId) {
        String sql = "SELECT * FROM PaymentConfirmations WHERE ConfirmationID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, confId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PaymentConfirmation pc = new PaymentConfirmation();
                pc.setConfirmationID(rs.getInt("ConfirmationID"));
                pc.setPaymentID(rs.getInt("PaymentID"));
                pc.setTenantID(rs.getInt("TenantID"));
                pc.setImageData(rs.getBytes("ImageData"));
                pc.setCreatedAt(rs.getTimestamp("CreatedAt"));
                return pc;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // New method to get pending proofs with room info
    public List<Map<String, Object>> getPendingProofs() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT pc.ConfirmationID AS confId, b.BillID AS billId, r.RoomNumber AS roomNumber, pc.CreatedAt AS createdAt " +
                     "FROM PaymentConfirmations pc " +
                     "JOIN Payments p ON pc.PaymentID = p.PaymentID " +
                     "JOIN Bills b ON p.BillID = b.BillID " +
                     "JOIN Contracts c ON b.ContractID = c.ContractID " +
                     "JOIN Rooms r ON c.RoomID = r.RoomID " +
                     "WHERE b.BillStatus = 'Unpaid' " +
                     "ORDER BY pc.CreatedAt DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("confId", rs.getInt("confId"));
                map.put("billId", rs.getInt("billId"));
                map.put("roomNumber", rs.getString("roomNumber"));
                map.put("createdAt", rs.getTimestamp("createdAt"));
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}