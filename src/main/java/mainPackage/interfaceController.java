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
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.stage.StageStyle;
import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.animation.ScaleTransition;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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

    private ReportsController reportsController;



    private ObservableList<InventoryItem> inventoryItems = FXCollections.observableArrayList();
    private FilteredList<InventoryItem> filteredItems;

    private void loadInventoryFromDatabase() {
        try {
            User currentUser = LoginController.getCurrentUser();
            if (currentUser != null) {
                inventoryItems.clear();
                inventoryItems.addAll(InventoryDAO.loadInventory(currentUser.getId()));
                updateStats();
                updateReports();
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load inventory: " + e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the ObservableList
        inventoryItems = FXCollections.observableArrayList();

        // Create the FilteredList wrapped around the ObservableList
        filteredItems = new FilteredList<>(inventoryItems, p -> true);

        // Set the FilteredList as the TableView's items
        inventoryTable.setItems(filteredItems);

        setupTable();
        setupSearch();
        setupActions();
        loadInventoryFromDatabase();
        updateStats();
        setupNavigation();
        updateReports();
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

            mainContentArea.getChildren().clear();

            switch (view) {
                case "Dashboard":
                    dashboardButton.setStyle("-fx-background-color: #f8f9fa; -fx-alignment: CENTER_LEFT; -fx-padding: 10 15;");
                    loadView("/fxml/Dashboard.fxml");
                    break;
                case "Inventory":
                    inventoryButton.setStyle("-fx-background-color: #f8f9fa; -fx-alignment: CENTER_LEFT; -fx-padding: 10 15;");

                    // Recreate the top bar
                    HBox topBar = new HBox();
                    topBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    topBar.setSpacing(15);
                    topBar.setStyle("-fx-padding: 20; -fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 5, 0, 0, 0);");

                    searchField.setPromptText("Search inventory...");
                    searchField.setStyle("-fx-pref-width: 300; -fx-background-radius: 20; -fx-padding: 8;");

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 8 20;");

                    topBar.getChildren().addAll(searchField, spacer, addButton);

                    // Recreate the stats cards
                    FlowPane statsPane = new FlowPane();
                    statsPane.setHgap(20);
                    statsPane.setVgap(20);
                    statsPane.setStyle("-fx-padding: 20;");

                    // Total Items card
                    VBox totalItemsCard = createStatsCard("Total Items", totalItemsLabel);

                    // Low Stock card
                    VBox lowStockCard = createStatsCard("Low Stock", lowStockLabel);
                    lowStockLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #f44336;");

                    // Total Value card
                    VBox totalValueCard = createStatsCard("Total Value", totalValueLabel);

                    statsPane.getChildren().addAll(totalItemsCard, lowStockCard, totalValueCard);

                    // Table container
                    VBox tableContainer = new VBox();
                    tableContainer.setStyle("-fx-padding: 20;");
                    VBox.setVgrow(tableContainer, Priority.ALWAYS);

                    inventoryTable.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 5, 0, 0, 0);");
                    VBox.setVgrow(inventoryTable, Priority.ALWAYS);

                    tableContainer.getChildren().add(inventoryTable);

                    // Add all components to mainContentArea
                    mainContentArea.getChildren().addAll(topBar, statsPane, tableContainer);
                    break;
                case "Reports":
                    reportsButton.setStyle("-fx-background-color: #f8f9fa; -fx-alignment: CENTER_LEFT; -fx-padding: 10 15;");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Reports.fxml"));
                    Parent reportsView = loader.load();
                    reportsController = loader.getController();
                    reportsController.updateReports(inventoryItems);  // Update reports with current inventory
                    mainContentArea.getChildren().clear();
                    mainContentArea.getChildren().add(reportsView);
                    break;
            }
        } catch (Exception e) {
            showAlert("Navigation Error", "Could not load " + view + " view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createStatsCard(String title, Label valueLabel) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; " +
                "-fx-min-width: 200; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 5, 0, 0, 0);");
        card.getStyleClass().add("stat-card");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #666;");

        valueLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private void updateReports() {
        if (reportsController != null) {
            reportsController.updateReports(inventoryItems);
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
            applySearchFilter(newValue);
            updateStats();
        });
    }

    private void setupActions() {
        addButton.setOnAction(e -> handleAdd());
    }
    private void loadSampleData() {
        inventoryItems.addAll(
                new InventoryItem("Sample Item", "SMP-001", "Section", 1, 10.00, "In Stock")
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
            try {
                User currentUser = LoginController.getCurrentUser();
                if (currentUser != null && InventoryDAO.addItem(currentUser.getId(), item)) {
                    // Add directly to the existing ObservableList
                    inventoryItems.add(item);

                    // Apply current search filter
                    applySearchFilter(searchField.getText());

                    // Update statistics and reports
                    updateStats();
                    updateReports();
                }
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to add item: " + e.getMessage());
            }
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

        String originalSku = item.getSku(); // Store original SKU for database update

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
            try {
                User currentUser = LoginController.getCurrentUser();
                if (currentUser != null && InventoryDAO.updateItem(currentUser.getId(), originalSku, updatedItem)) {
                    // Find the item in the list and update it
                    int index = inventoryItems.indexOf(item);
                    if (index >= 0) {
                        inventoryItems.set(index, updatedItem);
                    }

                    // Apply current search filter
                    applySearchFilter(searchField.getText());

                    // Update statistics and reports
                    updateStats();
                    updateReports();
                }
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to update item: " + e.getMessage());
            }
        });
    }

    private void handleDelete(InventoryItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Item");
        alert.setHeaderText("Delete " + item.getName());
        alert.setContentText("Are you sure you want to delete this item?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                User currentUser = LoginController.getCurrentUser();
                if (currentUser != null && InventoryDAO.deleteItem(currentUser.getId(), item.getSku())) {
                    // Remove directly from the existing ObservableList
                    inventoryItems.remove(item);

                    // Apply current search filter
                    applySearchFilter(searchField.getText());

                    // Update statistics and reports
                    updateStats();
                    updateReports();
                }
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to delete item: " + e.getMessage());
            }
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

    private void applySearchFilter(String searchText) {
        filteredItems.setPredicate(item -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = searchText.toLowerCase();
            return item.getName().toLowerCase().contains(lowerCaseFilter) ||
                    item.getSku().toLowerCase().contains(lowerCaseFilter) ||
                    item.getCategory().toLowerCase().contains(lowerCaseFilter);
        });
    }



}