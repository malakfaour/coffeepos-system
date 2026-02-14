package app;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestDB {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/coffee_pos";
        String user = "root";
        String pass = "Password"; 

        try {
            System.out.println("→ Loading driver...");
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("→ Connecting to database...");
            Connection c = DriverManager.getConnection(url, user, pass);

            System.out.println("✅ SUCCESS: Connected to database!");
            c.close();
        } catch (Exception e) {
            System.out.println("❌ FAILED: " + e.getMessage());
            e.printStackTrace(); 
        }
    }
}
