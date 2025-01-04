package mainPackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/inventory_manager";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Owlee101#";

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found.", e);
            }
        }
        return connection;
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            // Create users table
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password_hash VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;

            // Create inventory table
            String createInventoryTable = """
                CREATE TABLE IF NOT EXISTS inventory (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    user_id INT,
                    name VARCHAR(100) NOT NULL,
                    sku VARCHAR(50) NOT NULL,
                    category VARCHAR(50) NOT NULL,
                    quantity INT NOT NULL,
                    price DECIMAL(10,2) NOT NULL,
                    status VARCHAR(20) NOT NULL,
                    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """;

            // Create activity log table
            String createLogTable = """
                CREATE TABLE IF NOT EXISTS activity_log (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    user_id INT,
                    action_type VARCHAR(50) NOT NULL,
                    item_id INT,
                    description TEXT,
                    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (item_id) REFERENCES inventory(id)
                )
            """;

            try (PreparedStatement stmt1 = conn.prepareStatement(createUsersTable);
                 PreparedStatement stmt2 = conn.prepareStatement(createInventoryTable);
                 PreparedStatement stmt3 = conn.prepareStatement(createLogTable)) {

                stmt1.executeUpdate();
                stmt2.executeUpdate();
                stmt3.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
