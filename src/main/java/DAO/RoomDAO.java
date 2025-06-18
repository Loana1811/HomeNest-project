/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.DBContext;

/**
 *
 * @author ThanhTruc
 */
public class RoomDAO {

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public RoomDAO() {
        try {
            DBContext db = new DBContext();
            conn = db.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String query = "SELECT * FROM Rooms";
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Room(
                        rs.getInt("roomID"),
                        rs.getString("roomNumber"),
                        rs.getFloat("rentPrice"),
                        rs.getFloat("area"),
                        rs.getString("location"),
                        rs.getString("roomStatus"),
                        rs.getInt("blockID"),
                        rs.getInt("categoryID"),
                        rs.getString("highlights"),
                        rs.getString("imagePath"),
                        rs.getString("description"),
                        rs.getDate("postedDate")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Room getRoomById(int id) {
        try {
            String sql = "SELECT * FROM Rooms WHERE RoomID = ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new Room(
                        rs.getInt("RoomID"),
                        rs.getString("RoomNumber"),
                        rs.getFloat("RentPrice"),
                        rs.getFloat("Area"),
                        rs.getString("Location"),
                        rs.getString("roomStatus"),
                        rs.getInt("BlockID"),
                        rs.getInt("CategoryID"),
                        rs.getString("Highlights"),
                        rs.getString("ImagePath"),
                        rs.getString("Description"),
                        rs.getDate("PostedDate")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addRoom(Room room) {
        String sql = "INSERT INTO Rooms (roomNumber, rentPrice, area, location, roomStatus, blockID, categoryID, highlights, imagePath, description, postedDate) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, room.getRoomNumber());
            ps.setFloat(2, room.getRentPrice());
            ps.setFloat(3, room.getArea());
            ps.setString(4, room.getLocation());
            ps.setString(5, room.getRoomStatus());
            ps.setObject(6, room.getBlockID());
            ps.setObject(7, room.getCategoryID());
            ps.setString(8, room.getHighlights());
            ps.setString(9, room.getImagePath());
            ps.setString(10, room.getDescription());
            ps.setDate(11, room.getPostedDate());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateRoom(Room room) {
        String sql = "UPDATE Rooms SET roomNumber=?, rentPrice=?, area=?, location=?, roomStatus=?, blockID=?, categoryID=?, highlights=?, imagePath=?, description=?, postedDate=? WHERE roomID=?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, room.getRoomNumber());
            ps.setFloat(2, room.getRentPrice());
            ps.setFloat(3, room.getArea());
            ps.setString(4, room.getLocation());
            ps.setString(5, room.getRoomStatus());
            ps.setInt(6, room.getBlockID());
            ps.setInt(7, room.getCategoryID());
            ps.setString(8, room.getHighlights());
            ps.setString(9, room.getImagePath());
            ps.setString(10, room.getDescription());
            ps.setDate(11, room.getPostedDate());
            ps.setInt(12, room.getRoomID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteRoom(int id) throws SQLException {
        String sql = "DELETE FROM Rooms WHERE RoomID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        int rowsAffected = ps.executeUpdate();
        System.out.println("Deleted rows: " + rowsAffected);
    }

    public List<Room> getRoomsByBlockId(int blockId) {
        List<Room> list = new ArrayList<>();
        String query = "SELECT * FROM Rooms WHERE blockID = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, blockId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Room(
                        rs.getInt("roomID"),
                        rs.getString("roomNumber"),
                        rs.getFloat("rentPrice"),
                        rs.getFloat("area"),
                        rs.getString("location"),
                        rs.getString("roomStatus"),
                        rs.getInt("blockID"),
                        rs.getInt("categoryID"),
                        rs.getString("highlights"),
                        rs.getString("imagePath"),
                        rs.getString("description"),
                        rs.getDate("postedDate")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Room> getRoomsByCategoryId(int categoryId) {
        List<Room> list = new ArrayList<>();
        String query = "SELECT * FROM Rooms WHERE categoryID = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, categoryId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Room(
                        rs.getInt("roomID"),
                        rs.getString("roomNumber"),
                        rs.getFloat("rentPrice"),
                        rs.getFloat("area"),
                        rs.getString("location"),
                        rs.getString("roomStatus"),
                        rs.getInt("blockID"),
                        rs.getInt("categoryID"),
                        rs.getString("highlights"),
                        rs.getString("imagePath"),
                        rs.getString("description"),
                        rs.getDate("postedDate")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Room> searchRooms(String keyword) {
        List<Room> list = new ArrayList<>();
        String query = "SELECT * FROM Rooms WHERE roomNumber LIKE ? OR location LIKE ? OR description LIKE ?";
        try {
            ps = conn.prepareStatement(query);
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Room(
                        rs.getInt("roomID"),
                        rs.getString("roomNumber"),
                        rs.getFloat("rentPrice"),
                        rs.getFloat("area"),
                        rs.getString("location"),
                        rs.getString("roomStatus"),
                        rs.getInt("blockID"),
                        rs.getInt("categoryID"),
                        rs.getString("highlights"),
                        rs.getString("imagePath"),
                        rs.getString("description"),
                        rs.getDate("postedDate")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Room> getAvailableRooms() {
        List<Room> list = new ArrayList<>();
        String query = "SELECT * FROM Rooms WHERE roomStatus = 'Available'";
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Room(
                        rs.getInt("roomID"),
                        rs.getString("roomNumber"),
                        rs.getFloat("rentPrice"),
                        rs.getFloat("area"),
                        rs.getString("location"),
                        rs.getString("roomStatus"),
                        rs.getInt("blockID"),
                        rs.getInt("categoryID"),
                        rs.getString("highlights"),
                        rs.getString("imagePath"),
                        rs.getString("description"),
                        rs.getDate("postedDate")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Room> filterRoomsByCategoryAndBlock(String categoryID, String blockID) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM Rooms WHERE 1=1";

        if (categoryID != null && !categoryID.isEmpty()) {
            sql += " AND CategoryID = ?";
        }
        if (blockID != null && !blockID.isEmpty()) {
            sql += " AND BlockID = ?";
        }

        try {
            ps = conn.prepareStatement(sql);
            int index = 1;
            if (categoryID != null && !categoryID.isEmpty()) {
                ps.setInt(index++, Integer.parseInt(categoryID));
            }
            if (blockID != null && !blockID.isEmpty()) {
                ps.setInt(index++, Integer.parseInt(blockID));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room room = new Room();
                room.setRoomID(rs.getInt("RoomID"));
                room.setRoomNumber(rs.getString("RoomNumber"));
                room.setRentPrice(rs.getFloat("RentPrice"));
                room.setArea(rs.getFloat("Area"));
                room.setLocation(rs.getString("Location"));
                room.setRoomStatus(rs.getString("RoomStatus"));
                room.setBlockID(rs.getInt("BlockID"));
                room.setCategoryID(rs.getInt("CategoryID"));
                room.setHighlights(rs.getString("Highlights"));
                room.setImagePath(rs.getString("ImagePath"));
                room.setDescription(rs.getString("Description"));
                room.setPostedDate(rs.getDate("PostedDate"));
                list.add(room);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean isRoomNumberExists(String roomNumber) {
        String sql = "SELECT COUNT(*) FROM Rooms WHERE RoomNumber = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, roomNumber);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isRoomNumberExistsForOther(String roomNumber, int id) {
        String sql = "SELECT COUNT(*) FROM Rooms WHERE roomNumber = ? AND roomID <> ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, roomNumber);
            ps.setInt(2, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateRoomCount(int blockId) {
        String countSql = "SELECT COUNT(*) FROM Rooms WHERE blockID = ?";
        String updateSql = "UPDATE Blocks SET roomCount = ? WHERE blockID = ?";

        try {
            // Đếm số phòng trong block
            PreparedStatement psCount = conn.prepareStatement(countSql);
            psCount.setInt(1, blockId);
            ResultSet rs = psCount.executeQuery();
            int roomCount = 0;
            if (rs.next()) {
                roomCount = rs.getInt(1);
            }
            rs.close();
            psCount.close();

            // Cập nhật lại số phòng trong block
            PreparedStatement psUpdate = conn.prepareStatement(updateSql);
            psUpdate.setInt(1, roomCount);
            psUpdate.setInt(2, blockId);
            int rows = psUpdate.executeUpdate();
            System.out.println("Block " + blockId + " updated room count to " + roomCount + ", rows affected: " + rows);
            psUpdate.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Room> searchByRoomName(String keyword) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM Rooms WHERE RoomNumber LIKE ?";

        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("RoomID"),
                        rs.getString("RoomNumber"),
                        rs.getFloat("RentPrice"),
                        rs.getFloat("Area"),
                        rs.getString("Location"),
                        rs.getString("RoomStatus"),
                        rs.getInt("BlockID"),
                        rs.getInt("CategoryID"),
                        rs.getString("Highlights"),
                        rs.getString("ImagePath"),
                        rs.getString("Description"),
                        rs.getDate("PostedDate")
                );
                list.add(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}
