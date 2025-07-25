package dao;

import model.IncurredFeeType;
import utils.DBContext;
import java.sql.*;
import java.util.*;

public class IncurredFeeTypeDAO extends DBContext {

    private final DBContext dbContext = new DBContext();
    
       public List<IncurredFeeType> getAll() throws SQLException {
        List<IncurredFeeType> list = new ArrayList<>();
        String sql = "SELECT * FROM IncurredFeeTypes";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new IncurredFeeType(
                    rs.getInt("IncurredFeeTypeID"),
                    rs.getString("FeeName"),
                    rs.getBigDecimal("DefaultAmount")
                ));
            }
        }
         System.out.println("DEBUG: getAll() size = " + list.size());
        return list;
    }

    // Lấy phụ phí theo ID
    public IncurredFeeType getById(int id) throws SQLException {
        String sql = "SELECT * FROM IncurredFeeTypes WHERE IncurredFeeTypeID=?";
        try (Connection conn =getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
        }
        return null;
    }

    // Thêm mới phụ phí
    public void insert(IncurredFeeType feeType) throws SQLException {
        String sql = "INSERT INTO IncurredFeeTypes (FeeName, DefaultAmount) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, feeType.getFeeName());
            ps.setBigDecimal(2, feeType.getDefaultAmount());
            ps.executeUpdate();
        }
    }

    // Cập nhật phụ phí
    public void update(IncurredFeeType feeType) throws SQLException {
        String sql = "UPDATE IncurredFeeTypes SET FeeName=?, DefaultAmount=? WHERE IncurredFeeTypeID=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, feeType.getFeeName());
            ps.setBigDecimal(2, feeType.getDefaultAmount());
            ps.setInt(3, feeType.getIncurredFeeTypeID());
            ps.executeUpdate();
        }
    }

    // Xóa phụ phí
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM IncurredFeeTypes WHERE IncurredFeeTypeID=?";
        try (Connection conn =getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<IncurredFeeType> getAllIncurredFeeTypes() throws SQLException {
        List<IncurredFeeType> list = new ArrayList<>();
        String sql = "SELECT * FROM IncurredFeeTypes";
        try ( Connection c = dbContext.getConnection();  PreparedStatement ps = c.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public IncurredFeeType getIncurredFeeTypeById(int id) throws SQLException {
        String sql = "SELECT * FROM IncurredFeeTypes WHERE IncurredFeeTypeID=?";
        try ( Connection c = dbContext.getConnection();  PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public boolean insertIncurredFeeType(IncurredFeeType b) throws SQLException {
        String sql = "INSERT INTO IncurredFeeTypes (FeeName, DefaultAmount) VALUES (?, ?)";
        return dbContext.execUpdateQuery(sql, b.getFeeName(), b.getDefaultAmount()) > 0;
    }

    public boolean updateIncurredFeeType(IncurredFeeType b) throws SQLException {
        String sql = "UPDATE IncurredFeeTypes SET FeeName=?, DefaultAmount=? WHERE IncurredFeeTypeID=?";
        return dbContext.execUpdateQuery(sql, b.getFeeName(), b.getDefaultAmount(), b.getIncurredFeeTypeID()) > 0;
    }

    public boolean deleteIncurredFeeType(int id) throws SQLException {
        String sql = "DELETE FROM IncurredFeeTypes WHERE IncurredFeeTypeID=?";
        return dbContext.execUpdateQuery(sql, id) > 0;
    }

    private IncurredFeeType map(ResultSet rs) throws SQLException {
        IncurredFeeType b = new IncurredFeeType();
        b.setIncurredFeeTypeID(rs.getInt("IncurredFeeTypeID"));
        b.setFeeName(rs.getString("FeeName"));
        b.setDefaultAmount(rs.getBigDecimal("DefaultAmount"));
        return b;
    }
}
