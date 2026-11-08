package userservice.dao;

import userservice.entity.User;
import userservice.exception.DaoException;
import userservice.exception.ValidationException;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserDaoHibernate implements UserDao{

    private static final Logger log = LoggerFactory.getLogger(UserDaoHibernate.class);
    private final SessionFactory sessionFactory;

    public UserDaoHibernate(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User create(User user) {
        log.debug("Сохранение пользователя email={}", user.getEmail());
        try {
            sessionFactory.inTransaction(session -> session.persist(user));
            log.debug("Пользователь сохранён, id={}", user.getId());
            return user;
        }
        catch (HibernateException e) {
            log.error("Ошибка при сохранении пользователя email={}", user.getEmail(), e);
            throw new DaoException("Ошибка при сохранении пользователя", e);
        }
    }

    @Override
    public Optional<User> read(Long id) {
        try {
            log.debug("Чтение данных пользователя id={}", id);
            return Optional.ofNullable(sessionFactory
                    .fromSession(session -> session.find(User.class, id)));
        } catch (HibernateException e) {
            log.error("Ошибка при поиске пользователя id={}", id, e);
            throw new DaoException("Ошибка при поиске пользователя id=" + id, e);
        }
    }

    @Override
    public User update(User user) {
        try {
            User updated = sessionFactory.fromTransaction(session -> session.merge(user));
            log.debug("Данные пользователя id={} обновлены", updated.getId());
            return updated;
        }
        catch (org.hibernate.exception.ConstraintViolationException e) {
            throw new ValidationException("Email уже занят");
        }
        catch (HibernateException e) {
            log.error("Ошибка при обновлении данных пользователя id={}", user.getId(), e);
            throw new DaoException("Ошибка при обновлении пользователя", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try {
            log.debug("Удаление пользователя id={}", id);
            return sessionFactory.fromTransaction(session -> {
                User user = session.find(User.class, id);
                if (user == null) {
                    log.debug("Пользователь id={} не найден.", id);
                    return false;
                }
                session.remove(user);
                log.debug("Пользователь id={} удалён", id);
                return true;
            });
        }
        catch (HibernateException e) {
            log.error("Ошибка при удалении пользователя id={}", id, e);
            throw new DaoException("Ошибка при удалении пользователя id=" + id, e);
        }
    }

    @Override
    public List<User> readAll() {
        try {
            log.debug("Вывод списка пользователей");
            return sessionFactory
                    .fromSession(session -> session
                            .createSelectionQuery("from User order by id", User.class).getResultList());
        }
        catch (HibernateException e) {
            log.error("Ошибка при получении списка", e);
            throw new DaoException("Ошибка при получении списка", e);
        }
    }

    @Override
    public boolean emailExist(String email) {
        try {
            log.info("Запрос в БД на уникальность email");
            return sessionFactory.fromSession(session ->
                    session.createSelectionQuery("select count(u) from User u where u.email = :email", Long.class)
                            .setParameter("email", email)
                            .getSingleResult()) > 0;
        } catch (HibernateException e) {
            log.error("Ошибка при проверке email= {}", email, e);
            throw new DaoException("Ошибка при проверке почты", e);
        }
        catch (RuntimeException e) {
            throw new DaoException("Критическая ошибка при проверке Email", e);
        }
    }


}
