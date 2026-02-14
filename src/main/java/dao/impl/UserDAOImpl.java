package dao.impl;

import dao.UserDAO;
import config.DatabaseConnection;
import model.Role;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAOImpl implements UserDAO {

    @Override
    public User findByUsername(String username) throws Exception {
        final String SQL = """
            SELECT u.id, u.username, u.password_hash, u.full_name, u.is_active,
                   r.id AS role_id, r.name AS role_name
              FROM users u
              JOIN roles r ON u.role_id = r.id
             WHERE u.username = ?
             LIMIT 1
        """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setFullName(rs.getString("full_name"));
                u.setActive(rs.getBoolean("is_active"));
                u.setRole(new Role(rs.getInt("role_id"), rs.getString("role_name")));
                return u;
            }
        }
    }

    @Override
    public void create(User user) throws Exception {
        final String SQL = """
            INSERT INTO users(username, password_hash, full_name, role_id, is_active)
            VALUES (?,?,?,?,?)
        """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setInt(4, user.getRole().getId());
            ps.setBoolean(5, user.isActive());
            ps.executeUpdate();
        }
    }
}
