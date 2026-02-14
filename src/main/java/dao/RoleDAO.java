package dao;

import java.util.Optional;
import model.Role;

public interface RoleDAO {
    Optional<Role> findByName(String name) throws Exception;
}
