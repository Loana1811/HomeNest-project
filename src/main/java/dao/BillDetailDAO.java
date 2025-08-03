package dao;

import model.BillDetail;
import utils.DBContext;

import java.sql.*;
import java.util.*;

public class BillDetailDAO {
    private final DBContext dbContext = new DBContext();

    public List<BillDetail> getAllBills() throws SQLException {
        List<BillDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM Bills";
        try (Connection c = dbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public BillDetail getBillDetailById(int id) throws SQLException {
        String sql = "SELECT * FROM Bills WHERE BillID = ?";
        try (Connection c = dbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public boolean deleteBillDetail(int billId) throws SQLException {
        String sql = "DELETE FROM Bills WHERE BillID = ?";
        return dbContext.execUpdateQuery(sql, billId) > 0;
    }

    // ✅ Chỉ lấy các trường cơ bản có trong bảng Bills
    private BillDetail map(ResultSet rs) throws SQLException {
        BillDetail b = new BillDetail();
        b.setBillID(rs.getInt("BillID"));
        b.setContractID(rs.getInt("ContractID"));
        b.setBillDate(rs.getDate("BillDate"));
        b.setBillStatus(rs.getString("BillStatus"));
        b.setIsSent(rs.getBoolean("IsSent"));
        b.setReplacedBillID(rs.getObject("ReplacedBillID") != null ? rs.getInt("ReplacedBillID") : null);
        return b;
    }
}
