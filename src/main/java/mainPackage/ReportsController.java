package mainPackage;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {
    @FXML private Button exportButton;
    @FXML private TableView<CategoryReport> reportsTable;
    @FXML private TableColumn<CategoryReport, String> categoryColumn;
    @FXML private TableColumn<CategoryReport, Integer> totalItemsColumn;
    @FXML private TableColumn<CategoryReport, Double> averagePriceColumn;
    @FXML private TableColumn<CategoryReport, Double> totalValueColumn;
    @FXML private TableColumn<CategoryReport, Integer> lowStockColumn;

    private ObservableList<CategoryReport> reportItems = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();

        // Initialize the export button with its handler
        if (exportButton != null) {
            exportButton.setOnAction(event -> {
                if (reportsTable != null && !reportsTable.getItems().isEmpty()) {
                    exportReport();
                } else {
                    showAlert("Export Error", "No data available to export.");
                }
            });
        }
    }

    private void exportReport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Inventory Report");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showSaveDialog(reportsTable.getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                // Write header
                writer.println("Category,Total Items,Average Price,Total Value,Low Stock Items");

                // Write data
                for (CategoryReport report : reportsTable.getItems()) {
                    writer.printf("%s,%d,%.2f,%.2f,%d%n",
                            report.getCategory(),
                            report.getTotalItems(),
                            report.getAveragePrice(),
                            report.getTotalValue(),
                            report.getLowStockItems()
                    );
                }

                showAlert("Success", "Report exported successfully!");
            } catch (Exception e) {
                showAlert("Export Error", "Failed to export report: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void setupTable() {
        // Prevent column reordering
        reportsTable.setTableMenuButtonVisible(false);

        // Configure columns
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setReorderable(false);

        totalItemsColumn.setCellValueFactory(new PropertyValueFactory<>("totalItems"));
        totalItemsColumn.setReorderable(false);

        averagePriceColumn.setCellValueFactory(new PropertyValueFactory<>("averagePrice"));
        averagePriceColumn.setReorderable(false);

        totalValueColumn.setCellValueFactory(new PropertyValueFactory<>("totalValue"));
        totalValueColumn.setReorderable(false);

        lowStockColumn.setCellValueFactory(new PropertyValueFactory<>("lowStockItems"));
        lowStockColumn.setReorderable(false);

        // Format price columns
        averagePriceColumn.setCellFactory(column -> new TableCell<CategoryReport, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", price));
                }
            }
        });

        totalValueColumn.setCellFactory(column -> new TableCell<CategoryReport, Double>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", value));
                }
            }
        });

        reportsTable.setItems(reportItems);
    }

    public void updateReports(ObservableList<InventoryItem> inventoryItems) {
        reportItems.clear();

        Map<String, List<InventoryItem>> categoryGroups = inventoryItems.stream()
                .collect(Collectors.groupingBy(InventoryItem::getCategory));

        categoryGroups.forEach((category, items) -> {
            int totalItems = items.stream().mapToInt(InventoryItem::getQuantity).sum();
            double avgPrice = items.stream().mapToDouble(InventoryItem::getPrice).average().orElse(0.0);
            double totalValue = items.stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
            int lowStockItems = (int) items.stream()
                    .filter(item -> item.getQuantity() < 10)
                    .count();

            reportItems.add(new CategoryReport(
                    category,
                    totalItems,
                    avgPrice,
                    totalValue,
                    lowStockItems
            ));
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

