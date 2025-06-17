package dao;

import model.Notification;
import utils.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    private Connection conn;

    public NotificationDAO() {
        try {
            conn = new DBContext().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Notification> getAllNotifications() {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT NotificationID, CustomerID, Title, Message, IsRead, NotificationCreatedAt, SentBy FROM Notifications";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Notification(
                    rs.getInt("NotificationID"),
                    rs.getInt("CustomerID"),
                    rs.getString("Title"),
                    rs.getString("Message"),
                    rs.getBoolean("IsRead"),
                    rs.getTimestamp("NotificationCreatedAt"),
                    (Integer) rs.getObject("SentBy")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Notification getNotificationById(int id) {
        String sql = "SELECT NotificationID, CustomerID, Title, Message, IsRead, NotificationCreatedAt, SentBy "
                   + "FROM Notifications WHERE NotificationID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Notification(
                        rs.getInt("NotificationID"),
                        rs.getInt("CustomerID"),
                        rs.getString("Title"),
                        rs.getString("Message"),
                        rs.getBoolean("IsRead"),
                        rs.getTimestamp("NotificationCreatedAt"),
                        (Integer) rs.getObject("SentBy")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addNotification(Notification n) {
        String sql = "INSERT INTO Notifications (CustomerID, Title, Message, IsRead, NotificationCreatedAt, SentBy) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, n.getCustomerID());
            ps.setString(2, n.getTitle());
            ps.setString(3, n.getMessage());
            ps.setBoolean(4, n.isIsRead());
            ps.setTimestamp(5, n.getNotificationCreatedAt());
            if (n.getSentBy() != null) ps.setInt(6, n.getSentBy());
            else ps.setNull(6, java.sql.Types.INTEGER);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateNotification(Notification n) {
        String sql = "UPDATE Notifications SET CustomerID = ?, Title = ?, Message = ?, IsRead = ?, NotificationCreatedAt = ?, SentBy = ? "
                   + "WHERE NotificationID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, n.getCustomerID());
            ps.setString(2, n.getTitle());
            ps.setString(3, n.getMessage());
            ps.setBoolean(4, n.isIsRead());
            ps.setTimestamp(5, n.getNotificationCreatedAt());
            if (n.getSentBy() != null) ps.setInt(6, n.getSentBy());
            else ps.setNull(6, java.sql.Types.INTEGER);
            ps.setInt(7, n.getNotificationID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteNotification(int id) {
        String sql = "DELETE FROM Notifications WHERE NotificationID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Notification> getNotificationsByCustomerId(int customerId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT NotificationID, CustomerID, Title, Message, IsRead, NotificationCreatedAt, SentBy "
                   + "FROM Notifications WHERE CustomerID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Notification(
                        rs.getInt("NotificationID"),
                        rs.getInt("CustomerID"),
                        rs.getString("Title"),
                        rs.getString("Message"),
                        rs.getBoolean("IsRead"),
                        rs.getTimestamp("NotificationCreatedAt"),
                        (Integer) rs.getObject("SentBy")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void markAsRead(int notificationId) {
        String sql = "UPDATE Notifications SET IsRead = 1 WHERE NotificationID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
