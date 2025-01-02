package mainPackage;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML private Hyperlink githubLink;
    @FXML private Hyperlink linkedinLink;
    @FXML private Label repoCount;
    @FXML private Label contributionsCount;
    @FXML private Label lastActivity;
    @FXML private Label connectionsCount;
    @FXML private Label skillsCount;
    @FXML private Label profileViews;
    @FXML private ListView<String> activityFeed;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadGitHubStats();
        loadLinkedInStats();
        loadRecentActivity();
    }

    @FXML
    private void openGithub() {
        openUrl("https://github.com/clintms121");
    }

    @FXML
    private void openLinkedIn() {
        openUrl("www.linkedin.com/in/clint-stapleton-73284a2b8");
    }

    private void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            showAlert("Error", "Could not open URL: " + url);
        }
    }

    private void loadGitHubStats() {
        // In a real application, you would fetch these from GitHub's API
        repoCount.setText("3");
        contributionsCount.setText("15");
        lastActivity.setText("1 hour ago");
    }

    private void loadLinkedInStats() {
        // In a real application, you would fetch these from LinkedIn's API
        connectionsCount.setText("123");
        skillsCount.setText("Java, C#, Python, JavaScript, HTML, CSS, SQL");
        profileViews.setText("87 this week");
    }

    private void loadRecentActivity() {
        ObservableList<String> activities = FXCollections.observableArrayList(
                "Pushed to main in inventory manager repository",
                "Created new repository: inventory-manager",
                "Updated LinkedIn profile with new skills"
        );
        activityFeed.setItems(activities);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}