package userservice.service;

import userservice.dao.UserDao;
import userservice.entity.User;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserServiceDao implements UserService{

    private static final Logger log = LoggerFactory.getLogger(UserServiceDao.class);
    private static final int MAX_NAME_LENGTH = 50;
    private final UserDao userDao;

    public UserServiceDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User create(User user) {
        return userDao.create(user);
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
    public boolean emailExist(String email) {return userDao.emailExist(email); }
}
