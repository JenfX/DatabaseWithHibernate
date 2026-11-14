import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import userservice.dao.UserDao;
import userservice.entity.User;
import userservice.exception.DaoException;
import userservice.exception.ValidationException;
import userservice.service.UserServiceDao;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceDao service;

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("Сохранение пользователя с корректными данными")
        void validUserIsSaved() {
            User user = new User("Иван", "ivan@mail.com", 20);
            when(userDao.emailExists("ivan@mail.com")).thenReturn(false);
            when(userDao.create(user)).thenReturn(user);

            User result = service.create(user);

            assertSame(user, result);
            verify(userDao).create(user);
        }

        @Test
        @DisplayName("Занятый email — ValidationException, сохранение не вызывается")
        void duplicateEmailIsRejected() {
            User user = new User("Иван", "ivan@mail.com", 20);
            when(userDao.emailExists("ivan@mail.com")).thenReturn(true);

            assertThrows(ValidationException.class, () -> service.create(user));

            verify(userDao, never()).create(any());
        }

        @Test
        @DisplayName("Пустое имя — ValidationException, без обращения в БД")
        void emptyNameIsRejected() {
            User user = new User("", "ivan@mail.com", 20);
            assertThrows(ValidationException.class, () -> service.create(user));
            verifyNoInteractions(userDao);
        }
    }

    @Nested
    @DisplayName("read()")
    class Read {

        @Test
        @DisplayName("Возврат существующего пользователя")
        void existingUserIsReturned() {
            User user = new User("Иван", "ivan@mail.com", 20);
            when(userDao.read(1L)).thenReturn(Optional.of(user));

            assertEquals(Optional.of(user), service.read(1L));
        }

        @Test
        @DisplayName("Ошибка БД пробрасывается из сервиса")
        void daoExceptionIsPropagated() {
            when(userDao.readAll()).thenThrow(new DaoException("БД недоступна", null));
            assertThrows(DaoException.class, () -> service.readAll());
        }
    }


}
