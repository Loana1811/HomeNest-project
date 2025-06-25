/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import DB.DBContext;
import Model.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

/**
 *
 * @author Admin
 */
public class UserDAO extends DBContext {

    public User loginUser(String email, String hashedPassword) {
        String sql = "SELECT * FROM Users WHERE Email = ? AND Password = ? AND UserStatus = 'Active'";
        try (
                 PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, hashedPassword);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("UserID"),
                        rs.getString("UserFullName"),
                        rs.getString("Email"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Password"),
                        rs.getInt("RoleID"),
                        rs.getString("UserStatus"),
                        rs.getString("UserCreatedAt"),
                        rs.getObject("BlockID") == null ? null : rs.getInt("BlockID")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String hashMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashedBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
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
                    u.setUserId(rs.getInt("UserID"));
                    u.setUserFullName(rs.getString("UserFullName")); // sửa đúng tên cột
                    u.setEmail(rs.getString("Email"));
                    u.setRoleId(rs.getInt("RoleID"));                // sửa kiểu về int
                    u.setRoleName(rs.getString("RoleName"));         // lấy tên quyền
                    u.setUserStatus(rs.getString("UserStatus"));
                    u.setUserCreatedAt(rs.getString("UserCreatedAt"));
                    u.setBlockId(rs.getObject("BlockID") != null ? rs.getInt("BlockID") : null);
                    return u;
                }
            }
        }
        return null;
    }

    public void insertUserFromGoogle(User user) throws SQLException {
        String sql = "INSERT INTO Users (UserFullName, Email, PhoneNumber, Password, RoleID, UserStatus, UserCreatedAt, BlockID) "
                + "VALUES (?, ?, ?, ?, ?, 'Active', GETDATE(), NULL)";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUserFullName());
            ps.setString(2, user.getEmail());

            // ✅ Nếu không có số điện thoại thì sinh số giả tăng tự động
            String phone = user.getPhoneNumber();
            if (phone == null || phone.isEmpty()) {
                phone = generateSequentialPhoneNumber();
            }
            ps.setString(3, phone);

            // ✅ Mã hóa mật khẩu mặc định
            String hashedPassword = hashMd5("GOOGLE_AUTH");
            ps.setString(4, hashedPassword);

            ps.setInt(5, user.getRoleId());

            ps.executeUpdate();
        }
    }

    // ✅ Sinh số điện thoại giả dạng 09900000000, 09900000001, ...
    private String generateSequentialPhoneNumber() throws SQLException {
        String base = "099";
        int maxSuffix = getMaxPhoneSuffix(); // ví dụ đang là 12 → trả về 13
        return base + String.format("%08d", maxSuffix); // "09900000013"
    }

    // ✅ Tìm hậu tố lớn nhất đã được dùng trong PhoneNumber dạng 099xxxxxxx
    private int getMaxPhoneSuffix() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(PhoneNumber, 4, LEN(PhoneNumber)) AS INT)) AS maxSuffix "
                + "FROM Users WHERE PhoneNumber LIKE '099%'";
        try ( PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("maxSuffix") + 1; // tăng thêm 1
            }
        }
        return 0; // nếu chưa có thì bắt đầu từ 0
    }

    public User checkLogin(String email, String password) throws SQLException {
        String sql = "SELECT u.*, r.RoleName FROM Users u "
                + "JOIN Roles r ON u.RoleID = r.RoleID "
                + "WHERE u.Email = ? AND u.Password = ?";

        try ( PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("UserID"));
                    user.setUserFullName(rs.getString("UserFullName"));
                    user.setEmail(rs.getString("Email"));
                    user.setPhoneNumber(rs.getString("PhoneNumber"));
                    user.setPassword(rs.getString("Password"));
                    user.setRoleId(rs.getInt("RoleID"));
                    user.setUserStatus(rs.getString("UserStatus"));
                    user.setUserCreatedAt(rs.getString("UserCreatedAt"));
                    user.setBlockId(rs.getObject("BlockID") != null ? rs.getInt("BlockID") : null);
                    user.setRoleName(rs.getString("RoleName")); // <-- thêm dòng này
                    return user;
                }
            }
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
            // Mã hóa mật khẩu bằng MD5 trước khi lưu
            String hashedPassword = hashMd5(user.getPassword());
            ps.setString(4, hashedPassword);
            ps.setInt(5, 2);  // Mặc định RoleID = 2 (User)
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

  
}
