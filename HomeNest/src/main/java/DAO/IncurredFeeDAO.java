package dao;

import model.IncurredFee;
import utils.DBContext;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class IncurredFeeDAO {
    private Connection conn;

    public IncurredFeeDAO() {
        try {
            conn = new DBContext().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy tất cả phí phát sinh
    public List<IncurredFee> getAllIncurredFees() {
        List<IncurredFee> list = new ArrayList<>();
        String sql = "SELECT IncurredFeeID, BillID, IncurredFeeTypeID, Amount FROM IncurredFees";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new IncurredFee(
                    rs.getInt("IncurredFeeID"),
                    rs.getInt("BillID"),
                    rs.getInt("IncurredFeeTypeID"),
                    rs.getBigDecimal("Amount")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy 1 phí phát sinh theo ID
    public IncurredFee getIncurredFeeById(int id) {
        String sql = "SELECT IncurredFeeID, BillID, IncurredFeeTypeID, Amount "
                   + "FROM IncurredFees WHERE IncurredFeeID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new IncurredFee(
                        rs.getInt("IncurredFeeID"),
                        rs.getInt("BillID"),
                        rs.getInt("IncurredFeeTypeID"),
                        rs.getBigDecimal("Amount")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm mới phí phát sinh
    public void addIncurredFee(IncurredFee fee) {
        String sql = "INSERT INTO IncurredFees (BillID, IncurredFeeTypeID, Amount) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fee.getBillID());
            ps.setInt(2, fee.getIncurredFeeTypeID());
            ps.setBigDecimal(3, fee.getAmount());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cập nhật phí phát sinh
    public void updateIncurredFee(IncurredFee fee) {
        String sql = "UPDATE IncurredFees SET BillID = ?, IncurredFeeTypeID = ?, Amount = ? "
                   + "WHERE IncurredFeeID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fee.getBillID());
            ps.setInt(2, fee.getIncurredFeeTypeID());
            ps.setBigDecimal(3, fee.getAmount());
            ps.setInt(4, fee.getIncurredFeeID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Xóa phí phát sinh
    public void deleteIncurredFee(int id) {
        String sql = "DELETE FROM IncurredFees WHERE IncurredFeeID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy danh sách phí phát sinh theo hóa đơn
    public List<IncurredFee> getIncurredFeesByBillId(int billId) {
        List<IncurredFee> list = new ArrayList<>();
        String sql = "SELECT IncurredFeeID, BillID, IncurredFeeTypeID, Amount "
                   + "FROM IncurredFees WHERE BillID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new IncurredFee(
                        rs.getInt("IncurredFeeID"),
                        rs.getInt("BillID"),
                        rs.getInt("IncurredFeeTypeID"),
                        rs.getBigDecimal("Amount")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
