package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.model.enums.EventType.FRIEND;
import static ru.yandex.practicum.filmorate.model.enums.Operation.ADD;
import static ru.yandex.practicum.filmorate.model.enums.Operation.REMOVE;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class UserService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    private final FeedService feedService;

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
        checkUserExist(user.getId());
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
        checkUserExist(id);
        return userStorage.findUserById(id).
                orElseThrow(() -> new NotFoundException("Пользователь не найден!"));
    }

    public void addFriend(int id, int friendId) {
        checkUserExist(id);
        checkUserExist(friendId);
        userStorage.addFriend(findUserById(id), findUserById(friendId));
        feedService.addFeed(friendId, id, FRIEND, ADD);
    }

    public void deleteFriend(int id, int friendId) {
        checkUserExist(id);
        checkUserExist(friendId);
        userStorage.deleteFriend(findUserById(id), findUserById(friendId));
        feedService.addFeed(friendId, id, FRIEND, REMOVE);
    }

    public Collection<User> getFriendsFromUser(int userId) {
        return userStorage.getFriendsFromUser(findUserById(userId).getId());
    }

    public Collection<User> getCommonFriendsFromUser(int id, int otherId) {
        checkUserExist(id);
        return userStorage.getCommonFriendsFromUser(findUserById(id).getId(), findUserById(otherId).getId());
    }

    public Collection<Feed> getFeedByUserId(Integer id) {
        checkUserExist(id);
        return feedService.getFeedByUserId(id);
    }

    public void deleteById(int userId) {
        userStorage.deleteById(userId);
    }

    public void checkUserExist(Integer id) {
        if (!userStorage.checkUserExist(id)) {
            throw new NotFoundException(String.format("User with id: %d not found", id));
        }
    }

    public Collection<Film> getRecommendations(int userWantsRecomId) {
        log.info("Найден пользователь с похожими лайками");
        int userWithCommonLikesId = userStorage.findUserWithCommonLikes(userWantsRecomId);
        log.info("Выгружаем список рекомендованных фильмов для пользователя {}", userWantsRecomId);
        return filmStorage.getFilmRecommendation(userWantsRecomId, userWithCommonLikesId);
    }
}
