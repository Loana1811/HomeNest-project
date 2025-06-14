/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.UtilityHistoryView;
import utils.DBContext;

/**
 *
 * @author kloane
 */
public class UtilityHistoryDAO {

    public List<UtilityHistoryView> getHistory() throws SQLException {
        List<UtilityHistoryView> list = new ArrayList<>();
        String sql = "SELECT ur.UtilityTypeID, ut.UtilityName, ur.PriceUsed AS NewPrice, ur.OldPrice, ur.CreatedAt AS ChangeAt, ur.ChangedBy "
                + "FROM UtilityReadings ur "
                + "JOIN UtilityTypes ut ON ur.UtilityTypeID = ut.UtilityTypeID "
                + "WHERE ur.OldPrice IS NOT NULL "
                + "ORDER BY ur.CreatedAt DESC";

        try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new UtilityHistoryView(
                        rs.getInt("UtilityTypeID"),
                        rs.getString("UtilityName"),
                        rs.getDouble("OldPrice"),
                        rs.getDouble("NewPrice"),
                        rs.getString("ChangedBy"),
                        rs.getDate("ChangeAt")
                ));
            }
        }

        return list;
    }
}
