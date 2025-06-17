package dao;

import model.Customer;
import utils.DBContext;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private Connection conn;

    public CustomerDAO() {
        try {
            conn = new DBContext().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy tất cả khách hàng
    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT CustomerID, CustomerFullName, PhoneNumber, CCCD, Gender, BirthDate, Address, Email, CustomerPassword, CustomerStatus FROM Customers";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Customer(
                    rs.getInt("CustomerID"),
                    rs.getString("CustomerFullName"),
                    rs.getString("PhoneNumber"),
                    rs.getString("CCCD"),
                    rs.getString("Gender"),
                    rs.getDate("BirthDate"),
                    rs.getString("Address"),
                    rs.getString("Email"),
                    rs.getString("CustomerPassword"),
                    rs.getString("CustomerStatus")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy khách hàng theo ID
    public Customer getCustomerById(int id) {
        String sql = "SELECT CustomerID, CustomerFullName, PhoneNumber, CCCD, Gender, BirthDate, Address, Email, CustomerPassword, CustomerStatus FROM Customers WHERE CustomerID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                        rs.getInt("CustomerID"),
                        rs.getString("CustomerFullName"),
                        rs.getString("PhoneNumber"),
                        rs.getString("CCCD"),
                        rs.getString("Gender"),
                        rs.getDate("BirthDate"),
                        rs.getString("Address"),
                        rs.getString("Email"),
                        rs.getString("CustomerPassword"),
                        rs.getString("CustomerStatus")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm mới khách hàng
    public void addCustomer(Customer c) {
        String sql = "INSERT INTO Customers (CustomerFullName, PhoneNumber, CCCD, Gender, BirthDate, Address, Email, CustomerPassword, CustomerStatus) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCustomerFullName());
            ps.setString(2, c.getPhoneNumber());
            ps.setString(3, c.getCCCD());
            ps.setString(4, c.getGender());
            ps.setDate(5, c.getBirthDay());
            ps.setString(6, c.getAddress());
            ps.setString(7, c.getEmail());
            ps.setString(8, c.getCustomerPassword());
            ps.setString(9, c.getCustomerStatus());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cập nhật khách hàng
    public void updateCustomer(Customer c) {
        String sql = "UPDATE Customers SET CustomerFullName = ?, PhoneNumber = ?, CCCD = ?, Gender = ?, BirthDate = ?, Address = ?, Email = ?, CustomerPassword = ?, CustomerStatus = ? WHERE CustomerID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCustomerFullName());
            ps.setString(2, c.getPhoneNumber());
            ps.setString(3, c.getCCCD());
            ps.setString(4, c.getGender());
            ps.setDate(5, c.getBirthDay());
            ps.setString(6, c.getAddress());
            ps.setString(7, c.getEmail());
            ps.setString(8, c.getCustomerPassword());
            ps.setString(9, c.getCustomerStatus());
            ps.setInt(10, c.getCustomerID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Xóa khách hàng
    public void deleteCustomer(int id) {
        String sql = "DELETE FROM Customers WHERE CustomerID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy khách hàng theo số CCCD
    public Customer getCustomerByCCCD(String cccd) {
        String sql = "SELECT CustomerID, CustomerFullName, PhoneNumber, CCCD, Gender, BirthDate, Address, Email, CustomerPassword, CustomerStatus FROM Customers WHERE CCCD = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cccd);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                        rs.getInt("CustomerID"),
                        rs.getString("CustomerFullName"),
                        rs.getString("PhoneNumber"),
                        rs.getString("CCCD"),
                        rs.getString("Gender"),
                        rs.getDate("BirthDate"),
                        rs.getString("Address"),
                        rs.getString("Email"),
                        rs.getString("CustomerPassword"),
                        rs.getString("CustomerStatus")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tìm kiếm khách hàng theo từ khóa
    public List<Customer> searchCustomers(String keyword) {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT CustomerID, CustomerFullName, PhoneNumber, CCCD, Gender, BirthDate, Address, Email, CustomerPassword, CustomerStatus "
                   + "FROM Customers "
                   + "WHERE CustomerFullName LIKE ? OR PhoneNumber LIKE ? OR Email LIKE ? OR CCCD LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            ps.setString(4, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Customer(
                        rs.getInt("CustomerID"),
                        rs.getString("CustomerFullName"),
                        rs.getString("PhoneNumber"),
                        rs.getString("CCCD"),
                        rs.getString("Gender"),
                        rs.getDate("BirthDate"),
                        rs.getString("Address"),
                        rs.getString("Email"),
                        rs.getString("CustomerPassword"),
                        rs.getString("CustomerStatus")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
