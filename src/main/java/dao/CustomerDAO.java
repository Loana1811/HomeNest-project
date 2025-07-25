package dao;

import static dao.UserDAO.hashMd5;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import model.Customer;
import utils.DBContext;
import java.sql.*;
import java.util.*;
import model.EmailLogs;

public class CustomerDAO extends DBContext {

    // Map một dòng ResultSet thành Customer object
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
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

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM Customers";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query); 
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        }
        return customers;
    }

    public List<Map<String, Object>> getFullCustomerDetails(int customerID) throws SQLException {
        String sql = "SELECT " +
                     "c.CustomerID, " +
                     "c.CustomerFullName, " +
                     "c.PhoneNumber, " +
                     "c.CCCD, " +
                     "c.Gender, " +
                     "c.BirthDate, " +
                     "c.Address, " +
                     "c.Email, " +
                     "c.CustomerStatus, " +
                     "t.TenantID, " +
                     "t.JoinDate, " +
                     "con.ContractID, " +
                     "con.RoomID, " +
                     "con.StartDate, " +
                     "con.EndDate, " +
                     "con.ContractStatus, " +
                     "con.ContractCreatedAt, " +
                     "con.Deposit, " +
                     "r.RoomNumber, " +
                     "r.RentPrice, " +
                     "r.Area, " +
                     "r.RoomStatus, " +
                     "r.BlockID, " +
                     "b.BlockName, " +
                     "b.MaxRoom, " +
                     "b.RoomCount, " +
                     "b.BlockStatus, " +
                     "bill.BillID, " +
                     "bill.BillDate, " +
                     "bill.TotalAmount, " +
                     "bill.BillStatus, " +
                     "bill.IsSent, " +
                     "rr.RequestID, " +
                     "rr.RequestDate, " +
                     "rr.RequestStatus, " +
                     "rr.ApprovedBy, " +
                     "rr.ApprovedDate, " +
                     "rep.ReportID, " +
                     "rep.IssueDescription, " +
                     "rep.ReportStatus, " +
                     "rep.ReportCreatedAt, " +
                     "rep.ResolvedBy, " +
                     "rep.ResolvedDate, " +
                     "n.NotificationID, " +
                     "n.Title AS NotificationTitle, " +
                     "n.Message AS NotificationMessage, " +
                     "n.IsRead AS NotificationIsRead, " +
                     "n.NotificationCreatedAt, " +
                     "n.SentBy AS NotificationSentBy, " +
                     "n.RedirectUrl, " +
                     "el.EmailLogID, " +
                     "el.Subject AS EmailSubject, " +
                     "el.Message AS EmailMessage, " +
                     "el.SentAt, " +
                     "el.Status AS EmailStatus, " +
                     "el.ErrorMessage " +
                     "FROM [dbo].[Customers] c " +
                     "LEFT JOIN [dbo].[Tenants] t ON c.CustomerID = t.CustomerID " +
                     "LEFT JOIN [dbo].[Contracts] con ON t.TenantID = con.TenantID " +
                     "LEFT JOIN [dbo].[Rooms] r ON con.RoomID = r.RoomID " +
                     "LEFT JOIN [dbo].[Blocks] b ON r.BlockID = b.BlockID " +
                     "LEFT JOIN [dbo].[Bills] bill ON con.ContractID = bill.ContractID " +
                     "LEFT JOIN [dbo].[RentalRequests] rr ON c.CustomerID = rr.CustomerID " +
                     "LEFT JOIN [dbo].[Reports] rep ON c.CustomerID = rep.CustomerID " +
                     "LEFT JOIN [dbo].[Notifications] n ON c.CustomerID = n.CustomerID " +
                     "LEFT JOIN [dbo].[EmailLogs] el ON c.CustomerID = el.CustomerID " +
                     "WHERE c.CustomerID = ? " +
                     "ORDER BY c.CustomerID";

        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerID);
            try (ResultSet rs = ps.executeQuery()) {
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

    public boolean createCustomer(Customer customer) throws SQLException {
        String query = "INSERT INTO Customers (CustomerFullName, PhoneNumber, CCCD, Gender, BirthDate, Address, Email, CustomerStatus, CustomerPassword) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int result = execUpdateQuery(query,
                customer.getCustomerFullName(),
                customer.getPhoneNumber(),
                customer.getCCCD(),
                customer.getGender(),
                customer.getBirthDate(),
                customer.getAddress(),
                customer.getEmail(),
                customer.getCustomerStatus() != null ? customer.getCustomerStatus() : "Active",
                customer.getCustomerPassword()
        );
        return result > 0;
    }

    public Customer getCustomerById(int customerId) throws SQLException {
        String query = "SELECT * FROM Customers WHERE CustomerID = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
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
                customer.getBirthDate(),
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
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, phoneNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public int getTotalCustomerCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Customers";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query); 
             ResultSet rs = ps.executeQuery()) {
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

    public boolean hasContractOrRentalRequestOrNotification(int customerId) throws SQLException {
        String sql1 = "SELECT 1 FROM Tenants WHERE CustomerID = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql1)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        String sql2 = "SELECT 1 FROM Contracts c JOIN Tenants t ON c.TenantID = t.TenantID WHERE t.CustomerID = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql2)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        String sql3 = "SELECT 1 FROM RentalRequests WHERE CustomerID = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql3)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        String sql4 = "SELECT 1 FROM Notifications WHERE CustomerID = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql4)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getConvertedCustomerCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Customers WHERE CustomerStatus = 'Converted'";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query); 
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getPotentialCustomerCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Customers WHERE CustomerStatus = 'Active'";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query); 
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getInactiveCustomerCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Customers WHERE CustomerStatus = 'Disable'";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query); 
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public boolean isEmailExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM Customers WHERE Email = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean isNameExists(String fullName) throws SQLException {
        String query = "SELECT COUNT(*) FROM Customers WHERE CustomerFullName = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, fullName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public int getActiveCustomerCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Customers WHERE CustomerStatus = 'Active'";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query); 
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public Customer getCustomerByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Customers WHERE Email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer c = new Customer();
                    c.setCustomerID(rs.getInt("CustomerID"));
                    c.setCustomerFullName(rs.getString("CustomerFullName"));
                    c.setPhoneNumber(rs.getString("PhoneNumber"));
                    c.setCCCD(rs.getString("CCCD"));
                    c.setGender(rs.getString("Gender"));
                    c.setBirthDate(rs.getDate("BirthDate"));
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

    public boolean insertCustomerFromGoogle(Customer c) throws SQLException {
        if (isGoogleEmailExists(c.getEmail())) {
            System.out.println("Tài khoản Google này đã tồn tại trong hệ thống.");
            return false;
        }

        String sql = "INSERT INTO Customers (CustomerFullName, Email, PhoneNumber, CCCD, Gender, BirthDate, Address, CustomerPassword, CustomerStatus) " +
                     "VALUES (?, ?, NULL, NULL, NULL, NULL, NULL, ?, 'Active')";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCustomerFullName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getCustomerPassword()); // Đã hash MD5 trước đó
            return ps.executeUpdate() > 0;
        }
    }

    public boolean isGoogleEmailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM Customers WHERE Email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // true nếu email đã tồn tại
        }
    }

    public Customer checkLogin(String email, String hashedPassword) throws SQLException {
        String sql = "SELECT * FROM Customers WHERE Email = ? AND CustomerPassword = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            String Password = hashMd5(hashedPassword);
            ps.setString(2, Password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer c = new Customer();
                    c.setCustomerID(rs.getInt("CustomerID"));
                    c.setCustomerFullName(rs.getString("CustomerFullName"));
                    c.setPhoneNumber(rs.getString("PhoneNumber"));
                    c.setCCCD(rs.getString("CCCD"));
                    c.setGender(rs.getString("Gender"));
                    c.setBirthDate(rs.getDate("BirthDate"));
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
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHashedPassword);
            ps.setString(2, email);
            ps.executeUpdate();
        }
    }

    public boolean isDuplicate(Customer customer) throws SQLException {
        String sql = "SELECT 1 FROM Customers WHERE PhoneNumber = ? OR Email = ? OR CCCD = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getPhoneNumber());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getCCCD());
            ResultSet rs = ps.executeQuery();
            return rs.next(); // true nếu trùng
        }
    }

    public boolean insertCustomer(Customer customer) {
        String sql = "INSERT INTO Customers(CustomerFullName, PhoneNumber, CCCD, Gender, BirthDate, Address, Email, CustomerPassword, CustomerStatus) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Active')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getCustomerFullName());
            ps.setString(2, customer.getPhoneNumber());
            ps.setString(3, customer.getCCCD());
            ps.setString(4, customer.getGender());
            ps.setDate(5, customer.getBirthDate());
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

    public boolean logEmail(int customerID, Integer userID, String email, String subject, String message, String status, String errorMessage) throws SQLException {
        String query = "INSERT INTO EmailLogs (CustomerID, UserID, Email, Subject, Message, Status, ErrorMessage) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int result = execUpdateQuery(query,
                customerID == 0 ? null : customerID,
                userID,
                email,
                subject,
                message,
                status,
                errorMessage
        );
        return result > 0;
    }

    public boolean isCCCDExists(String cccd) throws SQLException {
        String query = "SELECT COUNT(*) FROM Customers WHERE CCCD = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, cccd);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean hasContractOrRentalRequest(int customerID) throws SQLException {
        String sql = "SELECT COUNT(*) " +
                     "FROM Customers c " +
                     "LEFT JOIN Tenants t ON c.CustomerID = t.CustomerID " +
                     "LEFT JOIN Contracts ct ON t.TenantID = ct.TenantID " +
                     "LEFT JOIN RentalRequests rq ON c.CustomerID = rq.CustomerID " +
                     "WHERE c.CustomerID = ? AND (ct.ContractID IS NOT NULL OR rq.RequestID IS NOT NULL)";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<EmailLogs> getAllEmailLogs() throws SQLException {
        List<EmailLogs> emailLogs = new ArrayList<>();
        String query = "SELECT * FROM EmailLogs ORDER BY SentAt DESC";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query); 
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                EmailLogs emailLog = new EmailLogs();
                emailLog.setEmailLogID(rs.getInt("EmailLogID"));
                emailLog.setCustomerID(rs.getObject("CustomerID") != null ? rs.getInt("CustomerID") : null);
                emailLog.setUserID(rs.getObject("UserID") != null ? rs.getInt("UserID") : null);
                emailLog.setEmail(rs.getString("Email"));
                emailLog.setSubject(rs.getString("Subject"));
                emailLog.setMessage(rs.getString("Message"));
                emailLog.setSentAt(rs.getTimestamp("SentAt"));
                emailLog.setStatus(rs.getString("Status"));
                emailLog.setErrorMessage(rs.getString("ErrorMessage"));
                emailLogs.add(emailLog);
            }
        }
        return emailLogs;
    }

    public int getDefaultCustomerId() throws SQLException {
        String query = "SELECT TOP 1 CustomerID FROM Customers WHERE CustomerStatus = 'Active' ORDER BY CustomerID";
        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query); 
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("CustomerID");
            }
        }
        return 0; // Return 0 if no active customers are found
    }

    public Map<Integer, Customer> getCustomersByBlock(int blockId) throws SQLException {
        Map<Integer, Customer> customerMap = new HashMap<>();
        String sql = "SELECT c.* FROM Customers c " +
                     "JOIN Tenants t ON c.CustomerID = t.CustomerID " +
                     "JOIN Contracts ct ON t.TenantID = ct.TenantID " +
                     "JOIN Rooms r ON ct.RoomID = r.RoomID " +
                     "WHERE r.BlockID = ? AND c.CustomerStatus = 'Active'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, blockId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Customer customer = mapResultSetToCustomer(rs);
                    customerMap.put(customer.getCustomerID(), customer);
                }
            }
        }
        return customerMap;
    }
}