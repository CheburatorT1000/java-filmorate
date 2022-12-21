package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User save(User user);

    User update(User user);

    Collection<User> findAll();

    Optional<User> findUserById(int id);

    void addFriend(User user, User friend);

    boolean deleteFriend(User user, User friend);

    Collection<User> getFriendsFromUser(int id);

    Collection<User> getCommonFriendsFromUser(int id, int otherId);

    void deleteById(int userId);

}
