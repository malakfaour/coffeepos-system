// service/InventoryService.java
package service;
import model.Product;
import java.util.List;
public interface InventoryService {
    List<Product> listAll() throws Exception;
    void addProduct(Product p) throws Exception;
    void updateProduct(Product p) throws Exception;
    void deleteProduct(int id) throws Exception;
    void decrementStock(int productId, int qty) throws Exception;
}
