package dao;

import model.Payment;
import utils.DBContext;

import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public class PaymentDAO extends DBContext {

    // Thêm thanh toán mới
    public void insertPayment(Payment payment) {
        String sql = "INSERT INTO Payments (BillID, AmountPaid, PaymentMethod, PaymentNote, PaymentDate) "
                + "VALUES (?, ?, ?, ?, ?)";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, payment.getBillId());
            ps.setBigDecimal(2, payment.getAmountPaid());
            ps.setString(3, payment.getPaymentMethod());
            ps.setString(4, payment.getPaymentNote());
            ps.setDate(5, payment.getPaymentDate());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy danh sách payment theo BillID
    public List<Payment> getPaymentsByBillId(int billId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM Payments WHERE BillID = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Payment p = new Payment();
                p.setPaymentId(rs.getInt("PaymentID"));
                p.setBillId(rs.getInt("BillID"));
                p.setAmountPaid(rs.getBigDecimal("AmountPaid"));
                p.setPaymentMethod(rs.getString("PaymentMethod"));
                p.setPaymentNote(rs.getString("PaymentNote"));
                p.setPaymentDate(rs.getDate("PaymentDate"));
                p.setCreatedAt(rs.getTimestamp("CreatedAt"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Xóa thanh toán theo ID
    public void deletePayment(int paymentId) {
        String sql = "DELETE FROM Payments WHERE PaymentID = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
