
package dao;

import utils.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BillHistoryDAO {
    private final DBContext dbContext = new DBContext();

    public void logBillEdit(int oldBillId, int newBillId, String reason, int modifiedBy) throws SQLException {
        String sql = "INSERT INTO BillHistory (OldBillID, NewBillID, Reason, ModifiedBy, ModifiedDate) VALUES (?, ?, ?, ?, GETDATE())";
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, oldBillId);
            ps.setInt(2, newBillId);
            ps.setString(3, reason);
            ps.setInt(4, modifiedBy);
            ps.executeUpdate();
        }
    }
}
