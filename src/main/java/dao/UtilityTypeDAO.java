/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.UtilityType;
import utils.DBContext;

/**
 *
 * @author kloane
 */
public class UtilityTypeDAO {

    public List<UtilityType> getAll() throws SQLException {
    List<UtilityType> list = new ArrayList<>();
    String sql = "SELECT * FROM UtilityTypes ORDER BY UtilityTypeID";
    try (Connection conn = new DBContext().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            int id = rs.getInt("UtilityTypeID");
            String name = rs.getString("UtilityName");
            BigDecimal price = rs.getBigDecimal("UnitPrice");
            String unit = rs.getString("Unit");
            list.add(new UtilityType(id, name, price, unit));
        }
    }
    return list;
}


    public UtilityType getById(int id) throws SQLException {
        String sql = "SELECT * FROM UtilityTypes WHERE UtilityTypeID = ?";
        try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new UtilityType(
                        id,
                        rs.getString("UtilityName"),
                        rs.getBigDecimal("UnitPrice"),
                        rs.getString("Unit")
//                        rs.getBoolean("IsSystem")
                );

            }
        }
        return null;
    }

    public void insert(UtilityType u) throws SQLException {
        String sql = "INSERT INTO UtilityTypes (UtilityName, UnitPrice, Unit) VALUES (?, ?, ?)";
        try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUtilityName());
            ps.setBigDecimal(2, u.getUnitPrice());
            ps.setString(3, u.getUnit());
            ps.executeUpdate();
        }
    }

    public void update(UtilityType u) throws SQLException {
        String sql = "UPDATE UtilityTypes SET UtilityName = ?, UnitPrice = ?, Unit = ? WHERE UtilityTypeID = ?";
        try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUtilityName());
            ps.setBigDecimal(2, u.getUnitPrice());
            ps.setString(3, u.getUnit());
            ps.setInt(4, u.getUtilityTypeID());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM UtilityTypes WHERE UtilityTypeID = ?";
        try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Object[]> getAllTypeIdName() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT UtilityTypeID, UtilityName FROM UtilityTypes";
        try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getInt("UtilityTypeID"), rs.getString("UtilityName")});
            }
        }
        return list;
    }

    public int getLastInsertedId() throws SQLException {
        String sql = "SELECT TOP 1 UtilityTypeID FROM UtilityTypes ORDER BY UtilityTypeID DESC";
        try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

   public boolean isUtilityTypeInUse(int utilityTypeId) throws SQLException {
    String sql = "SELECT COUNT(*) FROM UtilityReadings WHERE UtilityTypeID = ? AND (OldReading > 0 OR NewReading > 0)";

    try (Connection conn = new DBContext().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
         
        ps.setInt(1, utilityTypeId); // ✅ Chỉ cần set 1 tham số
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    }
    return false;
}

    public boolean isUtilityNameExists(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM UtilityTypes WHERE UtilityName = ?";
        try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
public List<UtilityType> getUtilitiesByRoomId(int roomId) throws SQLException {
    List<UtilityType> list = new ArrayList<>();
    String sql = "SELECT DISTINCT ut.UtilityTypeID, ut.UtilityName, ut.UnitPrice, ut.Unit " +
                 "FROM UtilityTypes ut " +
                 "JOIN UtilityReadings ur ON ut.UtilityTypeID = ur.UtilityTypeID " +
                 "WHERE ur.RoomID = ?";

    try (Connection conn = new DBContext().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, roomId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new UtilityType(
                rs.getInt("UtilityTypeID"),
                rs.getString("UtilityName"),
                rs.getBigDecimal("UnitPrice"),
                rs.getString("Unit")
            ));
        }
    }
    return list;
}



}
