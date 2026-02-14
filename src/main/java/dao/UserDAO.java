
package dao;
import model.User;
public interface UserDAO {
    User findByUsername(String username) throws Exception;
    void create(User user) throws Exception;
}