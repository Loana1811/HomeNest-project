/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import DB.DBContext;
import Model.Customer;
import Model.Tenant;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.time.LocalDate;



/**
 *
 * @author Admin
 */
public class TenantDAO extends DBContext {

    public ArrayList<Tenant> getAllTenants() {
        ArrayList<Tenant> tenants = new ArrayList<>();
        String squery = "SELECT * FROM Tenants WHERE TenantID NOT IN (SELECT TenantID FROM Contracts)";

        try ( ResultSet rs = this.execSelectQuery(squery)) {
            while (rs.next()) {
                Tenant tenant = new Tenant();
                tenant.setTenantID(rs.getInt("TenantID"));
                tenant.setCustomerID(rs.getInt("CustomerID"));
                tenant.setJoinDate(rs.getDate("JoinDate"));
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
                customer.setBirthDate(rs.getDate("BirthDate"));
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
