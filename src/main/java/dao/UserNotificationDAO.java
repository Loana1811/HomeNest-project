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

    public boolean createUserNotification(UserNotification notification) throws SQLException {
        String sql = "INSERT INTO UserNotifications (UserID, UserTitle, UserMessage, UserNotificationCreatedAt, SentBy) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, notification.getUserID());
            pstmt.setString(2, notification.getUserTitle());
            pstmt.setString(3, notification.getUserMessage());
            pstmt.setTimestamp(4, new java.sql.Timestamp(notification.getUserNotificationCreatedAt().getTime()));
            if (notification.getSentBy() != null) {
                pstmt.setInt(5, notification.getSentBy());
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }
            return pstmt.executeUpdate() > 0;
        }
    }

    public UserNotification getUserNotificationById(int userNotificationID) throws SQLException {
        String sql = "SELECT * FROM UserNotifications WHERE UserNotificationID = ?";
        try (Connection conn = dbContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userNotificationID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UserNotification n = new UserNotification();
                    n.setUserNotificationID(rs.getInt("UserNotificationID"));
                    n.setUserID(rs.getInt("UserID"));
                    n.setUserTitle(rs.getString("UserTitle"));
                    n.setUserMessage(rs.getString("UserMessage"));
                    n.setUserNotificationCreatedAt(rs.getTimestamp("UserNotificationCreatedAt"));
                    n.setSentBy(rs.getObject("SentBy") != null ? rs.getInt("SentBy") : null);
                    return n;
                }
            }
        }
        return null;
    }

    public List<UserNotification> getAllUserNotifications() throws SQLException {
        List<UserNotification> notificationList = new ArrayList<>();
        String sql = "SELECT * FROM UserNotifications";
        try (Connection conn = dbContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                UserNotification n = new UserNotification();
                n.setUserNotificationID(rs.getInt("UserNotificationID"));
                n.setUserID(rs.getInt("UserID"));
                n.setUserTitle(rs.getString("UserTitle"));
                n.setUserMessage(rs.getString("UserMessage"));
                n.setUserNotificationCreatedAt(rs.getTimestamp("UserNotificationCreatedAt"));
                n.setSentBy(rs.getObject("SentBy") != null ? rs.getInt("SentBy") : null);
                notificationList.add(n);
            }
        }
        return notificationList;
    }

    public List<UserNotification> getUserNotificationsByUserId(int userId) throws SQLException {
        List<UserNotification> notificationList = new ArrayList<>();
        String sql = "SELECT * FROM UserNotifications WHERE UserID = ?";
        try (Connection conn = dbContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    UserNotification n = new UserNotification();
                    n.setUserNotificationID(rs.getInt("UserNotificationID"));
                    n.setUserID(rs.getInt("UserID"));
                    n.setUserTitle(rs.getString("UserTitle"));
                    n.setUserMessage(rs.getString("UserMessage"));
                    n.setUserNotificationCreatedAt(rs.getTimestamp("UserNotificationCreatedAt"));
                    n.setSentBy(rs.getObject("SentBy") != null ? rs.getInt("SentBy") : null);
                    notificationList.add(n);
                }
            }
        }
        return notificationList;
    }

    public List<UserNotification> searchUserNotifications(String keyword) throws SQLException {
        List<UserNotification> notificationList = new ArrayList<>();
        String sql = "SELECT * FROM UserNotifications WHERE UserTitle LIKE ? OR UserMessage LIKE ?";
        try (Connection conn = dbContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    UserNotification n = new UserNotification();
                    n.setUserNotificationID(rs.getInt("UserNotificationID"));
                    n.setUserID(rs.getInt("UserID"));
                    n.setUserTitle(rs.getString("UserTitle"));
                    n.setUserMessage(rs.getString("UserMessage"));
                    n.setUserNotificationCreatedAt(rs.getTimestamp("UserNotificationCreatedAt"));
                    n.setSentBy(rs.getObject("SentBy") != null ? rs.getInt("SentBy") : null);
                    notificationList.add(n);
                }
            }
        }
        return notificationList;
    }

    public boolean updateUserNotification(UserNotification notification) throws SQLException {
        String sql = "UPDATE UserNotifications SET UserID = ?, UserTitle = ?, UserMessage = ?, UserNotificationCreatedAt = ?, SentBy = ? WHERE UserNotificationID = ?";
        try (Connection conn = dbContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, notification.getUserID());
            pstmt.setString(2, notification.getUserTitle());
            pstmt.setString(3, notification.getUserMessage());
            pstmt.setTimestamp(4, new java.sql.Timestamp(notification.getUserNotificationCreatedAt().getTime()));
            if (notification.getSentBy() != null) {
                pstmt.setInt(5, notification.getSentBy());
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }
            pstmt.setInt(6, notification.getUserNotificationID());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteUserNotification(int userNotificationID) throws SQLException {
        String sql = "DELETE FROM UserNotifications WHERE UserNotificationID = ?";
        try (Connection conn = dbContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userNotificationID);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<UserNotification> getNotificationsByBlock(int blockId) throws SQLException {
        List<UserNotification> notificationList = new ArrayList<>();
        String sql = "SELECT un.* FROM UserNotifications un " +
                     "JOIN Users u ON un.UserID = u.UserID " +
                     "WHERE u.BlockID = ? AND u.RoleID = 2 AND u.UserStatus = 'Active'";
        try (Connection conn = dbContext.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, blockId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    UserNotification n = new UserNotification();
                    n.setUserNotificationID(rs.getInt("UserNotificationID"));
                    n.setUserID(rs.getInt("UserID"));
                    n.setUserTitle(rs.getString("UserTitle"));
                    n.setUserMessage(rs.getString("UserMessage"));
                    n.setUserNotificationCreatedAt(rs.getTimestamp("UserNotificationCreatedAt"));
                    n.setSentBy(rs.getObject("SentBy") != null ? rs.getInt("SentBy") : null);
                    notificationList.add(n);
                }
            }
        }
        return notificationList;
    }
}