package mainPackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Use the correct relative path to locate the FXML file
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("inventory_ui.fxml"));

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        Application.launch(Launcher.class);
    }
}