
package service.impl;
import dao.UserDAO;
import model.User;
import security.PasswordHasher;
import service.AuthService;

public class AuthServiceImpl implements AuthService {
    private final UserDAO userDAO;
    public AuthServiceImpl(UserDAO userDAO) { this.userDAO = userDAO; }

    public User login(String username, String password) throws Exception {
        User u = userDAO.findByUsername(username);
        if (u == null || !u.isActive()) return null;
        return PasswordHasher.verify(password, u.getPasswordHash()) ? u : null;
    }
}
