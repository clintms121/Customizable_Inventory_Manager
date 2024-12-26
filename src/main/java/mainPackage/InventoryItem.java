package mainPackage;

public class InventoryItem {
    private String name;
    private String sku;
    private String category;
    private int quantity;
    private double price;
    private String status;

    public InventoryItem(String name, String sku, String category, int quantity, double price, String status) {
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
    }

    //getters
    public String getName() {
        return name;
    }

    public String getSku() {
        return sku;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    //setters
    public void setName(String name) {
        this.name = name;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
