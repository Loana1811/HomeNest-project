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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import utils.DBContext;

/**
 *
 * @author kloane
 */
public class RoomDAO {

    public List<Object[]> getRoomsAppliedToUtility(int utilityTypeId) throws SQLException {
        List<Object[]> rooms = new ArrayList<>();
        String sql
                = "SELECT r.RoomID, r.RoomNumber,\n"
                + "       CASE WHEN ur.UtilityTypeID IS NOT NULL THEN 1 ELSE 0 END AS IsChecked\n"
                + "FROM Rooms r\n"
                + "LEFT JOIN UtilityReadings ur\n"
                + "  ON r.RoomID = ur.RoomID AND ur.UtilityTypeID = ?\n"
                + "GROUP BY r.RoomID, r.RoomNumber, ur.UtilityTypeID";

        try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, utilityTypeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rooms.add(new Object[]{
                    rs.getInt("RoomID"),
                    rs.getString("RoomNumber"),
                    rs.getInt("IsChecked") == 1
                });
            }
        }
        return rooms;

    }
  public List<Object[]> getRoomIdNameByBlock(int blockId) throws SQLException {
    List<Object[]> rooms = new ArrayList<>();
    String query = "SELECT RoomID, RoomNumber FROM Rooms WHERE BlockID = ?";
    try ( Connection conn = new DBContext().getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setInt(1, blockId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rooms.add(new Object[]{rs.getInt("RoomID"), rs.getString("RoomNumber")});
            }
        }
    }
    return rooms;
}

  public int getBlockIdByRoomId(int roomId) throws SQLException {
    String query = "SELECT BlockID FROM Rooms WHERE RoomID = ?";
     try ( Connection conn = new DBContext().getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setInt(1, roomId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("BlockID");
            }
        }
    }
    return -1;
}

  public Map<String, List<Object[]>> getRoomsGroupedByBlock() throws SQLException {
    Map<String, List<Object[]>> map = new LinkedHashMap<>();
    String sql = "SELECT r.RoomID, r.RoomNumber, b.BlockName " +
                 "FROM Rooms r " +
                 "JOIN Blocks b ON r.BlockID = b.BlockID " +
                 "ORDER BY b.BlockName, r.RoomNumber";

    try (Connection conn = new DBContext().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            String blockName = rs.getString("BlockName");
            Object[] roomData = new Object[] {
                rs.getInt("RoomID"),
                rs.getString("RoomNumber")
            };

            // 👇 Sửa lại phần này
            if (!map.containsKey(blockName)) {
              map.put(blockName, new ArrayList<Object[]>());

            }
            map.get(blockName).add(roomData);
        }
    }

    return map;
}

  



    public List<Object[]> getAllRoomIdName() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT RoomID, RoomNumber FROM Rooms";
        try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{rs.getInt("RoomID"), rs.getString("RoomNumber")});
            }
        }
        return list;
    }
    
    
    

}
