package ru.yandex.practicum.filmorate.storage.mpa.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
public class MpaDb implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDb(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<MPA> findMpaById(int id) {
        String sqlQuery = "SELECT MPA_ID, NAME FROM MPA WHERE MPA_ID = ?";

        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if(mpaRows.next()) {
            MPA mpa = MPA.builder()
                    .id(mpaRows.getInt("MPA_ID"))
                    .name(mpaRows.getString("NAME"))
                    .build();
            log.info("Найден рейтинг {} с названием {} ", mpaRows.getInt("MPA_ID"),
                    mpaRows.getString("NAME"));
            return Optional.of(mpa);
        } else {
            log.info("Рейтинг с id {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public Collection<MPA> findAll() {
        String sqlQuery = "SELECT MPA_ID, NAME FROM MPA";

        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public MPA mapRowToMpa(ResultSet resultSet, int i) throws SQLException {
        return MPA.builder()
                .id(resultSet.getInt("MPA_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }
}
