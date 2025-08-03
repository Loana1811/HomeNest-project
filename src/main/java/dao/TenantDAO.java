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
import model.Customer;
import model.Tenant;
import utils.DBContext;

/**
 *
 * @author ThanhTruc
 */
public class TenantDAO extends DBContext {

    public boolean isTenant(int customerID) throws SQLException {
        String query = "SELECT 1 FROM Tenants WHERE CustomerID = ?";
        try ( Connection conn = this.getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, customerID);
            try ( ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true nếu có dòng kết quả
            }
        }
    }

    public ArrayList<Tenant> getAllTenants() {
        ArrayList<Tenant> tenants = new ArrayList<>();
        String query = "SELECT t.TenantID, t.CustomerID, t.JoinDate, "
                + "c.CustomerFullName, c.PhoneNumber, c.CCCD, "
                + "c.Gender, c.BirthDate, c.Address, c.Email, "
                + "c.CustomerPassword, c.CustomerStatus "
                + "FROM Tenants t "
                + "JOIN Customers c ON t.CustomerID = c.CustomerID "
                + "LEFT JOIN Contracts ct ON t.TenantID = ct.TenantID "
                + "     AND ct.ContractStatus IN ('Active', 'Pending') "
                + "WHERE ct.TenantID IS NULL";

        try ( ResultSet rs = this.execSelectQuery(query)) {
            while (rs.next()) {
                Tenant tenant = new Tenant();
                tenant.setTenantID(rs.getInt("TenantID"));
                tenant.setCustomerID(rs.getInt("CustomerID"));
                tenant.setJoinDate(rs.getDate("JoinDate"));
                tenant.setCustomerFullName(rs.getString("CustomerFullName"));
                tenant.setPhoneNumber(rs.getString("PhoneNumber"));
                tenant.setCCCD(rs.getString("CCCD"));
                tenant.setGender(rs.getString("Gender"));
                tenant.setBirthDate(rs.getDate("BirthDate"));
                tenant.setAddress(rs.getString("Address"));
                tenant.setEmail(rs.getString("Email"));
                tenant.setCustomerPassword(rs.getString("CustomerPassword"));
                tenant.setCustomerStatus(rs.getString("CustomerStatus"));

                tenants.add(tenant);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tenants;
    }

    public Tenant getTenantById(int tenantId) {
        Tenant tenant = null;
        String squery = "SELECT * FROM Tenants WHERE TenantID = ?";

        try ( PreparedStatement ps = conn.prepareStatement(squery)) {
            ps.setInt(1, tenantId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                tenant = new Tenant();
                tenant.setTenantID(rs.getInt("TenantID"));
                tenant.setCustomerID(rs.getInt("CustomerID"));
                tenant.setJoinDate(rs.getDate("JoinDate"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tenant;
    }

    public Customer getCustomerById(int customerId) throws SQLException {
        String sql = "SELECT * FROM Customers WHERE CustomerID = ?";
        try ( Connection conn = this.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerID(rs.getInt("CustomerID"));
                    customer.setCustomerFullName(rs.getString("CustomerFullName"));
                    customer.setPhoneNumber(rs.getString("PhoneNumber"));
                    customer.setCCCD(rs.getString("CCCD"));
                    customer.setGender(rs.getString("Gender"));
                    customer.setBirthDate(rs.getDate("BirthDate"));
                    customer.setAddress(rs.getString("Address"));
                    customer.setEmail(rs.getString("Email"));
                    customer.setCustomerStatus(rs.getString("CustomerStatus"));
                    customer.setCustomerPassword(rs.getString("CustomerPassword"));
                    return customer;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addTenant(int customerId, Date joinDate) throws SQLException {
        String sql = "INSERT INTO Tenants (CustomerID, JoinDate) VALUES (?, ?)";
        try ( Connection conn = new DBContext().getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.setDate(2, joinDate);
            stmt.executeUpdate();
        }
    }

    public boolean isCustomerAlreadyTenant(int customerId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Tenants WHERE CustomerID = ?";
        try ( PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, customerId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public Tenant getTenantByCustomerId(int customerId) throws SQLException {
        String sql = "SELECT * FROM Tenants WHERE CustomerID = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTenant(rs);
                }
            }
        }
        return null;
    }

    private Tenant mapResultSetToTenant(ResultSet rs) throws SQLException {
        Tenant tenant = new Tenant();
        tenant.setTenantID(rs.getInt("TenantID"));
        tenant.setCustomerID(rs.getInt("CustomerID"));
        tenant.setJoinDate(rs.getDate("JoinDate"));
        return tenant;
    }

    public int getCustomerIdByTenantId(int tenantId) throws SQLException {
        String sql = "SELECT CustomerID FROM Tenants WHERE TenantID = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("CustomerID");
            }
        }
        return -1; // hoặc ném ngoại lệ nếu không tìm thấy
    }

}
