package dao.impl;

import config.DatabaseConnection;
import dao.CategoryDAO;
import model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAOImpl implements CategoryDAO {

    @Override
    public void create(Category category) throws Exception {
        final String SQL = "INSERT INTO categories(name) VALUES (?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setString(1, category.getName());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Category category) throws Exception {
        final String SQL = "UPDATE categories SET name=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setString(1, category.getName());
            ps.setInt(2, category.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        final String SQL = "DELETE FROM categories WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Category findById(int id) throws Exception {
        final String SQL = "SELECT id, name FROM categories WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Category(rs.getInt("id"), rs.getString("name"));
            }
        }
        return null;
    }

    @Override
    public List<Category> findAll() throws Exception {
        final String SQL = "SELECT id, name FROM categories ORDER BY name";
        List<Category> out = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(SQL)) {
            while (rs.next()) {
                out.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        }
        return out;
    }
}
