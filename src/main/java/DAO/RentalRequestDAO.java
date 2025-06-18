package dao;

import model.RentalRequest;
import utils.DBContext;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RentalRequestDAO {
    private Connection conn;

    public RentalRequestDAO() {
        try {
            conn = new DBContext().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy tất cả yêu cầu thuê
    public List<RentalRequest> getAllRentalRequests() {
        List<RentalRequest> list = new ArrayList<>();
        String sql = "SELECT RequestID, CustomerID, RoomID, RequestDate, RequestStatus, ApprovedBy, ApprovedDate "
                   + "FROM RentalRequests";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new RentalRequest(
                    rs.getInt("RequestID"),
                    rs.getInt("CustomerID"),
                    rs.getInt("RoomID"),
                    rs.getDate("RequestDate"),
                    rs.getString("RequestStatus"),
                    rs.getInt("ApprovedBy"),
                    rs.getDate("ApprovedDate")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy 1 yêu cầu theo ID
    public RentalRequest getRentalRequestById(int id) {
        String sql = "SELECT RequestID, CustomerID, RoomID, RequestDate, RequestStatus, ApprovedBy, ApprovedDate "
                   + "FROM RentalRequests WHERE RequestID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new RentalRequest(
                        rs.getInt("RequestID"),
                        rs.getInt("CustomerID"),
                        rs.getInt("RoomID"),
                        rs.getDate("RequestDate"),
                        rs.getString("RequestStatus"),
                        rs.getInt("ApprovedBy"),
                        rs.getDate("ApprovedDate")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm mới yêu cầu
    public void addRentalRequest(RentalRequest r) {
        String sql = "INSERT INTO RentalRequests (CustomerID, RoomID, RequestDate, RequestStatus, ApprovedBy, ApprovedDate) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getCustomerID());
            ps.setInt(2, r.getRoomID());
            ps.setDate(3, r.getRequestDate());
            ps.setString(4, r.getRequestStatus());
            ps.setInt(5, r.getApprovedBy());
            ps.setDate(6, r.getApprovedDate());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cập nhật yêu cầu
    public void updateRentalRequest(RentalRequest r) {
        String sql = "UPDATE RentalRequests SET CustomerID = ?, RoomID = ?, RequestDate = ?, RequestStatus = ?, ApprovedBy = ?, ApprovedDate = ? "
                   + "WHERE RequestID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getCustomerID());
            ps.setInt(2, r.getRoomID());
            ps.setDate(3, r.getRequestDate());
            ps.setString(4, r.getRequestStatus());
            ps.setInt(5, r.getApprovedBy());
            ps.setDate(6, r.getApprovedDate());
            ps.setInt(7, r.getRequestID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Xóa yêu cầu
    public void deleteRentalRequest(int id) {
        String sql = "DELETE FROM RentalRequests WHERE RequestID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy theo khách hàng
    public List<RentalRequest> getRentalRequestsByCustomerId(int customerId) {
        List<RentalRequest> list = new ArrayList<>();
        String sql = "SELECT RequestID, CustomerID, RoomID, RequestDate, RequestStatus, ApprovedBy, ApprovedDate "
                   + "FROM RentalRequests WHERE CustomerID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new RentalRequest(
                        rs.getInt("RequestID"),
                        rs.getInt("CustomerID"),
                        rs.getInt("RoomID"),
                        rs.getDate("RequestDate"),
                        rs.getString("RequestStatus"),
                        rs.getInt("ApprovedBy"),
                        rs.getDate("ApprovedDate")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy theo phòng
    public List<RentalRequest> getRentalRequestsByRoomId(int roomId) {
        List<RentalRequest> list = new ArrayList<>();
        String sql = "SELECT RequestID, CustomerID, RoomID, RequestDate, RequestStatus, ApprovedBy, ApprovedDate "
                   + "FROM RentalRequests WHERE RoomID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new RentalRequest(
                        rs.getInt("RequestID"),
                        rs.getInt("CustomerID"),
                        rs.getInt("RoomID"),
                        rs.getDate("RequestDate"),
                        rs.getString("RequestStatus"),
                        rs.getInt("ApprovedBy"),
                        rs.getDate("ApprovedDate")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
