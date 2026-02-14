package dao.impl;

import config.DatabaseConnection;
import dao.ReportDAO;
import model.Category;
import model.Order;
import model.OrderItem;
import model.Product;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportDAOImpl implements ReportDAO {

    @Override
    public List<Order> findOrdersBetween(LocalDate start, LocalDate end) throws Exception {

        var startDT = start.atStartOfDay();
        var endDT = end.plusDays(1).atStartOfDay();

        final String SQL = """
            SELECT 
                o.id AS order_id,
                o.order_code,
                o.order_datetime,
                o.subtotal,
                o.discount,
                o.tax,
                o.total,
                o.payment_method,

                u.id AS cashier_id,
                u.username AS cashier_username,

                oi.id AS item_id,
                oi.quantity,
                oi.unit_price,
                oi.line_total,
                oi.customization,
                oi.customization_price,

                p.id AS product_id,
                p.name AS product_name,
                p.price AS product_price,

                c.id AS category_id,
                c.name AS category_name

            FROM orders o
            JOIN users u ON o.cashier_id = u.id
            LEFT JOIN order_items oi ON oi.order_id = o.id
            LEFT JOIN products p ON oi.product_id = p.id
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE o.order_datetime >= ?
              AND o.order_datetime < ?
            ORDER BY o.order_datetime DESC, o.id, oi.id
        """;

        Map<Integer, Order> map = new LinkedHashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setTimestamp(1, java.sql.Timestamp.valueOf(startDT));
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(endDT));

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    int oid = rs.getInt("order_id");
                    Order order = map.get(oid);

                    if (order == null) {
                        order = new Order();
                        order.setId(oid);
                        order.setOrderCode(rs.getString("order_code"));
                        order.setOrderDateTime(rs.getTimestamp("order_datetime").toLocalDateTime());
                        order.setSubtotal(rs.getDouble("subtotal"));
                        order.setDiscount(rs.getDouble("discount"));
                        order.setTax(rs.getDouble("tax"));
                        order.setTotal(rs.getDouble("total"));
                        order.setPaymentMethod(rs.getString("payment_method"));

                        User cashier = new User();
                        cashier.setId(rs.getInt("cashier_id"));
                        cashier.setUsername(rs.getString("cashier_username"));
                        order.setCashier(cashier);

                        map.put(oid, order);
                    }

                    int itemId = rs.getInt("item_id");
                    if (!rs.wasNull()) {

                        OrderItem item = new OrderItem();
                        item.setId(itemId);
                        item.setQuantity(rs.getInt("quantity"));
                        item.setUnitPrice(rs.getDouble("unit_price"));
                        item.setLineTotal(rs.getDouble("line_total"));
                        item.setCustomization(rs.getString("customization"));
                        item.setCustomizationPrice(rs.getDouble("customization_price"));

                        int pid = rs.getInt("product_id");
                        if (!rs.wasNull()) {
                            Product p = new Product();
                            p.setId(pid);
                            p.setName(rs.getString("product_name"));
                            p.setPrice(rs.getDouble("product_price"));

                            Category cat = new Category(
                                    rs.getInt("category_id"),
                                    rs.getString("category_name")
                            );
                            p.setCategory(cat);

                            item.setProduct(p);
                        }

                        // IMPORTANT FIX:
                        item.markLoadedFromDB();

                        order.getItems().add(item);
                    }
                }
            }
        }

        return new ArrayList<>(map.values());
    }

    @Override
    public List<OrderItem> findOrderItemsBetween(LocalDate start, LocalDate end) throws Exception {

        var startDT = start.atStartOfDay();
        var endDT = end.plusDays(1).atStartOfDay();

        final String SQL = """
            SELECT 
                oi.id,
                oi.order_id,
                oi.product_id,
                oi.quantity,
                oi.unit_price,
                oi.line_total,
                oi.customization,
                oi.customization_price,
                p.name AS product_name,
                p.category_id,
                c.name AS category_name
            FROM order_items oi
            JOIN products p ON oi.product_id = p.id
            JOIN categories c ON p.category_id = c.id
            JOIN orders o ON oi.order_id = o.id
            WHERE o.order_datetime >= ?
              AND o.order_datetime < ?
        """;

        List<OrderItem> items = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setTimestamp(1, java.sql.Timestamp.valueOf(startDT));
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(endDT));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Product p = new Product();
                    p.setId(rs.getInt("product_id"));
                    p.setName(rs.getString("product_name"));

                    Category cat = new Category(
                            rs.getInt("category_id"),
                            rs.getString("category_name")
                    );
                    p.setCategory(cat);

                    OrderItem item = new OrderItem();
                    item.setId(rs.getInt("id"));
                    item.setProduct(p);
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getDouble("unit_price"));
                    item.setLineTotal(rs.getDouble("line_total"));
                    item.setCustomization(rs.getString("customization"));
                    item.setCustomizationPrice(rs.getDouble("customization_price"));

                    // IMPORTANT FIX:
                    item.markLoadedFromDB();

                    items.add(item);
                }
            }
        }

        return items;
    }
}
