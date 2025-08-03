package dao;

import model.BillFeedback;
import utils.DBContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillFeedbackDAO {
    private final DBContext dbContext = new DBContext();

    // Thêm phản hồi
    public void addFeedback(BillFeedback feedback) throws SQLException {
        String sql = "INSERT INTO BillFeedback (BillID, UserID, Reason, Status, CreatedAt) VALUES (?, ?, ?, 'Pending', GETDATE())";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, feedback.getBillId());
            ps.setInt(2, feedback.getUserId());
            ps.setString(3, feedback.getReason());
            ps.executeUpdate();
        }
    }

    // Lấy danh sách phản hồi theo trạng thái
    public List<BillFeedback> getFeedbacksByStatus(String status) throws SQLException {
        List<BillFeedback> list = new ArrayList<>();
        String sql = "SELECT * FROM BillFeedback WHERE Status = ?";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BillFeedback f = new BillFeedback();
                f.setFeedbackId(rs.getInt("FeedbackID"));
                f.setBillId(rs.getInt("BillID"));
                f.setUserId(rs.getInt("UserID"));
                f.setReason(rs.getString("Reason"));
                f.setStatus(rs.getString("Status"));
                f.setCreatedAt(rs.getTimestamp("CreatedAt"));
                list.add(f);
            }
        }
        return list;
    }

    // Cập nhật trạng thái phản hồi
    public void updateStatus(int feedbackId, String newStatus) throws SQLException {
        String sql = "UPDATE BillFeedback SET Status = ? WHERE FeedbackID = ?";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, feedbackId);
            ps.executeUpdate();
        }
    }

    // Lấy phản hồi theo ID
    public BillFeedback getFeedbackById(int feedbackId) throws SQLException {
        String sql = "SELECT * FROM BillFeedback WHERE FeedbackID = ?";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, feedbackId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BillFeedback f = new BillFeedback();
                f.setFeedbackId(feedbackId);
                f.setBillId(rs.getInt("BillID"));
                f.setUserId(rs.getInt("UserID"));
                f.setReason(rs.getString("Reason"));
                f.setStatus(rs.getString("Status"));
                f.setCreatedAt(rs.getTimestamp("CreatedAt"));
                return f;
            }
        }
        return null;
    }
    
    public List<BillFeedback> getAllPendingFeedbacks() throws SQLException {
    List<BillFeedback> list = new ArrayList<>();
    String sql = "SELECT * FROM BillFeedback WHERE Status = 'Pending'";
    try (Connection conn = dbContext.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            BillFeedback fb = new BillFeedback();
            fb.setFeedbackId(rs.getInt("FeedbackID"));
            fb.setBillId(rs.getInt("BillID"));
            fb.setUserId(rs.getInt("UserID"));
            fb.setReason(rs.getString("Reason"));
            fb.setStatus(rs.getString("Status"));
            fb.setCreatedAt(rs.getTimestamp("CreatedAt"));
            list.add(fb);
        }
    }
    return list;
}

}
