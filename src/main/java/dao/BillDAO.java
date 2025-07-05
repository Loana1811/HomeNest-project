package dao;

import java.math.BigDecimal;
import model.Bill;
import utils.DBContext;
import java.sql.*;
import java.util.*;

public class BillDAO {

    private final DBContext dbContext = new DBContext();

    public List<Bill> getAllBills() throws SQLException {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT * FROM Bills";
        try ( Connection c = dbContext.getConnection();  PreparedStatement ps = c.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Bill getBillById(int id) throws SQLException {
        String sql = "SELECT * FROM Bills WHERE BillID=?";
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

    public boolean insertBill(Bill b) throws SQLException {
        String sql = "INSERT INTO Bills (ContractID, BillDate, TotalAmount, Status) VALUES (?, ?, ?, ?)";
        return dbContext.execUpdateQuery(sql, b.getContractID(), b.getBillDate(), b.getTotalAmount(), b.getBillStatus()) > 0;
    }

    public boolean updateBill(Bill b) throws SQLException {
        String sql = "UPDATE Bills SET ContractID=?, BillDate=?, TotalAmount=?, BillStatus = ? WHERE BillID=?";
        return dbContext.execUpdateQuery(sql, b.getContractID(), b.getBillDate(), b.getTotalAmount(), b.getBillStatus(), b.getBillID()) > 0;
    }

    public boolean deleteBill(int id) throws SQLException {
        String sql = "DELETE FROM Bills WHERE BillID=?";
        return dbContext.execUpdateQuery(sql, id) > 0;
    }

    private Bill map(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setBillID(rs.getInt("BillID"));
        b.setContractID(rs.getInt("ContractID"));
        b.setBillDate(rs.getDate("BillDate"));
        b.setTotalAmount(rs.getFloat("TotalAmount"));
        b.setBillStatus(rs.getString("BillStatus"));
        return b;
    }

    public boolean existsBillForRoomAndMonth(int roomId, String month) {
        String sql
                = "SELECT 1 "
                + "FROM Bills b "
                + "JOIN Contracts c ON b.ContractID = c.ContractID "
                + "WHERE c.RoomID = ? "
                + "AND FORMAT(b.BillDate, 'yyyy-MM') = ?";

        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setString(2, month);
            try ( ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true nếu tồn tại ít nhất 1 dòng
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int createBill(Bill bill) {
        int generatedId = -1;
        String sql = "INSERT INTO Bills (ContractID, BillDate, TotalAmount, BillStatus) VALUES (?, ?, ?, ?)";

        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, bill.getContractID());
            ps.setDate(2, bill.getBillDate());
            ps.setFloat(3, bill.getTotalAmount());
            ps.setString(4, bill.getBillStatus());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try ( ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1); // BillID vừa được sinh
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return generatedId;
    }

//public Bill getBillByContractAndMonth(int contractId, String month) throws SQLException {
//    String sql = "SELECT * FROM Bills WHERE ContractID = ? AND FORMAT(BillDate, 'yyyy-MM') = ?";
//    try (Connection conn = dbContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
//        ps.setInt(1, contractId);
//        ps.setString(2, month);
//        try (ResultSet rs = ps.executeQuery()) {
//            if (rs.next()) {
//                Bill bill = new Bill();
//                bill.setBillID(rs.getInt("BillID"));
//                bill.setContractID(rs.getInt("ContractID"));
//                bill.setTotalAmount(rs.getFloat("TotalAmount"));
//                bill.setBillStatus(rs.getString("BillStatus"));
//                bill.setBillDate(rs.getDate("BillDate"));
//                return bill;
//            }
//        }
//    }
//    return null;
//}
    public Bill getBillByContractAndMonth(int contractId, String ignoredMonth) throws SQLException {
        String sql = "SELECT TOP 1 * FROM Bills WHERE ContractID = ? ORDER BY BillDate DESC"; // lấy bill mới nhất
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public Bill getLatestBillByContract(int contractId) throws SQLException {
        String sql = "SELECT TOP 1 * FROM Bills WHERE ContractID = ? ORDER BY BillDate DESC";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

//public List<Map<String, Object>> getBillSummaryByMonth(String month) throws SQLException {
//    List<Map<String, Object>> result = new ArrayList<>();
//   String sql = ""
//    + "SELECT \n"
//    + "    b.BillID,\n"
//    + "    r.RoomNumber AS Room,\n"
//    + "    r.RentPrice AS RoomRent,\n"
//    + "    ISNULL(electricity.Amount, 0) AS Electricity,\n"
//    + "    ISNULL(water.Amount, 0) AS Water,\n"
//    + "    ISNULL(trash.UnitPrice, 0) AS Trash,\n"
//    + "    ISNULL(wifi.UnitPrice, 0) AS Wifi,\n"
//    + "    ISNULL(fee.ExtraFee, 0) AS ExtraFee,\n"
//    + "    ISNULL(fee.Deposit, 0) AS Deposit,\n"
//    + "    (\n"
//    + "        ISNULL(r.RentPrice, 0)\n"
//    + "        + ISNULL(electricity.Amount, 0)\n"
//    + "        + ISNULL(water.Amount, 0)\n"
//    + "        + ISNULL(trash.UnitPrice, 0)\n"
//    + "        + ISNULL(wifi.UnitPrice, 0)\n"
//    + "        + ISNULL(fee.ExtraFee, 0)\n"
//    + "    ) AS TotalCalculated,\n"
//    + "    b.BillStatus\n"
//    + "FROM Bills b\n"
//    + "JOIN Contracts c ON b.ContractID = c.ContractID\n"
//    + "JOIN Rooms r ON c.RoomID = r.RoomID\n"
//    + "OUTER APPLY (\n"
//    + "    SELECT SUM((ur.NewReading - ur.OldReading) * ut.UnitPrice) AS Amount\n"
//    + "    FROM UtilityReadings ur\n"
//    + "    JOIN UtilityTypes ut ON ur.UtilityTypeID = ut.UtilityTypeID\n"
//    + "    WHERE ur.UtilityTypeID = 1\n"
//    + "      AND ur.RoomID = r.RoomID\n"
//    + "      AND FORMAT(ur.ReadingDate, 'yyyy-MM') = ?\n"
//    + ") electricity\n"
//    + "OUTER APPLY (\n"
//    + "    SELECT SUM((ur.NewReading - ur.OldReading) * ut.UnitPrice) AS Amount\n"
//    + "    FROM UtilityReadings ur\n"
//    + "    JOIN UtilityTypes ut ON ur.UtilityTypeID = ut.UtilityTypeID\n"
//    + "    WHERE ur.UtilityTypeID = 2\n"
//    + "      AND ur.RoomID = r.RoomID\n"
//    + "      AND FORMAT(ur.ReadingDate, 'yyyy-MM') = ?\n"
//    + ") water\n"
//    + "OUTER APPLY (\n"
//    + "    SELECT TOP 1 ut.UnitPrice\n"
//    + "    FROM UtilityTypes ut\n"
//    + "    WHERE ut.UtilityTypeID = 3\n"
//    + ") trash\n"
//    + "OUTER APPLY (\n"
//    + "    SELECT TOP 1 ut.UnitPrice\n"
//    + "    FROM UtilityTypes ut\n"
//    + "    WHERE ut.UtilityTypeID = 4\n"
//    + ") wifi\n"
//    + "OUTER APPLY (\n"
//    + "    SELECT \n"
//    + "        SUM(CASE WHEN f.IncurredFeeTypeID = 99 THEN f.Amount ELSE 0 END) AS Deposit,\n"
//    + "        SUM(CASE WHEN f.IncurredFeeTypeID NOT IN (1, 99) THEN f.Amount ELSE 0 END) AS ExtraFee\n"
//    + "    FROM IncurredFees f\n"
//    + "    WHERE f.BillID = b.BillID\n"
//    + ") fee\n"
//    + "WHERE FORMAT(b.BillDate, 'yyyy-MM') = ?\n"
//    + "ORDER BY b.BillID DESC";
//
//
//    try (Connection conn = dbContext.getConnection();
//         PreparedStatement ps = conn.prepareStatement(sql)) {
//        for (int i = 1; i <= 3; i++) {
//            ps.setString(i, month);
//        }
//        ResultSet rs = ps.executeQuery();
//        while (rs.next()) {
//            Map<String, Object> row = new HashMap<>();
//            row.put("BillID", rs.getInt("BillID"));
//            row.put("Room", rs.getString("Room"));
//            row.put("RoomRent", rs.getBigDecimal("RoomRent"));
//            row.put("Electricity", rs.getBigDecimal("Electricity"));
//            row.put("Water", rs.getBigDecimal("Water"));
//            row.put("Trash", rs.getBigDecimal("Trash"));
//            row.put("Wifi", rs.getBigDecimal("Wifi"));
//            row.put("ExtraFee", rs.getBigDecimal("ExtraFee"));
//            row.put("Deposit", rs.getBigDecimal("Deposit"));
//            row.put("TotalCalculated", rs.getBigDecimal("TotalCalculated"));
//            row.put("BillStatus", rs.getString("BillStatus"));
//            result.add(row);
//        }
//    }
//    return result;
//}
    public List<Map<String, Object>> getBillSummaryByMonth(String month) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = ""
                + "SELECT \n"
                + "    b.BillID,\n"
                + "    r.RoomNumber AS Room,\n"
                + "    r.RentPrice AS RoomRent,\n"
                + "    ISNULL(electricity.Amount, 0) AS Electricity,\n"
                + "    ISNULL(water.Amount, 0) AS Water,\n"
                + "    ISNULL(trash.UnitPrice, 0) AS Trash,\n"
                + "    ISNULL(wifi.UnitPrice, 0) AS Wifi,\n"
                + "    ISNULL(fee.ExtraFee, 0) AS ExtraFee,\n"
                + "    ISNULL(fee.Deposit, 0) AS Deposit,\n"
                + "    (\n"
                + "        ISNULL(r.RentPrice, 0)\n"
                + "        + ISNULL(electricity.Amount, 0)\n"
                + "        + ISNULL(water.Amount, 0)\n"
                + "        + ISNULL(trash.UnitPrice, 0)\n"
                + "        + ISNULL(wifi.UnitPrice, 0)\n"
                + "        + ISNULL(fee.ExtraFee, 0)\n"
                + "    ) AS TotalCalculated,\n"
                + "    b.BillStatus\n"
                + "FROM Bills b\n"
                + "JOIN Contracts c ON b.ContractID = c.ContractID\n"
                + "JOIN Rooms r ON c.RoomID = r.RoomID\n"
                // Chỉ lấy bản ghi mới nhất theo UtilityReadingCreatedAt
                + "OUTER APPLY (\n"
                + "    SELECT TOP 1 (ur.NewReading - ur.OldReading) * ut.UnitPrice AS Amount\n"
                + "    FROM UtilityReadings ur\n"
                + "    JOIN UtilityTypes ut ON ur.UtilityTypeID = ut.UtilityTypeID\n"
                + "    WHERE ur.UtilityTypeID = 1 AND ur.RoomID = r.RoomID AND FORMAT(ur.ReadingDate, 'yyyy-MM') = ?\n"
                + "    ORDER BY ur.UtilityReadingCreatedAt DESC\n"
                + ") electricity\n"
                + "OUTER APPLY (\n"
                + "    SELECT TOP 1 (ur.NewReading - ur.OldReading) * ut.UnitPrice AS Amount\n"
                + "    FROM UtilityReadings ur\n"
                + "    JOIN UtilityTypes ut ON ur.UtilityTypeID = ut.UtilityTypeID\n"
                + "    WHERE ur.UtilityTypeID = 2 AND ur.RoomID = r.RoomID AND FORMAT(ur.ReadingDate, 'yyyy-MM') = ?\n"
                + "    ORDER BY ur.UtilityReadingCreatedAt DESC\n"
                + ") water\n"
                + "OUTER APPLY (\n"
                + "    SELECT TOP 1 ut.UnitPrice\n"
                + "    FROM UtilityTypes ut WHERE ut.UtilityTypeID = 3\n"
                + ") trash\n"
                + "OUTER APPLY (\n"
                + "    SELECT TOP 1 ut.UnitPrice\n"
                + "    FROM UtilityTypes ut WHERE ut.UtilityTypeID = 4\n"
                + ") wifi\n"
                + "OUTER APPLY (\n"
                + "    SELECT \n"
                + "        SUM(CASE WHEN f.IncurredFeeTypeID = 99 THEN f.Amount ELSE 0 END) AS Deposit,\n"
                + "        SUM(CASE WHEN f.IncurredFeeTypeID NOT IN (1, 99) THEN f.Amount ELSE 0 END) AS ExtraFee\n"
                + "    FROM IncurredFees f\n"
                + "    WHERE f.BillID = b.BillID\n"
                + ") fee\n"
                + "WHERE FORMAT(b.BillDate, 'yyyy-MM') = ?\n"
                + "ORDER BY b.BillID DESC";

        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, month); // for electricity
            ps.setString(2, month); // for water
            ps.setString(3, month); // for WHERE
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("BillID", rs.getInt("BillID"));
                row.put("Room", rs.getString("Room"));
                row.put("RoomRent", rs.getBigDecimal("RoomRent"));
                row.put("Electricity", rs.getBigDecimal("Electricity"));
                row.put("Water", rs.getBigDecimal("Water"));
                row.put("Trash", rs.getBigDecimal("Trash"));
                row.put("Wifi", rs.getBigDecimal("Wifi"));
                row.put("ExtraFee", rs.getBigDecimal("ExtraFee"));
                row.put("Deposit", rs.getBigDecimal("Deposit"));
                BigDecimal total = rs.getBigDecimal("TotalCalculated");
                BigDecimal deposit = rs.getBigDecimal("Deposit");
                BigDecimal due = total.subtract(deposit != null ? deposit : BigDecimal.ZERO);

                row.put("Total", total);
                row.put("DueAmount", due);

                row.put("BillStatus", rs.getString("BillStatus"));
                result.add(row);
            }
        }
        return result;
    }
public BigDecimal getBillTotalDue(int billId) {
    String sql = "SELECT TotalAmount - ISNULL(Deposit, 0) AS DueAmount FROM Bills WHERE BillID = ?";
    try (Connection conn = dbContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, billId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getBigDecimal("DueAmount");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return BigDecimal.ZERO;
}
public boolean updateBillStatus(int billId, String status) {
    String sql = "UPDATE Bills SET BillStatus = ? WHERE BillID = ?";
    try (Connection conn = dbContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, status);
        ps.setInt(2, billId);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}


}
