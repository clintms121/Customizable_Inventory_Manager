package mainPackage;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    public static void main(String[] args) {
        String URL = "jdbc:mysql://localhost:3306/inventory_manager";
        String username = "root";
        String password = "Owlee101#";

        System.out.println("Connecting to database...");

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded");

            // Establish the connection
            try (Connection conn = DriverManager.getConnection(URL, username, password)) {
                System.out.println("Connected to the database");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to connect to the database");
            }

        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find the driver in the classpath!", e);
        }
    }
}

