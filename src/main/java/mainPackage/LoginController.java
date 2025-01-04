package mainPackage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;

    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    @FXML
    private void initialize() {
        loginButton.setOnAction(e -> handleLogin());
        registerButton.setOnAction(e -> handleRegister());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            User user = UserDAO.authenticate(username, password);
            if (user != null) {
                currentUser = user;
                loadMainApplication();
            } else {
                showAlert("Login Failed", "Invalid username or password");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred during login: " + e.getMessage());
        }
    }

    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            if (UserDAO.registerUser(username, password)) {
                showAlert("Success", "Registration successful! Please log in.");
            } else {
                showAlert("Error", "Registration failed. Username may already exist.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred during registration: " + e.getMessage());
        }
    }

    private void loadMainApplication() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/inventory_ui.fxml"));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to load main application: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}