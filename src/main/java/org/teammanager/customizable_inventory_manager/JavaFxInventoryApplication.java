package org.teammanager.customizable_inventory_manager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// JavaFX Application class
public class JavaFxInventoryApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/inventory_ui.fxml"));

        try {
            Parent root = loader.load();
            primaryStage.setTitle("Inventory Manager");
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error loading FXML file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}