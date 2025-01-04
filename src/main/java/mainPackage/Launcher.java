package mainPackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize database
        DatabaseUtil.initializeDatabase();

        // Load login screen
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setTitle("Inventory Manager");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Clean up database connection when application closes
        DatabaseUtil.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}