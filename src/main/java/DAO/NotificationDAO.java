package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Notification;
import utils.DBContext;

public class NotificationDAO extends DBContext {

    // Gửi thông báo mới
    public void insertNotification(Notification notification) {
        String checkCustomerSql = "SELECT COUNT(*) FROM Customers WHERE CustomerID = ?";
        String insertSql = "INSERT INTO Notifications (CustomerID, Title, Message, IsRead, NotificationCreatedAt, SentBy) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection()) {

            System.out.println("▶ insertNotification() bắt đầu chạy với CustomerID = " + notification.getCustomerID());

            // B1: Kiểm tra Customers tồn tại
            try (PreparedStatement checkStmt = conn.prepareStatement(checkCustomerSql)) {
                checkStmt.setInt(1, notification.getCustomerID());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    System.err.println("❌ [insertNotification] CustomerID không tồn tại: " + notification.getCustomerID());
                    return;
                }
            }

            // B2: Insert
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, notification.getCustomerID());
                ps.setString(2, notification.getTitle());
                ps.setString(3, notification.getMessage());
                ps.setBoolean(4, notification.isIsRead());

                ps.setTimestamp(5, notification.getNotificationCreateAt()); // ✅ dùng Timestamp từ Java
                ps.setInt(6, Integer.parseInt(notification.getSentBy())); // ✅ ép String về int


                int rows = ps.executeUpdate();
                System.out.println("✅ [insertNotification] Insert thành công! Rows = " + rows);
            }

        } catch (SQLException e) {
            System.err.println("❌ [insertNotification] Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Lấy danh sách thông báo của 1 customer
    public List<Notification> getNotificationsByCustomer(int customerId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM Notifications WHERE CustomerID = ? ORDER BY NotificationCreatedAt DESC";

        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Notification n = new Notification();
                n.setNotificationID(rs.getInt("NotificationID"));
                n.setCustomerID(rs.getInt("CustomerID"));
                n.setTitle(rs.getString("Title"));
                n.setMessage(rs.getString("Message"));
                n.setIsRead(rs.getBoolean("IsRead"));
                n.setNotificationCreateAt(rs.getTimestamp("NotificationCreatedAt"));
                n.setSentBy(rs.getString("SentBy"));

                list.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Đánh dấu đã đọc
    public void markAsRead(int notificationId) {
        String sql = "UPDATE Notifications SET IsRead = 1 WHERE NotificationID = ?";

        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xoá thông báo
    public void deleteNotification(int notificationId) {
        String sql = "DELETE FROM Notifications WHERE NotificationID = ?";

        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra xem đã tồn tại thông báo tương tự chưa (chưa đọc)
    public boolean existsSimilarNotification(int customerId, String title, String message) {
        String sql = "SELECT COUNT(*) FROM Notifications "
                   + "WHERE CustomerID = ? AND Title = ? AND Message = ? AND IsRead = 0";

        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setString(2, title);
            ps.setString(3, message);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    public List<Notification> getNotificationsWithSenderNameByCustomer(int customerId) {
    List<Notification> list = new ArrayList<>();
    String sql = "SELECT n.*, u.UserFullName AS SenderName " +
                 "FROM Notifications n " +
                 "LEFT JOIN Users u ON n.SentBy = u.UserID " +
                 "WHERE n.CustomerID = ? " +
                 "ORDER BY n.NotificationCreatedAt DESC";

    try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Notification n = new Notification();
            n.setNotificationID(rs.getInt("NotificationID"));
            n.setCustomerID(rs.getInt("CustomerID"));
            n.setTitle(rs.getString("Title"));
            n.setMessage(rs.getString("Message"));
            n.setIsRead(rs.getBoolean("IsRead"));
            n.setNotificationCreateAt(rs.getTimestamp("NotificationCreatedAt"));
            n.setSentBy(rs.getString("SenderName"));  // gán tên người gửi vào đây

            list.add(n);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}

}
