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

public class UserDAO  extends DBContext {

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
        String query = "SELECT * FROM Users ORDER BY UserCreatedAt DESC";

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
        String query = "UPDATE Users SET UserStatus = 'Inactive' WHERE UserID = ?";

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
        String query = "SELECT * FROM Users WHERE PhoneNumber = ? AND UserStatus = 'Active'";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, phoneNumber);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    if (user.getPassword().equals(hashMD5(password))) {
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
}
