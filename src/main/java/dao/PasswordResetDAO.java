/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Admin
 */
    public class PasswordResetDAO extends DBContext {
        public void saveResetToken(String email, String token) throws SQLException {
            String sql = "INSERT INTO PasswordResetTokens (Token, Email, ExpiryTime) " +
                         "VALUES (?, ?, DATEADD(MINUTE, 30, GETDATE()))";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, token);
                ps.setString(2, email);
                ps.executeUpdate();
            }
        }

        public boolean isValidToken(String token) throws SQLException {
            String sql = "SELECT * FROM PasswordResetTokens WHERE Token = ? AND ExpiryTime > GETDATE()";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, token);
                ResultSet rs = ps.executeQuery();
                return rs.next();
            }
        }

        public String getEmailByToken(String token) throws SQLException {
            String sql = "SELECT Email FROM PasswordResetTokens WHERE Token = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, token);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getString("Email");
                }
            }
            return null;
        }

        public void deleteToken(String token) throws SQLException {
            String sql = "DELETE FROM PasswordResetTokens WHERE Token = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, token);
                ps.executeUpdate();
            }
        }
    }
