package dao;

import java.math.BigDecimal;
import model.Contract;
import utils.DBContext;
import java.sql.*;
import java.util.*;
import model.Notification;
import model.Tenant;

public class ContractDAO extends DBContext {

    public ArrayList<Contract> getAllContracts() {
        ArrayList<Contract> contracts = new ArrayList<>();
        String query = "SELECT c.ContractID, c.TenantID, c.RoomID, c.StartDate, c.EndDate, c.ContractStatus, c.ContractCreatedAt, "
                + "cu.CustomerFullName AS TenantName, "
                + "r.RoomNumber, r.RentPrice, r.Area, r.Location, r.BlockID, r.Description, "
                + "r.IsElectricityFree, r.IsWaterFree, r.IsWifiFree, r.IsTrashFree "
                + "FROM Contracts c "
                + "JOIN Tenants t ON c.TenantID = t.TenantID "
                + "JOIN Customers cu ON t.CustomerID = cu.CustomerID "
                + "JOIN Rooms r ON c.RoomID = r.RoomID";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Contract contract = new Contract(
                        rs.getInt("ContractID"),
                        rs.getInt("TenantID"),
                        rs.getInt("RoomID"),
                        rs.getDate("StartDate"),
                        rs.getDate("EndDate"),
                        rs.getString("ContractStatus"),
                        rs.getDate("ContractCreatedAt")
                );
                contract.setTenantName(rs.getString("TenantName"));
                contract.setRoomNumber(rs.getString("RoomNumber"));
                contract.setRoomRent(rs.getBigDecimal("RentPrice")); // Changed to BigDecimal
                contract.setArea(rs.getFloat("Area"));
                contract.setLocation(rs.getString("Location"));
                contract.setBlockId(rs.getInt("BlockID"));
                contract.setDescription(rs.getString("Description"));
                contract.setElectricityFree(rs.getBoolean("IsElectricityFree"));
                contract.setWaterFree(rs.getBoolean("IsWaterFree"));
                contract.setWifiFree(rs.getBoolean("IsWifiFree"));
                contract.setTrashFree(rs.getBoolean("IsTrashFree"));
                contracts.add(contract);
            }
            System.out.println("? Total contracts retrieved: " + contracts.size());
        } catch (SQLException ex) {
            System.out.println("? SQL Query Error: " + ex.getMessage());
        }
        return contracts;
    }

    public int addContract(int tenantId, int roomId, java.sql.Date startDate, java.sql.Date endDate) {
        String insertQuery = "INSERT INTO Contracts (TenantID, RoomID, StartDate, EndDate, ContractStatus, ContractCreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            String contractStatus = "Pending";
            Timestamp contractCreatedAt = new Timestamp(System.currentTimeMillis());

            ps.setInt(1, tenantId);
            ps.setInt(2, roomId);
            ps.setDate(3, startDate);
            ps.setDate(4, endDate);
            ps.setString(5, contractStatus);
            ps.setTimestamp(6, contractCreatedAt);

            int result = ps.executeUpdate();

            if (result > 0) {
                try ( ResultSet rsId = ps.getGeneratedKeys()) {
                    if (rsId.next()) {
                        return rsId.getInt(1); // Trả về ContractID
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void updateContract(Contract contract) throws SQLException {
        String query = "UPDATE Contracts SET TenantID = ?, RoomID = ?, StartDate = ?, EndDate = ?, ContractStatus = ? WHERE ContractID = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, contract.getTenantId());
            ps.setInt(2, contract.getRoomId());
            ps.setDate(3, new java.sql.Date(contract.getStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(contract.getEndDate().getTime()));
            ps.setString(5, contract.getContractStatus());
            ps.setInt(6, contract.getContractId()); // Added missing parameter
            ps.executeUpdate();
            System.out.println("? Contract updated for ContractID: " + contract.getContractId());
        } catch (SQLException ex) {
            System.out.println("? SQL Query Error: " + ex.getMessage());
            throw ex;
        }
    }

    public Contract getContractById(int contractId) {
        String sql = "SELECT * FROM Contracts WHERE ContractID = ?";
        DBContext db = new DBContext(); // Khởi tạo đối tượng DBContext
        try ( Connection conn = db.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Contract contract = new Contract();
                contract.setContractId(rs.getInt("ContractID"));
                contract.setTenantId(rs.getInt("TenantID"));
                contract.setRoomId(rs.getInt("RoomID"));
                contract.setStartDate(rs.getDate("StartDate"));
                contract.setEndDate(rs.getDate("EndDate"));
                contract.setContractStatus(rs.getString("ContractStatus"));
                contract.setContractCreatedAt(rs.getTimestamp("ContractCreatedAt"));
                return contract;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteContract(int contractId) {
        String selectSql = "SELECT StartDate, EndDate, ContractStatus FROM Contracts WHERE ContractID = ?";
        String deleteSql = "DELETE FROM Contracts WHERE ContractID = ?";

        try ( Connection conn = this.getConnection();  PreparedStatement selectPs = conn.prepareStatement(selectSql)) {

            selectPs.setInt(1, contractId);
            ResultSet rs = selectPs.executeQuery();

            if (rs.next()) {
                java.sql.Date startDate = rs.getDate("StartDate");
                String status = rs.getString("ContractStatus");

                java.util.Date today = new java.util.Date();

                // Không cho xóa nếu đã bắt đầu hoặc đã kết thúc
                if ("Ended".equalsIgnoreCase(status) || !startDate.after(today)) {
                    return false;
                }
            } else {
                // Không tìm thấy hợp đồng
                return false;
            }

            // Nếu điều kiện hợp lệ -> thực hiện xóa
            try ( PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {
                deletePs.setInt(1, contractId);
                int rowsAffected = deletePs.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<Contract> getContractHistoryByTenantId(int tenantId) {
        ArrayList<Contract> contracts = new ArrayList<>();
        String query = "SELECT c.ContractID, c.TenantID, c.RoomID, c.StartDate, c.EndDate, c.ContractStatus, c.ContractCreatedAt, "
                + "cu.CustomerFullName as TenantName, r.RoomNumber "
                + "FROM Contracts c "
                + "JOIN Tenants t ON c.TenantID = t.TenantID "
                + "JOIN Customers cu ON t.CustomerID = cu.CustomerID "
                + "JOIN Rooms r ON c.RoomID = r.RoomID "
                + "WHERE c.TenantID = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, tenantId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Contract contract = new Contract(
                        rs.getInt("ContractID"),
                        rs.getInt("TenantID"),
                        rs.getInt("RoomID"),
                        rs.getDate("StartDate"),
                        rs.getDate("EndDate"),
                        rs.getString("ContractStatus"),
                        rs.getDate("ContractCreatedAt")
                );
                contract.setTenantName(rs.getString("TenantName"));
                contract.setRoomNumber(rs.getString("RoomNumber"));
                contracts.add(contract);
            }
            System.out.println("✔ Retrieved contract history for tenantId = " + tenantId);
        } catch (SQLException ex) {
            System.out.println("✘ SQL error: " + ex.getMessage());
        }
        return contracts;
    }

    public List<Contract> getActiveContractsInMonth(String month) throws SQLException {
        List<Contract> list = new ArrayList<>();
        String sql
                = "SELECT c.ContractID, c.TenantID, c.RoomID, c.StartDate, c.EndDate, c.ContractStatus, "
                + "       r.RoomNumber, r.RentPrice "
                + "FROM Contracts c "
                + "JOIN Rooms r ON c.RoomID = r.RoomID "
                + "WHERE c.ContractStatus = 'Active' "
                + "  AND FORMAT(c.StartDate, 'yyyy-MM') <= ? "
                + "  AND (c.EndDate IS NULL OR FORMAT(c.EndDate, 'yyyy-MM') >= ?)";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, month);
            ps.setString(2, month);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contract c = new Contract();
                    c.setContractId(rs.getInt("ContractID"));
                    c.setTenantId(rs.getInt("TenantID"));
                    c.setRoomId(rs.getInt("RoomID"));
                    c.setStartDate(rs.getDate("StartDate"));
                    c.setEndDate(rs.getDate("EndDate"));
                    c.setContractStatus(rs.getString("ContractStatus"));
                    c.setRoomNumber(rs.getString("RoomNumber"));    // <-- tên phòng
                    c.setRoomRent(rs.getBigDecimal("RentPrice"));       // <-- giá thuê
                    list.add(c);
                }
            }
        }
        return list;
    }

    public float getRoomRentByContractId(int contractId) throws SQLException {
        String sql = "SELECT r.RentPrice "
                + "FROM Contracts c "
                + "JOIN Rooms r ON c.RoomID = r.RoomID "
                + "WHERE c.ContractID = ?";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getFloat("RentPrice");
                }
            }
        }

        return 0;
    }

    public Contract getActiveContractOfRoomInMonth(int roomId, String month) throws SQLException {
        String sql
                = "SELECT TOP 1 c.ContractID, c.TenantID, c.RoomID, c.StartDate, c.EndDate, c.ContractStatus, "
                + "       r.RoomNumber, r.RentPrice "
                + "FROM Contracts c "
                + "JOIN Rooms r ON c.RoomID = r.RoomID "
                + "WHERE c.ContractStatus = 'Active' "
                + "  AND c.RoomID = ? "
                + "  AND FORMAT(c.StartDate, 'yyyy-MM') <= ? "
                + "  AND (c.EndDate IS NULL OR FORMAT(c.EndDate, 'yyyy-MM') >= ?)"
                + "ORDER BY c.StartDate DESC"; // Nếu có nhiều hợp đồng trùng, lấy hợp đồng bắt đầu mới nhất
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setString(2, month);
            ps.setString(3, month);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contract c = new Contract();
                    c.setContractId(rs.getInt("ContractID"));
                    c.setTenantId(rs.getInt("TenantID"));
                    c.setRoomId(rs.getInt("RoomID"));
                    c.setStartDate(rs.getDate("StartDate"));
                    c.setEndDate(rs.getDate("EndDate"));
                    c.setContractStatus(rs.getString("ContractStatus"));
                    c.setRoomNumber(rs.getString("RoomNumber"));
                    c.setRoomRent(rs.getBigDecimal("RentPrice"));
                    return c;
                }
            }
        }
        return null;
    }

    public Contract getContractWithRoomAndTenantByContractId(int contractId) {
        String sql
                = "SELECT c.*, r.RoomNumber, r.RoomID, "
                + "       cu.CustomerFullName AS CustomerFullName, cu.PhoneNumber "
                + "FROM Contracts c "
                + "JOIN Rooms r ON c.RoomID = r.RoomID "
                + "JOIN Tenants t ON c.TenantID = t.TenantID "
                + "JOIN Customers cu ON t.CustomerID = cu.CustomerID "
                + "WHERE c.ContractID = ?";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, contractId);

            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contract c = new Contract();
                    c.setContractId(rs.getInt("ContractID"));
                    c.setRoomId(rs.getInt("RoomID"));
                    c.setTenantId(rs.getInt("TenantID"));
                    c.setStartDate(rs.getDate("StartDate"));
                    c.setEndDate(rs.getDate("EndDate"));
                    c.setDeposit(rs.getBigDecimal("Deposit"));
                    c.setRoomNumber(rs.getString("RoomNumber"));
                    c.setTenantName(rs.getString("CustomerFullName"));
                    c.setPhone(rs.getString("PhoneNumber")); // 👈 Thêm dòng này
                    return c;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean applyContract(int contractId) {
        String sql = "UPDATE Contracts "
                + "SET ContractStatus = ?, CustomerRespondedAt = ? "
                + "WHERE ContractID = ? AND ContractStatus = 'Pending'";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "Active");
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setInt(3, contractId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Contract> getContractsByCustomerId(int customerId) throws SQLException {
        List<Contract> list = new ArrayList<>();
        String sql
                = "SELECT c.ContractID, c.TenantID, r.RoomID, r.RoomNumber, c.StartDate, c.EndDate, "
                + "      c.ContractStatus, c.ContractCreatedAt "
                + "FROM Contracts c "
                + "JOIN Tenants t ON c.TenantID = t.TenantID "
                + "JOIN Rooms r ON c.RoomID = r.RoomID "
                + "WHERE t.CustomerID = ? ";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            System.out.println("Executing query for customerId: " + customerId); // Debug log
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Contract c = new Contract();
                c.setContractId(rs.getInt("ContractID"));
                c.setTenantId(rs.getInt("TenantID"));
                c.setRoomId(rs.getInt("RoomID"));
                c.setRoomNumber(rs.getString("RoomNumber")); // Ensure this field is used
                c.setStartDate(rs.getDate("StartDate"));
                c.setEndDate(rs.getDate("EndDate"));
                c.setContractStatus(rs.getString("ContractStatus"));
                c.setContractCreatedAt(rs.getTimestamp("ContractCreatedAt"));
                list.add(c);
            }
            System.out.println("Found " + list.size() + " contracts for customerId: " + customerId); // Debug log
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage()); // Log the exception
            throw e; // Re-throw to propagate the error
        }
        return list;
    }

    public boolean rejectContract(int contractId) {
        String sql = "UPDATE Contracts "
                + "SET ContractStatus = ?, CustomerRespondedAt = ? "
                + "WHERE ContractID = ? AND ContractStatus = 'Pending'";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "Terminated");
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setInt(3, contractId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateContractStatus(int contractId, String status) throws SQLException {
        String sql = "UPDATE Contracts SET ContractStatus = ? WHERE ContractID = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, contractId);

            return ps.executeUpdate() > 0;
        }
    }

    public Contract getActiveContractByRoom(int roomId) throws SQLException {
        String sql = "SELECT * FROM Contracts WHERE RoomID = ? AND ContractStatus = 'Active'";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Contract contract = new Contract();
                contract.setContractId(rs.getInt("ContractID"));
                contract.setStartDate(rs.getDate("StartDate"));
                contract.setEndDate(rs.getDate("EndDate"));
                contract.setRoomId(rs.getInt("RoomID"));
                contract.setContractStatus(rs.getString("ContractStatus"));
                // bạn thêm các trường khác nếu cần
                return contract;
            }
        }
        return null;
    }

}
