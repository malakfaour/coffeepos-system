package model;

public class OrderItem {

    private Integer id;
    private Product product;
    private int quantity;
    private double unitPrice;
    private double lineTotal;

    // Customization fields
    private String customization = "";
    private double customizationPrice = 0.0;

    // Flag to prevent recalculation when loading from DB
    private boolean loadedFromDB = false;

    public OrderItem() {
    }

    // Used when adding items from the POS screen
    public OrderItem(Product p, int qty, double price) {
        this(p, qty, price, "", 0.0);
    }

    public OrderItem(Product p, int qty, double price, String customization, double customizationPrice) {
        this.product = p;
        this.quantity = qty;
        this.unitPrice = price;
        this.customization = customization;
        this.customizationPrice = customizationPrice;
        recalculateLineTotal();
    }

    // Call this in DAO after setting values so recalc doesn't override DB values
    public void markLoadedFromDB() {
        this.loadedFromDB = true;
    }

    public void recalculateLineTotal() {
        if (loadedFromDB) return; // <-- FIX: DO NOT RECALC WHEN LOADING FROM DATABASE
        this.lineTotal = this.quantity * (this.unitPrice + this.customizationPrice);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Product getProduct() { return product; }

    public void setProduct(Product product) {
        this.product = product;
        if (!loadedFromDB) recalculateLineTotal();
    }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        if (!loadedFromDB) recalculateLineTotal();
    }

    public double getUnitPrice() { return unitPrice; }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        if (!loadedFromDB) recalculateLineTotal();
    }

    public double getLineTotal() { return lineTotal; }
    public void setLineTotal(double lineTotal) { this.lineTotal = lineTotal; }

    public String getCustomization() { return customization; }
    public void setCustomization(String customization) { this.customization = customization; }

    public double getCustomizationPrice() { return customizationPrice; }

    public void setCustomizationPrice(double customizationPrice) {
        this.customizationPrice = customizationPrice;
        if (!loadedFromDB) recalculateLineTotal();
    }
}
