package dao;

import model.Report;
import utils.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDAO extends DBContext {

    public ReportDAO() {
    }

    /**
     * Maps a ResultSet row to a Report object.
     *
     * @param rs The ResultSet containing report data.
     * @return A Report object populated with data from the ResultSet.
     * @throws SQLException If a database access error occurs.
     */
    public Report mapResultSetToReport(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setReportID(rs.getInt("ReportID"));
        report.setCustomerID(rs.getInt("CustomerID"));
        report.setRoomID(rs.getInt("RoomID"));
        report.setContractID(rs.getInt("ContractID"));
        report.setIssueDescription(rs.getString("IssueDescription"));
        report.setReportStatus(rs.getString("ReportStatus"));
        report.setReportCreatedAt(rs.getTimestamp("ReportCreatedAt"));
        report.setResolvedBy(rs.getObject("ResolvedBy", Integer.class));
        report.setResolvedDate(rs.getTimestamp("ResolvedDate"));
        report.setRoomNumber(rs.getString("RoomNumber"));
        report.setCustomerName(rs.getString("CustomerFullName")); // Added to map customer name
        report.setStartDate(rs.getDate("StartDate")); // Added to map contract start date
        report.setEndDate(rs.getDate("EndDate")); // Added to map contract end date
        return report;
    }

    /**
     * Retrieves all reports for a specific customer, including room number, ordered by creation date (descending).
     *
     * @param customerID The ID of the customer whose reports are to be retrieved.
     * @return A list of Report objects with RoomNumber for the specified customer.
     * @throws SQLException If a database access error occurs.
     */
    public List<Report> getReportsByCustomer(int customerID) throws SQLException {
        List<Report> reports = new ArrayList<>();
        String query = "SELECT r.*, rm.RoomNumber, cu.CustomerFullName, c.StartDate, c.EndDate " +
                      "FROM Reports r " +
                      "JOIN Contracts c ON r.ContractID = c.ContractID " +
                      "JOIN Rooms rm ON c.RoomID = rm.RoomID " +
                      "JOIN Customers cu ON r.CustomerID = cu.CustomerID " +
                      "WHERE r.CustomerID = ? ORDER BY r.ReportCreatedAt DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, customerID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] Error in getReportsByCustomer for customerID " + customerID + ": " + e.getMessage());
            throw e;
        }

        return reports;
    }

    /**
     * Creates a new report in the database.
     *
     * @param report The Report object to be created.
     * @return true if the report was created successfully, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean addReport(Report report) throws SQLException {
        String query = "INSERT INTO Reports (CustomerID, RoomID, ContractID, IssueDescription, ReportStatus, ReportCreatedAt) " +
                      "VALUES (?, ?, ?, ?, ?, GETDATE())";

        int result = execUpdateQuery(query,
                report.getCustomerID(),
                report.getRoomID(),
                report.getContractID(),
                report.getIssueDescription(),
                "Pending");

        if (result <= 0) {
            System.err.println("[ReportDAO] Failed to create report for customerID " + report.getCustomerID());
        }

        return result > 0;
    }

    /**
     * Validates if the specified room and contract belong to the customer and are active.
     *
     * @param customerID The ID of the customer.
     * @param roomID The ID of the room.
     * @param contractID The ID of the contract.
     * @return true if the room and contract are valid for the customer, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean validateRoomAndContract(int customerID, int roomID, int contractID) throws SQLException {
        String query = "SELECT COUNT(*) FROM Contracts c " +
                      "JOIN Tenants t ON c.TenantID = t.TenantID " +
                      "WHERE t.CustomerID = ? AND c.RoomID = ? AND c.ContractID = ? AND c.ContractStatus = 'Active'";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, customerID);
            ps.setInt(2, roomID);
            ps.setInt(3, contractID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] Error in validateRoomAndContract for customerID " + customerID + ": " + e.getMessage());
            throw e;
        }

        return false;
    }

    /**
     * Retrieves active, non-expired rooms and contracts for a specific customer.
     *
     * @param customerID The ID of the customer.
     * @return A list of maps containing ContractID, RoomID, RoomNumber, StartDate, and EndDate for active, non-expired contracts.
     * @throws SQLException If a database access error occurs.
     */
    public List<Map<String, Object>> getActiveNonExpiredRoomsAndContracts(int customerID) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        String query = "SELECT c.ContractID, r.RoomID, r.RoomNumber, c.StartDate, c.EndDate " +
                      "FROM Contracts c " +
                      "JOIN Tenants t ON c.TenantID = t.TenantID " +
                      "JOIN Rooms r ON c.RoomID = r.RoomID " +
                      "WHERE t.CustomerID = ? AND c.ContractStatus = 'Active' " +
                      "AND (c.EndDate IS NULL OR c.EndDate >= GETDATE())";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, customerID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("ContractID", rs.getInt("ContractID"));
                    row.put("RoomID", rs.getInt("RoomID"));
                    row.put("RoomNumber", rs.getString("RoomNumber"));
                    row.put("StartDate", rs.getDate("StartDate"));
                    row.put("EndDate", rs.getDate("EndDate"));
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] Error in getActiveNonExpiredRoomsAndContracts for customerID " + customerID + ": " + e.getMessage());
            throw e;
        }

        return results;
    }

    /**
     * Retrieves all reports in the system, including room number.
     *
     * @return A list of all Report objects with RoomNumber.
     * @throws SQLException If a database access error occurs.
     */
    public List<Report> getAllReports() throws SQLException {
        List<Report> reports = new ArrayList<>();
        String query = "SELECT r.*, rm.RoomNumber " +
                      "FROM Reports r " +
                      "JOIN Contracts c ON r.ContractID = c.ContractID " +
                      "JOIN Rooms rm ON c.RoomID = rm.RoomID " +
                      "ORDER BY r.ReportCreatedAt DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] Error in getAllReports: " + e.getMessage());
            throw e;
        }

        return reports;
    }

    /**
     * Updates the status and resolved details of a report.
     *
     * @param reportID The ID of the report to update.
     * @param status The new status of the report (e.g., Resolved, Closed).
     * @param resolvedBy The ID of the user resolving the report.
     * @return true if the report was updated successfully, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean updateReportStatus(int reportID, String status, int resolvedBy) throws SQLException {
        String query = "UPDATE Reports SET ReportStatus = ?, ResolvedBy = ?, ResolvedDate = GETDATE() WHERE ReportID = ?";

        int result = execUpdateQuery(query, status, resolvedBy, reportID);
        if (result <= 0) {
            System.err.println("[ReportDAO] Failed to update report with reportID " + reportID);
        }

        return result > 0;
    }

    /**
     * Retrieves all reports with detailed information including customer name and contract dates.
     *
     * @return A list of Report objects with RoomNumber, CustomerFullName, StartDate, and EndDate.
     * @throws SQLException If a database access error occurs.
     */
    public List<Report> getDetailedReports() throws SQLException {
        List<Report> reports = new ArrayList<>();
        String query = "SELECT r.*, rm.RoomNumber, cu.CustomerFullName, c.StartDate, c.EndDate " +
                      "FROM Reports r " +
                      "JOIN Contracts c ON r.ContractID = c.ContractID " +
                      "JOIN Rooms rm ON c.RoomID = rm.RoomID " +
                      "JOIN Customers cu ON r.CustomerID = cu.CustomerID " +
                      "ORDER BY r.ReportCreatedAt DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] Error in getDetailedReports: " + e.getMessage());
            throw e;
        }

        return reports;
    }

    /**
     * Retrieves reports by status, including room number, customer name, and contract dates.
     *
     * @param status The status to filter by (null or empty for all reports).
     * @return A list of Report objects with RoomNumber, CustomerFullName, StartDate, and EndDate.
     * @throws SQLException If a database access error occurs.
     */
    public List<Report> getReportsByStatus(String status) throws SQLException {
        List<Report> reports = new ArrayList<>();
        String query = "SELECT r.*, rm.RoomNumber, cu.CustomerFullName, c.StartDate, c.EndDate " +
                      "FROM Reports r " +
                      "JOIN Contracts c ON r.ContractID = c.ContractID " +
                      "JOIN Rooms rm ON c.RoomID = rm.RoomID " +
                      "JOIN Customers cu ON r.CustomerID = cu.CustomerID " +
                      (status != null && !status.isEmpty() ? "WHERE r.ReportStatus = ? " : "") +
                      "ORDER BY r.ReportCreatedAt DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            if (status != null && !status.isEmpty()) {
                ps.setString(1, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] Error in getReportsByStatus: " + e.getMessage());
            throw e;
        }

        return reports;
    }

    /**
     * Retrieves reports for a manager based on their assigned block and status filter.
     *
     * @param statusFilter The status to filter by (null or empty for all reports).
     * @param blockID The ID of the manager's assigned block.
     * @return A list of Report objects with RoomNumber, CustomerFullName, StartDate, and EndDate.
     * @throws SQLException If a database access error occurs.
     */
    public List<Report> getManagerReportsByStatus(String statusFilter, Integer blockID) throws SQLException {
        List<Report> reports = new ArrayList<>();
        String query = "SELECT r.*, rm.RoomNumber, cu.CustomerFullName, c.StartDate, c.EndDate " +
                      "FROM Reports r " +
                      "JOIN Contracts c ON r.ContractID = c.ContractID " +
                      "JOIN Rooms rm ON c.RoomID = rm.RoomID " +
                      "JOIN Customers cu ON r.CustomerID = cu.CustomerID " +
                      "WHERE r.RoomID IN (SELECT RoomID FROM Rooms WHERE BlockID = ?) " +
                      (statusFilter != null && !statusFilter.isEmpty() ? "AND r.ReportStatus = ? " : "") +
                      "ORDER BY r.ReportCreatedAt DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, blockID);
            if (statusFilter != null && !statusFilter.isEmpty()) {
                ps.setString(2, statusFilter);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] Error in getManagerReportsByStatus for blockID " + blockID + ": " + e.getMessage());
            throw e;
        }
        return reports;
    }

    /**
     * Retrieves all detailed reports for a manager based on their assigned block.
     *
     * @param blockID The ID of the manager's assigned block.
     * @return A list of Report objects with RoomNumber, CustomerFullName, StartDate, and EndDate.
     * @throws SQLException If a database access error occurs.
     */
    public List<Report> getManagerDetailedReports(Integer blockID) throws SQLException {
        List<Report> reports = new ArrayList<>();
        String query = "SELECT r.*, rm.RoomNumber, cu.CustomerFullName, c.StartDate, c.EndDate " +
                      "FROM Reports r " +
                      "JOIN Contracts c ON r.ContractID = c.ContractID " +
                      "JOIN Rooms rm ON c.RoomID = rm.RoomID " +
                      "JOIN Customers cu ON r.CustomerID = cu.CustomerID " +
                      "WHERE r.RoomID IN (SELECT RoomID FROM Rooms WHERE BlockID = ?) " +
                      "ORDER BY r.ReportCreatedAt DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, blockID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] Error in getManagerDetailedReports for blockID " + blockID + ": " + e.getMessage());
            throw e;
        }
        return reports;
    }

    /**
     * Checks if a report belongs to the manager's assigned block.
     *
     * @param reportID The ID of the report to check.
     * @param blockID The ID of the manager's assigned block.
     * @return true if the report belongs to the manager's block, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean isReportInManagerBlock(int reportID, Integer blockID) throws SQLException {
        String query = "SELECT COUNT(*) FROM Reports r " +
                      "JOIN Contracts c ON r.ContractID = c.ContractID " +
                      "JOIN Rooms rm ON c.RoomID = rm.RoomID " +
                      "WHERE r.ReportID = ? AND rm.BlockID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, reportID);
            ps.setInt(2, blockID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] Error in isReportInManagerBlock for reportID " + reportID + ": " + e.getMessage());
            throw e;
        }
        return false;
    }

    /**
     * Retrieves a report by its ID with detailed information.
     *
     * @param reportID The ID of the report to retrieve.
     * @return A Report object with detailed information, or null if not found.
     * @throws SQLException If a database access error occurs.
     */
    public Report getReportById(int reportID) throws SQLException {
        Report report = null;
        String query = "SELECT r.*, rm.RoomNumber, cu.CustomerFullName, c.StartDate, c.EndDate " +
                      "FROM Reports r " +
                      "JOIN Contracts c ON r.ContractID = c.ContractID " +
                      "JOIN Rooms rm ON c.RoomID = rm.RoomID " +
                      "JOIN Customers cu ON r.CustomerID = cu.CustomerID " +
                      "WHERE r.ReportID = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, reportID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    report = mapResultSetToReport(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ReportDAO] Error in getReportById for reportID " + reportID + ": " + e.getMessage());
            throw e;
        }

        return report;
    }
}