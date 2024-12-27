package mainPackage;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML private BarChart<String, Number> salesChart;
    @FXML private BarChart<String, Number> categoryChart;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupSalesChart();
        setupCategoryChart();
    }

    private void setupSalesChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Sales");

        // Add sample data
        series.getData().add(new XYChart.Data<>("Jan", 23500));
        series.getData().add(new XYChart.Data<>("Feb", 34200));
        series.getData().add(new XYChart.Data<>("Mar", 28700));
        series.getData().add(new XYChart.Data<>("Apr", 32100));

        salesChart.getData().add(series);
    }

    private void setupCategoryChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Items per Category");

        // Add sample data
        series.getData().add(new XYChart.Data<>("Electronics", 45));
        series.getData().add(new XYChart.Data<>("Furniture", 30));
        series.getData().add(new XYChart.Data<>("Accessories", 25));

        categoryChart.getData().add(series);
    }
}