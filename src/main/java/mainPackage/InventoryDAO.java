package mainPackage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class InventoryDAO {
    public static ObservableList<InventoryItem> loadInventory(int userId) throws SQLException {
        ObservableList<InventoryItem> items = FXCollections.observableArrayList();
        String sql = "SELECT * FROM inventory WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new InventoryItem(
                            rs.getString("name"),
                            rs.getString("sku"),
                            rs.getString("category"),
                            rs.getInt("quantity"),
                            rs.getDouble("price"),
                            rs.getString("status")
                    ));
                }
            }
        }
        return items;
    }

    public static boolean addItem(int userId, InventoryItem item) throws SQLException {
        String sql = "INSERT INTO inventory (user_id, name, sku, category, quantity, price, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, item.getName());
            stmt.setString(3, item.getSku());
            stmt.setString(4, item.getCategory());
            stmt.setInt(5, item.getQuantity());
            stmt.setDouble(6, item.getPrice());
            stmt.setString(7, item.getStatus());

            int result = stmt.executeUpdate();
            logActivity(userId, "ADD", -1, "Added item: " + item.getName());
            return result > 0;
        }
    }

    public static boolean updateItem(int userId, String originalSku, InventoryItem item) throws SQLException {
        String sql = "UPDATE inventory SET name = ?, sku = ?, category = ?, quantity = ?, " +
                "price = ?, status = ? WHERE user_id = ? AND sku = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getName());
            stmt.setString(2, item.getSku());
            stmt.setString(3, item.getCategory());
            stmt.setInt(4, item.getQuantity());
            stmt.setDouble(5, item.getPrice());
            stmt.setString(6, item.getStatus());
            stmt.setInt(7, userId);
            stmt.setString(8, originalSku);

            int result = stmt.executeUpdate();
            logActivity(userId, "UPDATE", -1, "Updated item: " + item.getName());
            return result > 0;
        }
    }

    public static boolean deleteItem(int userId, String sku) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);  // Start transaction

            // First, get the item_id
            String getItemIdSql = "SELECT id FROM inventory WHERE user_id = ? AND sku = ?";
            int itemId;

            try (PreparedStatement stmt = conn.prepareStatement(getItemIdSql)) {
                stmt.setInt(1, userId);
                stmt.setString(2, sku);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    return false;
                }
                itemId = rs.getInt("id");
            }

            // Log the activity before deletion
            logActivity(userId, "DELETE", itemId, "Deleted item with SKU: " + sku);

            // Then delete the inventory item
            String deleteItemSql = "DELETE FROM inventory WHERE user_id = ? AND sku = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteItemSql)) {
                stmt.setInt(1, userId);
                stmt.setString(2, sku);

                int result = stmt.executeUpdate();
                if (result > 0) {
                    conn.commit();  // Commit transaction
                    return true;
                } else {
                    conn.rollback();  // Rollback if delete failed
                    return false;
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);  // Reset auto-commit
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void logActivity(int userId, String actionType, int itemId, String description) throws SQLException {
        String sql = "INSERT INTO activity_log (user_id, action_type, item_id, description) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, actionType);
            stmt.setInt(3, itemId);
            stmt.setString(4, description);

            stmt.executeUpdate();
        }
    }


}