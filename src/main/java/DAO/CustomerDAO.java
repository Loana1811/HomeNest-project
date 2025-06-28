/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import DB.DBContext;
import Model.Customer;
import java.security.MessageDigest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Admin
 */
public class CustomerDAO extends DBContext {

    public Customer getCustomerByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Customers WHERE Email = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer c = new Customer();
                    c.setCustomerID(rs.getInt("CustomerID"));
                    c.setCustomerFullName(rs.getString("CustomerFullName"));
                    c.setPhoneNumber(rs.getString("PhoneNumber"));
                    c.setCCCD(rs.getString("CCCD"));
                    c.setGender(rs.getString("Gender"));
                    c.setBirthDay(rs.getDate("BirthDate"));
                    c.setAddress(rs.getString("Address"));
                    c.setEmail(rs.getString("Email"));
                    c.setCustomerPassword(rs.getString("CustomerPassword"));
                    c.setCustomerStatus(rs.getString("CustomerStatus"));
                    return c;
                }
            }
        }
        return null;
    }

    public void insertCustomerFromGoogle(Customer c) throws SQLException {
        String sql = "INSERT INTO Customers (CustomerFullName, Email, PhoneNumber, CCCD, Gender, BirthDate, Address, CustomerPassword, CustomerStatus) "
                + "VALUES (?, ?, NULL, NULL, NULL, NULL, NULL, ?, ?)";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCustomerFullName());     // ?
            ps.setString(2, c.getEmail());                // ?
            ps.setString(3, c.getCustomerPassword());     // ?
            ps.setString(4, c.getCustomerStatus());       // ?
            ps.executeUpdate();
        }
    }

    public Customer checkLogin(String email, String hashedPassword) throws SQLException {
        String sql = "SELECT * FROM Customers WHERE Email = ? AND CustomerPassword = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, hashedPassword);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer c = new Customer();
                    c.setCustomerID(rs.getInt("CustomerID"));
                    c.setCustomerFullName(rs.getString("CustomerFullName"));
                    c.setPhoneNumber(rs.getString("PhoneNumber"));
                    c.setCCCD(rs.getString("CCCD"));
                    c.setGender(rs.getString("Gender"));
                    c.setBirthDay(rs.getDate("BirthDate"));
                    c.setAddress(rs.getString("Address"));
                    c.setEmail(rs.getString("Email"));
                    c.setCustomerPassword(rs.getString("CustomerPassword"));
                    c.setCustomerStatus(rs.getString("CustomerStatus"));
                    return c;
                }
            }
        }
        return null;
    }

    public void updatePassword(String email, String newHashedPassword) throws SQLException {
        String sql = "UPDATE Customers SET CustomerPassword = ? WHERE Email = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHashedPassword);
            ps.setString(2, email);
            ps.executeUpdate();
        }
    }

    public boolean insertCustomer(Customer customer) {
        String sql = "INSERT INTO Customers(CustomerFullName, PhoneNumber, CCCD, Gender , BirthDate, Address, Email, CustomerPassword, CustomerStatus) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Potential')";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getCustomerFullName());
            ps.setString(2, customer.getPhoneNumber());
            ps.setString(3, customer.getCCCD());
            ps.setString(4, customer.getGender());
            ps.setDate(5, customer.getBirthDay());
            ps.setString(6, customer.getAddress());
            ps.setString(7, customer.getEmail());
            ps.setString(8, UserDAO.hashMd5(customer.getCustomerPassword()));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String hashMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
