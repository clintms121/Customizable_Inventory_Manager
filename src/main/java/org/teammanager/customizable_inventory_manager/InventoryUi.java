package org.teammanager.customizable_inventory_manager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class InventoryUi {

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        // Initialization logic here
        statusLabel.setText("Inventory Manager Initialized!");
    }
}