package dao;

import model.Block;
import utils.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BlockDAO {
    private Connection conn;

    public BlockDAO() {
        try {
            conn = new DBContext().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy tất cả block
    public List<Block> getAllBlocks() {
        List<Block> list = new ArrayList<>();
        String sql = "SELECT BlockID, BlockName, MaxRoom, RoomCount, BlockStatus FROM Blocks";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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

    // Lấy 1 block theo ID
    public Block getBlockById(int id) {
        String sql = "SELECT BlockID, BlockName, MaxRoom, RoomCount, BlockStatus FROM Blocks WHERE BlockID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
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

    // Thêm mới block
    public void addBlock(Block block) {
        String sql = "INSERT INTO Blocks (BlockName, MaxRoom, RoomCount, BlockStatus) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, block.getBlockName());
            ps.setInt(2, block.getMaxRoom());
            ps.setInt(3, block.getRoomCount());
            ps.setString(4, block.getBlockStatus());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cập nhật block
    public void updateBlock(Block block) {
        String sql = "UPDATE Blocks SET BlockName = ?, MaxRoom = ?, RoomCount = ?, BlockStatus = ? WHERE BlockID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, block.getBlockName());
            ps.setInt(2, block.getMaxRoom());
            ps.setInt(3, block.getRoomCount());
            ps.setString(4, block.getBlockStatus());
            ps.setInt(5, block.getBlockID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Xóa block
    public void deleteBlock(int id) {
        String sql = "DELETE FROM Blocks WHERE BlockID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
