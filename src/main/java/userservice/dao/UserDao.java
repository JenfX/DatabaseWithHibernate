package userservice.dao;

import userservice.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    User create(User user);
    Optional<User> read(Long id);
    User update(User user);
    boolean delete(Long id);
    List<User> readAll();
    boolean emailExists(String email);
}
