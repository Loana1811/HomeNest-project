/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.UtilityReading;
import utils.DBContext;

/**
 *
 * @author kloane
 */
public class UtilityReadingDAO {


   public void insert(UtilityReading ur) throws SQLException {
    String sql =
        "INSERT INTO UtilityReadings " +
        "(RoomID, UtilityTypeID, ReadingDate, OldReading, NewReading, PriceUsed, ChangedBy, CreatedAt) " +
        "VALUES (?, ?, GETDATE(), ?, ?, ?, ?, GETDATE())";
    try (Connection conn = new DBContext().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, ur.getRoomId());
        ps.setInt(2, ur.getUtilityTypeId());
        ps.setDouble(3, ur.getOldIndex());   // maps to OldReading
        ps.setDouble(4, ur.getNewIndex());   // maps to NewReading
        ps.setDouble(5, ur.getPriceUsed());
        ps.setString(6, ur.getChangedBy());
        ps.executeUpdate();
    }
}


    public double getLatestIndex(int roomId, int utilityTypeId) throws SQLException {
        String sql =
            "SELECT TOP 1 NewReading " +
            "FROM UtilityReadings " +
            "WHERE RoomID = ? AND UtilityTypeID = ? " +
            "ORDER BY CreatedAt DESC";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setInt(2, utilityTypeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }
}
