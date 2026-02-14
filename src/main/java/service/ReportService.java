package service;

import model.Order;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
public interface ReportService {
    List<Order> getOrdersBetween(LocalDate start, LocalDate end) throws Exception;
    Map<String, Double> getDailyZReport(LocalDate day) throws Exception;
}
