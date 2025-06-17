// UtilityTypeDAO.java
package dao;

import model.UtilityType;
import utils.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UtilityTypeDAO {
    private Connection conn;

    public UtilityTypeDAO() {
        try {
            conn = new DBContext().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy tất cả loại tiện ích
    public List<UtilityType> getAllUtilityTypes() {
        List<UtilityType> list = new ArrayList<>();
        String sql = "SELECT UtilityTypeID, UtilityName, UnitPrice, Unit, IsSystem FROM UtilityTypes";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new UtilityType(
                    rs.getInt("UtilityTypeID"),
                    rs.getString("UtilityName"),
                    rs.getFloat("UnitPrice"),
                    rs.getString("Unit"),
                    rs.getBoolean("IsSystem")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy theo ID
    public UtilityType getUtilityTypeById(int id) {
        String sql = "SELECT UtilityTypeID, UtilityName, UnitPrice, Unit, IsSystem FROM UtilityTypes WHERE UtilityTypeID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UtilityType(
                        rs.getInt("UtilityTypeID"),
                        rs.getString("UtilityName"),
                        rs.getFloat("UnitPrice"),
                        rs.getString("Unit"),
                        rs.getBoolean("IsSystem")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm mới
    public void addUtilityType(UtilityType t) {
        String sql = "INSERT INTO UtilityTypes (UtilityName, UnitPrice, Unit, IsSystem) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getUtilityName());
            ps.setFloat(2, t.getUnitPrice());
            ps.setString(3, t.getUnit());
            ps.setBoolean(4, t.isIsSystem());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cập nhật
    public void updateUtilityType(UtilityType t) {
        String sql = "UPDATE UtilityTypes SET UtilityName = ?, UnitPrice = ?, Unit = ?, IsSystem = ? WHERE UtilityTypeID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getUtilityName());
            ps.setFloat(2, t.getUnitPrice());
            ps.setString(3, t.getUnit());
            ps.setBoolean(4, t.isIsSystem());
            ps.setInt(5, t.getUtilityTypeID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Xóa
    public void deleteUtilityType(int id) {
        String sql = "DELETE FROM UtilityTypes WHERE UtilityTypeID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
