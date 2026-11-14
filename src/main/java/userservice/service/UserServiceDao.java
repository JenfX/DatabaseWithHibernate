package userservice.service;

import userservice.dao.UserDao;
import userservice.entity.User;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import userservice.exception.ValidationException;

public class UserServiceDao implements UserService{

    private static final Logger log = LoggerFactory.getLogger(UserServiceDao.class);
    private static final int MAX_NAME_LENGTH = 50;
    private final UserDao userDao;

    public UserServiceDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User create(User user) {
        validate(user);

        if (userDao.emailExists(user.getEmail())) {
            log.warn("Отказ в создании: email {} уже занят", user.getEmail());
            throw new ValidationException("Email уже занят: " + user.getEmail());
        }
        User saved = userDao.create(user);
        log.info("Создан пользователь id={}", saved.getId());
        return saved;
    }

    @Override
    public Optional<User> read(Long id) { return userDao.read(id); }

    @Override
    public User update(User user) {
        return userDao.update(user);
    }

    @Override
    public boolean delete(Long id) {
        return userDao.delete(id);
    }

    @Override
    public List<User> readAll() {
        return userDao.readAll();
    }

    @Override
    public boolean emailExists(String email) {return userDao.emailExists(email); }

    private void validate(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            throw new ValidationException("Имя не может быть пустым");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Имя длиннее " + MAX_NAME_LENGTH + " символов");
        }
        Integer age = user.getAge();
        if (age != null && (age < 0 || age > 150)) {
            throw new ValidationException("Возраст должен быть от 0 до 150");
        }
    }
}
