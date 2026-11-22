package userservice.service;

import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import userservice.entity.User;
import userservice.exception.NotFoundException;
import userservice.exception.ValidationException;
import userservice.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceSpring implements UserService{
    private static final Logger log = LoggerFactory.getLogger(UserServiceSpring.class);
    private static final int MAX_NAME_LENGTH = 50;

    private final UserRepository userRepository;

    public UserServiceSpring(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        validate(user);

        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Отказ в создании: email {} уже занят", user.getEmail());
            throw new ValidationException("Email уже занят: " + user.getEmail());
        }
        User saved = userRepository.save(user);
        log.info("Создан пользователь id={}", saved.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> read(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User update(User user) {
        validate(user);

        if (user.getId() == null || !userRepository.existsById(user.getId())) {
            throw new NotFoundException("Пользователь id=" + user.getId() + " не найден");
        }

        if (userRepository.existsByEmailAndIdNot(user.getEmail(), user.getId())) {
            log.warn("Отказ в обновлении id={}: email {} уже занят", user.getId(), user.getEmail());
            throw new ValidationException("Email уже занят: " + user.getEmail());
        }

        User saved = userRepository.save(user);
        log.info("Обновлён пользователь id={}", saved.getId());
        return saved;
    }

    @Override
    public boolean delete(Long id) {
        if (!userRepository.existsById(id)) {
            log.warn("Попытка удалить несуществующего пользователя id={}", id);
            return false;
        }
        userRepository.deleteById(id);
        log.info("Удалён пользователь id={}", id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> readAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

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
