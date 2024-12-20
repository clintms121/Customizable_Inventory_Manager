package org.teammanager.customizable_inventory_manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/inventory_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Owlee101#";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            //load jdbc driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            //establish connection
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Succesfully connected to database");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to connect to database");
        }
        return connection;
    }

    public static void testConnection() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT 1")) {
            if (resultSet.next()) {
                System.out.println("Test query succesful, database is working");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
