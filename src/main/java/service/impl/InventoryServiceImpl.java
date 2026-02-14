package service.impl;

import dao.ProductDAO;
import dao.impl.ProductDAOImpl;
import model.Product;
import service.InventoryService;

import java.util.List;

public class InventoryServiceImpl implements InventoryService {

    private final ProductDAO productDAO = new ProductDAOImpl();

    @Override
    public List<Product> listAll() throws Exception {
        return productDAO.findAll();
    }

    @Override
    public void addProduct(Product p) throws Exception {
        productDAO.create(p);
    }

    @Override
    public void updateProduct(Product p) throws Exception {
        productDAO.update(p);
    }

    @Override
    public void deleteProduct(int id) throws Exception {
        productDAO.delete(id);
    }

    @Override
    public void decrementStock(int productId, int qty) throws Exception {
        productDAO.updateStock(productId, qty);
    }
}
