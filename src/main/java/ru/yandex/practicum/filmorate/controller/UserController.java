package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int Id = 0;

    private int makeID() {
        return ++Id;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Выводим список всех пользователей, размер списка: '{}'", users.size());
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {

        log.info("Проверяем user в валидаторах");
        validateExistenceForPOST(user);
        validateLogin(user);
        user = validateName(user);

        log.info("Присваиваем id");
        user.setId(makeID());

        User userFromCreator = userCreator(user);

        log.info("Добавляем объект в коллекцию");
        users.put(userFromCreator.getId(), userFromCreator);

        return userFromCreator;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {

        log.info("Проверяем user в валидаторах");
        validateExistenceForPUT(user);
        validateLogin(user);
        user = validateName(user);

        User userFromCreator = userCreator(user);

        log.info("Обновляем объект в коллекции");
        users.put(userFromCreator.getId(), userFromCreator);

        return user;
    }

    public User userCreator(User user) {
        log.info("Создаем объект");
        User userFromBuilder = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();
        log.info("Объект User создан, имя : '{}'", userFromBuilder.getName());
        return userFromBuilder;
    }

    public void validateExistenceForPOST(User user) {
        if (users.containsKey(user.getId())) {
            log.info("Id пользователя '{}' ", user.getId());
            throw new ValidationException("Пользователь с таким id уже существует!");
        }
    }

    public void validateExistenceForPUT(User user) {
        if (!users.containsKey(user.getId())) {
            log.info("Id пользователя '{}' ", user.getId());
            throw new ValidationException("Пользователь отсутствует!");
        }
    }

    public void validateLogin(User user) {
        if (user.getLogin().contains(" ")) {
            log.info("login пользователя '{}' ", user.getLogin());
            throw new ValidationException("Пробел в login недопустим!");
        }
    }
    public User validateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Присваиваем поле login '{}' для поля name '{}' ", user.getLogin(), user.getName());
            user.setName(user.getLogin());
        }
        return user;
    }
}
