package dao;

import model.Order;
import model.OrderItem;
import java.util.List;

public interface OrderDAO {
    int save(Order order, List<OrderItem> items) throws Exception;
    Order findById(int id) throws Exception;
}
