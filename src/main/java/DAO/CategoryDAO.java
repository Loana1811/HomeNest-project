package dao;

import model.Category;
import utils.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private Connection conn;

    public CategoryDAO() {
        try {
            conn = new DBContext().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy tất cả category
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT CategoryID, CategoryName, Description FROM Categories";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Category(
                    rs.getInt("CategoryID"),
                    rs.getString("CategoryName"),
                    rs.getString("Description")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy 1 category theo ID
    public Category getCategoryById(int id) {
        String sql = "SELECT CategoryID, CategoryName, Description FROM Categories WHERE CategoryID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Category(
                        rs.getInt("CategoryID"),
                        rs.getString("CategoryName"),
                        rs.getString("Description")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm mới category
    public void addCategory(Category c) {
        String sql = "INSERT INTO Categories (CategoryName, Description) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCategoriesName());
            ps.setString(2, c.getDescription());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cập nhật category
    public void updateCategory(Category c) {
        String sql = "UPDATE Categories SET CategoryName = ?, Description = ? WHERE CategoryID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCategoriesName());
            ps.setString(2, c.getDescription());
            ps.setInt(3, c.getCategoriesID());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Xóa category
    public void deleteCategory(int id) {
        String sql = "DELETE FROM Categories WHERE CategoryID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
