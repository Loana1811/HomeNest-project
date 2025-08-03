package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.UtilityHistoryView;
import utils.DBContext;

public class UtilityHistoryDAO {

   public List<UtilityHistoryView> getHistory() throws SQLException {
    List<UtilityHistoryView> list = new ArrayList<>();

   String sql =
    "SELECT " +
    "    ur.UtilityTypeID, " +
    "    ut.UtilityName, " +
    "    ur.PriceUsed AS CurrentPrice, " +
    "    ur.UtilityReadingCreatedAt AS ChangeAt, " +
    "    ut.UnitPrice AS UpcomingPrice, " +
    "    DATEADD(DAY, 30, ur.UtilityReadingCreatedAt) AS ApplyAt " +
    "FROM UtilityReadings ur " +
    "JOIN UtilityTypes ut ON ur.UtilityTypeID = ut.UtilityTypeID " +
    "WHERE ur.UtilityReadingCreatedAt = ( " +
    "    SELECT MAX(u2.UtilityReadingCreatedAt) " +
    "    FROM UtilityReadings u2 " +
    "    WHERE u2.UtilityTypeID = ur.UtilityTypeID " +
    "      AND u2.PriceUsed IS NOT NULL " +
    "      AND u2.PriceUsed > 0 " +
    ") " +
    "AND ut.UnitPrice != ur.PriceUsed " +
    "ORDER BY ur.UtilityTypeID";


    try (Connection conn = new DBContext().getConnection(); 
         PreparedStatement ps = conn.prepareStatement(sql); 
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            UtilityHistoryView history = new UtilityHistoryView(
                rs.getInt("UtilityTypeID"),
                rs.getString("UtilityName"),
                rs.getBigDecimal("CurrentPrice"), // oldPrice ← dùng current luôn
                rs.getBigDecimal("UpcomingPrice"), // newPrice là unit price sắp áp dụng
                null,
                rs.getDate("ChangeAt")
            );
            history.setApplyAt(rs.getDate("ApplyAt"));
            list.add(history);
        }
    }

    return list;
}


    public void insertHistory(
            int utilityTypeId, String utilityName,
            double oldPrice, double newPrice,
            String unused, Date date
    ) throws SQLException {
        String sql = "INSERT INTO UtilityReadings "
                   + "(UtilityTypeID, RoomID, ReadingDate, OldReading, NewReading, OldPrice, PriceUsed, UtilityReadingCreatedAt) "
                   + "VALUES (?, ?, ?, 0, 0, ?, ?, ?)";

        try (Connection conn = new DBContext().getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, utilityTypeId);
            ps.setInt(2, 1); // RoomID placeholder
            ps.setDate(3, date);
            ps.setDouble(4, oldPrice);
            ps.setDouble(5, newPrice);
            ps.setDate(6, date);
            ps.executeUpdate();
        }
    }

    public void insertHistory(int id, String utilityName, BigDecimal oldPrice, BigDecimal price, String admin, Date valueOf) {
        try {
            insertHistory(id, utilityName, oldPrice.doubleValue(), price.doubleValue(), admin, valueOf);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertHistoryForIncurredFee(int feeTypeId, String feeName,
            double oldPrice, double newPrice,
            String changedBy, Date date) throws SQLException {
        String sql = "INSERT INTO IncurredFees "
                   + "(IncurredFeeTypeID, BillID, Amount, OldFeeAmount, FeeModifiedBy, FeeModifiedAt) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = new DBContext().getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, feeTypeId);
            ps.setNull(2, java.sql.Types.INTEGER); // BillID
            ps.setDouble(3, newPrice);
            ps.setDouble(4, oldPrice);
            ps.setString(5, changedBy);
            ps.setDate(6, date);
            ps.executeUpdate();
        }
    }

    public List<UtilityHistoryView> getUpcomingPrices() throws SQLException {
        List<UtilityHistoryView> list = new ArrayList<>();
        String sql = "SELECT "
                   + "    ur.UtilityTypeID, "
                   + "    ut.UtilityName, "
                   + "    ur.PriceUsed AS OldPrice, "
                   + "    ut.UnitPrice AS NewPrice, "
                   + "    ur.UtilityReadingCreatedAt AS ChangeAt, "
                   + "    DATEADD(DAY, 30, ur.UtilityReadingCreatedAt) AS ApplyAt "
                   + "FROM UtilityReadings ur "
                   + "JOIN UtilityTypes ut ON ur.UtilityTypeID = ut.UtilityTypeID "
                   + "WHERE ur.UtilityReadingCreatedAt = ("
                   + "    SELECT MAX(u2.UtilityReadingCreatedAt) "
                   + "    FROM UtilityReadings u2 "
                   + "    WHERE u2.UtilityTypeID = ur.UtilityTypeID "
                   + "      AND u2.PriceUsed IS NOT NULL "
                   + "      AND u2.PriceUsed > 0"
                   + ") "
                   + "AND ut.UnitPrice != ur.PriceUsed "
                   + "ORDER BY ur.UtilityTypeID;";

        try (Connection conn = new DBContext().getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new UtilityHistoryView(
                    rs.getInt("UtilityTypeID"),
                    rs.getString("UtilityName"),
                    rs.getBigDecimal("OldPrice"),
                    rs.getBigDecimal("NewPrice"),
                    null,
                    rs.getDate("ChangeAt"),
                    rs.getDate("ApplyAt")
                ));
            }
        }
        return list;
    }
} 
