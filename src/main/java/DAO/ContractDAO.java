package dao;

import model.Contract;
import utils.DBContext;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ContractDAO {
    private Connection conn;

    public ContractDAO() {
        try {
            conn = new DBContext().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy tất cả hợp đồng
    public List<Contract> getAllContracts() {
        List<Contract> list = new ArrayList<>();
        String sql = "SELECT ContractID, TenantID, RoomID, StartDate, EndDate, ContractStatus, ContractCreatedAt "
                   + "FROM Contracts";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Contract(
                    rs.getInt("ContractID"),
                    rs.getInt("TenantID"),
                    rs.getInt("RoomID"),
                    rs.getDate("StartDate"),
                    rs.getDate("EndDate"),
                    rs.getString("ContractStatus"),
                    rs.getDate("ContractCreatedAt")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy 1 hợp đồng theo ID
    public Contract getContractById(int id) {
        String sql = "SELECT ContractID, TenantID, RoomID, StartDate, EndDate, ContractStatus, ContractCreatedAt "
                   + "FROM Contracts WHERE ContractID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Contract(
                        rs.getInt("ContractID"),
                        rs.getInt("TenantID"),
                        rs.getInt("RoomID"),
                        rs.getDate("StartDate"),
                        rs.getDate("EndDate"),
                        rs.getString("ContractStatus"),
                        rs.getDate("ContractCreatedAt")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm mới hợp đồng
    public void addContract(Contract c) {
        String sql = "INSERT INTO Contracts (TenantID, RoomID, StartDate, EndDate, ContractStatus) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getTenantID());
            ps.setInt(2, c.getRoomID());
            ps.setDate(3, c.getStartDate());
            ps.setDate(4, c.getEndDate());
            ps.setString(5, c.getContractStatus());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cập nhật hợp đồng
    public void updateContract(Contract c) {
        String sql = "UPDATE Contracts SET TenantID = ?, RoomID = ?, StartDate = ?, EndDate = ?, ContractStatus = ? "
                   + "WHERE ContractID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getTenantID());
            ps.setInt(2, c.getRoomID());
            ps.setDate(3, c.getStartDate());
            ps.setDate(4, c.getEndDate());
            ps.setString(5, c.getContractStatus());
            ps.setInt(6, c.getContractID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Xóa hợp đồng
    public void deleteContract(int id) {
        String sql = "DELETE FROM Contracts WHERE ContractID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy danh sách hợp đồng theo tenant
    public List<Contract> getContractsByTenantId(int tenantId) {
        List<Contract> list = new ArrayList<>();
        String sql = "SELECT ContractID, TenantID, RoomID, StartDate, EndDate, ContractStatus, ContractCreatedAt "
                   + "FROM Contracts WHERE TenantID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Contract(
                        rs.getInt("ContractID"),
                        rs.getInt("TenantID"),
                        rs.getInt("RoomID"),
                        rs.getDate("StartDate"),
                        rs.getDate("EndDate"),
                        rs.getString("ContractStatus"),
                        rs.getDate("ContractCreatedAt")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
