/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

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
        String sql = "SELECT UtilityTypeID, UtilityName, UnitPrice, Unit FROM UtilityTypes";
        try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new UtilityType(
                        rs.getInt("UtilityTypeID"),
                        rs.getString("UtilityName"),
                        rs.getString("Unit"), // phải là unit trước
                        rs.getDouble("UnitPrice") // sau đó mới đến price
                ));

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
                        rs.getString("Unit"),
                        rs.getDouble("UnitPrice")
                        
                );
            }
        }
        return null;
    }

    public void insert(UtilityType u) throws SQLException {
        String sql = "INSERT INTO UtilityTypes (UtilityName, UnitPrice, Unit) VALUES (?, ?, ?)";
        try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getName());
            ps.setDouble(2, u.getPrice());
            ps.setString(3, u.getUnit());
            ps.executeUpdate();
        }
    }

    public void update(UtilityType u) throws SQLException {
        String sql = "UPDATE UtilityTypes SET UtilityName = ?, UnitPrice = ?, Unit = ? WHERE UtilityTypeID = ?";
        try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getName());
            ps.setDouble(2, u.getPrice());
            ps.setString(3, u.getUnit());
            ps.setInt(4, u.getId());
            ps.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
    String sql = "DELETE FROM UtilityTypes WHERE UtilityTypeID = ?";
    try (Connection conn = new DBContext().getConnection(); 
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, id);
        ps.executeUpdate();
    }
}

}
