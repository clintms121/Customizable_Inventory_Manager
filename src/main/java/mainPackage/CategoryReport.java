package mainPackage;

public class CategoryReport {
    private String category;
    private int totalItems;
    private double averagePrice;
    private double totalValue;
    private int lowStockItems;

    // Constructor
    public CategoryReport(String category, int totalItems, double averagePrice,
                          double totalValue, int lowStockItems) {
        this.category = category;
        this.totalItems = totalItems;
        this.averagePrice = averagePrice;
        this.totalValue = totalValue;
        this.lowStockItems = lowStockItems;
    }

    // Getters
    public String getCategory() {
        return category;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public int getLowStockItems() {
        return lowStockItems;
    }

    // Setters
    public void setCategory(String category) {
        this.category = category;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public void setLowStockItems(int lowStockItems) {
        this.lowStockItems = lowStockItems;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "CategoryReport{" +
                "category='" + category + '\'' +
                ", totalItems=" + totalItems +
                ", averagePrice=" + averagePrice +
                ", totalValue=" + totalValue +
                ", lowStockItems=" + lowStockItems +
                '}';
    }
}