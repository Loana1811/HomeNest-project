package dao;

import model.IncurredFeeType;
import utils.DBContext;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class IncurredFeeTypeDAO {
    private Connection conn;

    public IncurredFeeTypeDAO() {
        try {
            conn = new DBContext().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<IncurredFeeType> getAllIncurredFeeTypes() {
        List<IncurredFeeType> list = new ArrayList<>();
        String sql = "SELECT IncurredFeeTypeID, FeeName, DefaultAmount FROM IncurredFeeTypes";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new IncurredFeeType(
                    rs.getInt("IncurredFeeTypeID"),
                    rs.getString("FeeName"),
                    rs.getBigDecimal("DefaultAmount")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public IncurredFeeType getIncurredFeeTypeById(int id) {
        String sql = "SELECT IncurredFeeTypeID, FeeName, DefaultAmount FROM IncurredFeeTypes WHERE IncurredFeeTypeID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new IncurredFeeType(
                        rs.getInt("IncurredFeeTypeID"),
                        rs.getString("FeeName"),
                        rs.getBigDecimal("DefaultAmount")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addIncurredFeeType(IncurredFeeType t) {
        String sql = "INSERT INTO IncurredFeeTypes (FeeName, DefaultAmount) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getFeeName());
            ps.setBigDecimal(2, t.getDefaultAmount());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateIncurredFeeType(IncurredFeeType t) {
        String sql = "UPDATE IncurredFeeTypes SET FeeName = ?, DefaultAmount = ? WHERE IncurredFeeTypeID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getFeeName());
            ps.setBigDecimal(2, t.getDefaultAmount());
            ps.setInt(3, t.getIncurredFeeTypeID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteIncurredFeeType(int id) {
        String sql = "DELETE FROM IncurredFeeTypes WHERE IncurredFeeTypeID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
