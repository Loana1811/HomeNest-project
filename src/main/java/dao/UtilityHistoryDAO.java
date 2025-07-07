package dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.UtilityHistoryView;
import utils.DBContext;

public class UtilityHistoryDAO {

    public List<UtilityHistoryView> getHistory() throws SQLException {
        List<UtilityHistoryView> list = new ArrayList<>();

        String utilitySql =
            "SELECT ur.UtilityTypeID AS ID, " +
            "ISNULL(ut.UtilityName, " +
            "  CASE WHEN CHARINDEX('|', ur.ChangedBy) > 0 " +
            "       THEN SUBSTRING(ur.ChangedBy, CHARINDEX('|', ur.ChangedBy) + 1, 100) " +
            "       ELSE ur.ChangedBy END) AS Name, " +
            "ur.OldPrice, ur.PriceUsed AS NewPrice, " +
            "CASE WHEN CHARINDEX('|', ur.ChangedBy) > 0 " +
            "     THEN LEFT(ur.ChangedBy, CHARINDEX('|', ur.ChangedBy) - 1) " +
            "     ELSE ur.ChangedBy END AS ChangedBy, " +
            "ur.UtilityReadingCreatedAt AS ChangeAt, " +
            "1 AS IsUtility " +
            "FROM UtilityReadings ur " +
            "LEFT JOIN UtilityTypes ut ON ur.UtilityTypeID = ut.UtilityTypeID " +
            "WHERE ur.OldPrice IS NOT NULL";

      String feeSql =
    "SELECT ir.IncurredFeeTypeID AS ID, " +
    "ISNULL(ift.FeeName, '[Removed]') AS Name, " +
    "ir.OldFeeAmount AS OldPrice, " +
    "ir.Amount AS NewPrice, " +
    "ir.FeeModifiedBy AS ChangedBy, " +
    "ir.FeeModifiedAt AS ChangeAt, " +
    "0 AS IsUtility " +
    "FROM IncurredFees ir " +
    "LEFT JOIN IncurredFeeTypes ift ON ir.IncurredFeeTypeID = ift.IncurredFeeTypeID " +
    "WHERE ir.OldFeeAmount IS NOT NULL";


        String fullSql = utilitySql + " UNION ALL " + feeSql + " ORDER BY ChangeAt DESC";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(fullSql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new UtilityHistoryView(
                    rs.getInt("ID"),
                    rs.getString("Name"),
                    rs.getBigDecimal("OldPrice"),
                    rs.getBigDecimal("NewPrice"),
                    rs.getString("ChangedBy"),
                    rs.getDate("ChangeAt"),
                    rs.getBoolean("IsUtility")
                ));
            }
        }

        return list;
    }

    public void insertHistory(int utilityTypeId, String utilityName,
                               double oldPrice, double newPrice,
                               String changedBy, Date date) throws SQLException {
        String sql = "INSERT INTO UtilityReadings "
                   + "(UtilityTypeID, RoomID, ReadingDate, OldReading, NewReading, OldPrice, PriceUsed, ChangedBy, UtilityReadingCreatedAt) "
                   + "VALUES (?, ?, ?, 0, 0, ?, ?, ?, ?)";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, utilityTypeId);
            ps.setInt(2, 1); // dummy roomId
            ps.setDate(3, date);
            ps.setDouble(4, oldPrice);
            ps.setDouble(5, newPrice);
            ps.setString(6, changedBy + "|" + utilityName);
            ps.setDate(7, date);
            ps.executeUpdate();
        }
    }

    public void insertHistoryForIncurredFee(int feeTypeId, String feeName,
                                        double oldPrice, double newPrice,
                                        String changedBy, Date date) throws SQLException {
    String sql = "INSERT INTO IncurredFees " +
                 "(IncurredFeeTypeID, BillID, Amount, OldFeeAmount, FeeModifiedBy, FeeModifiedAt) " +
                 "VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = new DBContext().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, feeTypeId);
        ps.setNull(2, java.sql.Types.INTEGER); // BillID
        ps.setDouble(3, newPrice);             // Amount
        ps.setDouble(4, oldPrice);             // OldFeeAmount
        ps.setString(5, changedBy);            // FeeModifiedBy
        ps.setDate(6, date);                   // FeeModifiedAt
        ps.executeUpdate();
    }
}


}
