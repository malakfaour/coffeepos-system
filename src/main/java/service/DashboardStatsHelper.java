package service;

import model.Order;
import model.OrderItem;
import model.Product;
import service.impl.InventoryServiceImpl;
import service.impl.ReportServiceImpl;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardStatsHelper {

    private final ReportService reportService = new ReportServiceImpl();
    private final InventoryService inventoryService = new InventoryServiceImpl();

    // ============================
    // TODAY TOTAL SALES (ADMIN/USER)
    // ============================
    public Map<String, Double> getTodayStats() {
        Map<String, Double> data = new HashMap<>();

        try {
            LocalDate today = LocalDate.now();
            List<Order> orders = reportService.getOrdersBetween(today, today);

            double totalSales = orders.stream().mapToDouble(Order::getTotal).sum();
            int totalOrders = orders.size();
            double avgOrder = totalOrders > 0 ? totalSales / totalOrders : 0;

            data.put("totalSales", totalSales);
            data.put("totalOrders", (double) totalOrders);
            data.put("avgOrder", avgOrder);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    // ============================
    // TODAY SALES (CASHIER ONLY)
    // ============================
    public Map<String, Double> getTodayStatsForCashier(int cashierId) {
        Map<String, Double> data = new HashMap<>();

        try {
            LocalDate today = LocalDate.now();
            List<Order> orders = reportService.getOrdersBetween(today, today);

            List<Order> myOrders = orders.stream()
                    .filter(o -> o.getCashier() != null && o.getCashier().getId() == cashierId)
                    .collect(Collectors.toList());

            double totalSales = myOrders.stream().mapToDouble(Order::getTotal).sum();
            int totalOrders = myOrders.size();
            double avgOrder = totalOrders > 0 ? totalSales / totalOrders : 0;

            double cashSales = myOrders.stream()
                    .filter(o -> "CASH".equalsIgnoreCase(o.getPaymentMethod()))
                    .mapToDouble(Order::getTotal).sum();

            double cardSales = myOrders.stream()
                    .filter(o -> "CARD".equalsIgnoreCase(o.getPaymentMethod()))
                    .mapToDouble(Order::getTotal).sum();

            data.put("totalSales", totalSales);
            data.put("totalOrders", (double) totalOrders);
            data.put("avgOrder", avgOrder);
            data.put("cashSales", cashSales);
            data.put("cardSales", cardSales);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    // ============================
    // HOURLY SALES (8 AM → 10 PM)
    // ============================
    public Map<String, Double> getTodayHourlySalesForCashier(int cashierId) {
        Map<String, Double> hourly = new LinkedHashMap<>();

        for (int h = 8; h <= 22; h++) {
            hourly.put(String.format("%02d:00", h), 0.0);
        }

        try {
            LocalDate today = LocalDate.now();
            List<Order> orders = reportService.getOrdersBetween(today, today);

            orders.stream()
                    .filter(o -> o.getCashier() != null && o.getCashier().getId() == cashierId)
                    .forEach(o -> {
                        int h = o.getOrderDateTime().getHour();
                        if (h < 8 || h > 22) return;

                        String label = String.format("%02d:00", h);
                        hourly.put(label, hourly.get(label) + o.getTotal());
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return hourly;
    }

    // ============================
    // CATEGORY SALES (TODAY)
    // ============================
    public Map<String, Double> getTodayCategorySalesForCashier(int cashierId) {
        Map<String, Double> map = new HashMap<>();

        try {
            LocalDate today = LocalDate.now();
            List<Order> orders = reportService.getOrdersBetween(today, today);

            orders.stream()
                    .filter(o -> o.getCashier() != null && o.getCashier().getId() == cashierId)
                    .forEach(o -> {
                        for (OrderItem it : o.getItems()) {
                            Product p = it.getProduct();
                            if (p == null || p.getCategory() == null) continue;

                            String cat = p.getCategory().getName();
                            double amount = it.getLineTotal();

                            map.put(cat, map.getOrDefault(cat, 0.0) + amount);
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    // ============================
    // TOP 5 BEST-SELLING PRODUCTS
    // ============================
    public Map<String, Integer> getTodayTopProductsForCashier(int cashierId) {
        Map<String, Integer> countMap = new HashMap<>();

        try {
            LocalDate today = LocalDate.now();
            List<Order> orders = reportService.getOrdersBetween(today, today);

            orders.stream()
                    .filter(o -> o.getCashier() != null && o.getCashier().getId() == cashierId)
                    .forEach(o -> {
                        for (OrderItem it : o.getItems()) {
                            Product p = it.getProduct();
                            if (p == null) continue;

                            countMap.put(
                                    p.getName(),
                                    countMap.getOrDefault(p.getName(), 0) + it.getQuantity()
                            );
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return countMap.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(5)
                .collect(LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue()),
                        LinkedHashMap::putAll);
    }

    // ============================
    // LOW STOCK ITEMS
    // ============================
    public List<Product> getLowStockProducts() {
        try {
            return inventoryService.listAll().stream()
                    .filter(p -> p.getStockQty() <= 5)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // ============================
    // LAST 7 DAYS SALES
    // ============================
    public Map<String, Double> getLast7DaysSales() {
        Map<String, Double> map = new LinkedHashMap<>();

        try {
            LocalDate end = LocalDate.now();
            LocalDate start = end.minusDays(6);

            List<Order> orders = reportService.getOrdersBetween(start, end);

            for (int i = 0; i < 7; i++) {
                LocalDate day = start.plusDays(i);
                String label = day.getDayOfWeek().toString().substring(0, 3);

                double total = orders.stream()
                        .filter(o -> o.getOrderDateTime().toLocalDate().equals(day))
                        .mapToDouble(Order::getTotal)
                        .sum();

                map.put(label, total);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}
