/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.User;
import model.Role;
import utils.DBContext;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Block;
import model.Customer;

public class UserDAO extends DBContext {

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

    private final RoleDAO roleDAO = new RoleDAO();

    public UserDAO() {
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserID(rs.getInt("UserID"));
        user.setUserFullName(rs.getString("UserFullName"));
        user.setEmail(rs.getString("Email"));
        user.setPhoneNumber(rs.getString("PhoneNumber"));
        user.setPassword(rs.getString("Password"));
        user.setUserStatus(rs.getString("UserStatus"));
        user.setUserCreatedAt(rs.getDate("UserCreatedAt"));

        int roleId = rs.getInt("RoleID");
        if (!rs.wasNull()) {
            Role role = roleDAO.getRoleById(roleId);
            user.setRole(role);
        } else {
            user.setRole(null);
        }

        int blockId = rs.getInt("BlockID");
        if (!rs.wasNull() && roleId == 2) {
            BlockDAO blockDAO = new BlockDAO();
            user.setBlock(blockDAO.getBlockById(blockId));
        }

        return user;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users ORDER BY UserID ASC";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in getAllUsers: " + e.getMessage());
            throw e;
        }

        return users;
    }

    public User getUserById(int userId) throws SQLException {
        String query = "SELECT * FROM Users WHERE UserID = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    public boolean addUser(User user) throws SQLException {
        String query = "INSERT INTO Users (UserFullName, Email, PhoneNumber, Password, RoleID, UserStatus, BlockID) VALUES (?, ?, ?, ?, ?, ?, ?)";

        String hashedPassword = hashMD5(user.getPassword());
        int result = execUpdateQuery(query,
                user.getUserFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                hashedPassword,
                user.getRoleID(),
                user.getUserStatus() != null ? user.getUserStatus() : "Active",
                user.getBlockID()
        );
        return result > 0;
    }

    public boolean updateUser(User user) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder("UPDATE Users SET UserFullName = ?, Email = ?, PhoneNumber = ?, RoleID = ?, UserStatus = ?");

        boolean hasBlock = (user.getRoleID() == 2);
        if (hasBlock) {
            queryBuilder.append(", BlockID = ?");
        }

        boolean hasPassword = (user.getPassword() != null && !user.getPassword().isEmpty());
        if (hasPassword) {
            queryBuilder.append(", Password = ?");
        }

        queryBuilder.append(" WHERE UserID = ?");
        String query = queryBuilder.toString();

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {

            int paramIndex = 1;
            ps.setString(paramIndex++, user.getUserFullName());
            ps.setString(paramIndex++, user.getEmail());
            ps.setString(paramIndex++, user.getPhoneNumber());
            ps.setInt(paramIndex++, user.getRoleID());
            ps.setString(paramIndex++, user.getUserStatus());

            if (hasBlock) {
                ps.setObject(paramIndex++, user.getBlockID());
            }

            if (hasPassword) {
                ps.setString(paramIndex++, user.getPassword());
            }

            ps.setInt(paramIndex, user.getUserID());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM Users WHERE UserID = ?";
        int result = execUpdateQuery(query, userId);
        return result > 0;
    }

    public boolean deactivateUser(int userId) throws SQLException {
        String query = "UPDATE Users SET UserStatus = 'Disable' WHERE UserID = ?";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean activateUser(int userId) throws SQLException {
        String query = "UPDATE Users SET UserStatus = 'Active' WHERE UserID = ?";
        int result = execUpdateQuery(query, userId);
        return result > 0;
    }

    public User login(String phoneNumber, String password) throws SQLException {
        String query = "SELECT u.UserID, u.UserFullName, u.Email, u.PhoneNumber, u.Password, "
                + "u.RoleID, u.UserStatus, u.UserCreatedAt, u.BlockID, "
                + "r.RoleName "
                + "FROM Users u "
                + "JOIN Roles r ON u.RoleID = r.RoleID "
                + "WHERE u.Email = ? AND u.UserStatus = 'Active'";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, phoneNumber);

            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = hashMD5(password);
                    String storedPassword = rs.getString("Password");

                    if (storedPassword.equals(hashedPassword)) {
                        User user = new User();
                        user.setUserID(rs.getInt("UserID"));
                        user.setUserFullName(rs.getString("UserFullName"));
                        user.setEmail(rs.getString("Email"));
                        user.setPhoneNumber(rs.getString("PhoneNumber"));
                        user.setPassword(storedPassword);
                        user.setUserStatus(rs.getString("UserStatus"));
                        user.setUserCreatedAt(rs.getDate("UserCreatedAt"));

                        int blockId = rs.getInt("BlockID");
                        if (!rs.wasNull()) {
                            user.setBlockID(blockId);
                        } else {
                            user.setBlockID(null);
                        }

                        // G?n Role
                        int roleId = rs.getInt("RoleID");
                        String roleName = rs.getString("RoleName");
                        Role role = new Role(roleId, roleName);
                        user.setRole(role);
                        user.setRoleId(roleId);         // ?? d�ng getRoleId()
                        user.setRoleName(roleName);     // ?? d�ng getRoleName()

                        return user;
                    }
                }
            }
        }

        return null;
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

    public boolean isEmailExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM Users WHERE Email = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            try ( ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean isNameExists(String fullName) throws SQLException {
        String query = "SELECT COUNT(*) FROM Users WHERE UserFullName = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, fullName);
            try ( ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT u.*, r.RoleName FROM Users u "
                + "JOIN Roles r ON u.RoleID = r.RoleID "
                + "WHERE u.Email = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setUserID(rs.getInt("UserID"));
                    u.setUserFullName(rs.getString("UserFullName")); // s?a ?�ng t�n c?t
                    u.setEmail(rs.getString("Email"));
                    u.setRoleID(rs.getInt("RoleID"));                // s?a ki?u v? int
                    u.setRoleName(rs.getString("RoleName"));         // l?y t�n quy?n
                    u.setUserStatus(rs.getString("UserStatus"));
                    u.setUserCreatedAt(rs.getDate("UserCreatedAt"));
                    u.setBlockID(rs.getObject("BlockID") != null ? rs.getInt("BlockID") : null);
                    return u;
                }
            }
        }
        return null;
    }

    public User loginUser(String email, String hashedPassword) {
        String sql = "SELECT * FROM Users WHERE Email = ? AND Password = ? AND UserStatus = 'Active'";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, hashedPassword);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int roleId = rs.getInt("RoleID");

              
                Role role = new Role();
                role.setRoleID(roleId);
                if (roleId == 1) {
                    role.setRoleName("Admin");
                } else if (roleId == 2) {
                    role.setRoleName("Manager");
                } else {
                    role.setRoleName("Other");
                }

                // T?o ??i t??ng Block n?u c�
                Integer blockId = rs.getObject("BlockID") != null ? rs.getInt("BlockID") : null;
                Block block = null;
                if (blockId != null) {
                    block = new Block();
                    block.setBlockID(blockId);
                }

                return new User(
                        rs.getInt("UserID"),
                        rs.getString("UserFullName"),
                        rs.getString("Email"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Password"),
                        role,
                        rs.getString("UserStatus"),
                        rs.getDate("UserCreatedAt"),
                        block
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertUser(User user) {
        String sql = "INSERT INTO Users (UserFullName, Email, PhoneNumber, Password, RoleID, UserStatus, UserCreatedAt, BlockID) "
                + "VALUES (?, ?, ?, ?, ?, 'Active', GETDATE(), NULL)";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUserFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhoneNumber());
         
            String hashedPassword = hashMd5(user.getPassword());
            ps.setString(4, hashedPassword);
            ps.setInt(5, 2);  
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updatePassword(String email, String newHashedPassword) throws SQLException {
        String sql = "UPDATE Users SET Password = ? WHERE Email = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHashedPassword);
            ps.setString(2, email);
            ps.executeUpdate();
        }
    }

    public List<User> getUsersByRole(String roleName) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT u.* FROM Users u JOIN Roles r ON u.RoleID = r.RoleID WHERE r.RoleName = ? AND u.UserStatus = 'Active' ORDER BY u.UserCreatedAt DESC";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, roleName);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in getUsersByRole: " + e.getMessage());
            throw e;
        }

        return users;
    }

    public Customer getCustomerById(int customerId) throws SQLException {
        String sql = "SELECT * FROM Customers WHERE CustomerID = ? AND CustomerStatus = 'Active'";
        try ( Connection conn = getConnection();  PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try ( ResultSet rs = pstmt.executeQuery()) {
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
                    customer.setCustomerPassword(rs.getString("CustomerPassword"));
                    customer.setCustomerStatus(rs.getString("CustomerStatus"));
                    return customer;
                }
            }
        }
        return null;
    }

    public List<User> getManagersByBlockId(int blockId) throws SQLException {
        List<User> managers = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE RoleID = 2 AND BlockID = ?";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, blockId);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    managers.add(mapResultSetToUser(rs));
                }
            }
        }

        return managers;
    }
    
   
public List<Customer> getCustomersByBlock(int blockId) throws SQLException {
    List<Customer> customers = new ArrayList<>();
    String sql = "SELECT DISTINCT c.CustomerID, c.CustomerFullName, c.PhoneNumber, c.Email "
            + "FROM Customers c "
            + "JOIN Tenants t ON c.CustomerID = t.CustomerID "
            + "JOIN Contracts ct ON t.TenantID = ct.TenantID "
            + "JOIN Rooms r ON ct.RoomID = r.RoomID "
            + "WHERE r.BlockID = ? AND ct.ContractStatus = 'Active' AND c.CustomerStatus = 'Active'";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, blockId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Customer c = new Customer();
                c.setCustomerID(rs.getInt("CustomerID"));
                c.setCustomerFullName(rs.getString("CustomerFullName"));
                c.setPhoneNumber(rs.getString("PhoneNumber"));
                c.setEmail(rs.getString("Email"));
                customers.add(c);
            }
        }
    }
    return customers;
}

    
    public boolean hasActiveManager(int blockId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Users WHERE BlockID = ? AND RoleID = 2 AND UserStatus = 'Active'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, blockId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}