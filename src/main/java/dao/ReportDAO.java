package dao;
import model.OrderItem;

import java.time.LocalDate;
import java.util.List;
import model.Order;

public interface ReportDAO {
    List<Order> findOrdersBetween(LocalDate start, LocalDate end) throws Exception;

 // ADD THIS METHOD ↓↓↓↓↓↓↓↓↓↓↓
 List<OrderItem> findOrderItemsBetween(LocalDate start, LocalDate end) throws Exception;
}
