package dao.impl;

import config.DatabaseConnection;
import dao.PaymentDAO;
import model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class PaymentDAOImpl implements PaymentDAO {

    @Override
    public void save(Payment payment) throws Exception {
        final String SQL = """
            INSERT INTO payments (sale_id, method, paid_amount)
            VALUES (?,?,?)
        """;

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL)) {

            ps.setInt(1, payment.getSaleId());
            ps.setString(2, payment.getMethod());
            ps.setDouble(3, payment.getPaidAmount());

            ps.executeUpdate();
        }
    }
}
