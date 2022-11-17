package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    UserStorage userStorage;
    UserService userService;
    UserController userController;
    User user;

    @BeforeEach
    void preparingToTest() {

        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        userController = new UserController(userService);
        user = User.builder()
                .email("mail@mail.ru")
                .login("Vasya")
                .birthday(LocalDate.EPOCH)
                .build();
    }

    @Test
    void shouldReturnListWithUsers() {
        userController.create(user);
        user.setId(2);
        userController.create(user);
        user.setId(3);
        userController.create(user);
        assertEquals(3, userController.findAll().size());
    }

    @Test
    void shouldCreateUserWithIdEquals1() {
        User userFormTest = userController.create(user);
        assertEquals(1, userFormTest.getId());
    }

    @Test
    void shouldFailValidation() {
        user.setLogin("login WithSpace");
        assertThrows(ValidationException.class, () -> userController.put(user));
    }

    @Test
    void shouldCreateUserWithEmptyName() {
        userController.create(user);
        assertEquals("Vasya", user.getName());
    }

    @Test
    void shouldFailValidationId() {
        userController.create(user);
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldFailValidationForPutMethod() {
        userController.create(user);
        user.setId(89);
        assertThrows(ValidationException.class, () -> userController.put(user));
    }

    @Test
    void shouldUpdateName() {
        userController.create(user);
        User user2 = User.builder()
                .id(1)
                .email("mail@mail.ru")
                .login("Masha")
                .birthday(LocalDate.EPOCH)
                .build();
        userController.put(user2);
        List<User> tempList = new ArrayList<User>(userController.findAll());
        User user3 = tempList.get(0);
        assertEquals("Masha", user3.getName());
    }

    @Test
    void shouldFailUpdateMethod() {
        userController.create(user);
        User user2 = User.builder()
                .id(5)
                .email("mail@mail.ru")
                .login("Masha")
                .birthday(LocalDate.EPOCH)
                .build();
        assertThrows(ValidationException.class, () -> userController.put(user2));
    }
}