package dao;

import model.Bill;
import utils.DBContext;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {
    private Connection conn;

    public BillDAO() {
        try {
            conn = new DBContext().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy tất cả hóa đơn
    public List<Bill> getAllBills() {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT BillID, ContractID, BillDate, TotalAmount, BillStatus FROM Bills";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Bill b = new Bill(
                    rs.getInt("BillID"),
                    rs.getInt("ContractID"),
                    rs.getDate("BillDate"),
                    rs.getFloat("TotalAmount"),
                    rs.getString("BillStatus")
                );
                list.add(b);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy 1 hóa đơn theo ID
    public Bill getBillById(int id) {
        String sql = "SELECT BillID, ContractID, BillDate, TotalAmount, BillStatus "
                   + "FROM Bills WHERE BillID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Bill(
                        rs.getInt("BillID"),
                        rs.getInt("ContractID"),
                        rs.getDate("BillDate"),
                        rs.getFloat("TotalAmount"),
                        rs.getString("BillStatus")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm mới hóa đơn
    public void addBill(Bill bill) {
        String sql = "INSERT INTO Bills (ContractID, BillDate, TotalAmount, BillStatus) "
                   + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bill.getContractID());
            ps.setDate(2, bill.getBillDate());
            ps.setFloat(3, bill.getTotalAmount());
            ps.setString(4, bill.getBillStatus());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cập nhật hóa đơn
    public void updateBill(Bill bill) {
        String sql = "UPDATE Bills SET ContractID = ?, BillDate = ?, TotalAmount = ?, BillStatus = ? "
                   + "WHERE BillID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bill.getContractID());
            ps.setDate(2, bill.getBillDate());
            ps.setFloat(3, bill.getTotalAmount());
            ps.setString(4, bill.getBillStatus());
            ps.setInt(5, bill.getBillID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Xóa hóa đơn
    public void deleteBill(int id) {
        String sql = "DELETE FROM Bills WHERE BillID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy danh sách hóa đơn theo hợp đồng
    public List<Bill> getBillsByContractId(int contractId) {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT BillID, ContractID, BillDate, TotalAmount, BillStatus "
                   + "FROM Bills WHERE ContractID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bill b = new Bill(
                        rs.getInt("BillID"),
                        rs.getInt("ContractID"),
                        rs.getDate("BillDate"),
                        rs.getFloat("TotalAmount"),
                        rs.getString("BillStatus")
                    );
                    list.add(b);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
