package model;

import model.base.BaseEntity;

public class Product extends BaseEntity {

    private String sku;
    private String name;
    private Category category;
    private double price;
    private String barcode;
    private int stockQty;
    private boolean active = true;

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStockQty() { return stockQty; }
    public void setStockQty(int stockQty) { this.stockQty = stockQty; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
