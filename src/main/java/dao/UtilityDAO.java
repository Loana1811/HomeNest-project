/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.UtilityReading;
import model.UtilityType;
import utils.DBContext;

/**
 *
 * @author kloane
 */
public class UtilityDAO {
     public List<UtilityReading> getReadingHistory(int roomId, int utilityTypeId, Date readingDate) {
        List<UtilityReading> list = new ArrayList<>();
        String sql = "SELECT * FROM UtilityReadings WHERE RoomID=? AND UtilityTypeID=? AND ReadingDate=? ORDER BY UtilityReadingCreatedAt DESC";

      try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setInt(2, utilityTypeId);
            ps.setDate(3, readingDate);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UtilityReading ur = new UtilityReading();
                ur.setReadingId(rs.getInt("ReadingID"));
                ur.setRoomID(rs.getInt("RoomID"));
                ur.setUtilityTypeID(rs.getInt("UtilityTypeID"));
                ur.setReadingDate(rs.getDate("ReadingDate"));
                ur.setOldReading(rs.getBigDecimal("OldReading"));
                ur.setNewReading(rs.getBigDecimal("NewReading"));
                ur.setPriceUsed(rs.getBigDecimal("PriceUsed"));
                ur.setChangedBy(rs.getString("ChangedBy"));
                ur.setUtilityReadingCreatedAt(rs.getTimestamp("UtilityReadingCreatedAt"));
                list.add(ur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

  public List<UtilityType> getAllUtilityTypes() {
        List<UtilityType> list = new ArrayList<>();
        String sql = "SELECT UtilityTypeID, UtilityName, UnitPrice, Unit, IsSystem FROM UtilityTypes";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UtilityType u = new UtilityType();
                u.setUtilityTypeID(rs.getInt("UtilityTypeID"));
                u.setUtilityName(rs.getString("UtilityName"));
                u.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                u.setUnit(rs.getString("Unit"));
                u.setIsSystem(rs.getBoolean("IsSystem"));
                list.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}