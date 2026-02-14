package dao;

import java.util.List;
import model.Product;

public interface ProductDAO {
    List<Product> findAll() throws Exception;
    Product findBySku(String sku) throws Exception;
    void create(Product product) throws Exception;
    void update(Product product) throws Exception;
    void delete(int id) throws Exception;
    List<Product> search(String keyword) throws Exception;

    Product findByBarcode(String barcode) throws Exception;

    void updateStock(int productId, int qtySold) throws Exception;
}
