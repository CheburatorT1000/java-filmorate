package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Repository
@Qualifier("userDb")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User findUserById(int id) {
        String sqlQuery = "SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY " +
                "FROM USERS " +
                "WHERE USER_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
    }

    @Override
    public Collection<User> findAll() {
        String sqlQuery = "SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY " +
                "FROM USERS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User save(User user) {
        String sqlQuery = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE USERS SET " +
                "EMAIL = ?, " +
                "LOGIN = ?, " +
                "NAME = ?, " +
                "BIRTHDAY = ? " +
                "WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public void addFriend(User user, User friend) {

        String sqlQuery = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery,
                user.getId(),
                friend.getId());
    }

    @Override
    public boolean deleteFriend(User user, User friend) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE FRIEND_ID = ?";

        return jdbcTemplate.update(sqlQuery,
                friend.getId()) > 0;
    }

    @Override
    public Collection<User> getFriendsFromUser(int userId) {
        String sqlQuery = "SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY " +
                "FROM USERS " +
                "WHERE USER_ID IN " +
                "(SELECT FRIEND_ID FROM FRIENDS WHERE FRIENDS.USER_ID = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
    }

    @Override
    public Collection<User> getCommonFriendsFromUser(int id, int otherId) {
        String sqlQuery = "SELECT * FROM USERS " +
                "WHERE USER_ID IN (" +
                "SELECT F.FRIEND_ID " +
                "FROM FRIENDS AS F " +
                "JOIN FRIENDS AS FF ON F.FRIEND_ID = FF.FRIEND_ID " +
                "WHERE F.USER_ID = ?" +
                "AND FF.USER_ID = ?);";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, otherId);
    }

    @Override
    public boolean isAdded(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM USERS WHERE USER_ID = ?", id);

        if(userRows.next()) {
            log.info("Найден пользователь: {} {}", userRows.getString("USER_ID"),
                    userRows.getString("LOGIN"));
            return true;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return false;
        }
    }
    @Override
    public boolean isFriendAdded(User user, User friend) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM FRIENDS WHERE USER_ID = ? AND " +
                "FRIEND_ID = ?", friend.getId(), user.getId());

        if(userRows.next()) {
            log.info("Пользователь {} уже был добавлен в друзья {}", userRows.getString("USER_ID"),
                    userRows.getString("FRIEND_ID"));
            return true;
        } else {
            log.info("Добавляем пользователя с идентификатором {} в друзья.",
                    friend.getId());
            return false;
        }
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("USER_ID"))
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .name(resultSet.getString("NAME"))
                .birthday(resultSet.getObject("BIRTHDAY", LocalDate.class))
                .build();
    }

}
