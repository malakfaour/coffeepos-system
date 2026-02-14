package service.impl;

import dao.ReportDAO;
import dao.impl.ReportDAOImpl;
import model.Order;
import service.ReportService;
import util.CsvExporter;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
public class ReportServiceImpl implements ReportService {

    private final ReportDAO reportDAO = new ReportDAOImpl();

    @Override
    public List<Order> getOrdersBetween(LocalDate start, LocalDate end) throws Exception {
        return reportDAO.findOrdersBetween(start, end);
    }
    @Override
    public Map<String, Double> getDailyZReport(LocalDate day) throws Exception {
        List<Order> orders = reportDAO.findOrdersBetween(day, day);
        Map<String, Double> data = new HashMap<>();

        double totalSales = orders.stream().mapToDouble(Order::getTotal).sum();
        double subtotalTotal = orders.stream().mapToDouble(Order::getSubtotal).sum();
        double discountTotal = orders.stream().mapToDouble(Order::getDiscount).sum();
        double taxTotal = orders.stream().mapToDouble(Order::getTax).sum();
        int totalOrders = orders.size();

        double cashSales = orders.stream()
                .filter(o -> "CASH".equalsIgnoreCase(o.getPaymentMethod()))
                .mapToDouble(Order::getTotal)
                .sum();

        double cardSales = orders.stream()
                .filter(o -> "CARD".equalsIgnoreCase(o.getPaymentMethod()))
                .mapToDouble(Order::getTotal)
                .sum();

        // In your model: total already = subtotal - discount + tax
        double netTotal = totalSales;

        data.put("subtotalTotal", subtotalTotal);
        data.put("discountTotal", discountTotal);
        data.put("taxTotal", taxTotal);
        data.put("totalSales", totalSales);
        data.put("netTotal", netTotal);
        data.put("cashSales", cashSales);
        data.put("cardSales", cardSales);
        data.put("totalOrders", (double) totalOrders);

        return data;
    }
    public void exportToCsv(List<Order> orders) throws Exception {
        File file = new File("sales_report.csv");
        var rows = orders.stream()
                .map(o -> new String[]{
                        o.getOrderCode(),
                        o.getOrderDateTime().toString(),
                        o.getCashier().getUsername(),
                        String.valueOf(o.getTotal())
                }).toList();

        CsvExporter.write(file.getAbsolutePath(), rows);
    }
}
