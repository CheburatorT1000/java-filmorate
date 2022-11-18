package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    public UserStorage userStorage;
    public UserService userService;
    public UserController userController;
    public User user;

    @BeforeEach
    public void preparingToTest() {

        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        userController = new UserController(userService);
        user = User.builder()
                .email("mail@mail.ru")
                .login("Vasya")
                .birthday(LocalDate.EPOCH)
                .friends(new HashSet<>())
                .build();
    }

    @Test
    public void shouldReturnListWithUsers() {
        userController.create(user);
        user.setId(2);
        userController.create(user);
        user.setId(3);
        userController.create(user);
        assertEquals(3, userController.findAll().size());
    }

    @Test
    public void shouldCreateUserWithIdEquals1() {
        User userFormTest = userController.create(user);
        assertEquals(1, userFormTest.getId());
    }

    @Test
    public void shouldFailValidation() {
        user.setLogin("login WithSpace");
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    public void shouldCreateUserWithEmptyName() {
        userController.create(user);
        assertEquals("Vasya", user.getName());
    }

    @Test
    public void shouldFailValidationId() {
        userController.create(user);
        assertThrows(NotFoundException.class, () -> userController.put(user));
    }

    @Test
    public void shouldFailValidationForPutMethod() {
        userController.create(user);
        user.setId(89);
        assertThrows(NotFoundException.class, () -> userController.put(user));
    }

    @Test
    public void shouldUpdateName() {
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
    public void shouldFailUpdateMethod() {
        userController.create(user);
        User user2 = User.builder()
                .id(5)
                .email("mail@mail.ru")
                .login("Masha")
                .birthday(LocalDate.EPOCH)
                .build();
        assertThrows(NotFoundException.class, () -> userController.put(user2));
    }
}