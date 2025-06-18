// RoleDAO.java
package dao;

import model.Role;
import utils.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {
    private Connection conn;

    public RoleDAO() {
        try {
            conn = new DBContext().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Role> getAllRoles() {
        List<Role> list = new ArrayList<>();
        String sql = "SELECT RoleID, RoleName FROM Roles";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Role(
                    rs.getInt("RoleID"),
                    rs.getString("RoleName")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Role getRoleById(int id) {
        String sql = "SELECT RoleID, RoleName FROM Roles WHERE RoleID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Role(
                        rs.getInt("RoleID"),
                        rs.getString("RoleName")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addRole(Role r) {
        String sql = "INSERT INTO Roles (RoleName) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getRoleName());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateRole(Role r) {
        String sql = "UPDATE Roles SET RoleName = ? WHERE RoleID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getRoleName());
            ps.setInt(2, r.getRoleID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteRole(int id) {
        String sql = "DELETE FROM Roles WHERE RoleID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
