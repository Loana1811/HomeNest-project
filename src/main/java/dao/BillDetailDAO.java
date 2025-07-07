package dao;

import model.BillDetail;
import utils.DBContext;
import java.sql.*;
import java.util.*;

public class BillDetailDAO {
    private final DBContext dbContext = new DBContext();

    public List<BillDetail> getAllBillDetails() throws SQLException {
        List<BillDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM BillDetails";
        try(Connection c = dbContext.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while(rs.next()) list.add(map(rs));
        }
        return list;
    }

   public BillDetail getBillDetailById(int id) throws SQLException {
    String sql = "SELECT bd.*, r.RentPrice " +
                 "FROM BillDetails bd " +
                 "JOIN Bills b ON bd.BillID = b.BillID " +
                 "JOIN Contracts c ON b.ContractID = c.ContractID " +
                 "JOIN Rooms r ON c.RoomID = r.RoomID " +
                 "WHERE bd.BillID = ?";

    try (Connection c = dbContext.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, id); // id là BillID
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                BillDetail b = map(rs);
                b.setRoomrent(rs.getFloat("RentPrice")); // Lấy tiền phòng từ bảng Rooms
                return b;
            }
        }
    }
    return null;
}


    public boolean insertBillDetail(BillDetail b) throws SQLException {
        String sql = "INSERT INTO BillDetails (BillID, RoomRent, ElectricityCost, WaterCost, WifiCost) VALUES (?, ?, ?, ?, ?)";
        return dbContext.execUpdateQuery(sql, b.getBillID(), b.getRoomrent(), b.getElectricityCost(), b.getWaterCost(), b.getWifiCost()) > 0;
    }

    public boolean updateBillDetail(BillDetail b) throws SQLException {
        String sql = "UPDATE BillDetails SET BillID=?, RoomRent=?, ElectricityCost=?, WaterCost=?, WifiCost=? WHERE BillDetailID=?";
        return dbContext.execUpdateQuery(sql, b.getBillID(), b.getRoomrent(), b.getElectricityCost(), b.getWaterCost(), b.getWifiCost(), b.getBillDetailID()) > 0;
    }

    public boolean deleteBillDetail(int id) throws SQLException {
        String sql = "DELETE FROM BillDetails WHERE BillDetailID=?";
        return dbContext.execUpdateQuery(sql, id) > 0;
    }

    private BillDetail map(ResultSet rs) throws SQLException {
        BillDetail b = new BillDetail();
        b.setBillDetailID(rs.getInt("BillDetailID"));
        b.setBillID(rs.getInt("BillID"));
        b.setRoomrent(rs.getFloat("RoomRent"));
        b.setElectricityCost(rs.getFloat("ElectricityCost"));
        b.setWaterCost(rs.getFloat("WaterCost"));
        b.setWifiCost(rs.getFloat("WifiCost"));
        return b;
    }
}
