package dao;

import java.math.BigDecimal;
import model.Bill;
import utils.DBContext;
import java.sql.*;
import java.util.*;
import model.UtilityReading;

public class BillDAO {

    private final DBContext dbContext = new DBContext();
    private RevenueDAO revenueDAO = new RevenueDAO();

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
        String sql = "SELECT "
                + "b.BillID, b.ContractID, b.BillDate, b.BillStatus, b.IsSent, "
                + "r.RoomID, r.RoomNumber, r.RentPrice, "
                + "r.IsElectricityFree, r.IsWaterFree, r.IsWifiFree, r.IsTrashFree, "
                + "ur.ReadingID, ur.UtilityTypeID, ut.UtilityName, ur.RoomID AS URRoomID, "
                + "ur.ReadingDate, ur.OldReading, ur.NewReading, ur.PriceUsed, ur.OldPrice, "
                + "ur.UtilityReadingCreatedAt, ur.IsLocked, ur.IsChanged, ur.Reason, ur.ChangeAmount "
                + "FROM Bills b "
                + "JOIN Contracts c ON b.ContractID = c.ContractID "
                + "JOIN Rooms r ON c.RoomID = r.RoomID "
                + "LEFT JOIN UtilityReadings ur "
                + "  ON ur.RoomID = r.RoomID "
                + "  AND MONTH(ur.ReadingDate) = MONTH(b.BillDate) "
                + "  AND YEAR(ur.ReadingDate) = YEAR(b.BillDate) "
                + "LEFT JOIN UtilityTypes ut ON ut.UtilityTypeID = ur.UtilityTypeID "
                + "WHERE b.BillID = ?";

        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try ( ResultSet rs = ps.executeQuery()) {
                Bill bill = null;

                // các biến để cộng tiền
                BigDecimal rentPrice = BigDecimal.ZERO;
                BigDecimal totalUtility = BigDecimal.ZERO;
                boolean isElectricityFree = false;
                boolean isWaterFree = false;
                boolean isWifiFree = false;
                boolean isTrashFree = false;

                while (rs.next()) {
                    if (bill == null) {
                        bill = new Bill();
                        bill.setBillID(rs.getInt("BillID"));
                        bill.setContractID(rs.getInt("ContractID"));
                        bill.setBillDate(rs.getDate("BillDate"));
                        bill.setBillStatus(rs.getString("BillStatus"));
                        bill.setRoomNumber(rs.getString("RoomNumber"));
                        rentPrice = rs.getBigDecimal("RentPrice");         // 👈 Lấy từ bảng Rooms
                        bill.setRoomRent(rentPrice);

                        // lấy trạng thái miễn phí tiện ích
                        isElectricityFree = !rs.getBoolean("IsElectricityFree");
                        isWaterFree = !rs.getBoolean("IsWaterFree");
                        isWifiFree = !rs.getBoolean("IsWifiFree");
                        isTrashFree = !rs.getBoolean("IsTrashFree");
                    }

                    int readingId = rs.getInt("ReadingID");
                    if (!rs.wasNull()) {
                        UtilityReading ur = new UtilityReading();
                        ur.setReadingID(readingId);
                        ur.setUtilityTypeID(rs.getInt("UtilityTypeID"));
                        ur.setUtilityName(rs.getString("UtilityName"));
                        ur.setRoomID(rs.getInt("URRoomID"));
                        ur.setReadingDate(rs.getDate("ReadingDate"));
                        ur.setOldReading(rs.getBigDecimal("OldReading"));
                        ur.setNewReading(rs.getBigDecimal("NewReading"));
                        ur.setPriceUsed(rs.getBigDecimal("PriceUsed"));
                        ur.setOldPrice(rs.getBigDecimal("OldPrice"));
                        ur.setUtilityReadingCreatedAt(rs.getTimestamp("UtilityReadingCreatedAt"));
                        ur.setIsLocked(rs.getBoolean("IsLocked"));
                        ur.setIsChanged(rs.getBoolean("IsChanged"));
                        ur.setReason(rs.getString("Reason"));
                        ur.setChangedAmount(rs.getBigDecimal("ChangeAmount"));

                        bill.getUtilityReadings().add(ur);

                        // cộng tiền tiện ích nếu không được miễn phí
                        BigDecimal fee = ur.getPriceUsed() != null ? ur.getPriceUsed() : BigDecimal.ZERO;
                        int typeId = ur.getUtilityTypeID();

                        if ((typeId == 1 && !isElectricityFree)
                                || (typeId == 2 && !isWaterFree)
                                || (typeId == 3 && !isTrashFree)
                                || (typeId == 4 && !isWifiFree)) {
                            totalUtility = totalUtility.add(fee);
                        }
                    }
                }

                if (bill != null) {
                    bill.setTotalAmount(rentPrice.add(totalUtility));
                }
                System.out.println(bill);

                return bill;
            }
        }
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
                        generatedId = rs.getInt(1);
                        if ("PAID".equalsIgnoreCase(bill.getBillStatus())
                                || "Đã thanh toán".equalsIgnoreCase(bill.getBillStatus())) {
                            int revenueCategoryId = 1; // Mặc định là 1
                            boolean revenueCreated = revenueDAO.createRevenueForPaidBill(generatedId, revenueCategoryId);
                            if (!revenueCreated) {
                                System.err.println("Failed to create revenue for billId: " + generatedId);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException in createBill: " + e.getMessage());
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
                + "    r.IsElectricityFree,\n"
                + "    r.IsWaterFree,\n"
                + "    r.IsTrashFree,\n"
                + "    r.IsWifiFree,\n"
                + "    ISNULL(electricity.Amount, 0) AS Electricity,\n"
                + "    ISNULL(water.Amount, 0) AS Water,\n"
                + "    CASE WHEN r.IsTrashFree = 0 THEN 0\n"
                + "         ELSE ISNULL((SELECT TOP 1 ut.UnitPrice FROM UtilityTypes ut WHERE ut.UtilityTypeID = 4), 0)\n"
                + "    END AS Trash,\n"
                + "    CASE WHEN r.IsWifiFree = 0 THEN 0\n"
                + "         ELSE ISNULL((SELECT TOP 1 ut.UnitPrice FROM UtilityTypes ut WHERE ut.UtilityTypeID = 3), 0)\n"
                + "    END AS Wifi,\n"
                + "    ISNULL(fee.ExtraFee, 0) AS ExtraFee,\n"
                + "    ISNULL(fee.Deposit, 0) AS Deposit,\n"
                + "    ISNULL(paid.TotalPaid, 0) AS TotalPaid,\n"
                + "    b.TotalAmount,\n"
                + "    b.BillStatus,\n"
                + "    b.BillDate\n"
                + "FROM Bills b\n"
                + "JOIN Contracts c ON b.ContractID = c.ContractID\n"
                + "JOIN Rooms r ON c.RoomID = r.RoomID\n"
                + "JOIN Blocks bl ON r.BlockID = bl.BlockID\n"
                + "OUTER APPLY (\n"
                + "    SELECT SUM(ur.PriceUsed) AS Amount\n"
                + "    FROM UtilityReadings ur\n"
                + "    WHERE ur.UtilityTypeID = 1 AND ur.RoomID = r.RoomID AND FORMAT(ur.ReadingDate, 'yyyy-MM') = ?\n"
                + ") electricity\n"
                + "OUTER APPLY (\n"
                + "    SELECT SUM(ur.PriceUsed) AS Amount\n"
                + "    FROM UtilityReadings ur\n"
                + "    WHERE ur.UtilityTypeID = 2 AND ur.RoomID = r.RoomID AND FORMAT(ur.ReadingDate, 'yyyy-MM') = ?\n"
                + ") water\n"
                + "OUTER APPLY (\n"
                + "    SELECT TOP 1 ut.UnitPrice FROM UtilityTypes ut WHERE ut.UtilityTypeID = 4\n"
                + ") trash\n"
                + "OUTER APPLY (\n"
                + "    SELECT TOP 1 ut.UnitPrice FROM UtilityTypes ut WHERE ut.UtilityTypeID = 3\n"
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
            ps.setString(3, month); // bill date

            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    BigDecimal total = rs.getBigDecimal("TotalAmount");
                    BigDecimal roomRent = rs.getBigDecimal("RoomRent");
                    BigDecimal totalPaid = rs.getBigDecimal("TotalPaid");

                    // ✅ Tính tổng = TotalAmount + RoomRent
                    BigDecimal finalTotal = total.add(roomRent != null ? roomRent : BigDecimal.ZERO);
                    BigDecimal due = finalTotal.subtract(totalPaid != null ? totalPaid : BigDecimal.ZERO);
                    if (due.compareTo(BigDecimal.ZERO) < 0) {
                        due = BigDecimal.ZERO;
                    }

                    row.put("BillID", rs.getInt("BillID"));
                    row.put("BlockID", rs.getInt("BlockID"));
                    row.put("Room", rs.getString("Room"));
                    row.put("RoomRent", roomRent);

                    Boolean isElectricityFree = (Boolean) rs.getObject("IsElectricityFree");
                    Boolean isWaterFree = (Boolean) rs.getObject("IsWaterFree");
                    Boolean isTrashFree = (Boolean) rs.getObject("IsTrashFree");
                    Boolean isWifiFree = (Boolean) rs.getObject("IsWifiFree");

                    row.put("IsElectricityFree", isElectricityFree);
                    row.put("IsWaterFree", isWaterFree);
                    row.put("IsTrashFree", isTrashFree);
                    row.put("IsWifiFree", isWifiFree);

                    row.put("Electricity", rs.getBigDecimal("Electricity"));
                    row.put("Water", rs.getBigDecimal("Water"));
                    row.put("Trash", rs.getBigDecimal("Trash"));
                    row.put("Wifi", rs.getBigDecimal("Wifi"));

                    row.put("ExtraFee", rs.getBigDecimal("ExtraFee"));
                    row.put("Deposit", rs.getBigDecimal("Deposit"));
                    row.put("TotalPaid", totalPaid);
                    row.put("TotalAmount", finalTotal); // ✅ Gán giá trị đúng
                    row.put("DueAmount", due);
                    row.put("BillStatus", rs.getString("BillStatus"));
                    row.put("BillDate", rs.getDate("BillDate"));

                    result.add(row);
                    System.out.println(result);
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
        try ( Connection conn = dbContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, billId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0 && ("PAID".equalsIgnoreCase(status) || "Đã thanh toán".equalsIgnoreCase(status))) {
                int revenueCategoryId = 1; // Mặc định là 1
                boolean revenueCreated = revenueDAO.createRevenueForPaidBill(billId, revenueCategoryId);
                if (!revenueCreated) {
                    System.err.println("Failed to create revenue for billId: " + billId);
                }
                return revenueCreated;
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("SQLException in updateBillStatus: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
        String sql = "SELECT r.BlockID "
                + "FROM Bills b "
                + "JOIN Contracts c ON b.ContractID = c.ContractID "
                + "JOIN Rooms r ON c.RoomID = r.RoomID "
                + "WHERE b.BillID = ?";
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

    private void createRevenueForBill(Connection conn, int billID, Bill bill) throws SQLException {
        RevenueCategoryDAO categoryDAO = new RevenueCategoryDAO();
        int revenueCategoryID = categoryDAO.getOrCreateRevenueCategoryId(conn, "Thu hóa đơn");

        String sql = "INSERT INTO Revenues (RevenueCategoryID, CustomerID, ContractID, RevenueNote, RevenueAmount, RevenueDate) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, revenueCategoryID);
            ps.setInt(2, getCustomerIdByContract(conn, bill.getContractID()));
            ps.setInt(3, bill.getContractID());
            ps.setString(4, "Thu tiền hóa đơn ID: " + billID);
            ps.setBigDecimal(5, bill.getTotalAmount());
            ps.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            ps.executeUpdate();
        }
    }

    private int getCustomerIdByContract(Connection conn, int contractID) throws SQLException {
        String sql = "SELECT CustomerID FROM Contracts WHERE ContractID = ?";

        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractID);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("CustomerID");
                }
            }
        }
        throw new SQLException("Không tìm thấy khách hàng với hợp đồng ID: " + contractID);
    }

    private void addDebtForCustomer(Connection conn, int contractID, BigDecimal amount) throws SQLException {
        String sql = "UPDATE Customers SET Debt = ISNULL(Debt, 0) + ? WHERE CustomerID = (SELECT CustomerID FROM Contracts WHERE ContractID = ?)";

        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, amount);
            ps.setInt(2, contractID);
            ps.executeUpdate();
        }
    }

    public boolean hasBillForMonth(int roomId, String month) {
        String sql = "SELECT COUNT(*) FROM Bill WHERE RoomID = ? AND FORMAT(BillDate, 'yyyy-MM') = ?";
        try (
                 Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setString(2, month);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

   public boolean markBillAsPaid(int billID) throws SQLException {
    String sql = "UPDATE Bills " +
                 "SET BillStatus = 'PAID' " +
                 "WHERE BillID = ?";

    try (Connection conn = dbContext.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, billID);
        return ps.executeUpdate() > 0;
    }
}

    public List<Bill> getBillsByTenantId(Integer tenantId, String month) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


}
