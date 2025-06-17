package dao;

import model.BillDetail;
import utils.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BillDetailDAO {
    private Connection conn;

    public BillDetailDAO() {
        try {
            conn = new DBContext().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy tất cả chi tiết hóa đơn
    public List<BillDetail> getAllBillDetails() {
        List<BillDetail> list = new ArrayList<>();
        String sql = "SELECT BillDetailID, BillID, RoomRent, ElectricityCost, WaterCost, WifiCost FROM BillDetails";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new BillDetail(
                    rs.getInt("BillDetailID"),
                    rs.getInt("BillID"),
                    rs.getFloat("RoomRent"),
                    rs.getFloat("ElectricityCost"),
                    rs.getFloat("WaterCost"),
                    rs.getFloat("WifiCost")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy chi tiết hóa đơn theo ID
    public BillDetail getBillDetailById(int id) {
        String sql = "SELECT BillDetailID, BillID, RoomRent, ElectricityCost, WaterCost, WifiCost "
                   + "FROM BillDetails WHERE BillDetailID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new BillDetail(
                        rs.getInt("BillDetailID"),
                        rs.getInt("BillID"),
                        rs.getFloat("RoomRent"),
                        rs.getFloat("ElectricityCost"),
                        rs.getFloat("WaterCost"),
                        rs.getFloat("WifiCost")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm mới chi tiết hóa đơn
    public void addBillDetail(BillDetail bd) {
        String sql = "INSERT INTO BillDetails (BillID, RoomRent, ElectricityCost, WaterCost, WifiCost) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bd.getBillID());
            ps.setFloat(2, bd.getRoomrent());
            ps.setFloat(3, bd.getElectricityCost());
            ps.setFloat(4, bd.getWaterCost());
            ps.setFloat(5, bd.getWifiCost());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cập nhật chi tiết hóa đơn
    public void updateBillDetail(BillDetail bd) {
        String sql = "UPDATE BillDetails SET BillID = ?, RoomRent = ?, ElectricityCost = ?, WaterCost = ?, WifiCost = ? "
                   + "WHERE BillDetailID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bd.getBillID());
            ps.setFloat(2, bd.getRoomrent());
            ps.setFloat(3, bd.getElectricityCost());
            ps.setFloat(4, bd.getWaterCost());
            ps.setFloat(5, bd.getWifiCost());
            ps.setInt(6, bd.getBillDetailID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Xóa chi tiết hóa đơn
    public void deleteBillDetail(int id) {
        String sql = "DELETE FROM BillDetails WHERE BillDetailID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy danh sách chi tiết theo hóa đơn
    public List<BillDetail> getBillDetailsByBillId(int billId) {
        List<BillDetail> list = new ArrayList<>();
        String sql = "SELECT BillDetailID, BillID, RoomRent, ElectricityCost, WaterCost, WifiCost "
                   + "FROM BillDetails WHERE BillID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new BillDetail(
                        rs.getInt("BillDetailID"),
                        rs.getInt("BillID"),
                        rs.getFloat("RoomRent"),
                        rs.getFloat("ElectricityCost"),
                        rs.getFloat("WaterCost"),
                        rs.getFloat("WifiCost")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
