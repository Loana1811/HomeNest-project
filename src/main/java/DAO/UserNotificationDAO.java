package dao;

import model.UserNotification;
import utils.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserNotificationDAO {
    private final DBContext dbContext = new DBContext();

    /**
     * Creates a new user notification in the database.
     */
    public boolean createUserNotification(UserNotification notification) throws SQLException {
        String sql = "INSERT INTO UserNotifications (UserID, UserTitle, UserMessage, IsRead, UserNotificationCreatedAt, SentBy) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, notification.getUserID());
            pstmt.setString(2, notification.getUserTitle());
            pstmt.setString(3, notification.getUserMessage());
            pstmt.setBoolean(4, notification.getIsRead());
            pstmt.setTimestamp(5, new java.sql.Timestamp(notification.getUserNotificationCreatedAt().getTime()));
            if (notification.getSentBy() != null) {
                pstmt.setInt(6, notification.getSentBy());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves a user notification by its ID.
     */
    public UserNotification getUserNotificationById(int userNotificationID) throws SQLException {
        String sql = "SELECT * FROM UserNotifications WHERE UserNotificationID = ?";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userNotificationID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new UserNotification(
                        rs.getInt("UserNotificationID"),
                        rs.getInt("UserID"),
                        rs.getString("UserTitle"),
                        rs.getString("UserMessage"),
                        rs.getBoolean("IsRead"),
                        rs.getTimestamp("UserNotificationCreatedAt"),
                        rs.getObject("SentBy") != null ? rs.getInt("SentBy") : null
                    );
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all user notifications.
     */
    public List<UserNotification> getAllUserNotifications() throws SQLException {
        List<UserNotification> notificationList = new ArrayList<>();
        String sql = "SELECT * FROM UserNotifications";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                notificationList.add(new UserNotification(
                    rs.getInt("UserNotificationID"),
                    rs.getInt("UserID"),
                    rs.getString("UserTitle"),
                    rs.getString("UserMessage"),
                    rs.getBoolean("IsRead"),
                    rs.getTimestamp("UserNotificationCreatedAt"),
                    rs.getObject("SentBy") != null ? rs.getInt("SentBy") : null
                ));
            }
        }
        return notificationList;
    }

    /**
     * Retrieves user notifications for a specific user ID.
     */
    public List<UserNotification> getUserNotificationsByUserId(int userId) throws SQLException {
        List<UserNotification> notificationList = new ArrayList<>();
        String sql = "SELECT * FROM UserNotifications WHERE UserID = ?";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notificationList.add(new UserNotification(
                        rs.getInt("UserNotificationID"),
                        rs.getInt("UserID"),
                        rs.getString("UserTitle"),
                        rs.getString("UserMessage"),
                        rs.getBoolean("IsRead"),
                        rs.getTimestamp("UserNotificationCreatedAt"),
                        rs.getObject("SentBy") != null ? rs.getInt("SentBy") : null
                    ));
                }
            }
        }
        return notificationList;
    }

    /**
     * Searches user notifications by keyword in title or message.
     */
    public List<UserNotification> searchUserNotifications(String keyword) throws SQLException {
        List<UserNotification> notificationList = new ArrayList<>();
        String sql = "SELECT * FROM UserNotifications WHERE UserTitle LIKE ? OR UserMessage LIKE ?";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notificationList.add(new UserNotification(
                        rs.getInt("UserNotificationID"),
                        rs.getInt("UserID"),
                        rs.getString("UserTitle"),
                        rs.getString("UserMessage"),
                        rs.getBoolean("IsRead"),
                        rs.getTimestamp("UserNotificationCreatedAt"),
                        rs.getObject("SentBy") != null ? rs.getInt("SentBy") : null
                    ));
                }
            }
        }
        return notificationList;
    }

    /**
     * Updates an existing user notification.
     */
    public boolean updateUserNotification(UserNotification notification) throws SQLException {
        String sql = "UPDATE UserNotifications SET UserID = ?, UserTitle = ?, UserMessage = ?, IsRead = ?, UserNotificationCreatedAt = ?, SentBy = ? WHERE UserNotificationID = ?";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, notification.getUserID());
            pstmt.setString(2, notification.getUserTitle());
            pstmt.setString(3, notification.getUserMessage());
            pstmt.setBoolean(4, notification.getIsRead());
            pstmt.setTimestamp(5, new java.sql.Timestamp(notification.getUserNotificationCreatedAt().getTime()));
            if (notification.getSentBy() != null) {
                pstmt.setInt(6, notification.getSentBy());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }
            pstmt.setInt(7, notification.getUserNotificationID());
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a user notification by its ID.
     */
    public boolean deleteUserNotification(int userNotificationID) throws SQLException {
        String sql = "DELETE FROM UserNotifications WHERE UserNotificationID = ?";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userNotificationID);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<UserNotification> getNotificationsByBlock(int blockId) throws SQLException {
        List<UserNotification> notificationList = new ArrayList<>();
        String sql = "SELECT un.* FROM UserNotifications un " +
                     "JOIN Users u ON un.UserID = u.UserID " +
                     "WHERE u.BlockID = ? AND u.RoleID = 2 AND u.UserStatus = 'Active'";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, blockId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notificationList.add(new UserNotification(
                        rs.getInt("UserNotificationID"),
                        rs.getInt("UserID"),
                        rs.getString("UserTitle"),
                        rs.getString("UserMessage"),
                        rs.getBoolean("IsRead"),
                        rs.getTimestamp("UserNotificationCreatedAt"),
                        rs.getObject("SentBy") != null ? rs.getInt("SentBy") : null
                    ));
                }
            }
        }
        return notificationList;
    }
}