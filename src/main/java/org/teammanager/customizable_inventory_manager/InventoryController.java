package org.teammanager.customizable_inventory_manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

@Controller
public class InventoryController {

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @FXML
    private Button addButton; //declare button

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
        DatabaseConnection.testConnection();
       // initalizing buttons, tables
        addButton.setOnAction(e -> addItem());

        //set up the columns in the table view
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        //load inventory items into the table
        loadInventoryItems();
    }

    //private helper method to load inventory items into the table
    private void loadInventoryItems() {
        ObservableList<InventoryItem> items = FXCollections.observableArrayList(inventoryItemRepository.findAll());

        //set items in the tableview
        inventoryTable.setItems(items);
    }

    @FXML
    private void addItem(){
        //logic for adding an inventory item
        InventoryItem item = new InventoryItem();
        item.setName("Sample Item");
        item.setCategory("Category A");
        item.setQuantity(10);
        item.setPrice(100.0);
        inventoryItemRepository.save(item);
    }

    @FXML
    private void editItem () {
        InventoryItem selectedItem = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            //open an edit form with the current values populated in the fields
            selectedItem.setName("Updated Item Name");
            selectedItem.setCategory("Updated Category");
            selectedItem.setQuantity(15);
            selectedItem.setPrice(200.0);

            //save updated item back to the database
            inventoryItemRepository.save(selectedItem);

            //refresh table
            loadInventoryItems();
        }
    }

    @FXML
    private void deleteItem() {
        InventoryItem selectedItem = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            //delete item from database
            inventoryItemRepository.delete(selectedItem);

            //Refresh table
            loadInventoryItems();
        }
    }

    @FXML
    private void searchInventory() {
        String searchTerm = searchTextField.getText().toLowerCase();
        ObservableList<InventoryItem> filteredItems = FXCollections.observableArrayList(
                inventoryItemRepository.findAll().stream()
                        .filter(item -> item.getName().toLowerCase().contains(searchTerm))
                        .collect(Collectors.toList())
        );
        inventoryTable.setItems(filteredItems);
    }

    @FXML
    private void filterByCategory() {
        String selectedCategory = categoryComboBox.getValue();
        ObservableList<InventoryItem> filteredItems = FXCollections.observableArrayList(
                inventoryItemRepository.findAll().stream()
                        .filter(item -> item.getCategory().equals(selectedCategory))
                        .collect(Collectors.toList())
        );
        inventoryTable.setItems(filteredItems);
    }

    @FXML
    private void generateReport() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        inventoryItemRepository.findAll().stream()
                .collect(Collectors.groupingBy(InventoryItem::getCategory, Collectors.counting()))
                .forEach((category, count) -> pieChartData.add(new PieChart.Data(category, count)));

        stockPieChart.setData(pieChartData);
    }

    @FXML
    private void exportToCSV() throws IOException {
        FileWriter writer = new FileWriter("inventory_report.csv");

        writer.append("ID, Name, Category, Quantity, Price\n");

            for(InventoryItem item : inventoryItemRepository.findAll()) {
                writer.append(item.getId() + ", " + item.getName() + ", " + item.getCategory() + ", " +
                        item.getQuantity() + ", " + item.getPrice() + "\n");
            }

            writer.flush();
            writer.close();
    }
}
