/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import DB.DBContext;
import Model.Contract;
import Model.Customer;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class ContractDAO extends DBContext {

    public ArrayList<Contract> getAllContracts() {
        ArrayList<Contract> contracts = new ArrayList<>();
        String query = "SELECT c.ContractID, c.TenantID, c.RoomID, c.StartDate, c.EndDate, c.ContractStatus, c.ContractCreatedAt, "
                + "cu.CustomerFullName as TenantName, r.RoomNumber "
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
                contracts.add(contract);
            }
            System.out.println("? Total contracts retrieved: " + contracts.size());
        } catch (SQLException ex) {
            System.out.println("? SQL Query Error: " + ex.getMessage());
        }
        return contracts;
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

    public boolean addContract(int tenantId, int roomId, Date startDate, Date endDate) {
        String query = "INSERT INTO Contracts (TenantID, RoomID, StartDate, EndDate, ContractStatus, ContractCreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            // Mặc định trạng thái là "Active", CreatedAt là thời điểm hiện tại
            String contractstatus = "Active";
            Timestamp contractcreatedAt = new Timestamp(System.currentTimeMillis());

            Object[] params = {tenantId, roomId, startDate, endDate, contractstatus, contractcreatedAt};

            int result = this.execQuery(query, params);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateContract(Contract contract) throws SQLException {
        String query = "UPDATE Contracts SET TenantID = ?, RoomID = ?, StartDate = ?, EndDate = ?, ContractStatus = ? WHERE ContractID = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, contract.getTenantId());
            ps.setInt(2, contract.getRoomId());
            ps.setDate(3, new java.sql.Date(contract.getStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(contract.getEndDate().getTime()));
            ps.setString(5, contract.getContractstatus());
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
                contract.setContractstatus(rs.getString("ContractStatus"));
                contract.setContractcreatedAt(rs.getTimestamp("ContractCreatedAt"));
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
                Date startDate = rs.getDate("StartDate");
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

}
