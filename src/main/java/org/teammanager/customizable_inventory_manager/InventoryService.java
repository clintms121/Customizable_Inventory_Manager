package org.teammanager.customizable_inventory_manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    public ObservableList<InventoryItem> getAllItems() {
        return FXCollections.observableArrayList(inventoryItemRepository.findAll());
    }

    public List<String> getAllCategories() {
        return inventoryItemRepository.findAll().stream()
                .map(InventoryItem::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    public InventoryItem createSampleItem() {
        InventoryItem item = new InventoryItem();
        item.setName("Sample Item");
        item.setCategory("Sample Category");
        item.setQuantity(10);
        item.setPrice(100.0);
        return item;
    }

    public void saveItem(InventoryItem item) {
        inventoryItemRepository.save(item);
    }

    public void deleteItem(InventoryItem item) {
        inventoryItemRepository.delete(item);
    }

    public void updateSampleItem(InventoryItem item) {
        item.setName("Updated Name");
        item.setCategory("Updated Category");
        item.setQuantity(20);
        item.setPrice(150.0);
        inventoryItemRepository.save(item);
    }

    public ObservableList<InventoryItem> searchItems(String searchTerm) {
        return FXCollections.observableArrayList(
                inventoryItemRepository.findAll().stream()
                        .filter(item -> item.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                        .collect(Collectors.toList())
        );
    }

    public ObservableList<InventoryItem> getItemsByCategory(String category) {
        return FXCollections.observableArrayList(
                inventoryItemRepository.findAll().stream()
                        .filter(item -> item.getCategory().equals(category))
                        .collect(Collectors.toList())
        );
    }

    public ObservableList<PieChart.Data> getCategoryReportData() {
        return FXCollections.observableArrayList(
                inventoryItemRepository.findAll().stream()
                        .collect(Collectors.groupingBy(InventoryItem::getCategory, Collectors.counting()))
                        .entrySet().stream()
                        .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList())
        );
    }

    public void exportInventoryToCSV(String fileName) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("ID, Name, Category, Quantity, Price\n");
            for (InventoryItem item : inventoryItemRepository.findAll()) {
                writer.write(String.format("%d, %s, %s, %d, %.2f\n",
                        item.getId(), item.getName(), item.getCategory(), item.getQuantity(), item.getPrice()));
            }
        }
    }

    public ObservableList<InventoryItem> loadInventoryFromDatabase() {
        // Implement database loading logic here
        return FXCollections.observableArrayList(); // Replace with actual loaded data
    }
}
