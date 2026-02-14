package dao;

import java.util.List;
import model.Category;

public interface CategoryDAO {
    void create(Category category) throws Exception;
    void update(Category category) throws Exception;
    void delete(int id) throws Exception;
    Category findById(int id) throws Exception;
    List<Category> findAll() throws Exception;
}
