package dao;

import model.IncurredFee;
import utils.DBContext;
import java.sql.*;
import java.util.*;

public class IncurredFeeDAO {

    private final DBContext dbContext = new DBContext();

    public List<IncurredFee> getAllIncurredFees() throws SQLException {
        List<IncurredFee> list = new ArrayList<>();
        String sql = "SELECT * FROM IncurredFees";
        try ( Connection c = dbContext.getConnection();  PreparedStatement ps = c.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public IncurredFee getIncurredFeeById(int id) throws SQLException {
        String sql = "SELECT * FROM IncurredFees WHERE IncurredFeeID=?";
        try ( Connection c = dbContext.getConnection();  PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public boolean insertIncurredFee(IncurredFee b) throws SQLException {
        String sql = "INSERT INTO IncurredFees (BillID, IncurredFeeTypeID, Amount) VALUES (?, ?, ?)";
        return dbContext.execUpdateQuery(sql, b.getBillID(), b.getIncurredFeeTypeID(), b.getAmount()) > 0;
    }

    public boolean updateIncurredFee(IncurredFee b) throws SQLException {
        String sql = "UPDATE IncurredFees SET BillID=?, IncurredFeeTypeID=?, Amount=? WHERE IncurredFeeID=?";
        return dbContext.execUpdateQuery(sql, b.getBillID(), b.getIncurredFeeTypeID(), b.getAmount(), b.getIncurredFeeID()) > 0;
    }

    public boolean deleteIncurredFee(int id) throws SQLException {
        String sql = "DELETE FROM IncurredFees WHERE IncurredFeeID=?";
        return dbContext.execUpdateQuery(sql, id) > 0;
    }

    private IncurredFee map(ResultSet rs) throws SQLException {
        IncurredFee b = new IncurredFee();
        b.setIncurredFeeID(rs.getInt("IncurredFeeID"));
        b.setBillID(rs.getInt("BillID"));
        b.setIncurredFeeTypeID(rs.getInt("IncurredFeeTypeID"));
        b.setAmount(rs.getBigDecimal("Amount"));
        return b;
    }

    public List<IncurredFee> getDefaultFeesForRoom(int roomId) {
        List<IncurredFee> list = new ArrayList<>();
        String sql = "SELECT IncurredFeeTypeID, DefaultAmount FROM IncurredFeeTypes";

        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                IncurredFee fee = new IncurredFee();
                fee.setIncurredFeeTypeID(rs.getInt("IncurredFeeTypeID"));
                fee.setAmount(rs.getBigDecimal("DefaultAmount"));
                list.add(fee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insertFee(IncurredFee fee) {
        String sql = "INSERT INTO IncurredFees (BillID, IncurredFeeTypeID, Amount) VALUES (?, ?, ?)";

        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, fee.getBillID());
            ps.setInt(2, fee.getIncurredFeeTypeID());
            ps.setBigDecimal(3, fee.getAmount());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<IncurredFee> getFeesByRoomAndMonth(int roomId, String month) throws SQLException {
        List<IncurredFee> list = new ArrayList<>();
        String sql
                = "SELECT f.* "
                + "FROM IncurredFees f "
                + "JOIN Bills b ON f.BillID = b.BillID "
                + "JOIN Contracts c ON b.ContractID = c.ContractID "
                + "WHERE c.RoomID = ? AND FORMAT(b.BillDate, 'yyyy-MM') = ?";

        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setString(2, month);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                IncurredFee f = new IncurredFee();
                f.setIncurredFeeID(rs.getInt("IncurredFeeID"));
                f.setBillID(rs.getInt("BillID"));
                f.setIncurredFeeTypeID(rs.getInt("IncurredFeeTypeID"));
                f.setAmount(rs.getBigDecimal("Amount"));
                list.add(f);
            }
        }
        return list;
    }

    public List<IncurredFee> getFeesByBillId(int billId) {
        List<IncurredFee> list = new ArrayList<>();
        String sql = "SELECT * FROM IncurredFees WHERE BillID = ?";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                IncurredFee fee = new IncurredFee();
                fee.setIncurredFeeID(rs.getInt("IncurredFeeID"));
                fee.setBillID(rs.getInt("BillID"));
                fee.setIncurredFeeTypeID(rs.getInt("IncurredFeeTypeID"));
                fee.setAmount(rs.getBigDecimal("Amount"));
                list.add(fee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateFeeWithBillId(IncurredFee fee) {
        String sql = "UPDATE IncurredFee SET billID = ? WHERE incurredFeeID = ?";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fee.getBillID());
            ps.setInt(2, fee.getIncurredFeeID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteFeesByBillId(int billId) throws SQLException {
        String sql = "DELETE FROM IncurredFees WHERE BillID = ?";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            ps.executeUpdate();
        }
    }

}
