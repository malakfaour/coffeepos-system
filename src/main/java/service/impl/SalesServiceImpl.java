package service.impl;

import config.AppSettings;
import dao.InventoryMovementDAO;
import dao.OrderDAO;
import dao.PaymentDAO;
import dao.ProductDAO;
import dao.impl.InventoryMovementDAOImpl;
import dao.impl.OrderDAOImpl;
import dao.impl.PaymentDAOImpl;
import dao.impl.ProductDAOImpl;
import model.InventoryMovement;
import model.Order;
import model.OrderItem;
import model.Payment;
import model.Product;
import model.pricing.DiscountStrategy;
import service.SalesService;
import util.CsvExporter;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class SalesServiceImpl implements SalesService {

    private final ProductDAO productDAO = new ProductDAOImpl();
    private final OrderDAO orderDAO = new OrderDAOImpl();
    private final PaymentDAO paymentDAO = new PaymentDAOImpl();
    private final InventoryMovementDAO inventoryDAO = new InventoryMovementDAOImpl();

    @Override
    public void addToCart(Order order, Product product, int qty) {
        if (product.getStockQty() < qty)
            throw new IllegalArgumentException("Not enough stock for " + product.getName());
        order.addItem(product, qty);
    }

    @Override
    public void applyDiscount(Order order, DiscountStrategy strategy) {
        double discount = strategy.apply(order.getSubtotal());
        order.setDiscount(discount);
        order.recalcTotals();
    }

    @Override
    public Order checkout(Order order, String paymentMethod, double paidAmount) throws Exception {

  
        double tax = order.getSubtotal() * AppSettings.TAX_RATE;
        order.setTax(tax);
        order.recalcTotals();

  
        if (paidAmount < order.getTotal()) {
            throw new IllegalArgumentException("Paid amount is less than total. Payment failed.");
        }

 
        int orderId = orderDAO.save(order, order.getItems());
        order.setId(orderId);

        Payment payment = new Payment(orderId, paymentMethod, paidAmount);
        paymentDAO.save(payment);


        for (OrderItem item : order.getItems()) {
            int pid = item.getProduct().getId();
            int qty = item.getQuantity();

            productDAO.updateStock(pid, qty);

            InventoryMovement movement =
                    new InventoryMovement(pid, -qty, "SALE", orderId);

            inventoryDAO.record(movement);
        }

    
        generateReceipt(order, paymentMethod, paidAmount);

        return order; 
    }

    private void generateReceipt(Order order, String method, double paidAmount) throws Exception {
        File receiptFile = new File("receipt_" + order.getOrderCode() + ".csv");

        List<String[]> rows = order.getItems().stream()
                .map(i -> new String[]{
                        i.getProduct().getName(),
                        String.valueOf(i.getQuantity()),
                        String.valueOf(i.getLineTotal())
                })
                .collect(Collectors.toList());

        rows.add(new String[]{"Subtotal", "", String.valueOf(order.getSubtotal())});
        rows.add(new String[]{"Discount", "", String.valueOf(order.getDiscount())});
        rows.add(new String[]{"Tax", "", String.valueOf(order.getTax())});
        rows.add(new String[]{"Total", "", String.valueOf(order.getTotal())});
        rows.add(new String[]{"Payment Method", "", method});
        rows.add(new String[]{"Paid", "", String.valueOf(paidAmount)});
        rows.add(new String[]{"Change", "", String.valueOf(paidAmount - order.getTotal())});

        CsvExporter.write(receiptFile.getAbsolutePath(), rows);
    }
}
