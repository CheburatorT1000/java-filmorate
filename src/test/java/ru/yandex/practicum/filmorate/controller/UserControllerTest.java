package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    private final UserService userService;
    private User user = User.builder()
            .email("pochta@mail.ru")
            .login("kakoi-to_login")
            .name("asdasd")
            .birthday(LocalDate.parse("1967-03-01"))
            .build();


    @Test
    void ShouldSaveWithId1() {
        userService.create(user);
        assertEquals(1, userService.findUserById(1).getId());
    }

    @Test
    void ShouldFailGetUserById() {
        assertThrows(NotFoundException.class, () -> userService.findUserById(999));
    }
}