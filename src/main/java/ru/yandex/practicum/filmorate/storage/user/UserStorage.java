package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    boolean isAdded(int id);

    User save(User user);

    User update(User user);

    Collection<User> findAll();

    User findUserById(int id);

    void addFriend(User user, User friend);

    boolean deleteFriend(User user, User friend);

    Collection<User> getFriendsFromUser(int id);

    Collection<User> getCommonFriendsFromUser(int id, int otherId);

    boolean isFriendAdded(User user, User friend);
}
