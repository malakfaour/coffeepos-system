package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private Integer id;
    private String orderCode;
    private User cashier;
    private LocalDateTime orderDateTime = LocalDateTime.now();
    private List<OrderItem> items = new ArrayList<>();

    private double discount;
    private double tax;
    private double subtotal;
    private double total;
    private String paymentMethod;

    public Order() {
        this.orderDateTime = LocalDateTime.now();
        this.items = new ArrayList<>();
    }

    public Order(String orderCode, User cashier, LocalDateTime dateTime) {
        this.orderCode = orderCode;
        this.cashier = cashier;
        this.orderDateTime = (dateTime != null ? dateTime : LocalDateTime.now());
        this.items = new ArrayList<>();
    }

    public void addItem(Product p, int qty) {
        OrderItem it = new OrderItem(p, qty, p.getPrice());
        items.add(it);
        recalcTotals();
    }

    public void recalcTotals() {
        subtotal = items.stream().mapToDouble(OrderItem::getLineTotal).sum();
        total = subtotal - discount + tax;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }

    public User getCashier() { return cashier; }
    public void setCashier(User cashier) { this.cashier = cashier; }

    public LocalDateTime getOrderDateTime() { return orderDateTime; }
    public void setOrderDateTime(LocalDateTime orderDateTime) {
        this.orderDateTime = (orderDateTime != null ? orderDateTime : LocalDateTime.now());
    }

    public List<OrderItem> getItems() {
        if (items == null) items = new ArrayList<>();
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
