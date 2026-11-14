import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import userservice.dao.UserDao;
import userservice.dao.UserDaoHibernate;
import userservice.entity.User;
import userservice.exception.DaoException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class UserDaoTest {

    @Container
    private static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    private static SessionFactory sessionFactory;
    private UserDao userDao;

    @BeforeAll
    static void initSessionFactory() {
        sessionFactory = new Configuration()
                .configure()
                .setProperty("hibernate.connection.url", postgres.getJdbcUrl())
                .setProperty("hibernate.connection.username", postgres.getUsername())
                .setProperty("hibernate.connection.password", postgres.getPassword())
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .addAnnotatedClass(User.class)
                .buildSessionFactory();
    }

    @AfterAll
    static void closeSessionFactory() {
        sessionFactory.close();
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoHibernate(sessionFactory);
        sessionFactory.inTransaction(s -> s.createMutationQuery("delete from User").executeUpdate());
    }

    @Test
    @DisplayName("create() сохраняет пользователя и присваивает id")
    void createAssignsId() {
        User user = userDao.create(new User("Иван", "ivan@mail.com", 20));

        assertNotNull(user.getId());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    @DisplayName("read() находит сохранённого пользователя")
    void readReturnsSavedUser() {
        Long id = userDao.create(new User("Иван", "ivan@mail.com", 20)).getId();

        Optional<User> found = userDao.read(id);

        assertTrue(found.isPresent());
        assertEquals("ivan@mail.com", found.get().getEmail());
    }

    @Test
    @DisplayName("read() возвращает пустой Optional для несуществующего id")
    void readMissingReturnsEmpty() {
        assertTrue(userDao.read(999L).isEmpty());
    }

    @Test
    @DisplayName("readAll() возвращает всех пользователей")
    void readAllReturnsEveryone() {
        userDao.create(new User("Иван", "ivan@mail.com", 20));
        userDao.create(new User("Пётр", "petr@mail.com", 30));

        List<User> all = userDao.readAll();

        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("update() сохраняет изменения в БД")
    void updatePersistsChanges() {
        User user = userDao.create(new User("Иван", "ivan@mail.com", 20));

        user.setName("Иван Петрович");
        userDao.update(user);

        User updated = userDao.read(user.getId()).orElseThrow();
        assertEquals("Иван Петрович", updated.getName());
    }

    @Test
    @DisplayName("delete() удаляет пользователя")
    void deleteRemovesUser() {
        Long id = userDao.create(new User("Иван", "ivan@mail.com", 20)).getId();

        assertTrue(userDao.delete(id));
        assertTrue(userDao.read(id).isEmpty());
    }

    @Test
    @DisplayName("emailExists() видит занятый email")
    void emailExistsFindsEmail() {
        userDao.create(new User("Иван", "ivan@mail.com", 20));

        assertTrue(userDao.emailExists("ivan@mail.com"));
        assertFalse(userDao.emailExists("ne_ivan@mail.com"));
    }

    @Test
    @DisplayName("Ограничение UNIQUE в БД не даёт сохранить дубликат email")
    void duplicateEmailViolatesConstraint() {
        userDao.create(new User("Иван", "ivan@mail.com", 20));

        assertThrows(DaoException.class,
                () -> userDao.create(new User("Пётр", "ivan@mail.com", 30)));
    }
}
