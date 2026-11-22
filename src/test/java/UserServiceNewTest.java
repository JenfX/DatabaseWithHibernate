

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import userservice.entity.User;
import userservice.exception.NotFoundException;
import userservice.exception.ValidationException;
import userservice.repository.UserRepository;
import userservice.service.UserServiceSpring;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceNewTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceSpring service;

    private static User userWithId(Long id, String name, String email, Integer age) {
        User user = new User(name, email, age);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("Корректный пользователь сохраняется")
        void validUserIsSaved() {
            User user = new User("Иван", "ivan@mail.com", 20);
            when(userRepository.existsByEmail("ivan@mail.com")).thenReturn(false);
            when(userRepository.save(user)).thenReturn(user);

            User result = service.create(user);

            assertSame(user, result);
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Занятый email — ValidationException, сохранения нет")
        void duplicateEmailIsRejected() {
            User user = new User("Иван", "ivan@mail.com", 20);
            when(userRepository.existsByEmail("ivan@mail.com")).thenReturn(true);

            assertThrows(ValidationException.class, () -> service.create(user));

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Пустое имя — ValidationException, к БД не обращаемся")
        void emptyNameIsRejected() {
            User user = new User("", "ivan@mail.com", 20);

            assertThrows(ValidationException.class, () -> service.create(user));

            verifyNoInteractions(userRepository);
        }
    }

    @Nested
    @DisplayName("read() / readAll()")
    class Read {

        @Test
        @DisplayName("Существующий пользователь возвращается в Optional")
        void existingUserIsReturned() {
            User user = userWithId(1L, "Иван", "ivan@mail.com", 20);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            Optional<User> result = service.read(1L);

            assertTrue(result.isPresent());
            assertSame(user, result.get());
        }

        @Test
        @DisplayName("Несуществующий пользователь — пустой Optional")
        void missingUserReturnsEmpty() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertTrue(service.read(99L).isEmpty());
        }

        @Test
        @DisplayName("readAll() возвращает список из репозитория")
        void readAllReturnsList() {
            List<User> users = List.of(
                    userWithId(1L, "Иван", "ivan@mail.com", 20),
                    userWithId(2L, "Пётр", "petr@mail.com", 30));
            when(userRepository.findAll()).thenReturn(users);

            assertEquals(users, service.readAll());
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("Корректное обновление сохраняется")
        void validUpdateIsSaved() {
            User user = userWithId(1L, "Иван Петрович", "ivan@mail.com", 21);
            when(userRepository.existsById(1L)).thenReturn(true);
            when(userRepository.existsByEmailAndIdNot("ivan@mail.com", 1L)).thenReturn(false);
            when(userRepository.save(user)).thenReturn(user);

            User result = service.update(user);

            assertSame(user, result);
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Несуществующий пользователь — NotFoundException")
        void missingUserThrowsNotFound() {
            User user = userWithId(99L, "Иван", "ivan@mail.com", 20);
            when(userRepository.existsById(99L)).thenReturn(false);

            assertThrows(NotFoundException.class, () -> service.update(user));

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Email занят другим пользователем — ValidationException")
        void emailTakenByAnotherUser() {
            User user = userWithId(1L, "Иван", "petr@mail.com", 20);
            when(userRepository.existsById(1L)).thenReturn(true);
            when(userRepository.existsByEmailAndIdNot("petr@mail.com", 1L)).thenReturn(true);

            assertThrows(ValidationException.class, () -> service.update(user));

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("Существующий пользователь удаляется, возвращается true")
        void existingUserIsDeleted() {
            when(userRepository.existsById(1L)).thenReturn(true);

            assertTrue(service.delete(1L));

            verify(userRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Несуществующий пользователь — false, удаления нет")
        void missingUserReturnsFalse() {
            when(userRepository.existsById(99L)).thenReturn(false);

            assertFalse(service.delete(99L));

            verify(userRepository, never()).deleteById(any());
        }
    }
}