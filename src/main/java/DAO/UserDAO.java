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

public class UserDAO extends DBContext {

    private final RoleDAO roleDAO = new RoleDAO();

    public UserDAO() {
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("UserID"));
        user.setUserFullName(rs.getString("UserFullName"));
        user.setEmail(rs.getString("Email"));
        user.setPhoneNumber(rs.getString("PhoneNumber"));
        user.setPassword(rs.getString("Password"));
        user.setUserStatus(rs.getString("UserStatus"));
        user.setUserCreatedAt(rs.getDate("UserCreatedAt"));

        int roleId = rs.getInt("RoleID");
        user.setRoleId(roleId);
        if (!rs.wasNull()) {
            Role role = roleDAO.getRoleById(roleId);
            user.setRole(role);
        } else {
            user.setRole(null);
        }

        int blockId = rs.getInt("BlockID");
        
if (!rs.wasNull()) {
    user.setBlockId(blockId); // ✅ Luôn gán blockId

    if (roleId == 2) { // ✅ Nếu là manager thì cố gắng lấy Block object
        BlockDAO blockDAO = new BlockDAO();
        Block block = blockDAO.getBlockById(blockId);
        if (block != null) {
            user.setBlock(block);
        } else {
            System.err.println("⚠️ Không tìm thấy Block với ID = " + blockId);
        }
    }
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
                user.getRoleId(),
                user.getUserStatus() != null ? user.getUserStatus() : "Active",
                user.getBlockId()
        );
        return result > 0;
    }

    public boolean updateUser(User user) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder("UPDATE Users SET UserFullName = ?, Email = ?, PhoneNumber = ?, RoleID = ?, UserStatus = ?");

        boolean hasBlock = (user.getRoleId() == 2);
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
            ps.setInt(paramIndex++, user.getRoleId());
            ps.setString(paramIndex++, user.getUserStatus());

            if (hasBlock) {
                ps.setObject(paramIndex++, user.getBlockId());
            }

            if (hasPassword) {
                ps.setString(paramIndex++, user.getPassword());
            }

            ps.setInt(paramIndex, user.getUserId());

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

    public static String hashMD5(String input) {
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

public User loginUser(String email, String rawPassword) throws SQLException {
    String hashedPassword = hashMD5(rawPassword); // ✅ Mã hóa tại đây
    String sql = "SELECT * FROM Users WHERE Email = ? AND Password = ? AND UserStatus = 'Active'";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, email);
        ps.setString(2, hashedPassword);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        }
    }
    return null;
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
                    u.setUserCreatedAt(rs.getDate("UserCreatedAt")); // ✅ đúng kiểu

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
            String hashedPassword = hashMD5("GOOGLE_AUTH");
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
                    user.setUserCreatedAt(rs.getDate("UserCreatedAt")); // ✅ đúng kiểu

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
            String hashedPassword = hashMD5(user.getPassword());
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

}
