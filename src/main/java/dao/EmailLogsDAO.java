package dao;

import model.EmailLogs;
import utils.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmailLogsDAO extends DBContext {

    private EmailLogs mapResultSetToEmailLogs(ResultSet rs) throws SQLException {
        EmailLogs emailLog = new EmailLogs();
        emailLog.setEmailLogID(rs.getInt("EmailLogID"));
        emailLog.setCustomerID(rs.getObject("CustomerID") != null ? rs.getInt("CustomerID") : null);
        emailLog.setUserID(rs.getObject("UserID") != null ? rs.getInt("UserID") : null);
        emailLog.setSentByUserID(rs.getObject("SentByUserID") != null ? rs.getInt("SentByUserID") : null);
        emailLog.setEmail(rs.getString("Email"));
        emailLog.setSubject(rs.getString("Subject"));
        emailLog.setMessage(rs.getString("Message"));
        emailLog.setSentAt(rs.getTimestamp("SentAt"));
        emailLog.setStatus(rs.getString("Status"));
        emailLog.setErrorMessage(rs.getString("ErrorMessage"));
        return emailLog;
    }

    public List<EmailLogs> getAllEmailLogs() throws SQLException {
        List<EmailLogs> emailLogs = new ArrayList<>();
        String query = "SELECT * FROM EmailLogs ORDER BY SentAt DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                emailLogs.add(mapResultSetToEmailLogs(rs));
            }
        }
        return emailLogs;
    }

    public List<EmailLogs> getEmailLogsByCustomerID(Integer customerID) throws SQLException {
        List<EmailLogs> emailLogs = new ArrayList<>();
        String query = customerID == null ?
                "SELECT * FROM EmailLogs WHERE CustomerID IS NULL ORDER BY SentAt DESC" :
                "SELECT * FROM EmailLogs WHERE CustomerID = ? ORDER BY SentAt DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            if (customerID != null) {
                ps.setInt(1, customerID);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    emailLogs.add(mapResultSetToEmailLogs(rs));
                }
            }
        }
        return emailLogs;
    }

    public List<EmailLogs> getEmailLogsByUserID(Integer userID) throws SQLException {
        List<EmailLogs> emailLogs = new ArrayList<>();
        String query = userID == null ?
                "SELECT * FROM EmailLogs WHERE UserID IS NULL ORDER BY SentAt DESC" :
                "SELECT * FROM EmailLogs WHERE UserID = ? ORDER BY SentAt DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            if (userID != null) {
                ps.setInt(1, userID);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    emailLogs.add(mapResultSetToEmailLogs(rs));
                }
            }
        }
        return emailLogs;
    }

    public boolean logEmail(Integer customerID, Integer userID, Integer sentByUserID, String email, String subject, 
                           String message, String status, String errorMessage) throws SQLException {
        String query = "INSERT INTO EmailLogs (CustomerID, UserID, SentByUserID, Email, Subject, Message, Status, ErrorMessage) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, customerID);
            ps.setObject(2, userID);
            ps.setObject(3, sentByUserID);
            ps.setString(4, email);
            ps.setString(5, subject);
            ps.setString(6, message);
            ps.setString(7, status);
            ps.setString(8, errorMessage);
            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    public EmailLogs getEmailLogById(int emailLogID) throws SQLException {
        String query = "SELECT * FROM EmailLogs WHERE EmailLogID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, emailLogID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmailLogs(rs);
                }
            }
        }
        return null;
    }
}