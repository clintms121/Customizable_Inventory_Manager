package mainPackage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class interfaceController implements Initializable {
    @FXML private TextField searchField;
    @FXML private TableView<InventoryItem> inventoryTable;
    @FXML private TableColumn<InventoryItem, String> nameColumn;
    @FXML private TableColumn<InventoryItem, String> skuColumn;
    @FXML private TableColumn<InventoryItem, String> categoryColumn;
    @FXML private TableColumn<InventoryItem, Integer> quantityColumn;
    @FXML private TableColumn<InventoryItem, Double> priceColumn;
    @FXML private TableColumn<InventoryItem, String> statusColumn;
    @FXML private TableColumn<InventoryItem, Void> actionsColumn;
    @FXML private Label totalItemsLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label totalValueLabel;
    @FXML private Button addButton;

    private ObservableList<InventoryItem> inventoryItems = FXCollections.observableArrayList();
    private FilteredList<InventoryItem> filteredItems;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        setupSearch();
        setupActions();
        loadSampleData();
        updateStats();
    }

    private void setupTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        skuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Custom status column with colored labels
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label statusLabel = new Label(status);
                    statusLabel.setStyle(
                            status.equals("In Stock") ? "-fx-background-color: #e8f5e9; -fx-text-fill: #2e7d32; -fx-padding: 5 10; -fx-background-radius: 15;" :
                                    status.equals("Low Stock") ? "-fx-background-color: #fff3e0; -fx-text-fill: #ef6c00; -fx-padding: 5 10; -fx-background-radius: 15;" :
                                            "-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-padding: 5 10; -fx-background-radius: 15;"
                    );
                    setGraphic(statusLabel);
                }
            }
        });

        // Custom actions column with edit/delete buttons
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                buttons.setAlignment(Pos.CENTER);
                editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 15;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 15;");

                editButton.setOnAction(event -> {
                    InventoryItem item = getTableRow().getItem();
                    if (item != null) handleEdit(item);
                });

                deleteButton.setOnAction(event -> {
                    InventoryItem item = getTableRow().getItem();
                    if (item != null) handleDelete(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });

        filteredItems = new FilteredList<>(inventoryItems, p -> true);
        inventoryTable.setItems(filteredItems);
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredItems.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return item.getName().toLowerCase().contains(lowerCaseFilter) ||
                        item.getSku().toLowerCase().contains(lowerCaseFilter) ||
                        item.getCategory().toLowerCase().contains(lowerCaseFilter);
            });
            updateStats();
        });
    }

    private void setupActions() {
        addButton.setOnAction(e -> handleAdd());
    }
    private void loadSampleData() {
        inventoryItems.addAll(
                new InventoryItem("MacBook Pro", "LAP-001", "Electronics", 15, 1299.99, "In Stock"),
                new InventoryItem("Wireless Mouse", "ACC-001", "Accessories", 8, 49.99, "Low Stock"),
                new InventoryItem("Office Chair", "FUR-001", "Furniture", 5, 199.99, "Low Stock"),
                new InventoryItem("Monitor 27\"", "DSP-001", "Electronics", 12, 349.99, "In Stock"),
                new InventoryItem("USB-C Cable", "ACC-002", "Accessories", 50, 19.99, "In Stock")
        );
    }

    private void handleAdd() {
        Dialog<InventoryItem> dialog = new Dialog<>();
        dialog.setTitle("Add New Item");
        dialog.setHeaderText("Enter item details");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        TextField sku = new TextField();
        TextField category = new TextField();
        TextField quantity = new TextField();
        TextField price = new TextField();

        grid.add(new Label("Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("SKU:"), 0, 1);
        grid.add(sku, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(category, 1, 2);
        grid.add(new Label("Quantity:"), 0, 3);
        grid.add(quantity, 1, 3);
        grid.add(new Label("Price:"), 0, 4);
        grid.add(price, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to an InventoryItem object when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int qty = Integer.parseInt(quantity.getText());
                    double prc = Double.parseDouble(price.getText());
                    String status = qty < 10 ? "Low Stock" : "In Stock";

                    return new InventoryItem(
                            name.getText(),
                            sku.getText(),
                            category.getText(),
                            qty,
                            prc,
                            status
                    );
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter valid numbers for quantity and price.");
                    return null;
                }
            }
            return null;
        });

        Optional<InventoryItem> result = dialog.showAndWait();
        result.ifPresent(item -> {
            inventoryItems.add(item);
            updateStats();
        });
    }

    private void handleEdit(InventoryItem item) {
        Dialog<InventoryItem> dialog = new Dialog<>();
        dialog.setTitle("Edit Item");
        dialog.setHeaderText("Edit item details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField(item.getName());
        TextField sku = new TextField(item.getSku());
        TextField category = new TextField(item.getCategory());
        TextField quantity = new TextField(String.valueOf(item.getQuantity()));
        TextField price = new TextField(String.format("%.2f", item.getPrice()));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("SKU:"), 0, 1);
        grid.add(sku, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(category, 1, 2);
        grid.add(new Label("Quantity:"), 0, 3);
        grid.add(quantity, 1, 3);
        grid.add(new Label("Price:"), 0, 4);
        grid.add(price, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int qty = Integer.parseInt(quantity.getText());
                    double prc = Double.parseDouble(price.getText());
                    String status = qty < 10 ? "Low Stock" : "In Stock";

                    return new InventoryItem(
                            name.getText(),
                            sku.getText(),
                            category.getText(),
                            qty,
                            prc,
                            status
                    );
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter valid numbers for quantity and price.");
                    return null;
                }
            }
            return null;
        });

        Optional<InventoryItem> result = dialog.showAndWait();
        result.ifPresent(updatedItem -> {
            int index = inventoryItems.indexOf(item);
            inventoryItems.set(index, updatedItem);
            updateStats();
        });
    }

    private void handleDelete(InventoryItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Item");
        alert.setHeaderText("Delete " + item.getName());
        alert.setContentText("Are you sure you want to delete this item?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            inventoryItems.remove(item);
            updateStats();
        }
    }

    private void updateStats() {
        int totalItems = inventoryItems.stream()
                .mapToInt(InventoryItem::getQuantity)
                .sum();

        long lowStockCount = inventoryItems.stream()
                .filter(item -> item.getQuantity() < 10)
                .count();

        double totalValue = inventoryItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();

        Platform.runLater(() -> {
            totalItemsLabel.setText(String.format("%,d", totalItems));
            lowStockLabel.setText(String.valueOf(lowStockCount));
            totalValueLabel.setText(String.format("$%,.2f", totalValue));
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}