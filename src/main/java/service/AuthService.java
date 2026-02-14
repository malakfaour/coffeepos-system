// service/AuthService.java
package service;
import model.User;
public interface AuthService {
    User login(String username, String password) throws Exception;
}
