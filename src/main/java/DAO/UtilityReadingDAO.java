// UtilityReadingDAO.java
package dao;

import model.UtilityReading;
import utils.DBContext;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UtilityReadingDAO {
    private Connection conn;

    public UtilityReadingDAO() {
        try {
            conn = new DBContext().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy tất cả bản ghi chỉ số
    public List<UtilityReading> getAllUtilityReadings() {
        List<UtilityReading> list = new ArrayList<>();
        String sql = "SELECT ReadingID, UtilityTypeID, RoomID, ReadingDate, OldReading, NewReading, PriceUsed, OldPrice, ChangedBy, UtilityReadingCreatedAt "
                   + "FROM UtilityReadings";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new UtilityReading(
                    rs.getInt("ReadingID"),
                    rs.getInt("UtilityTypeID"),
                    rs.getInt("RoomID"),
                    rs.getDate("ReadingDate"),
                    rs.getBigDecimal("OldReading"),
                    rs.getBigDecimal("NewReading"),
                    rs.getBigDecimal("PriceUsed"),
                    rs.getBigDecimal("OldPrice"),
                    rs.getString("ChangedBy"),
                    rs.getTimestamp("UtilityReadingCreatedAt")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy theo ID
    public UtilityReading getUtilityReadingById(int id) {
        String sql = "SELECT ReadingID, UtilityTypeID, RoomID, ReadingDate, OldReading, NewReading, PriceUsed, OldPrice, ChangedBy, UtilityReadingCreatedAt "
                   + "FROM UtilityReadings WHERE ReadingID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UtilityReading(
                        rs.getInt("ReadingID"),
                        rs.getInt("UtilityTypeID"),
                        rs.getInt("RoomID"),
                        rs.getDate("ReadingDate"),
                        rs.getBigDecimal("OldReading"),
                        rs.getBigDecimal("NewReading"),
                        rs.getBigDecimal("PriceUsed"),
                        rs.getBigDecimal("OldPrice"),
                        rs.getString("ChangedBy"),
                        rs.getTimestamp("UtilityReadingCreatedAt")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm mới
    public void addUtilityReading(UtilityReading u) {
        String sql = "INSERT INTO UtilityReadings (UtilityTypeID, RoomID, ReadingDate, OldReading, NewReading, PriceUsed, OldPrice, ChangedBy) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, u.getUtilityTypeID());
            ps.setInt(2, u.getRoomID());
            ps.setDate(3, u.getReadingDate());
            ps.setBigDecimal(4, u.getOldReading());
            ps.setBigDecimal(5, u.getNewReading());
            ps.setBigDecimal(6, u.getPriceUsed());
            ps.setBigDecimal(7, u.getOldPrice());
            ps.setString(8, u.getChangedBy());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cập nhật
    public void updateUtilityReading(UtilityReading u) {
        String sql = "UPDATE UtilityReadings SET UtilityTypeID = ?, RoomID = ?, ReadingDate = ?, OldReading = ?, NewReading = ?, PriceUsed = ?, OldPrice = ?, ChangedBy = ? "
                   + "WHERE ReadingID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, u.getUtilityTypeID());
            ps.setInt(2, u.getRoomID());
            ps.setDate(3, u.getReadingDate());
            ps.setBigDecimal(4, u.getOldReading());
            ps.setBigDecimal(5, u.getNewReading());
            ps.setBigDecimal(6, u.getPriceUsed());
            ps.setBigDecimal(7, u.getOldPrice());
            ps.setString(8, u.getChangedBy());
            ps.setInt(9, u.getReadingID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Xóa
    public void deleteUtilityReading(int id) {
        String sql = "DELETE FROM UtilityReadings WHERE ReadingID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy theo phòng
    public List<UtilityReading> getUtilityReadingsByRoomId(int roomId) {
        List<UtilityReading> list = new ArrayList<>();
        String sql = "SELECT ReadingID, UtilityTypeID, RoomID, ReadingDate, OldReading, NewReading, PriceUsed, OldPrice, ChangedBy, UtilityReadingCreatedAt "
                   + "FROM UtilityReadings WHERE RoomID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new UtilityReading(
                        rs.getInt("ReadingID"),
                        rs.getInt("UtilityTypeID"),
                        rs.getInt("RoomID"),
                        rs.getDate("ReadingDate"),
                        rs.getBigDecimal("OldReading"),
                        rs.getBigDecimal("NewReading"),
                        rs.getBigDecimal("PriceUsed"),
                        rs.getBigDecimal("OldPrice"),
                        rs.getString("ChangedBy"),
                        rs.getTimestamp("UtilityReadingCreatedAt")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
