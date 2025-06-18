// TenantDAO.java
package dao;

import model.Tenant;
import utils.DBContext;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TenantDAO {
    private Connection conn;

    public TenantDAO() {
        try {
            conn = new DBContext().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Tenant> getAllTenants() {
        List<Tenant> list = new ArrayList<>();
        String sql = "SELECT TenantID, CustomerID, JoinDate FROM Tenants";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Tenant(
                    rs.getInt("TenantID"),
                    rs.getInt("CustomerID"),
                    rs.getDate("JoinDate")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Tenant getTenantById(int id) {
        String sql = "SELECT TenantID, CustomerID, JoinDate FROM Tenants WHERE TenantID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Tenant(
                        rs.getInt("TenantID"),
                        rs.getInt("CustomerID"),
                        rs.getDate("JoinDate")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addTenant(Tenant t) {
        String sql = "INSERT INTO Tenants (CustomerID, JoinDate) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getCustomerID());
            ps.setDate(2, t.getJoinDate());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTenant(Tenant t) {
        String sql = "UPDATE Tenants SET CustomerID = ?, JoinDate = ? WHERE TenantID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getCustomerID());
            ps.setDate(2, t.getJoinDate());
            ps.setInt(3, t.getTenantID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteTenant(int id) {
        String sql = "DELETE FROM Tenants WHERE TenantID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
