package dao.impl;

import config.DatabaseConnection;
import dao.CashRegisterClosingDAO;
import model.CashRegisterClosing;
import model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CashRegisterClosingDAOImpl implements CashRegisterClosingDAO {

    @Override
    public void create(CashRegisterClosing closing) throws Exception {
        final String SQL = """
            INSERT INTO cash_register_closing
              (cashier_id, closing_datetime, expected_total, counted_total, difference)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, closing.getCashier().getId());
            ps.setTimestamp(2, Timestamp.valueOf(closing.getClosingDateTime()));
            ps.setDouble(3, closing.getExpectedTotal());
            ps.setDouble(4, closing.getCountedTotal());
            ps.setDouble(5, closing.getDifference());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    closing.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public List<CashRegisterClosing> findByCashierAndDate(int cashierId, LocalDate date) throws Exception {
        final String SQL = """
            SELECT id, cashier_id, closing_datetime, expected_total, counted_total, difference
            FROM cash_register_closing
            WHERE cashier_id = ?
              AND DATE(closing_datetime) = ?
            ORDER BY closing_datetime DESC
        """;

        List<CashRegisterClosing> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setInt(1, cashierId);
            ps.setDate(2, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CashRegisterClosing c = new CashRegisterClosing();

                    c.setId(rs.getInt("id"));

                    User u = new User();
                    u.setId(rs.getInt("cashier_id"));
                    c.setCashier(u);

                    c.setClosingDateTime(rs.getTimestamp("closing_datetime").toLocalDateTime());
                    c.setExpectedTotal(rs.getDouble("expected_total"));
                    c.setCountedTotal(rs.getDouble("counted_total"));
                    c.setDifference(rs.getDouble("difference"));

                    list.add(c);
                }
            }
        }
        return list;
    }

    @Override
    public List<CashRegisterClosing> findAll() throws Exception {
        final String SQL = """
            SELECT id, cashier_id, closing_datetime, expected_total, counted_total, difference
            FROM cash_register_closing
            ORDER BY closing_datetime DESC
        """;

        List<CashRegisterClosing> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CashRegisterClosing c = new CashRegisterClosing();

                c.setId(rs.getInt("id"));

                User u = new User();
                u.setId(rs.getInt("cashier_id"));
                c.setCashier(u);

                c.setClosingDateTime(rs.getTimestamp("closing_datetime").toLocalDateTime());
                c.setExpectedTotal(rs.getDouble("expected_total"));
                c.setCountedTotal(rs.getDouble("counted_total"));
                c.setDifference(rs.getDouble("difference"));

                list.add(c);
            }
        }
        return list;
    }
}
