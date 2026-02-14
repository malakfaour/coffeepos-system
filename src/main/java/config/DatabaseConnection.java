package config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/coffee_pos";

    private static final String USER = "root";
    private static final String PASSWORD = "Password"; 

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database connected successfully!");
            return conn;

        } catch (Exception e) {
            System.out.println("❌ Database connection FAILED!");
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
    }
}