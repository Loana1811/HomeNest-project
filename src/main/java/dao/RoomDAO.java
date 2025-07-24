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
import model.Block;
import model.Contract;
import model.Room;
import model.RoomStatusDTO;
import utils.DBContext;

/**
 *
 * @author kloane
 */
public class RoomDAO extends DBContext {

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
        try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, blockId);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rooms.add(new Object[]{rs.getInt("RoomID"), rs.getString("RoomNumber")});
                }
            }
        }
        return rooms;
    }

    public int getBlockIdByRoomId(int roomId) throws SQLException {
        String query = "SELECT BlockID FROM Rooms WHERE RoomID = ?";
        try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, roomId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("BlockID");
                }
            }
        }
        return -1;
    }

    public Map<String, List<Object[]>> getRoomsGroupedByBlock() throws SQLException {
        Map<String, List<Object[]>> map = new LinkedHashMap<>();
        String sql = "SELECT r.RoomID, r.RoomNumber, b.BlockName "
                + "FROM Rooms r "
                + "JOIN Blocks b ON r.BlockID = b.BlockID "
                + "ORDER BY b.BlockName, r.RoomNumber";

        try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String blockName = rs.getString("BlockName");
                Object[] roomData = new Object[]{
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

    public ArrayList<Room> getAvailableRoomss() {
        ArrayList<Room> rooms = new ArrayList<>();
        String squery = "SELECT * FROM Rooms WHERE RoomID NOT IN (SELECT RoomID FROM Contracts)";
        try ( ResultSet rs = this.execSelectQuery(squery)) {
            while (rs.next()) {
                Room room = new Room();
                room.setRoomID(rs.getInt("RoomID"));
                room.setRoomNumber(rs.getString("RoomNumber"));
                room.setRentPrice(rs.getDouble("RentPrice"));
                room.setArea(rs.getDouble("Area"));
                room.setLocation(rs.getString("Location"));
                room.setRoomStatus(rs.getString("RoomStatus"));
                room.setBlockID(rs.getInt("BlockID"));
                room.setImagePath(rs.getBytes("ImagePath"));
                room.setDescription(rs.getString("Description"));
                room.setPostedDate(rs.getDate("PostedDate"));

                rooms.add(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rooms;
    }

    public ArrayList<Room> getAvailableRoomsIncludingCurrent(int currentRoomId) {
        ArrayList<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM Rooms WHERE RoomID NOT IN (SELECT RoomID FROM Contracts) OR RoomID = ?";

        try ( PreparedStatement ps = getConnection().prepareStatement(query)) {
            ps.setInt(1, currentRoomId);
//            ps.setInt(2, currentRoomId);  // Gán cả hai tham số
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room room = new Room();
                room.setRoomID(rs.getInt("RoomID"));
                room.setRoomNumber(rs.getString("RoomNumber"));
                room.setRentPrice(rs.getDouble("RentPrice"));
                room.setArea(rs.getDouble("Area"));
                room.setLocation(rs.getString("Location"));
                room.setRoomStatus(rs.getString("RoomStatus"));
                room.setBlockID(rs.getInt("BlockID"));
                room.setImagePath(rs.getBytes("ImagePath"));
                room.setDescription(rs.getString("Description"));
                room.setPostedDate(rs.getDate("PostedDate"));
                rooms.add(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public Room getRoomById(int roomId) {
        Room room = null;
        String squery = "SELECT * FROM Rooms WHERE RoomID = ?";

        try ( PreparedStatement ps = conn.prepareStatement(squery)) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                room = new Room();
                room.setRoomID(rs.getInt("RoomID"));
                room.setRoomNumber(rs.getString("RoomNumber"));
                room.setRentPrice(rs.getDouble("RentPrice"));
                room.setArea(rs.getDouble("Area"));
                room.setLocation(rs.getString("Location"));
                room.setRoomStatus(rs.getString("RoomStatus"));
                room.setBlockID(rs.getInt("BlockID"));
                room.setImagePath(rs.getBytes("ImagePath"));
                room.setDescription(rs.getString("Description"));
                room.setPostedDate(rs.getDate("PostedDate"));

                // ✅ Thêm 4 cột tiện ích
                room.setIsElectricityFree(rs.getInt("IsElectricityFree"));
                room.setIsWaterFree(rs.getInt("IsWaterFree"));
                room.setIsWifiFree(rs.getInt("IsWifiFree"));
                room.setIsTrashFree(rs.getInt("IsTrashFree"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return room;
    }

    public Room getRoomByIds(int id) {
        String query = "SELECT * FROM Rooms WHERE roomID = ?";
        try (
                 Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Room(
                        rs.getInt("roomID"),
                        rs.getString("roomNumber"),
                        rs.getFloat("rentPrice"),
                        rs.getFloat("area"),
                        rs.getString("location"),
                        rs.getString("roomStatus"),
                        rs.getInt("blockID"),
                        rs.getBytes("imagePath"),
                        rs.getString("description"),
                        rs.getDate("postedDate")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

// Cập nhật phương thức addRoom để thêm các cột mới
    public void addRoom(Room room) {
        String query = "INSERT INTO Rooms (roomNumber, rentPrice, area, location, roomStatus, blockID, imagePath, description, postedDate, IsElectricityFree, IsWaterFree, IsWifiFree, IsTrashFree) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, room.getRoomNumber());
            ps.setDouble(2, room.getRentPrice());
            ps.setDouble(3, room.getArea());
            ps.setString(4, room.getLocation());
            ps.setString(5, room.getRoomStatus());
            ps.setInt(6, room.getBlockID());
            ps.setBytes(7, room.getImagePath());
            ps.setString(8, room.getDescription());
            ps.setDate(9, room.getPostedDate());

            // Thêm các cột mới vào câu truy vấn
            ps.setInt(10, room.getIsElectricityFree());  // Thêm cột IsElectricityFree
            ps.setInt(11, room.getIsWaterFree());        // Thêm cột IsWaterFree
            ps.setInt(12, room.getIsWifiFree());         // Thêm cột IsWifiFree
            ps.setInt(13, room.getIsTrashFree());        // Thêm cột IsTrashFree

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// Cập nhật phương thức updateRoom để sửa các cột mới
    public void updateRoom(Room room) {
        String query = "UPDATE Rooms SET roomNumber=?, rentPrice=?, area=?, location=?, roomStatus=?, blockID=?, imagePath=?, description=?, postedDate=?, "
                + "IsElectricityFree=?, IsWaterFree=?, IsWifiFree=?, IsTrashFree=? WHERE roomID=?";

        try ( Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, room.getRoomNumber());
            ps.setDouble(2, room.getRentPrice());
            ps.setDouble(3, room.getArea());
            ps.setString(4, room.getLocation());
            ps.setString(5, room.getRoomStatus());
            ps.setInt(6, room.getBlockID());
            ps.setBytes(7, room.getImagePath());
            ps.setString(8, room.getDescription());
            ps.setDate(9, room.getPostedDate());

            // Thêm các cột mới vào câu truy vấn
            ps.setInt(10, room.getIsElectricityFree());  // Cập nhật cột IsElectricityFree
            ps.setInt(11, room.getIsWaterFree());        // Cập nhật cột IsWaterFree
            ps.setInt(12, room.getIsWifiFree());         // Cập nhật cột IsWifiFree
            ps.setInt(13, room.getIsTrashFree());        // Cập nhật cột IsTrashFree
            ps.setInt(14, room.getRoomID());             // ID phòng để cập nhật

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteRoom(int id) {
        String query = "DELETE FROM Rooms WHERE roomID = ?";
        try (
                 Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Room> getRoomsByBlockId(int blockId) {
        List<Room> list = new ArrayList<>();
        String query = "SELECT * FROM Rooms WHERE blockID = ?";
        try (
                 Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, blockId);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Room(
                            rs.getInt("roomID"),
                            rs.getString("roomNumber"),
                            rs.getFloat("rentPrice"),
                            rs.getFloat("area"),
                            rs.getString("location"),
                            rs.getString("roomStatus"),
                            rs.getInt("blockID"),
                            rs.getBytes("imagePath"),
                            rs.getString("description"),
                            rs.getDate("postedDate")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Room> searchRooms(String keyword) {
        List<Room> list = new ArrayList<>();
        String query = "SELECT * FROM Rooms WHERE roomNumber LIKE ? OR location LIKE ? OR description LIKE ?";
        String searchPattern = "%" + keyword + "%";
        try (
                 Connection conn = new DBContext().getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Room(
                            rs.getInt("roomID"),
                            rs.getString("roomNumber"),
                            rs.getFloat("rentPrice"),
                            rs.getFloat("area"),
                            rs.getString("location"),
                            rs.getString("roomStatus"),
                            rs.getInt("blockID"),
                            rs.getBytes("imagePath"),
                            rs.getString("description"),
                            rs.getDate("postedDate")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     *
     * @return
     */
    // nay cua khanh
    public List<Room> getAvailableRooms() {
        List<Room> list = new ArrayList<>();
        String query = "SELECT * FROM Rooms WHERE roomStatus = 'Available'";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(query);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractRoom(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Room> filterRooms(String block, String status,
            Double minPrice, Double maxPrice,
            Double minArea, Double maxArea,
            int page, int pageSize) throws SQLException {
        List<Room> filteredRooms = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Rooms WHERE 1=1");
        List<Object> params = new ArrayList<>();

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

        sql.append(" ORDER BY roomID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filteredRooms.add(extractRoom(rs));
                }
            }
        }

        return filteredRooms;
    }

    public int countFilteredRooms(String block, String status,
            Double minPrice, Double maxPrice,
            Double minArea, Double maxArea) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Rooms WHERE 1=1");
        List<Object> params = new ArrayList<>();

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

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    public List<Room> getFeaturedRooms() throws SQLException {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT TOP 3 * FROM Rooms ORDER BY NEWID()";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractRoom(rs));
            }
        }

        return list;
    }

    public List<Room> getRoomsByBlock(String blockID) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM Rooms WHERE blockID = ?";
         try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, blockID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room r = new Room();
                r.setRoomID(rs.getInt("roomID"));
                r.setRoomNumber(rs.getString("roomNumber"));
                r.setBlockID(rs.getInt("blockID"));
                r.setRentPrice(rs.getDouble("rentPrice"));
                r.setArea(rs.getDouble("area"));
                r.setLocation(rs.getString("location"));
                r.setRoomStatus(rs.getString("roomStatus"));
                r.setImagePath(rs.getBytes("imagePath"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean isRoomNumberExists(String roomNumber) {
        String sql = "SELECT COUNT(*) FROM Rooms WHERE RoomNumber = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roomNumber);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isRoomNumberExistsForOther(String roomNumber, int id) {
        String sql = "SELECT COUNT(*) FROM Rooms WHERE roomNumber = ? AND roomID <> ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roomNumber);
            ps.setInt(2, id);
            try ( ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateRoomCount(int blockId) {
        String countSql = "SELECT COUNT(*) FROM Rooms WHERE blockID = ?";
        String updateSql = "UPDATE Blocks SET roomCount = ? WHERE blockID = ?";

        try ( Connection conn = getConnection();  PreparedStatement psCount = conn.prepareStatement(countSql)) {

            psCount.setInt(1, blockId);
            try ( ResultSet rs = psCount.executeQuery()) {
                if (rs.next()) {
                    int roomCount = rs.getInt(1);

                    try ( PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                        psUpdate.setInt(1, roomCount);
                        psUpdate.setInt(2, blockId);
                        psUpdate.executeUpdate();
                        System.out.println("Block " + blockId + " updated room count to " + roomCount);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Room> searchByRoomName(String keyword) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM Rooms WHERE RoomNumber LIKE ?";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractRoom(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Helper method để tránh lặp code khi tạo Room từ ResultSet
    private Room extractRoom(ResultSet rs) throws SQLException {
        return new Room(
                rs.getInt("RoomID"),
                rs.getString("RoomNumber"),
                rs.getFloat("RentPrice"),
                rs.getFloat("Area"),
                rs.getString("Location"),
                rs.getString("RoomStatus"),
                rs.getInt("BlockID"),
                rs.getBytes("ImagePath"),
                rs.getString("Description"),
                rs.getDate("PostedDate")
        );
    }

    public Map<Block, List<RoomStatusDTO>> getRoomStatusByBlock(String month) throws SQLException {
        Map<Block, List<RoomStatusDTO>> result = new LinkedHashMap<>();

        String sql = "DECLARE @month VARCHAR(7) = ? "
                + "SELECT b.BlockID, b.BlockName, r.RoomID, r.RoomNumber, "
                + "CASE WHEN COUNT(DISTINCT ur.UtilityTypeID) >= 2 THEN 1 ELSE 0 END AS FullyRecorded, "
                + "CASE WHEN EXISTS (SELECT 1 FROM Bills bi "
                + "JOIN Contracts ct ON ct.ContractID = bi.ContractID "
                + "WHERE ct.RoomID = r.RoomID AND FORMAT(bi.BillDate, 'yyyy-MM') = @month) THEN 1 ELSE 0 END AS IsClosed "
                + "FROM Rooms r "
                + "JOIN Blocks b ON r.BlockID = b.BlockID "
                + "LEFT JOIN Contracts c ON c.RoomID = r.RoomID AND c.ContractStatus = 'Active' "
                + "LEFT JOIN UtilityReadings ur ON ur.RoomID = r.RoomID AND FORMAT(ur.ReadingDate, 'yyyy-MM') = @month "
                + "GROUP BY b.BlockID, b.BlockName, r.RoomID, r.RoomNumber "
                + "ORDER BY b.BlockName, r.RoomNumber";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, month);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int blockId = rs.getInt("BlockID");
                    String blockName = rs.getString("BlockName");

                    Block block = new Block();
                    block.setBlockID(blockId);
                    block.setBlockName(blockName);

                    RoomStatusDTO room = new RoomStatusDTO();
                    room.setRoomId(rs.getInt("RoomID"));
                    room.setRoomName(rs.getString("RoomNumber"));
                    room.setFullyRecorded(rs.getInt("FullyRecorded") == 1);
                    room.setClosed(rs.getInt("IsClosed") == 1);

                    // Thêm vào map (không dùng lambda)
                    List<RoomStatusDTO> roomList = null;
                    if (result.containsKey(block)) {
                        roomList = result.get(block);
                    } else {
                        roomList = new ArrayList<>();
                        result.put(block, roomList);
                    }
                    roomList.add(room);
                }
            }
        }

        return result;
    }

    public Contract getActiveContractByRoomId(int roomId) {
        String sql = "SELECT * FROM Contracts WHERE RoomID = ? AND ContractStatus = 'Active'";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contract c = new Contract();
                    c.setContractId(rs.getInt("ContractID"));
                    c.setTenantId(rs.getInt("TenantID"));
                    c.setRoomId(rs.getInt("RoomID"));
                    c.setStartDate(rs.getDate("StartDate"));
                    c.setEndDate(rs.getDate("EndDate"));
                    c.setContractStatus(rs.getString("ContractStatus"));
                    c.setContractCreatedAt(rs.getTimestamp("ContractCreatedAt"));
                    return c;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public float getRoomPriceByRoomId(int roomId) {
        float rentPrice = 0;
        String sql = "SELECT RentPrice FROM Rooms WHERE RoomID = ?";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    rentPrice = rs.getFloat("RentPrice");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rentPrice;
    }

    public boolean existsBillForRoomAndMonth(int roomId, String month) {
        String sql = "SELECT 1 FROM Bills b "
                + "JOIN Contracts c ON b.ContractID = c.ContractID "
                + "WHERE c.RoomID = ? "
                + "AND FORMAT(b.BillDate, 'yyyy-MM') = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setString(2, month);
            try ( ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Đã có bill → true
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getAllLocations() {
        List<String> locations = new ArrayList<>();
        String sql = "SELECT DISTINCT location FROM Rooms WHERE location IS NOT NULL AND location <> ''";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    locations.add(rs.getString("location"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return locations;
    }

    public void updateRoomStatus(int roomId, String status) {
        String sql = "UPDATE Rooms SET RoomStatus = ? WHERE RoomID = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, roomId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT TOP (1000) [RoomID], [RoomNumber], [RentPrice], [Area], [Location], [RoomStatus], "
                + "[BlockID], [ImagePath], [Description], [PostedDate], [IsElectricityFree], [IsWaterFree], "
                + "[IsWifiFree], [IsTrashFree] FROM [RentalManagement_New].[dbo].[Rooms]";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Room room = new Room();
                room.setRoomID(rs.getInt("RoomID"));
                room.setRoomNumber(rs.getString("RoomNumber"));
                room.setRentPrice(rs.getDouble("RentPrice"));
                room.setArea(rs.getDouble("Area"));
                room.setLocation(rs.getString("Location"));
                room.setRoomStatus(rs.getString("RoomStatus"));
                room.setBlockID(rs.getInt("BlockID"));
                room.setImagePath(rs.getBytes("ImagePath"));
                room.setDescription(rs.getString("Description"));
                room.setPostedDate(rs.getDate("PostedDate"));
                room.setIsElectricityFree(rs.getInt("IsElectricityFree"));
                room.setIsWaterFree(rs.getInt("IsWaterFree"));
                room.setIsWifiFree(rs.getInt("IsWifiFree"));
                room.setIsTrashFree(rs.getInt("IsTrashFree"));

                list.add(room);
                System.out.println(room.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Room> getRoomsWithActiveContractsAndReadings(int blockId, String month) {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT DISTINCT r.*, c.ContractID "
                + "FROM Rooms r "
                + "JOIN Contracts c ON r.RoomID = c.RoomID "
                + "WHERE r.BlockID = ? AND c.ContractStatus = 'Active'";

        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, blockId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room room = new Room();
                room.setRoomID(rs.getInt("RoomID"));
                room.setRoomNumber(rs.getString("RoomNumber"));
                room.setRentPrice(rs.getDouble("RentPrice"));
                room.setBlockID(rs.getInt("BlockID"));
                room.setActiveContractCode(rs.getString("ContractID"));

                // ✅ Gán thêm 4 cột tiện ích miễn phí
                room.setIsElectricityFree(rs.getInt("IsElectricityFree"));
                room.setIsWaterFree(rs.getInt("IsWaterFree"));
                room.setIsWifiFree(rs.getInt("IsWifiFree"));
                room.setIsTrashFree(rs.getInt("IsTrashFree"));

                list.add(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
