package dao;

import model.RentalRequest;
import utils.DBContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalRequestDAO extends DBContext {

    private final DBContext dbContext = new DBContext();

    public List<RentalRequest> getAllRentalRequests() throws SQLException {
        List<RentalRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM RentalRequests";
        try ( Connection c = dbContext.getConnection();  PreparedStatement ps = c.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public RentalRequest getRentalRequestById(int id) throws SQLException {
        String sql = "SELECT * FROM RentalRequests WHERE RequestID=?";
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

    public boolean insertRentalRequest(RentalRequest b) throws SQLException {
        String sql = "INSERT INTO RentalRequests (CustomerID, RoomID, RequestDate, RequestStatus) VALUES (?, ?, ?, ?)";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, b.getCustomerID());
            ps.setInt(2, b.getRoomID());
            ps.setDate(3, b.getRequestDate());
            ps.setString(4, b.getRequestStatus());
            int rows = ps.executeUpdate();
            System.out.println("✅ Rows inserted: " + rows);
            return rows > 0;
        }
    }

    public boolean updateRentalRequest(RentalRequest b) throws SQLException {
        String sql = "UPDATE RentalRequests SET CustomerID=?, RoomID=?, RequestDate=?, RequestStatus=?, ApprovedBy=?, ApprovedDate=? WHERE RequestID=?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, b.getCustomerID());
            ps.setInt(2, b.getRoomID());
            ps.setDate(3, b.getRequestDate());
            ps.setString(4, b.getRequestStatus());

            if (b.getApprovedBy() == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, b.getApprovedBy());
            }

            if (b.getApprovedDate() == null) {
                ps.setNull(6, Types.DATE);
            } else {
                ps.setDate(6, b.getApprovedDate());
            }

            ps.setInt(7, b.getRequestID());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteRentalRequest(int id) throws SQLException {
        String sql = "DELETE FROM RentalRequests WHERE RequestID=?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean existsPendingRequest(int customerId, int roomId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM RentalRequests WHERE CustomerID=? AND RoomID=? AND RequestStatus = 'Pending'";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            System.out.println("➡️ [DEBUG] Checking pending request for CustomerID = " + customerId + ", RoomID = " + roomId);
            ps.setInt(1, customerId);
            ps.setInt(2, roomId);
            try ( ResultSet rs = ps.executeQuery()) {
                boolean exists = rs.next() && rs.getInt(1) > 0;
                System.out.println("✅ [DEBUG] existsPendingRequest: " + exists);
                return exists;
            }
        }
    }

    public void cancelPendingRequest(int customerId, int roomId) throws SQLException {
        String sql = "DELETE FROM RentalRequests WHERE CustomerID = ? AND RoomID = ? AND RequestStatus = 'Pending'";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, roomId);
            ps.executeUpdate();
        }
    }

    // Dùng cho admin duyệt yêu cầu
    public RentalRequest getRequestById(int requestId) {
        String sql = "SELECT * FROM RentalRequests WHERE RequestID = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                RentalRequest r = new RentalRequest();
                r.setRequestID(rs.getInt("RequestID"));
                r.setRoomID(rs.getInt("RoomID"));
                r.setCustomerID(rs.getInt("CustomerID"));
                r.setRequestDate(rs.getDate("RequestDate"));
                r.setRequestStatus(rs.getString("RequestStatus"));
                r.setApprovedBy(rs.getObject("ApprovedBy") != null ? rs.getInt("ApprovedBy") : null);
                r.setApprovedDate(rs.getDate("ApprovedDate"));
                return r;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateStatus(int requestId, String status) {
        String sql = "UPDATE RentalRequests SET RequestStatus = ? WHERE RequestID = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, requestId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private RentalRequest map(ResultSet rs) throws SQLException {
        RentalRequest b = new RentalRequest();
        b.setRequestID(rs.getInt("RequestID"));
        b.setCustomerID(rs.getInt("CustomerID"));
        b.setRoomID(rs.getInt("RoomID"));
        b.setRequestDate(rs.getDate("RequestDate"));
        b.setRequestStatus(rs.getString("Status"));
        b.setApprovedBy(rs.getObject("ApprovedBy") != null ? rs.getInt("ApprovedBy") : null);
        b.setApprovedDate(rs.getDate("ApprovedDate"));
        return b;
    }

    public List<RentalRequest> getAllRequests() throws SQLException {
        List<RentalRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM RentalRequests";
        try ( PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<RentalRequest> getRentalRequestsByBlockId(int blockId) throws SQLException {
        List<RentalRequest> list = new ArrayList<>();
        String query = "SELECT r.* FROM RentalRequests r "
                + "JOIN Rooms ro ON r.RoomID = ro.RoomID "
                + "WHERE ro.BlockID = ? "
                + "ORDER BY r.RequestDate DESC";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, blockId);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RentalRequest r = new RentalRequest();
                    r.setRequestID(rs.getInt("RequestID"));
                    r.setCustomerID(rs.getInt("CustomerID"));
                    r.setRoomID(rs.getInt("RoomID"));
                    r.setRequestDate(rs.getDate("RequestDate"));
                    r.setRequestStatus(rs.getString("RequestStatus"));
                    r.setApprovedBy(rs.getObject("ApprovedBy") != null ? rs.getInt("ApprovedBy") : null);
                    r.setApprovedDate(rs.getDate("ApprovedDate"));
                    list.add(r);
                }
            }
        }

        return list;
    }

    public boolean existsAnyPendingRequest(int customerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM RentalRequests WHERE CustomerID = ? AND RequestStatus = 'Pending'";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean hasPendingRequestForRoom(int customerId, int roomId) throws SQLException {
        String sql = "SELECT 1 FROM RentalRequests WHERE CustomerID = ? AND RoomID = ? AND RequestStatus = 'Pending'";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, roomId);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // có kết quả là true
        }
    }
}
