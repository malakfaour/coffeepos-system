package dao.impl;

import config.DatabaseConnection;
import dao.OrderDAO;
import model.Order;
import model.OrderItem;
import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {

    @Override
    public int save(Order order, List<OrderItem> items) throws Exception {
        final String INSERT_ORDER = """
            INSERT INTO orders(order_code, cashier_id, order_datetime, subtotal, discount, tax, total, payment_method)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        // Added customization and customization_price
        final String INSERT_ITEM = """
            INSERT INTO order_items(order_id, product_id, quantity, unit_price, line_total, customization, customization_price)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psItem = null;
        ResultSet keys = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            psOrder = conn.prepareStatement(INSERT_ORDER, Statement.RETURN_GENERATED_KEYS);
            psOrder.setString(1, order.getOrderCode());
            psOrder.setInt(2, order.getCashier().getId());
            psOrder.setTimestamp(3, Timestamp.valueOf(order.getOrderDateTime()));
            psOrder.setDouble(4, order.getSubtotal());
            psOrder.setDouble(5, order.getDiscount());
            psOrder.setDouble(6, order.getTax());
            psOrder.setDouble(7, order.getTotal());
            psOrder.setString(8, order.getPaymentMethod());
            psOrder.executeUpdate();

            keys = psOrder.getGeneratedKeys();
            if (keys.next()) {
                order.setId(keys.getInt(1));
            } else {
                throw new IllegalStateException("Could not retrieve generated order ID");
            }

            psItem = conn.prepareStatement(INSERT_ITEM);
            for (OrderItem it : items) {
                psItem.setInt(1, order.getId());
                psItem.setInt(2, it.getProduct().getId());
                psItem.setInt(3, it.getQuantity());
                psItem.setDouble(4, it.getUnitPrice());
                psItem.setDouble(5, it.getLineTotal());
                psItem.setString(6, it.getCustomization());
                psItem.setDouble(7, it.getCustomizationPrice());
                psItem.addBatch();
            }
            psItem.executeBatch();

            conn.commit();
            return order.getId();

        } catch (Exception ex) {
            if (conn != null) conn.rollback();
            throw ex;
        } finally {
            if (keys != null) keys.close();
            if (psItem != null) psItem.close();
            if (psOrder != null) psOrder.close();
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    @Override
    public Order findById(int id) throws Exception {
        final String SQL = """
            SELECT id, order_code, cashier_id, order_datetime, total
            FROM orders WHERE id=?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Order o = new Order();
                o.setId(rs.getInt("id"));
                o.setOrderCode(rs.getString("order_code"));
                o.setOrderDateTime(rs.getTimestamp("order_datetime").toLocalDateTime());
                o.setTotal(rs.getDouble("total"));
                return o;
            }
        }
    }
    public List<OrderItem> findItemsByOrderId(int orderId) throws Exception {
        final String SQL = """
            SELECT oi.id, oi.product_id, p.name, oi.quantity, oi.unit_price,
                   oi.line_total, oi.customization, oi.customization_price
            FROM order_items oi
            JOIN products p ON oi.product_id = p.id
            WHERE oi.order_id = ?
        """;

        List<OrderItem> items = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("product_id"));
                    product.setName(rs.getString("name"));

                    OrderItem item = new OrderItem();
                    item.setId(rs.getInt("id"));
                    item.setProduct(product);
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getDouble("unit_price"));
                    item.setLineTotal(rs.getDouble("line_total"));
                    item.setCustomization(rs.getString("customization"));
                    item.setCustomizationPrice(rs.getDouble("customization_price"));

                    items.add(item);
                }
            }
        }
        return items;
    }

}
