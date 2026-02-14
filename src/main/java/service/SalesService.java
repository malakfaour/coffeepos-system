package service;

import model.Order;
import model.Product;
import model.pricing.DiscountStrategy;

public interface SalesService {

    void addToCart(Order order, Product product, int qty);

    void applyDiscount(Order order, DiscountStrategy strategy);

    Order checkout(Order order, String paymentMethod, double paidAmount) throws Exception;
}
