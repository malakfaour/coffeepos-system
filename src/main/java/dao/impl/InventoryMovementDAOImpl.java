package dao.impl;

import config.DatabaseConnection;
import dao.InventoryMovementDAO;
import model.InventoryMovement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryMovementDAOImpl implements InventoryMovementDAO {

    @Override
    public void record(InventoryMovement m) throws Exception {
        final String SQL = """
            INSERT INTO inventory_movements (product_id, change_qty, reason, ref_id)
            VALUES (?,?,?,?)
        """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setInt(1, m.getProductId());
            ps.setInt(2, m.getChangeQty());
            ps.setString(3, m.getReason());
            if (m.getReferenceId() == null) ps.setNull(4, Types.INTEGER);
            else ps.setInt(4, m.getReferenceId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<InventoryMovement> lowStock(int threshold) throws Exception {
        final String SQL = """
            SELECT p.id AS product_id, p.name, p.stock_qty
              FROM products p
             WHERE p.stock_qty <= ?
             ORDER BY p.stock_qty ASC
        """;
        List<InventoryMovement> out = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setInt(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                
                    InventoryMovement m = new InventoryMovement(
                            rs.getInt("product_id"),
                            0,
                            "LOW_STOCK",
                            null
                    );
                    out.add(m);
                }
            }
        }
        return out;
    }
}
