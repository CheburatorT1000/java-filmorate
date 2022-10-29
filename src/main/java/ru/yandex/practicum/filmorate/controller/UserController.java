package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int userId = 0;

    private int makeID() {
        return ++userId;
    }

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) throws ValidationException {

        if (users.containsKey(user.getId()))
            throw new ValidationException("Пользователь с таким id уже существует!");

        if (user.getLogin().contains(" "))
            throw new ValidationException("Пробел в login недопустим!");

        if (user.getName() == null || user.getName().isBlank())
            user.setName(user.getLogin());

        log.info("Создаем объект");
        user.setId(makeID());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) throws ValidationException {

        if (!users.containsKey(user.getId()))
            throw new ValidationException("Пользователь отсутствует!");

        if (user.getLogin().contains(" "))
            throw new ValidationException("Пробел в login недопустим!");

        if (user.getName() == null || user.getName().isBlank())
            user.setName(user.getLogin());

        log.info("Создаем объект");
        users.put(user.getId(), user);
        return user;
    }
}
