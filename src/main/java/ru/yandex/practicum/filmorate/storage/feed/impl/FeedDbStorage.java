package ru.yandex.practicum.filmorate.storage.feed.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import org.springframework.jdbc.core.RowMapper;

@Component
//@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Feed> getFeedByUserId(Integer id) {
        String sqlQuery = "SELECT * FROM FEED WHERE USER_ID = ? ORDER BY TIME_STAMP ASC";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFeed, id);
    }

    @Override
    public void addFeed(Integer entityId, Integer userId, Integer timeStamp,
                        EventType eventType, Operation operation) {
        String sqlQuery = "INSERT INTO FEED(ENTITY_ID, USER_ID, TIME_STAMP, EVENT_TYPE, OPERATION) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery, entityId, userId, timeStamp, eventType.toString(), operation.toString());
    }

    private Feed mapRowToFeed(ResultSet resultSet, int rowNum) throws SQLException {
        return Feed.builder()
                .id(resultSet.getInt("EVENT_ID"))
                .entityId(resultSet.getInt("ENTITY_ID"))
                .userId(resultSet.getInt("USER_ID"))
                .timeStamp(resultSet.getInt("TIME_STAMP"))
                .eventType(EventType.valueOf(resultSet.getString("EVENT_TYPE")))
                .operation(Operation.valueOf(resultSet.getString("OPERATION")))
                .build();
    }
}