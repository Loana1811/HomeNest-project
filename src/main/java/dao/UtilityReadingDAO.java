/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.UtilityReading;
import model.UtilityUsageView;
import utils.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilityReadingDAO {

    private final DBContext dbContext = new DBContext();

    // Lấy tất cả bản ghi UtilityReading
    public List<UtilityReading> getAllUtilityReadings() throws SQLException {
        List<UtilityReading> list = new ArrayList<>();
        String sql = "SELECT * FROM UtilityReadings";
        try ( Connection c = dbContext.getConnection();  PreparedStatement ps = c.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    // Lấy theo ID
    public UtilityReading getUtilityReadingById(int id) throws SQLException {
        String sql = "SELECT * FROM UtilityReadings WHERE ReadingID=?";
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

    // Thêm bản ghi UtilityReading
    public boolean insertUtilityReading(UtilityReading b) throws SQLException {
        String sql = "INSERT INTO UtilityReadings "
                + "(UtilityTypeID, RoomID, ReadingDate, OldReading, NewReading, PriceUsed, OldPrice, ChangedBy, UtilityReadingCreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";
        return dbContext.execUpdateQuery(sql, b.getUtilityTypeID(), b.getRoomID(), b.getReadingDate(),
                b.getOldReading(), b.getNewReading(), b.getPriceUsed(), b.getOldPrice(), b.getChangedBy()) > 0;
    }

    // Update bản ghi
    public boolean updateUtilityReading(UtilityReading b) throws SQLException {
        String sql = "UPDATE UtilityReadings SET UtilityTypeID=?, RoomID=?, ReadingDate=?, OldReading=?, NewReading=?, PriceUsed=?, OldPrice=?, ChangedBy=? WHERE ReadingID=?";
        return dbContext.execUpdateQuery(sql, b.getUtilityTypeID(), b.getRoomID(), b.getReadingDate(),
                b.getOldReading(), b.getNewReading(), b.getPriceUsed(), b.getOldPrice(), b.getChangedBy(), b.getReadingID()) > 0;
    }

    // Xoá bản ghi
    public boolean deleteUtilityReading(int id) throws SQLException {
        String sql = "DELETE FROM UtilityReadings WHERE ReadingID=?";
        return dbContext.execUpdateQuery(sql, id) > 0;
    }

    public void insert(UtilityReading ur) throws SQLException {
        String sql = "INSERT INTO UtilityReadings "
                + "(RoomID, UtilityTypeID, ReadingDate, OldReading, NewReading, PriceUsed, ChangedBy, UtilityReadingCreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ur.getRoomID());
            ps.setInt(2, ur.getUtilityTypeID());
            ps.setDate(3, ur.getReadingDate());  // Hoặc GETDATE() tuỳ field
            ps.setBigDecimal(4, ur.getOldReading());
            ps.setBigDecimal(5, ur.getNewReading());
            ps.setBigDecimal(6, ur.getPriceUsed());
            ps.setString(7, ur.getChangedBy());
            ps.executeUpdate();
        }
    }

    // Lấy chỉ số mới nhất của 1 phòng & utility
    public double getLatestIndex(int roomId, int utilityTypeId) throws SQLException {
      String sql = "SELECT TOP 1 NewReading FROM UtilityReadings WHERE RoomID = ? AND UtilityTypeID = ? ORDER BY UtilityReadingCreatedAt DESC";

        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setInt(2, utilityTypeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }

    // Trả về danh sách sử dụng tiện ích (View)
    public List<UtilityUsageView> getAllUsages() throws SQLException {
    List<UtilityUsageView> list = new ArrayList<>();

    String sql = "SELECT ur.ReadingID, r.RoomNumber, ut.UtilityName, " +
                 "       ur.OldReading AS OldIndex, " +
                 "       ur.NewReading AS NewIndex, " +
                 "       ut.UnitPrice AS UnitPrice, " +
                 "       (ur.NewReading - ur.OldReading) * ut.UnitPrice AS PriceUsed, " +
                 "       ur.ChangedBy, " +
                 "       ur.ReadingDate AS ReadingDate " +
                 "FROM UtilityReadings ur " +
                 "JOIN Rooms r ON ur.RoomID = r.RoomID " +
                 "JOIN UtilityTypes ut ON ur.UtilityTypeID = ut.UtilityTypeID " +
                 "WHERE (ur.OldReading > 0 OR ur.NewReading > 0) " +         // ⚠️ bỏ bản ghi 0-0
                 "AND (ur.ChangedBy NOT LIKE '%|%') " +                      // ⚠️ bỏ bản ghi history giá
                 "ORDER BY ur.ReadingDate DESC";

    try (
        Connection conn = dbContext.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()
    ) {
        while (rs.next()) {
            list.add(new UtilityUsageView(
                rs.getInt("ReadingID"),
                rs.getString("RoomNumber"),
                rs.getString("UtilityName"),
                rs.getDouble("OldIndex"),
                rs.getDouble("NewIndex"),
                rs.getDouble("PriceUsed"),
                rs.getString("ChangedBy"),
                rs.getDate("ReadingDate")
            ));
        }
    }

    return list;
}


    // Gán utility cho phòng (init reading = 0)
    public void assignUtilityToRoom(int roomId, int utilityTypeId) throws SQLException {
        String sql = "INSERT INTO UtilityReadings (RoomID, UtilityTypeID, ReadingDate, OldReading, NewReading) VALUES (?, ?, GETDATE(), 0, 0)";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setInt(2, utilityTypeId);
            ps.executeUpdate();
        }
    }

    // Xoá reading = 0 theo utilityType
    public void deleteZeroReadingsByUtilityType(int utilityTypeId) throws SQLException {
        String sql = "DELETE FROM UtilityReadings WHERE UtilityTypeID = ? AND OldReading = 0 AND NewReading = 0";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, utilityTypeId);
            ps.executeUpdate();
        }
    }

    // Map ResultSet -> UtilityReading
    private UtilityReading map(ResultSet rs) throws SQLException {
        UtilityReading b = new UtilityReading();
        b.setReadingID(rs.getInt("ReadingID"));
        b.setUtilityTypeID(rs.getInt("UtilityTypeID"));
        b.setRoomID(rs.getInt("RoomID"));
        b.setReadingDate(rs.getDate("ReadingDate"));
        b.setOldReading(rs.getBigDecimal("OldReading"));
        b.setNewReading(rs.getBigDecimal("NewReading"));
        b.setPriceUsed(rs.getBigDecimal("PriceUsed"));
        b.setOldPrice(rs.getBigDecimal("OldPrice"));
        b.setChangedBy(rs.getString("ChangedBy"));
        return b;
    }
}
