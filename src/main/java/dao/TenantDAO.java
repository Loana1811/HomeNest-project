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
                + "WHERE t.TenantID NOT IN (SELECT TenantID FROM Contracts)";

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

    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM Customers WHERE CustomerID = ?";
        try ( Connection conn = this.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerID(rs.getInt("CustomerID"));
                customer.setCustomerFullName(rs.getString("CustomerFullName"));
                customer.setPhoneNumber(rs.getString("PhoneNumber"));
                customer.setCCCD(rs.getString("CCCD"));
                customer.setGender(rs.getString("Gender"));
                customer.setBirthDay(rs.getDate("BirthDate"));
                customer.setAddress(rs.getString("Address"));
                customer.setEmail(rs.getString("Email"));
                return customer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
