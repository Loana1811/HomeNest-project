package dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import model.Customer;
import utils.DBContext;
import java.sql.*;
import java.util.*;

public class CustomerDAO extends DBContext{

    // Map một dòng ResultSet thành Customer object
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerID(rs.getInt("CustomerID"));
        customer.setCustomerFullName(rs.getString("CustomerFullName"));
        customer.setPhoneNumber(rs.getString("PhoneNumber"));
        customer.setCCCD(rs.getString("CCCD"));
        customer.setGender(rs.getString("Gender"));
        customer.setBirthDay(rs.getDate("BirthDate"));
        customer.setAddress(rs.getString("Address"));
        customer.setEmail(rs.getString("Email"));
        customer.setCustomerStatus(rs.getString("CustomerStatus"));
        customer.setCustomerPassword(rs.getString("CustomerPassword"));
        return customer;
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM Customers";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        }
        return customers;
    }

    public boolean createCustomer(Customer customer) throws SQLException {
        String query = "INSERT INTO Customers (CustomerFullName, PhoneNumber, CCCD, Gender, BirthDate, Address, Email, CustomerStatus, CustomerPassword) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int result = execUpdateQuery(query,
                customer.getCustomerFullName(),
                customer.getPhoneNumber(),
                customer.getCCCD(),
                customer.getGender(),
                customer.getBirthDay(),
                customer.getAddress(),
                customer.getEmail(),
                customer.getCustomerStatus() != null ? customer.getCustomerStatus() : "Potential",
                customer.getCustomerPassword()
        );
        return result > 0;
    }

    public Customer getCustomerById(int customerId) throws SQLException {
        String query = "SELECT * FROM Customers WHERE CustomerID = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, customerId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        }
        return null;
    }

    public boolean updateCustomer(Customer customer) throws SQLException {
        String query = "UPDATE Customers SET CustomerFullName = ?, PhoneNumber = ?, CCCD = ?, Gender = ?, BirthDate = ?, Address = ?, Email = ?, CustomerStatus = ? WHERE CustomerID = ?";
        int result = execUpdateQuery(query,
                customer.getCustomerFullName(),
                customer.getPhoneNumber(),
                customer.getCCCD(),
                customer.getGender(),
                customer.getBirthDay(),
                customer.getAddress(),
                customer.getEmail(),
                customer.getCustomerStatus(),
                customer.getCustomerID()
        );
        return result > 0;
    }

    public boolean updateCustomerStatus(int customerID, String status) throws SQLException {
        String query = "UPDATE Customers SET CustomerStatus = ? WHERE CustomerID = ?";
        int result = execUpdateQuery(query, status, customerID);
        return result > 0;
    }

    public boolean deleteCustomer(int customerId) throws SQLException {
        String query = "DELETE FROM Customers WHERE CustomerID = ?";
        int result = execUpdateQuery(query, customerId);
        return result > 0;
    }

    public boolean isPhoneNumberExists(String phoneNumber) throws SQLException {
        String query = "SELECT COUNT(*) FROM Customers WHERE PhoneNumber = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, phoneNumber);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public int getTotalCustomerCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Customers";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query);  ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public String hashMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasContractOrTenant(int customerId) throws SQLException {
        String sql1 = "SELECT 1 FROM Tenants WHERE CustomerID = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql1)) {
            ps.setInt(1, customerId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        String sql2 = "SELECT 1 FROM Contracts c JOIN Tenants t ON c.TenantID = t.TenantID WHERE t.CustomerID = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql2)) {
            ps.setInt(1, customerId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Map<String, Object>> getFullCustomerDetails(int customerID) throws SQLException {
        String sql = "SELECT c.CustomerID, c.CustomerFullName AS CustomerName, c.PhoneNumber, c.Email, c.CustomerStatus AS CustomerStatus, "
                + "t.TenantID, t.JoinDate, "
                + "ct.ContractID, ct.RoomID, ct.StartDate, ct.EndDate, ct.ContractStatus AS ContractStatus, ct.ContractCreatedAt AS ContractCreatedAt, "
                + "r.RoomNumber, r.Location, r.RoomStatus AS RoomStatus, "
                + "rq.RequestID, rq.RequestDate, rq.RequestStatus AS RequestStatus, "
                + "b.BillID, b.BillDate, b.TotalAmount, b.BillStatus AS BillStatus, "
                + "bd.BillDetailID, bd.RoomRent, bd.ElectricityCost, bd.WaterCost, bd.WifiCost, "
                + "ir.IncurredFeeID, ift.FeeName, ir.Amount, "
                + "ur.ReadingID, ut.UtilityName, ur.ReadingDate, ur.OldReading, ur.NewReading, ur.PriceUsed "
                + "FROM Customers c "
                + "LEFT JOIN Tenants t ON c.CustomerID = t.CustomerID "
                + "LEFT JOIN Contracts ct ON t.TenantID = ct.TenantID "
                + "LEFT JOIN Rooms r ON ct.RoomID = r.RoomID "
                + "LEFT JOIN Bills b ON ct.ContractID = b.ContractID "
                + "LEFT JOIN BillDetails bd ON b.BillID = bd.BillID "
                + "LEFT JOIN IncurredFees ir ON b.BillID = ir.BillID "
                + "LEFT JOIN IncurredFeeTypes ift ON ir.IncurredFeeTypeID = ift.IncurredFeeTypeID "
                + "LEFT JOIN RentalRequests rq ON c.CustomerID = rq.CustomerID "
                + "LEFT JOIN UtilityReadings ur ON r.RoomID = ur.RoomID "
                + "LEFT JOIN UtilityTypes ut ON ur.UtilityTypeID = ut.UtilityTypeID "
                + "WHERE c.CustomerID = ? "
                + "ORDER BY ct.ContractID DESC, b.BillDate DESC";

        List<Map<String, Object>> list = new ArrayList<>();
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerID);
            try ( ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= colCount; i++) {
                        row.put(meta.getColumnLabel(i), rs.getObject(i));
                    }
                    list.add(row);
                }
            }
        }
        return list;
    }

    public int getConvertedCustomerCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Customers WHERE CustomerStatus = 'Converted'";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query);  ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getPotentialCustomerCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Customers WHERE CustomerStatus = 'Potential'";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query);  ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getInactiveCustomerCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Customers WHERE CustomerStatus = 'Inactive'";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query);  ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    public boolean isEmailExists(String email) throws SQLException {
    String query = "SELECT COUNT(*) FROM Customers WHERE Email = ?";
    try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setString(1, email);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
    }
    return false;
}

// Kiểm tra tên đã tồn tại chưa
public boolean isNameExists(String fullName) throws SQLException {
    String query = "SELECT COUNT(*) FROM Customers WHERE CustomerFullName = ?";
    try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setString(1, fullName);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
    }
    return false;
}

// Lấy số lượng khách hàng đang "Active"
public int getActiveCustomerCount() throws SQLException {
    String query = "SELECT COUNT(*) FROM Customers WHERE CustomerStatus = 'Active'";
    try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
            return rs.getInt(1);
        }
    }
    return 0;
}
}
