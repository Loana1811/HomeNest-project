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
        String sql
                = "SELECT b.*, "
                + "       ISNULL(bd.RoomRent, 0) AS RoomRent, "
                + "       ISNULL(f.TotalFee, 0) AS IncurredFeeTotal, "
                + "       ISNULL(u.UtilityTotal, 0) AS UtilityReadingTotal, "
                + "       ISNULL(trash.UnitPrice, 0) AS TrashFee, "
                + "       ISNULL(wifi.UnitPrice, 0) AS WifiFee, "
                + "       ( "
                + "           ISNULL(bd.RoomRent, 0) + "
                + "           ISNULL(u.UtilityTotal, 0) + "
                + "           ISNULL(f.TotalFee, 0) + "
                + "           ISNULL(trash.UnitPrice, 0) + "
                + "           ISNULL(wifi.UnitPrice, 0) "
                + "       ) AS CalculatedTotal "
                + "FROM Bills b "
                + "LEFT JOIN BillDetails bd ON bd.BillID = b.BillID "
                + "LEFT JOIN Contracts c ON b.ContractID = c.ContractID "
                + "LEFT JOIN Rooms r ON c.RoomID = r.RoomID "
                + "OUTER APPLY ( "
                + "    SELECT SUM(ur.PriceUsed) AS UtilityTotal "
                + "    FROM UtilityReadings ur "
                + "    WHERE ur.RoomID = r.RoomID "
                + "      AND MONTH(ur.ReadingDate) = MONTH(b.BillDate) "
                + "      AND YEAR(ur.ReadingDate) = YEAR(b.BillDate) "
                + "      AND ur.UtilityTypeID IN (1, 2) "
                + ") u "
                + "OUTER APPLY ( "
                + "    SELECT SUM(f.Amount) AS TotalFee "
                + "    FROM IncurredFees f "
                + "    WHERE f.BillID = b.BillID "
                + ") f "
                + "LEFT JOIN UtilityTypes trash ON trash.UtilityTypeID = 3 "
                + "LEFT JOIN UtilityTypes wifi ON wifi.UtilityTypeID = 4 "
                + "WHERE b.BillID = ?";

        try ( Connection c = dbContext.getConnection();  PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Bill b = map(rs); // map thông thường các trường của Bills table
                    b.setTotalAmount(rs.getBigDecimal("CalculatedTotal")); // gán lại total chính xác
                    return b;
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
        b.setTotalAmount(rs.getBigDecimal("TotalAmount"));
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
            ps.setBigDecimal(3, bill.getTotalAmount());
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

    public List<Map<String, Object>> getBillSummaryByMonth(String month) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();

        String sql = ""
                + "SELECT \n"
                + "    b.BillID,\n"
                + "    bl.BlockID,\n"
                + "    r.RoomNumber AS Room,\n"
                + "    r.RentPrice AS RoomRent,\n"
                + "    ISNULL(electricity.Amount, 0) AS Electricity,\n"
                + "    ISNULL(water.Amount, 0) AS Water,\n"
                + "    ISNULL(trash.UnitPrice, 0) AS Trash,\n"
                + "    ISNULL(wifi.UnitPrice, 0) AS Wifi,\n"
                + "    ISNULL(fee.ExtraFee, 0) AS ExtraFee,\n"
                + "    ISNULL(fee.Deposit, 0) AS Deposit,\n"
                + "    ISNULL(paid.TotalPaid, 0) AS TotalPaid,\n"
                + "    (\n"
                + "        ISNULL(r.RentPrice, 0)\n"
                + "        + ISNULL(electricity.Amount, 0)\n"
                + "        + ISNULL(water.Amount, 0)\n"
                + "        + ISNULL(trash.UnitPrice, 0)\n"
                + "        + ISNULL(wifi.UnitPrice, 0)\n"
                + "        + ISNULL(fee.ExtraFee, 0)\n"
                + "    ) AS TotalCalculated,\n"
                + "    b.BillStatus,\n"
                + "    b.BillDate\n"
                + "FROM Bills b\n"
                + "JOIN Contracts c ON b.ContractID = c.ContractID\n"
                + "JOIN Rooms r ON c.RoomID = r.RoomID\n"
                + "JOIN Blocks bl ON r.BlockID = bl.BlockID\n"
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
                + "    SELECT TOP 1 ut.UnitPrice FROM UtilityTypes ut WHERE ut.UtilityTypeID = 3\n"
                + ") trash\n"
                + "OUTER APPLY (\n"
                + "    SELECT TOP 1 ut.UnitPrice FROM UtilityTypes ut WHERE ut.UtilityTypeID = 4\n"
                + ") wifi\n"
                + "OUTER APPLY (\n"
                + "    SELECT \n"
                + "        SUM(CASE WHEN f.IncurredFeeTypeID = 99 THEN f.Amount ELSE 0 END) AS Deposit,\n"
                + "        SUM(CASE WHEN f.IncurredFeeTypeID != 99 THEN f.Amount ELSE 0 END) AS ExtraFee\n"
                + "    FROM IncurredFees f\n"
                + "    WHERE f.BillID = b.BillID\n"
                + ") fee\n"
                + "OUTER APPLY (\n"
                + "    SELECT SUM(AmountPaid) AS TotalPaid FROM Payments WHERE BillID = b.BillID\n"
                + ") paid\n"
                + "WHERE FORMAT(b.BillDate, 'yyyy-MM') = ?\n"
                + "ORDER BY b.BillID DESC";

        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, month); // electricity
            ps.setString(2, month); // water
            ps.setString(3, month); // billDate

            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    BigDecimal total = rs.getBigDecimal("TotalCalculated");
                    BigDecimal totalPaid = rs.getBigDecimal("TotalPaid");
                    BigDecimal due = total.subtract(totalPaid != null ? totalPaid : BigDecimal.ZERO);
                    if (due.compareTo(BigDecimal.ZERO) < 0) {
                        due = BigDecimal.ZERO;
                    }

                    row.put("BillID", rs.getInt("BillID"));
                    row.put("BlockID", rs.getInt("BlockID"));
                    row.put("Room", rs.getString("Room"));
                    row.put("RoomRent", rs.getBigDecimal("RoomRent"));
                    row.put("Electricity", rs.getBigDecimal("Electricity"));
                    row.put("Water", rs.getBigDecimal("Water"));
                    row.put("Trash", rs.getBigDecimal("Trash"));
                    row.put("Wifi", rs.getBigDecimal("Wifi"));
                    row.put("ExtraFee", rs.getBigDecimal("ExtraFee"));
                    row.put("Deposit", rs.getBigDecimal("Deposit"));
                    row.put("TotalPaid", totalPaid);
                    row.put("TotalAmount", total);
                    row.put("DueAmount", due);
                    row.put("BillStatus", rs.getString("BillStatus"));
                    row.put("BillDate", rs.getDate("BillDate"));

                    result.add(row);
                }
            }
        }

        return result;
    }

    public BigDecimal getBillTotalDue(int billId) {
        String sql = "SELECT TotalAmount - ISNULL(Deposit, 0) AS DueAmount FROM Bills WHERE BillID = ?";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
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
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, billId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteBillFully(int billId) throws SQLException {
        try ( Connection conn = dbContext.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Xóa phụ phí
                try ( PreparedStatement ps = conn.prepareStatement("DELETE FROM IncurredFees WHERE BillID = ?")) {
                    ps.setInt(1, billId);
                    ps.executeUpdate();
                }

                // Xóa chi tiết bill
                try ( PreparedStatement ps = conn.prepareStatement("DELETE FROM BillDetails WHERE BillID = ?")) {
                    ps.setInt(1, billId);
                    ps.executeUpdate();
                }

                // Xóa bill
                try ( PreparedStatement ps = conn.prepareStatement("DELETE FROM Bills WHERE BillID = ?")) {
                    ps.setInt(1, billId);
                    int rows = ps.executeUpdate();
                    conn.commit();
                    return rows > 0;
                }

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public boolean isBillSent(int billId) throws SQLException {
        String sql = "SELECT IsSent FROM Bills WHERE BillID = ?";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("IsSent");
                }
            }
        }
        return false;
    }

    public String getBlockIdByBillId(int billId) throws SQLException {
        String sql = "SELECT r.BlockID FROM Bills b JOIN Rooms r ON b.RoomID = r.RoomID WHERE b.BillID = ?";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("BlockID");
                }
            }
        }
        return null;
    }

    public boolean markBillAsSent(int billId) throws SQLException {
        String sql = "UPDATE Bills SET IsSent = 1 WHERE BillID = ?";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            return ps.executeUpdate() > 0;
        }
    }
}
