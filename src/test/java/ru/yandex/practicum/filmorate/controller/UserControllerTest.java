package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void testSaveWithId1() {
        userService.create(user);

        assertEquals(1, userService.findUserById(1).getId());
        assertEquals(1, userService.findAll().size());

        user.setId(1);
        user.setName("Test");
        userService.update(user);

        assertEquals("Test", userService.findUserById(1).getName());
    }

    @Test
    void shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.findUserById(999));
    }
}