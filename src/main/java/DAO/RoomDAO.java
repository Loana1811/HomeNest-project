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
    // Constructor mới: bạn dùng ở servlet để tái sử dụng connection

    public RoomDAO(Connection conn) {
        this.conn = conn;
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
        String query = "SELECT * FROM Rooms WHERE roomID = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new Room(
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
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addRoom(Room room) {
        String query = "INSERT INTO Rooms (roomNumber, rentPrice, area, location, roomStatus, blockID, categoryID, highlights, imagePath, description, postedDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            ps = conn.prepareStatement(query);
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
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateRoom(Room room) {
        String query = "UPDATE Rooms SET roomNumber=?, rentPrice=?, area=?, location=?, roomStatus=?, blockID=?, categoryID=?, highlights=?, imagePath=?, description=?, postedDate=? WHERE roomID=?";
        try {
            ps = conn.prepareStatement(query);
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

    public void deleteRoom(int id) {
        String query = "DELETE FROM Rooms WHERE roomID = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public List<Room> filterRooms(Integer categoryId, String block, String status,
            Double minPrice, Double maxPrice,
            Double minArea, Double maxArea,
            int page, int pageSize) throws SQLException {

        List<Room> filteredRooms = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM Rooms WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (categoryId != null) {
            sql.append(" AND categoryID = ?");
            params.add(categoryId);
        }

        if (block != null && !block.isEmpty()) {
            sql.append(" AND blockID = ?");
            params.add(Integer.parseInt(block));
        }

        if (status != null && !status.isEmpty()) {
            sql.append(" AND LOWER(roomStatus) = ?");
            params.add(status.toLowerCase());
        }

        if (minPrice != null) {
            sql.append(" AND rentPrice >= ?");
            params.add(minPrice);
        }

        if (maxPrice != null) {
            sql.append(" AND rentPrice <= ?");
            params.add(maxPrice);
        }

        if (minArea != null) {
            sql.append(" AND area >= ?");
            params.add(minArea);
        }

        if (maxArea != null) {
            sql.append(" AND area <= ?");
            params.add(maxArea);
        }

        // PHÂN TRANG: sử dụng OFFSET - FETCH
        sql.append(" ORDER BY roomID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize); // OFFSET
        params.add(pageSize);              // LIMIT

        try ( PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room room = new Room(
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
                );
                filteredRooms.add(room);
            }
        }

        return filteredRooms;
    }

    public List<Room> getFeaturedRooms() throws SQLException {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT TOP 3 * FROM Rooms ORDER BY NEWID()";

        try ( PreparedStatement stmt = conn.prepareStatement(sql);  ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Room room = new Room(
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
                );
                list.add(room);
            }
        }

        return list;
    }

    public int countFilteredRooms(Integer categoryId, String block, String status,
            Double minPrice, Double maxPrice,
            Double minArea, Double maxArea) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Rooms WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (categoryId != null) {
            sql.append(" AND categoryID = ?");
            params.add(categoryId);
        }

        if (block != null && !block.isEmpty()) {
            sql.append(" AND blockID = ?");
            params.add(Integer.parseInt(block));
        }

        if (status != null && !status.isEmpty()) {
            sql.append(" AND LOWER(roomStatus) = ?");
            params.add(status.toLowerCase());
        }

        if (minPrice != null) {
            sql.append(" AND rentPrice >= ?");
            params.add(minPrice);
        }

        if (maxPrice != null) {
            sql.append(" AND rentPrice <= ?");
            params.add(maxPrice);
        }

        if (minArea != null) {
            sql.append(" AND area >= ?");
            params.add(minArea);
        }

        if (maxArea != null) {
            sql.append(" AND area <= ?");
            params.add(maxArea);
        }

        try ( PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

}
