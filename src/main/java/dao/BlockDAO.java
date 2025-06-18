package dao;

import model.Block;
import utils.DBContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BlockDAO {

    private final DBContext dbContext = new DBContext();

    // Get all blocks
   public List<Block> getAllBlocks() throws SQLException {
    List<Block> blocks = new ArrayList<>();
    String query = "SELECT * FROM Blocks ORDER BY BlockName";

    try (Connection conn = dbContext.getConnection();
         PreparedStatement ps = conn.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Block block = new Block();
            block.setBlockID(rs.getInt("BlockID"));
            block.setBlockName(rs.getString("BlockName"));
            block.setRoomCount(rs.getInt("RoomCount"));
            block.setMaxRoom(rs.getInt("MaxRoom"));
            block.setBlockStatus(rs.getString("BlockStatus"));

            // ✅ Thêm vào list
            blocks.add(block);
        }
    }

    return blocks;
}


    // Get block by ID
    public Block getBlockByID(int blockID) throws SQLException {
        String query = "SELECT * FROM Blocks WHERE BlockID = ?";
        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, blockID);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Block block = new Block();
                    block.setBlockID(rs.getInt("BlockID"));
                    block.setBlockName(rs.getString("BlockName"));
                    block.setRoomCount(rs.getInt("RoomCount"));
                    block.setMaxRoom(rs.getInt("MaxRoom"));
                    block.setBlockStatus(rs.getString("BlockStatus"));
                    return block;
                }
            }
        }
        return null;
    }

    // Optional: Create new block
    public boolean createBlock(Block block) throws SQLException {
       String query = "INSERT INTO Blocks (BlockName, RoomCount, MaxRoom, BlockStatus) VALUES (?, 0, ?, 'Available')";

        try ( Connection conn = dbContext.getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, block.getBlockName());
            ps.setInt(2, block.getMaxRoom());
            return ps.executeUpdate() > 0;
        }
    }

    public Block getBlockById(int blockID) throws SQLException {
        return getBlockByID(blockID); // hoặc gọi lại hàm đã viết sẵn
    }

}
