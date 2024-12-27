package mainPackage;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        loadSampleData();

        exportButton.setOnAction(e -> handleExport());
    }

    private void setupTable() {
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        totalItemsColumn.setCellValueFactory(new PropertyValueFactory<>("totalItems"));
        averagePriceColumn.setCellValueFactory(new PropertyValueFactory<>("averagePrice"));
        totalValueColumn.setCellValueFactory(new PropertyValueFactory<>("totalValue"));
        lowStockColumn.setCellValueFactory(new PropertyValueFactory<>("lowStockItems"));
    }

    private void loadSampleData() {
        ObservableList<CategoryReport> reports = FXCollections.observableArrayList(
                new CategoryReport("Electronics", 57, 899.99, 51299.43, 3),
                new CategoryReport("Furniture", 30, 299.99, 8999.70, 5),
                new CategoryReport("Accessories", 75, 49.99, 3749.25, 2)
        );
        reportsTable.setItems(reports);
    }

    private void handleExport() {
        // Implement export functionality
        System.out.println("Exporting report...");
    }
}