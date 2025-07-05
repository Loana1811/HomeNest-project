package dao;

import model.Notification;
import utils.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    private final DBContext dbContext = new DBContext();
    /**
     * Tạo một thông báo mới
     */
    public boolean createNotification(Notification notification) throws SQLException {
        String sql = "INSERT INTO Notifications (CustomerID, Title, Message, IsRead, NotificationCreatedAt, SentBy) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, notification.getCustomerID());
            pstmt.setString(2, notification.getTitle());
            pstmt.setString(3, notification.getMessage());
            pstmt.setBoolean(4, notification.getIsRead());
            pstmt.setTimestamp(5, new java.sql.Timestamp(notification.getNotificationCreatedAt().getTime()));
            if (notification.getSentBy() != null) {
                pstmt.setInt(6, notification.getSentBy());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Lấy thông báo theo ID
     */
    public Notification getNotificationById(int notificationID) throws SQLException {
        String sql = "SELECT * FROM Notifications WHERE NotificationID = ?";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, notificationID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Notification(
                        rs.getInt("NotificationID"),
                        rs.getInt("CustomerID"),
                        rs.getString("Title"),
                        rs.getString("Message"),
                        rs.getBoolean("IsRead"),
                        rs.getTimestamp("NotificationCreatedAt"),
                        rs.getObject("SentBy", Integer.class)
                    );
                }
            }
        }
        return null;
    }

    /**
     * Lấy tất cả các thông báo
     */
    public List<Notification> getAllNotifications() throws SQLException {
        List<Notification> notificationList = new ArrayList<>();
        String sql = "SELECT * FROM Notifications";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                notificationList.add(new Notification(
                    rs.getInt("NotificationID"),
                    rs.getInt("CustomerID"),
                    rs.getString("Title"),
                    rs.getString("Message"),
                    rs.getBoolean("IsRead"),
                    rs.getTimestamp("NotificationCreatedAt"),
                    rs.getObject("SentBy", Integer.class)
                ));
            }
        }
        return notificationList;
    }

    /**
     * Tìm kiếm thông báo theo từ khóa (Title hoặc Message)
     */
    public List<Notification> searchNotifications(String keyword) throws SQLException {
        List<Notification> notificationList = new ArrayList<>();
        String sql = "SELECT * FROM Notifications WHERE Title LIKE ? OR Message LIKE ?";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notificationList.add(new Notification(
                        rs.getInt("NotificationID"),
                        rs.getInt("CustomerID"),
                        rs.getString("Title"),
                        rs.getString("Message"),
                        rs.getBoolean("IsRead"),
                        rs.getTimestamp("NotificationCreatedAt"),
                        rs.getObject("SentBy", Integer.class)
                    ));
                }
            }
        }
        return notificationList;
    }

    /**
     * Cập nhật thông báo
     */
    public boolean updateNotification(Notification notification) throws SQLException {
        String sql = "UPDATE Notifications SET CustomerID = ?, Title = ?, Message = ?, IsRead = ?, NotificationCreatedAt = ?, SentBy = ? WHERE NotificationID = ?";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, notification.getCustomerID());
            pstmt.setString(2, notification.getTitle());
            pstmt.setString(3, notification.getMessage());
            pstmt.setBoolean(4, notification.getIsRead());
            pstmt.setTimestamp(5, new java.sql.Timestamp(notification.getNotificationCreatedAt().getTime()));
            if (notification.getSentBy() != null) {
                pstmt.setInt(6, notification.getSentBy());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }
            pstmt.setInt(7, notification.getNotificationID());
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Xóa thông báo
     */
    public boolean deleteNotification(int notificationID) throws SQLException {
        String sql = "DELETE FROM Notifications WHERE NotificationID = ?";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, notificationID);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Lấy thông báo theo BlockID
     */
    public List<Notification> getNotificationsByBlock(int blockId) throws SQLException {
        List<Notification> notificationList = new ArrayList<>();
        String sql = "SELECT n.* FROM Notifications n " +
                     "JOIN Customers c ON n.CustomerID = c.CustomerID " +
                     "JOIN Tenants t ON c.CustomerID = t.CustomerID " +
                     "JOIN Contracts ct ON t.TenantID = ct.TenantID " +
                     "JOIN Rooms r ON ct.RoomID = r.RoomID " +
                     "WHERE r.BlockID = ? AND c.CustomerStatus = 'Active'";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, blockId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notificationList.add(new Notification(
                        rs.getInt("NotificationID"),
                        rs.getInt("CustomerID"),
                        rs.getString("Title"),
                        rs.getString("Message"),
                        rs.getBoolean("IsRead"),
                        rs.getTimestamp("NotificationCreatedAt"),
                        rs.getObject("SentBy", Integer.class)
                    ));
                }
            }
        }
        return notificationList;
    }

    /**
     * Lấy thông báo theo CustomerID
     */
    public List<Notification> getNotificationsByCustomer(int customerID) throws SQLException {
        List<Notification> notificationList = new ArrayList<>();
        String sql = "SELECT * FROM Notifications WHERE CustomerID = ? ORDER BY NotificationCreatedAt DESC";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerID);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notificationList.add(new Notification(
                        rs.getInt("NotificationID"),
                        rs.getInt("CustomerID"),
                        rs.getString("Title"),
                        rs.getString("Message"),
                        rs.getBoolean("IsRead"),
                        rs.getTimestamp("NotificationCreatedAt"),
                        rs.getObject("SentBy", Integer.class)
                    ));
                }
            }
        }
        return notificationList;
    }
}