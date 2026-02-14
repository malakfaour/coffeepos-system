package dao.impl;

import config.DatabaseConnection;
import dao.ProductDAO;
import model.Category;
import model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {

    @Override
    public List<Product> findAll() throws Exception {
        final String SQL = """
            SELECT p.id, p.sku, p.name, p.price, p.stock_qty, p.is_active,
                   c.id AS cat_id, c.name AS cat_name
            FROM products p
            JOIN categories c ON p.category_id = c.id
            ORDER BY p.name
        """;
        List<Product> out = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SQL)) {

            while (rs.next()) {
                Category c = new Category(rs.getInt("cat_id"), rs.getString("cat_name"));
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setSku(rs.getString("sku"));
                p.setName(rs.getString("name"));
                p.setCategory(c);
                p.setPrice(rs.getDouble("price"));
                p.setStockQty(rs.getInt("stock_qty"));
                p.setActive(rs.getBoolean("is_active"));
                out.add(p);
            }
        }
        return out;
    }

    @Override
    public Product findBySku(String sku) throws Exception {
        final String SQL = """
            SELECT p.id, p.sku, p.name, p.price, p.stock_qty, p.is_active,
                   c.id AS cat_id, c.name AS cat_name
            FROM products p
            JOIN categories c ON p.category_id = c.id
            WHERE p.sku = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setString(1, sku);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category c = new Category(rs.getInt("cat_id"), rs.getString("cat_name"));
                    Product p = new Product();
                    p.setId(rs.getInt("id"));
                    p.setSku(rs.getString("sku"));
                    p.setName(rs.getString("name"));
                    p.setCategory(c);
                    p.setPrice(rs.getDouble("price"));
                    p.setStockQty(rs.getInt("stock_qty"));
                    p.setActive(rs.getBoolean("is_active"));
                    return p;
                }
            }
        }
        return null;
    }

    @Override
    public void create(Product p) throws Exception {
    	final String SQL = """
    		    INSERT INTO products (sku, name, category_id, price, stock_qty, is_active, barcode)
    		    VALUES (?, ?, ?, ?, ?, ?, ?)
    		""";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setString(1, p.getSku());
            ps.setString(2, p.getName());
            ps.setInt(3, p.getCategory().getId());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getStockQty());
            ps.setBoolean(6, p.isActive());
           
            ps.setString(7, p.getBarcode());
            ps.executeUpdate();

        }
    }
    @Override
    public Product findByBarcode(String barcode) throws Exception {
        final String SQL = """
            SELECT p.id, p.sku, p.name, p.price, p.stock_qty, p.is_active, p.barcode,
                   c.id AS cat_id, c.name AS cat_name
            FROM products p
            JOIN categories c ON p.category_id = c.id
            WHERE p.barcode = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setString(1, barcode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category c = new Category(rs.getInt("cat_id"), rs.getString("cat_name"));
                    Product p = new Product();
                    p.setId(rs.getInt("id"));
                    p.setSku(rs.getString("sku"));
                    p.setName(rs.getString("name"));
                    p.setCategory(c);
                    p.setPrice(rs.getDouble("price"));
                    p.setStockQty(rs.getInt("stock_qty"));
                    p.setActive(rs.getBoolean("is_active"));
                    p.setBarcode(rs.getString("barcode"));
                    return p;
                }
            }
        }
        return null;
    }

    @Override
    public void update(Product p) throws Exception {
    	final String SQL = """
    		    UPDATE products
    		       SET sku=?, name=?, category_id=?, price=?, stock_qty=?, is_active=?, barcode=?
    		     WHERE id=?
    		""";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setString(1, p.getSku());
            ps.setString(2, p.getName());
            ps.setInt(3, p.getCategory().getId());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getStockQty());
            ps.setBoolean(6, p.isActive());
            ps.setInt(7, p.getId());
      
            ps.setString(7, p.getBarcode());
            ps.setInt(8, p.getId());
            ps.executeUpdate();

        }
    }

    @Override
    public void delete(int id) throws Exception {
        final String SQL = "DELETE FROM products WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Product> search(String keyword) throws Exception {
        final String SQL = """
            SELECT p.id, p.sku, p.name, p.price, p.stock_qty, p.is_active,
                   c.id AS cat_id, c.name AS cat_name
            FROM products p
            JOIN categories c ON p.category_id = c.id
            WHERE p.name LIKE ? OR p.sku LIKE ?
            ORDER BY p.name
        """;
        List<Product> out = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Category c = new Category(rs.getInt("cat_id"), rs.getString("cat_name"));
                    Product p = new Product();
                    p.setId(rs.getInt("id"));
                    p.setSku(rs.getString("sku"));
                    p.setName(rs.getString("name"));
                    p.setCategory(c);
                    p.setPrice(rs.getDouble("price"));
                    p.setStockQty(rs.getInt("stock_qty"));
                    p.setActive(rs.getBoolean("is_active"));
                    out.add(p);
                }
            }
        }
        return out;
    }

    @Override
    public void updateStock(int productId, int qtySold) throws Exception {
        final String SQL = "UPDATE products SET stock_qty = stock_qty - ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setInt(1, qtySold);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }
}
