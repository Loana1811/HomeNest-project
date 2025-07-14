package dao;

import model.Contract;
import utils.DBContext;
import java.sql.*;
import java.util.*;

public class ContractDAO {

    private final DBContext dbContext = new DBContext();

    public List<Contract> getAllContracts() throws SQLException {
        List<Contract> list = new ArrayList<>();
        String sql = "SELECT * FROM Contracts";
        try ( Connection c = dbContext.getConnection();  PreparedStatement ps = c.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Contract getContractById(int id) throws SQLException {
        String sql = "SELECT * FROM Contracts WHERE ContractID=?";
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

    public boolean insertContract(Contract contract) throws SQLException {
        String sql = "INSERT INTO Contracts (TenantID, RoomID, StartDate, EndDate, ContractStatus, ContractCreatedAt) VALUES (?, ?, ?, ?, ?, ?)";

        return dbContext.execUpdateQuery(sql,
                contract.getTenantID(), contract.getRoomID(), contract.getStartDate(), contract.getEndDate(), contract.getContractStatus(), contract.getContractCreatedAt()
        ) > 0;
    }

    public boolean updateContract(Contract contract) throws SQLException {
        String sql = "UPDATE Contracts SET TenantID=?, RoomID=?, StartDate=?, EndDate=?, ContractStatus=?, ContractCreatedAt=? WHERE ContractID=?";

        return dbContext.execUpdateQuery(sql,
                contract.getTenantID(), contract.getRoomID(), contract.getStartDate(), contract.getEndDate(), contract.getContractStatus(), contract.getContractCreatedAt(), contract.getContractID()
        ) > 0;
    }

    public boolean deleteContract(int id) throws SQLException {
        String sql = "DELETE FROM Contracts WHERE ContractID=?";
        return dbContext.execUpdateQuery(sql, id) > 0;
    }

    private Contract map(ResultSet rs) throws SQLException {
        Contract c = new Contract();
        c.setContractID(rs.getInt("ContractID"));
        c.setTenantID(rs.getInt("TenantID"));
        c.setRoomID(rs.getInt("RoomID"));
        c.setStartDate(rs.getDate("StartDate"));
        c.setEndDate(rs.getDate("EndDate"));
        c.setContractStatus(rs.getString("ContractStatus"));
        c.setContractCreatedAt(rs.getDate("ContractCreatedAt"));

        return c;
    }
}
