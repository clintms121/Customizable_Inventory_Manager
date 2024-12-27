package mainPackage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.StageStyle;
import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.animation.ScaleTransition;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class interfaceController implements Initializable {

    @FXML private Button dashboardButton;
    @FXML private Button inventoryButton;
    @FXML private Button reportsButton;
    @FXML private VBox mainContentArea;


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
        setupNavigation();
    }

    private void setupNavigation() {
        dashboardButton.setOnAction(e -> navigateTo("Dashboard"));
        inventoryButton.setOnAction(e -> navigateTo("Inventory"));
        reportsButton.setOnAction(e -> navigateTo("Reports"));
    }

    private void navigateTo(String view) {
        try {
            // First, reset all button styles
            dashboardButton.setStyle("-fx-background-color: transparent; -fx-alignment: CENTER_LEFT; -fx-padding: 10 15;");
            inventoryButton.setStyle("-fx-background-color: transparent; -fx-alignment: CENTER_LEFT; -fx-padding: 10 15;");
            reportsButton.setStyle("-fx-background-color: transparent; -fx-alignment: CENTER_LEFT; -fx-padding: 10 15;");

            // Highlight the selected button
            switch (view) {
                case "Dashboard":
                    dashboardButton.setStyle("-fx-background-color: #f8f9fa; -fx-alignment: CENTER_LEFT; -fx-padding: 10 15;");
                    loadView("/fxml/Dashboard.fxml");
                    break;
                case "Inventory":
                    inventoryButton.setStyle("-fx-background-color: #f8f9fa; -fx-alignment: CENTER_LEFT; -fx-padding: 10 15;");
                    mainContentArea.getChildren().clear();
                    mainContentArea.getChildren().addAll(
                            searchField.getParent().getParent(),
                            inventoryTable.getParent().getParent()
                    );
                    break;
                case "Reports":
                    reportsButton.setStyle("-fx-background-color: #f8f9fa; -fx-alignment: CENTER_LEFT; -fx-padding: 10 15;");
                    loadView("/fxml/Reports.fxml");
                    break;
            }
        } catch (Exception e) {
            showAlert("Navigation Error", "Could not load " + view + " view.");
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainContentArea.getChildren().clear();
            mainContentArea.getChildren().add(view);

            // Add fade transition
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), view);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load view: " + fxmlPath);
        }
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
                buttons.setTranslateX(-10);
                editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 5 10; -fx-font-size: 10px; -fx-font-weight: bold; -fx-border-color: #1E88E5; -fx-border-width: 1px; -fx-border-radius: 15; -fx-pref-width: 30px; -fx-pref-height: 20px; -fx-alignment: center;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 5 10; -fx-font-size: 10px; -fx-font-weight: bold; -fx-border-color: #1E88E5; -fx-border-width: 1px; -fx-border-radius: 15; -fx-pref-width: 30px; -fx-pref-height: 20px; -fx-alignment: center;");

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
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/dialogStyles.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        //grab the save and cancel buttons in the pane
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);

        ScaleTransition saveButtonScale = new ScaleTransition(Duration.millis(200), saveButton);
        saveButtonScale.setToX(1.1);  // Scale to 110% of original size
        saveButtonScale.setToY(1.1);
        saveButtonScale.setAutoReverse(true);
        saveButton.setOnMouseEntered(e -> saveButtonScale.playFromStart());
        saveButton.setOnMouseExited(e -> {
            saveButtonScale.stop();
            saveButton.setScaleX(1.1);
            saveButton.setScaleY(1.0);
        });

        ScaleTransition cancelButtonScale = new ScaleTransition(Duration.millis(200), cancelButton);
        cancelButtonScale.setToX(1.1);  // Scale to 110% of original size
        cancelButtonScale.setToY(1.1);
        cancelButtonScale.setAutoReverse(true);
        cancelButton.setOnMouseEntered(e -> cancelButtonScale.playFromStart());
        cancelButton.setOnMouseExited(e -> {
            cancelButtonScale.stop();
            cancelButton.setScaleX(1.1);
            cancelButton.setScaleY(1.0);
        });

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
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/dialogStyles.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

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

        //grab the save and cancel buttons in the pane
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);

        ScaleTransition saveButtonScale = new ScaleTransition(Duration.millis(200), saveButton);
        saveButtonScale.setToX(1.1);  // Scale to 110% of original size
        saveButtonScale.setToY(1.1);
        saveButtonScale.setAutoReverse(true);
        saveButton.setOnMouseEntered(e -> saveButtonScale.playFromStart());
        saveButton.setOnMouseExited(e -> {
            saveButtonScale.stop();
            saveButton.setScaleX(1.1);
            saveButton.setScaleY(1.0);
        });

        ScaleTransition cancelButtonScale = new ScaleTransition(Duration.millis(200), cancelButton);
        cancelButtonScale.setToX(1.1);  // Scale to 110% of original size
        cancelButtonScale.setToY(1.1);
        cancelButtonScale.setAutoReverse(true);
        cancelButton.setOnMouseEntered(e -> cancelButtonScale.playFromStart());
        cancelButton.setOnMouseExited(e -> {
            cancelButtonScale.stop();
            cancelButton.setScaleX(1.1);
            cancelButton.setScaleY(1.0);
        });

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

        alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/alertStyles.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("dialog-pane");

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