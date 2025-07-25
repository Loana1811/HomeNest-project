package dao;

import model.Block;
import utils.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BlockDAO extends DBContext {

    public BlockDAO() {
        super(); // Gọi constructor từ DBContext
    }

    // Lấy tất cả block
    public List<Block> getAllBlocks() {
        List<Block> list = new ArrayList<>();
        String sql = "SELECT BlockID, BlockName, MaxRoom, RoomCount, BlockStatus FROM Blocks";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Block(
                        rs.getInt("BlockID"),
                        rs.getString("BlockName"),
                        rs.getInt("MaxRoom"),
                        rs.getInt("RoomCount"),
                        rs.getString("BlockStatus")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy block theo ID
    public Block getBlockById(int id) {
        String sql = "SELECT BlockID, BlockName, MaxRoom, RoomCount, BlockStatus FROM Blocks WHERE BlockID = ?";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Block(
                            rs.getInt("BlockID"),
                            rs.getString("BlockName"),
                            rs.getInt("MaxRoom"),
                            rs.getInt("RoomCount"),
                            rs.getString("BlockStatus")
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addBlock(Block block) {
        if (isBlockNameExists(block.getBlockName())) {
            return false;
        }

        String sql = "INSERT INTO Blocks (BlockName, MaxRoom, RoomCount, BlockStatus) VALUES (?, ?, ?, ?)";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, block.getBlockName());
            ps.setInt(2, block.getMaxRooms());
            ps.setInt(3, block.getRoomCount());
            ps.setString(4, block.getBlockStatus());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật block
    public void updateBlock(Block block) {
        String sql = "UPDATE Blocks SET BlockName = ?, MaxRoom = ?, BlockStatus = ? WHERE BlockID = ?";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, block.getBlockName());
            ps.setInt(2, block.getMaxRooms());
            ps.setString(3, block.getBlockStatus());
            ps.setInt(4, block.getBlockID());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Xóa block
    public void deleteBlock(int id) {
        String sql = "DELETE FROM Blocks WHERE BlockID = ?";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isBlockNameExists(String blockName) {
        String sql = "SELECT 1 FROM Blocks WHERE BlockName = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, blockName);
            try ( ResultSet rs = ps.executeQuery()) {
                return rs.next(); // có kết quả => tồn tại
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật số lượng phòng
    public void updateRoomCount(int blockId) {
        String sql = "UPDATE Blocks SET RoomCount = (SELECT COUNT(*) FROM Rooms WHERE BlockID = ?) WHERE BlockID = ?";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, blockId);
            ps.setInt(2, blockId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
