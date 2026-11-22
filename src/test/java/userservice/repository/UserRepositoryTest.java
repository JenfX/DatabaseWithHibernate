package userservice.repository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import userservice.entity.User;
import userservice.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByEmailFindsSavedEmail() {
        userRepository.save(new User("Иван", "ivan@mail.com", 20));

        assertTrue(userRepository.existsByEmail("ivan@mail.com"));
        assertFalse(userRepository.existsByEmail("other@mail.com"));
    }

    @Test
    void existsByEmailAndIdNotIgnoresOwnRecord() {
        User ivan = userRepository.save(new User("Иван", "ivan@mail.com", 20));
        User petr = userRepository.save(new User("Пётр", "petr@mail.com", 30));

        assertFalse(userRepository.existsByEmailAndIdNot("ivan@mail.com", ivan.getId())); // свой email у самого себя - не занят
        assertTrue(userRepository.existsByEmailAndIdNot("ivan@mail.com", petr.getId()));
    }

    @Test
    void createdAtIsFilledOnSave() {
        User saved = userRepository.saveAndFlush(new User("Иван", "ivan@mail.com", 20));

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void duplicateEmailViolatesUniqueConstraint() {
        userRepository.saveAndFlush(new User("Иван", "ivan@mail.com", 20));

        assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.saveAndFlush(new User("Пётр", "ivan@mail.com", 30)));
    }
}