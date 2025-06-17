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
        String query = "SELECT * FROM Room";
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
        String query = "SELECT * FROM Room WHERE roomID = ?";
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
        String query = "INSERT INTO Room (roomNumber, rentPrice, area, location, roomStatus, blockID, categoryID, highlights, imagePath, description, postedDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
        String query = "UPDATE Room SET roomNumber=?, rentPrice=?, area=?, location=?, roomStatus=?, blockID=?, categoryID=?, highlights=?, imagePath=?, description=?, postedDate=? WHERE roomID=?";
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
        String query = "DELETE FROM Room WHERE roomID = ?";
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
        String query = "SELECT * FROM Room WHERE blockID = ?";
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
        String query = "SELECT * FROM Room WHERE categoryID = ?";
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
        String query = "SELECT * FROM Room WHERE roomNumber LIKE ? OR location LIKE ? OR description LIKE ?";
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
        String query = "SELECT * FROM Room WHERE roomStatus = 'Available'";
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
} 