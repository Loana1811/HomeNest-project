/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import DB.DBContext;
import Model.Contract;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class ContractDAO extends DBContext {

    public ArrayList<Contract> getAllContracts() {
        // Initialize an empty ArrayList to store the contract records
        ArrayList<Contract> contracts = new ArrayList<>();
        // SQL query to select all contracts with tenant names from the Contracts and Tenants tables
        String query = "SELECT * from Contracts";
        //

        // Use try-with-resources to automatically close database resources
        try ( Connection conn = getConnection(); // Establish database connection
                  PreparedStatement ps = conn.prepareStatement(query); // Prepare the SQL query
                  ResultSet rs = ps.executeQuery()) { // Execute the query and get the result set

            // Check if the connection is null (though this check is redundant due to try-with-resources)
            if (conn == null) {
                System.out.println("? Unable to connect to the database");
                return null;
            }

            // Loop through each row in the result set
            while (rs.next()) {
                // Create a new Contract object with the retrieved data
                Contract contract = new Contract(
                        rs.getInt("ContractID"), // Contract ID 
                        rs.getInt("TenantID"),
                        rs.getInt("RoomID"), // Room ID
                        rs.getDate("StartDate"), // Start date
                        rs.getDate("EndDate"), // End date
                        rs.getString("Status"), // Status
                        rs.getDate("CreatedAt") // Created at
                );
                // Add the contract object to the list
                contracts.add(contract);              
            }

            // Debug statement to print the total number of contracts retrieved
            System.out.println("? Total contracts retrieved: " + contracts.size());

        } catch (SQLException ex) {
            // Print an error message if a SQL query error occurs
            System.out.println("? SQL Query Error: " + ex.getMessage());
        }
        // Return the list of contracts
        return contracts;
    }

 public void addContract(Contract contract) throws SQLException {
        try (Connection conn = getConnection()) {
            if (conn == null) {
                throw new SQLException("K?t n?i c? s? d? li?u không kh? d?ng");
            }

            // Begin transaction
            conn.setAutoCommit(false);

            // Validate inputs
            validateTenantId(conn, contract.getTenantId());
            validateRoomIdAndStatus(conn, contract.getRoomId());

            // Insert contract
            insertContract(conn, contract);


            // Commit transaction
            conn.commit();
            System.out.println("? H?p ??ng ???c thêm cho TenantID: " + contract.getTenantId());
        } catch (SQLException ex) {
            System.out.println("? L?i truy v?n SQL: " + ex.getMessage());
            throw ex;
        }
    }

    private void validateTenantId(Connection conn, int tenantId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM Tenants WHERE TenantID = ?");
             ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, tenantId);
            if (!rs.next()) {
                throw new SQLException("ID Ng??i thuê " + tenantId + " không t?n t?i trong b?ng Tenants");
            }
        }
    }

    private void validateRoomIdAndStatus(Connection conn, int roomId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT Status FROM Rooms WHERE RoomID = ?");
             ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, roomId);
            if (!rs.next()) {
                throw new SQLException("ID Phòng " + roomId + " không t?n t?i trong b?ng Rooms");
            }
            if (!"Available".equals(rs.getString("Status"))) {
                throw new SQLException("Phòng " + roomId + " không kh? d?ng");
            }
        }
    }

    private void insertContract(Connection conn, Contract contract) throws SQLException {
        String insertQuery = "INSERT INTO Contracts (TenantID, RoomID, StartDate, EndDate, Status, CreatedAt) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            ps.setInt(1, contract.getTenantId());
            ps.setInt(2, contract.getRoomId());
            ps.setDate(3, (Date) contract.getStartDate());
            ps.setDate(4, (Date) contract.getEndDate());
            ps.setString(5, contract.getStatus());
            ps.setDate(6, (Date) contract.getCreatedAt());
            ps.executeUpdate();
        }
    }

    public void updateContract(Contract contract) throws SQLException {
        String query = "UPDATE Contracts SET TenantID = ?, RoomID = ?, StartDate = ?, EndDate = ?, Status = ? WHERE ContractID = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, contract.getTenantId());
            ps.setInt(2, contract.getRoomId());
            ps.setDate(3, new java.sql.Date(contract.getStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(contract.getEndDate().getTime()));
            ps.setString(5, contract.getStatus());
            ps.setInt(6, contract.getContractId()); // Added missing parameter
            ps.executeUpdate();
            System.out.println("? Contract updated for ContractID: " + contract.getContractId());
        } catch (SQLException ex) {
            System.out.println("? SQL Query Error: " + ex.getMessage());
            throw ex;
        }
    }

    public Contract getContractById(int id) throws SQLException {
        String query = "SELECT c.[ContractID], c.[TenantID], c.[RoomID], c.[StartDate], c.[EndDate], c.[Status], c.[CreatedAt] "
                + "FROM [RentalManagement].[dbo].[Contracts] c "
                + "JOIN [RentalManagement].[dbo].[Tenants] t ON c.[TenantID] = t.[TenantID] "
                + "WHERE c.[ContractID] = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contract contract = new Contract(
                            rs.getInt("ContractID"),
                            rs.getInt("TenantID"),
                            rs.getInt("RoomID"),
                            rs.getDate("StartDate"),
                            rs.getDate("EndDate"),
                            rs.getString("Status"),
                            rs.getDate("CreatedAt")
                    );
                    System.out.println("? Retrieved contract: " + contract.getContractId());
                    return contract;
                }
            }
        } catch (SQLException ex) {
            System.out.println("? SQL Query Error: " + ex.getMessage());
            throw ex;
        }
        return null;
    }
}
