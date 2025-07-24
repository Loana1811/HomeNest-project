/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.math.BigDecimal;
import model.UtilityReading;
import model.UtilityUsageView;
import utils.DBContext;
import java.sql.*;
import java.time.LocalDate;
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

    public boolean isReadingExists(int roomId, int utilityTypeId, String month) throws SQLException {
        String sql = "SELECT COUNT(*) FROM UtilityReadings WHERE RoomID = ? AND UtilityTypeID = ? AND FORMAT(ReadingDate, 'yyyy-MM') = ?";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setInt(2, utilityTypeId);
            ps.setString(3, month);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Lấy theo ID
//    public UtilityReading getUtilityReadingById(int id) throws SQLException {
//        String sql = "SELECT * FROM UtilityReadings WHERE ReadingID=?";
//        try ( Connection c = dbContext.getConnection();  PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setInt(1, id);
//            try ( ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    return map(rs);
//                }
//            }
//        }
//        return null;
//    }
    public UtilityReading getUtilityReadingById(int id) throws SQLException {
        String sql = "SELECT * FROM UtilityReadings WHERE ReadingID = ?";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UtilityReading u = new UtilityReading();
                u.setReadingID(rs.getInt("ReadingID"));
                u.setRoomID(rs.getInt("RoomID"));
                u.setUtilityTypeID(rs.getInt("UtilityTypeID"));
                u.setOldReading(rs.getBigDecimal("OldReading"));
                u.setNewReading(rs.getBigDecimal("NewReading"));
                u.setPriceUsed(rs.getBigDecimal("PriceUsed"));
                u.setReadingDate(rs.getDate("ReadingDate"));
                u.setLocked(rs.getBoolean("IsLocked"));
                return u;
            }
        }
        return null;
    }

    // Thêm bản ghi UtilityReading
    public boolean insertUtilityReading(UtilityReading b) throws SQLException {
        String sql = "INSERT INTO UtilityReadings "
                + "(UtilityTypeID, RoomID, ReadingDate, OldReading, NewReading, PriceUsed, OldPrice, UtilityReadingCreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";
        return dbContext.execUpdateQuery(sql,
                b.getUtilityTypeID(),
                b.getRoomID(),
                b.getReadingDate(),
                b.getOldReading(),
                b.getNewReading(),
                b.getPriceUsed(),
                b.getOldPrice()) > 0;
    }

    public boolean updateUtilityReading(UtilityReading b) throws SQLException {
        String sql = "UPDATE UtilityReadings SET UtilityTypeID=?, RoomID=?, ReadingDate=?, OldReading=?, NewReading=?, PriceUsed=?, OldPrice=? WHERE ReadingID=?";
        return dbContext.execUpdateQuery(sql,
                b.getUtilityTypeID(),
                b.getRoomID(),
                b.getReadingDate(),
                b.getOldReading(),
                b.getNewReading(),
                b.getPriceUsed(),
                b.getOldPrice(),
                b.getReadingID()) > 0;
    }

    // Xoá bản ghi
    public boolean deleteUtilityReading(int id) throws SQLException {
        String sql = "DELETE FROM UtilityReadings WHERE ReadingID=?";
        return dbContext.execUpdateQuery(sql, id) > 0;
    }

    public void insert(UtilityReading ur) throws SQLException {
        String sql = "INSERT INTO UtilityReadings "
                + "(RoomID, UtilityTypeID, ReadingDate, OldReading, NewReading, PriceUsed, UtilityReadingCreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ur.getRoomID());
            ps.setInt(2, ur.getUtilityTypeID());
            ps.setDate(3, ur.getReadingDate());
            ps.setBigDecimal(4, ur.getOldReading());
            ps.setBigDecimal(5, ur.getNewReading());
            ps.setBigDecimal(6, ur.getPriceUsed());
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

        String sql = "SELECT ur.ReadingID, r.RoomNumber, ut.UtilityName, "
                + "       ur.OldReading AS OldIndex, "
                + "       ur.NewReading AS NewIndex, "
                + "       ut.UnitPrice AS UnitPrice, "
                + "       (ur.NewReading - ur.OldReading) * ut.UnitPrice AS PriceUsed, "
                + "       ur.ReadingDate AS ReadingDate, "
                + "       b.BlockName AS BlockName "
                + "FROM UtilityReadings ur "
                + "JOIN Rooms r ON ur.RoomID = r.RoomID "
                + "JOIN Blocks b ON r.BlockID = b.BlockID "
                + "JOIN UtilityTypes ut ON ur.UtilityTypeID = ut.UtilityTypeID "
                + "WHERE (ur.OldReading > 0 OR ur.NewReading > 0) "
                + "ORDER BY b.BlockName, r.RoomNumber, ur.ReadingDate DESC";

        try (
                 Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new UtilityUsageView(
                        rs.getInt("ReadingID"),
                        rs.getString("RoomNumber"),
                        rs.getString("UtilityName"),
                        rs.getDouble("OldIndex"),
                        rs.getDouble("NewIndex"),
                        rs.getDouble("PriceUsed"),
                        rs.getDate("ReadingDate"),
                        rs.getString("BlockName")
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
        return b;
    }

    public List<UtilityReading> getReadingsForRoomAndMonth(int roomId, String month) {
        List<UtilityReading> readings = new ArrayList<>();
        String sql
                = "SELECT * FROM UtilityReadings "
                + "WHERE RoomID = ? AND FORMAT(ReadingDate, 'yyyy-MM') = ?";

        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            ps.setString(2, month);

            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UtilityReading r = new UtilityReading();
                    r.setReadingID(rs.getInt("ReadingID"));
                    r.setRoomID(rs.getInt("RoomID"));
                    r.setUtilityTypeID(rs.getInt("UtilityTypeID"));
                    r.setReadingDate(rs.getDate("ReadingDate"));
                    r.setOldReading(rs.getBigDecimal("OldReading"));
                    r.setNewReading(rs.getBigDecimal("NewReading"));
                    r.setPriceUsed(rs.getBigDecimal("PriceUsed"));
                    r.setOldPrice(rs.getBigDecimal("OldPrice"));

                    r.setUtilityReadingCreatedAt(rs.getTimestamp("UtilityReadingCreatedAt"));
                    readings.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return readings;
    }

    public List<UtilityReading> getReadingsByRoomAndMonth(int roomId, String month) throws SQLException {
        List<UtilityReading> list = new ArrayList<>();
        String sql = "SELECT * FROM UtilityReadings "
                + "WHERE RoomID = ? AND FORMAT(ReadingDate, 'yyyy-MM') = ?";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setString(2, month);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UtilityReading u = new UtilityReading();
                u.setReadingID(rs.getInt("ReadingID"));
                u.setRoomID(roomId);
                u.setUtilityTypeID(rs.getInt("UtilityTypeID"));
                u.setOldReading(rs.getBigDecimal("OldReading"));
                u.setNewReading(rs.getBigDecimal("NewReading"));
                u.setPriceUsed(rs.getBigDecimal("PriceUsed"));

                u.setReadingDate(rs.getDate("ReadingDate"));

                list.add(u);
            }
        }
        return list;
    }
// Lấy chỉ số mới nhất (newReading) của phòng, loại tiện ích, *TRƯỚC* tháng đang chọn

    public double getLatestIndex(int roomId, int utilityTypeId, String readingMonth) {
        String sql = "SELECT TOP 1 NewReading FROM [UtilityReadings] "
                + "WHERE RoomID = ? AND UtilityTypeID = ? AND ReadingDate <= ? "
                + "ORDER BY UtilityReadingCreatedAt DESC";

        // Convert "2025-07" to LocalDate "2025-07-01"
        LocalDate currentMonthStart = LocalDate.parse(readingMonth + "-01");

        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            ps.setInt(2, utilityTypeId);
            ps.setDate(3, java.sql.Date.valueOf(currentMonthStart));

            System.out.println("🔍 Querying old index for RoomID = " + roomId
                    + ", UtilityTypeID = " + utilityTypeId
                    + ", Before = " + currentMonthStart);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double oldIndex = rs.getDouble("NewReading");
                System.out.println("✅ Found old reading: " + oldIndex);
                return oldIndex;
            } else {
                System.out.println("⚠️ No old reading found.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error in getLatestIndex: " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }

    public UtilityReading getReading(int roomId, int utilityTypeId, String month) {
        // month dạng "yyyy-MM"
        String sql = "SELECT TOP 1 * FROM UtilityReadings "
                + "WHERE roomID = ? AND utilityTypeID = ? AND FORMAT(readingDate,'yyyy-MM') = ? "
                + "ORDER BY readingDate DESC";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setInt(2, utilityTypeId);
            ps.setString(3, month);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // mapping fields (bạn cần sửa lại tên trường cho đúng DB nếu khác)
                UtilityReading ur = new UtilityReading();
                ur.setReadingID(rs.getInt("readingID"));
                ur.setUtilityTypeID(rs.getInt("utilityTypeID"));
                ur.setRoomID(rs.getInt("roomID"));
                ur.setReadingDate(rs.getDate("readingDate"));
                ur.setOldReading(rs.getBigDecimal("oldReading"));
                ur.setNewReading(rs.getBigDecimal("newReading"));
                ur.setPriceUsed(rs.getBigDecimal("priceUsed"));

                // ... các trường khác nếu có
                return ur;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasUtilityReading(int roomId, String month) throws SQLException {
        return getReadingsByRoomAndMonth(roomId, month).size() > 0;
    }

//    return getReadingsByRoomAndMonth(roomId, month).size() > 0;
    public void insertOrUpdate(int roomId, int utilityTypeId, String readingMonth, double newIndex) throws SQLException {
        // Lấy chỉ số cũ trước tháng đang ghi
        Double oldIndex = getLatestIndex(roomId, utilityTypeId, readingMonth);

        // Kiểm tra xem đã tồn tại chỉ số của tháng này chưa
        UtilityReading existing = getReading(roomId, utilityTypeId, readingMonth);

        if (existing != null) {
            // Nếu đã có -> update chỉ số mới
            String updateSql = "UPDATE UtilityReadings SET NewReading = ?, OldReading = ?, UtilityReadingCreatedAt = GETDATE() "
                    + "WHERE ReadingID = ?";
            try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setDouble(1, newIndex);
                ps.setDouble(2, oldIndex);
                ps.setString(3, "admin");
                ps.setInt(4, existing.getReadingID());
                ps.executeUpdate();
            }
        } else {
            // Nếu chưa có -> insert mới
            String insertSql = "INSERT INTO UtilityReadings (RoomID, UtilityTypeID, ReadingDate, OldReading, NewReading, UtilityReadingCreatedAt) "
                    + "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
            try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, roomId);
                ps.setInt(2, utilityTypeId);

                // Chuyển yyyy-MM -> java.sql.Date
                LocalDate date = LocalDate.parse(readingMonth + "-01");
                ps.setDate(3, Date.valueOf(date));

                ps.setDouble(4, oldIndex);
                ps.setDouble(5, newIndex);
                ps.setString(6, "admin");
                ps.executeUpdate();
            }
        }
    }

//    public void updateReading(UtilityReading ur) {
//        String sql = "UPDATE UtilityReadings SET OldReading=?, NewReading=?, PriceUsed=?, ChangedBy=?, UtilityReadingCreatedAt=? "
//                + "WHERE RoomID=? AND UtilityTypeID=? AND ReadingDate=?";
//        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setBigDecimal(1, ur.getOldReading());
//            ps.setBigDecimal(2, ur.getNewReading());
//            ps.setBigDecimal(3, ur.getPriceUsed());
//            ps.setString(4, ur.getChangedBy());
//            ps.setTimestamp(5, ur.getUtilityReadingCreatedAt());
//            ps.setInt(6, ur.getRoomID());
//            ps.setInt(7, ur.getUtilityTypeID());
//            ps.setDate(8, ur.getReadingDate());
//            ps.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
    public void updateReading(UtilityReading ur) throws SQLException {
        String sql = "UPDATE UtilityReadings SET NewReading = ?, PriceUsed = ?, UtilityReadingCreatedAt = GETDATE() WHERE ReadingID = ?";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, ur.getNewReading());
            ps.setBigDecimal(2, ur.getPriceUsed());
            ps.setInt(3, ur.getReadingID());
            ps.executeUpdate();
        }
    }

    public List<UtilityReading> getLatestReadingsByRoomAndMonth(int roomId, String month) {
        List<UtilityReading> readings = new ArrayList<>();
        String sql
                = "SELECT ur.* "
                + "FROM UtilityReadings ur "
                + "JOIN ( "
                + "    SELECT UtilityTypeID, MAX(UtilityReadingCreatedAt) AS MaxDate "
                + "    FROM UtilityReadings "
                + "    WHERE RoomID = ? AND YEAR(ReadingDate) = ? AND MONTH(ReadingDate) = ? "
                + "    GROUP BY UtilityTypeID "
                + ") latest ON ur.UtilityTypeID = latest.UtilityTypeID "
                + "         AND ur.UtilityReadingCreatedAt = latest.MaxDate "
                + "WHERE ur.RoomID = ? AND YEAR(ur.ReadingDate) = ? AND MONTH(ur.ReadingDate) = ?";

        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            LocalDate parsedDate = LocalDate.parse(month + "-01");
            int year = parsedDate.getYear();
            int monthValue = parsedDate.getMonthValue();

            ps.setInt(1, roomId);
            ps.setInt(2, year);
            ps.setInt(3, monthValue);
            ps.setInt(4, roomId);
            ps.setInt(5, year);
            ps.setInt(6, monthValue);

            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UtilityReading ur = new UtilityReading();
                    ur.setReadingID(rs.getInt("ReadingID"));
                    ur.setRoomID(rs.getInt("RoomID"));
                    ur.setUtilityTypeID(rs.getInt("UtilityTypeID"));
                    ur.setReadingDate(rs.getDate("ReadingDate"));
                    ur.setOldReading(rs.getBigDecimal("OldReading"));
                    ur.setNewReading(rs.getBigDecimal("NewReading"));
                    ur.setPriceUsed(rs.getBigDecimal("PriceUsed"));
                    ur.setOldPrice(rs.getBigDecimal("OldPrice"));
                    ur.setUtilityReadingCreatedAt(rs.getTimestamp("UtilityReadingCreatedAt"));
                    readings.add(ur);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return readings;
    }

    public boolean hasReadingForMonth(int roomId, String month) {
        String sql = "SELECT COUNT(*) FROM UtilityReadings WHERE RoomID = ? AND FORMAT(ReadingDate, 'yyyy-MM') = ?";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setString(2, month);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<UtilityUsageView> getUsagesByRoomName(String roomName, String month) throws SQLException {
        String sql = "SELECT ur.ReadingID, r.RoomNumber, ut.UtilityName, "
                + "ur.OldReading AS OldIndex, ur.NewReading AS NewIndex, "
                + "(ur.NewReading - ur.OldReading) * ut.UnitPrice AS PriceUsed, "
                + "ur.ReadingDate AS ReadingDate, b.BlockName AS BlockName "
                + "FROM UtilityReadings ur "
                + "JOIN Rooms r ON ur.RoomID = r.RoomID "
                + "JOIN Blocks b ON r.BlockID = b.BlockID "
                + "JOIN UtilityTypes ut ON ur.UtilityTypeID = ut.UtilityTypeID "
                + "WHERE r.RoomNumber = ? "
                + (month != null && !month.isEmpty() ? "AND FORMAT(ur.ReadingDate, 'yyyy-MM') = ? " : "")
                + "ORDER BY ur.ReadingDate DESC";

        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomName);
            if (month != null && !month.isEmpty()) {
                ps.setString(2, month);
            }

            ResultSet rs = ps.executeQuery();
            List<UtilityUsageView> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new UtilityUsageView(
                        rs.getInt("ReadingID"),
                        rs.getString("RoomNumber"),
                        rs.getString("UtilityName"),
                        rs.getDouble("OldIndex"),
                        rs.getDouble("NewIndex"),
                        rs.getDouble("PriceUsed"),
                        rs.getDate("ReadingDate"),
                        rs.getString("BlockName")
                ));
            }
            return list;
        }
    }

    public void lockReadingByRoomMonth(int roomId, String month, String user) throws SQLException {
        String sql = "UPDATE UtilityReadings SET IsLocked = 1, LockedBy = ?, LockedAt = GETDATE() WHERE RoomID = ? AND FORMAT(ReadingDate, 'yyyy-MM') = ?";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setInt(2, roomId);
            ps.setString(3, month);
            ps.executeUpdate();

        }
    }

}
