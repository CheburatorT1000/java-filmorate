package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        log.info("Выводим список всех пользователей");
        return userStorage.findAll();
    }

    public User create(User user) {

        log.info("Проверяем user в валидаторах");
        validateLogin(user);
        user = validateName(user);

        User userFromCreator = userCreator(user);

        log.info("Добавляем объект в коллекцию");

        return userStorage.save(userFromCreator);
    }

    public User update(User user) {

        log.info("Проверяем user в валидаторах");
        validateLogin(user);
        user = validateName(user);

        User userFromCreator = userCreator(user);
        log.info("Обновляем объект в коллекции");

        findUserById(userFromCreator.getId());
        return userStorage.update(userFromCreator);
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

    // на удаление
    public User findUserById(int id) {
        return userStorage.findUserById(id).
                orElseThrow(() -> new NotFoundException("Пользователь не найден!"));
    }

    public void addFriend(int id, int friendId) {
        userStorage.addFriend(findUserById(id), findUserById(friendId));
    }

    public void deleteFriend(int id, int friendId) {
        userStorage.deleteFriend(findUserById(id), findUserById(friendId));
    }

    public Collection<User> getFriendsFromUser(int userId) {
        return userStorage.getFriendsFromUser(findUserById(userId).getId());
    }

    public Collection<User> getCommonFriendsFromUser(int id, int otherId) {
        return userStorage.getCommonFriendsFromUser(findUserById(id).getId(), findUserById(otherId).getId());
    }

    public void deleteById(int userId) {
        userStorage.deleteById(userId);
    }
}
