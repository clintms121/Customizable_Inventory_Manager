package org.teammanager.customizable_inventory_manager;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.io.IOException;
import java.util.stream.Collectors;

@Controller
public class InventoryController {

    @Autowired
    private InventoryService inventoryService; // Service Layer

    @FXML
    private Button addButton;

    @FXML
    private TableView<InventoryItem> inventoryTable;

    @FXML
    private TableColumn<InventoryItem, String> nameColumn;

    @FXML
    private TableColumn<InventoryItem, String> categoryColumn;

    @FXML
    private TableColumn<InventoryItem, Integer> quantityColumn;

    @FXML
    private TableColumn<InventoryItem, Double> priceColumn;

    @FXML
    private TextField searchTextField;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private PieChart stockPieChart;

    @FXML
    private void initialize() {
        // Configure TableView Columns
        configureTableColumns();

        // Set button actions
        addButton.setOnAction(e -> addItem());

        // Load the initial items
        loadInventoryItems();

        // Populate category combo box
        populateCategoryComboBox();
    }

    private void configureTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    private void loadInventoryItems() {
        ObservableList<InventoryItem> items = inventoryService.getAllItems();
        setTableData(items);
    }

    private void populateCategoryComboBox() {
        // Load all unique categories for the dropdown
        categoryComboBox.setItems(FXCollections.observableArrayList(inventoryService.getAllCategories()));
    }

    private void setTableData(ObservableList<InventoryItem> items) {
        Platform.runLater(() -> {
            if (inventoryTable != null) {
                if (items != null && !items.isEmpty()) {
                    inventoryTable.setItems(items);
                } else {
                    inventoryTable.getItems().clear();
                    inventoryTable.setPlaceholder(new Label("No data available"));
                }
            }
        });
    }

    @FXML
    private void addItem() {
        InventoryItem newItem = inventoryService.createSampleItem(); // Generic item creation
        inventoryService.saveItem(newItem);
        loadInventoryItems(); // Refresh table
        AlertUtil.showInfo("Item Added", "A new sample item has been added successfully.");
    }

    @FXML
    private void editItem() {
        InventoryItem selectedItem = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            inventoryService.updateSampleItem(selectedItem); // Edit existing item
            loadInventoryItems();
            AlertUtil.showInfo("Item Updated", "The selected item has been updated.");
        }
    }

    @FXML
    private void deleteItem() {
        InventoryItem selectedItem = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            inventoryService.deleteItem(selectedItem);
            loadInventoryItems(); // Refresh table
            AlertUtil.showInfo("Item Deleted", "The selected item has been deleted.");
        }
    }

    @FXML
    private void searchInventory() {
        String searchTerm = searchTextField.getText();
        ObservableList<InventoryItem> filteredItems = inventoryService.searchItems(searchTerm);
        setTableData(filteredItems);
    }

    @FXML
    private void filterByCategory() {
        String selectedCategory = categoryComboBox.getValue();
        ObservableList<InventoryItem> filteredItems = inventoryService.getItemsByCategory(selectedCategory);
        setTableData(filteredItems);
    }

    @FXML
    private void generateReport() {
        ObservableList<PieChart.Data> reportData = inventoryService.getCategoryReportData();
        stockPieChart.setData(reportData);
    }

    @FXML
    private void exportToCSV() {
        try {
            inventoryService.exportInventoryToCSV("inventory_report.csv");
            AlertUtil.showInfo("Export Complete", "Inventory report successfully exported to CSV.");
        } catch (IOException e) {
            AlertUtil.showError("Error", "Failed to export inventory to CSV.");
        }
    }

    @FXML
    private void onNewButtonClicked() {
        AlertUtil.showInfo("New Inventory", "You can now start managing a new inventory.");
    }

    @FXML
    private void onLoadButtonClicked() {
        ObservableList<InventoryItem> items = inventoryService.loadInventoryFromDatabase();
        setTableData(items);
    }
}
