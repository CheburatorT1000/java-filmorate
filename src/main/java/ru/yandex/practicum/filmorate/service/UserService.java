package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
        validateExistenceForPOST(user);
        validateLogin(user);
        user = validateName(user);

        User userFromCreator = userCreator(user);

        log.info("Добавляем объект в коллекцию");

        return userStorage.add(userFromCreator);
    }

    public User put(User user) {

        log.info("Проверяем user в валидаторах");
        validateExistenceForPUT(user);
        validateLogin(user);
        user = validateName(user);

        User userFromCreator = userCreator(user);

        log.info("Обновляем объект в коллекции");
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
                .friends(new HashSet<>())
                .build();
        log.info("Объект User создан, имя : '{}'", userFromBuilder.getName());
        return userFromBuilder;
    }

    public void validateExistenceForPOST(User user) {
        if (userStorage.isAdded(user.getId())) {
            log.info("Id пользователя '{}' ", user.getId());
            throw new ValidationException("Пользователь с таким id уже существует!");
        }
    }

    public void validateExistenceForPUT(User user) {
        if (!userStorage.isAdded(user.getId())) {
            log.info("Id пользователя '{}' ", user.getId());
            throw new NotFoundException("Пользователь отсутствует!");
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

    public User getUserFromStorage(int id) {

        if(userStorage.isAdded(id))
            return userStorage.get(id);
        else
            throw new NotFoundException("Пользователь не найден!");
    }

    public void addFriend(int id, int friendId) {

        User user1 = getUserFromStorage(id);
        User user2 = getUserFromStorage(friendId);

        user1.getFriends().add(friendId);
        user2.getFriends().add(id);
    }

    public void deleteFriend(int id, int friendId) {

        User user1 = getUserFromStorage(id);
        User user2 = getUserFromStorage(friendId);

        user1.getFriends().remove(friendId);
        user2.getFriends().remove(id);
    }

    public Collection<User> getFriendsFromUser(int userId) {

        User user = getUserFromStorage(userId);
        
        return user.getFriends().stream()
                .map(userStorage::get)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriendsFromUser(int id, int otherId) {

        Set<Integer> user1Friends = getUserFromStorage(id).getFriends();
        Set<Integer> user2Friends = getUserFromStorage(otherId).getFriends();

        return user1Friends.stream()
                .filter(user2Friends::contains)
                .map(userStorage::get)
                .collect(Collectors.toList());
    }
}
