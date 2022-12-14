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

@Slf4j
@Repository
public class MpaDb implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDb(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MPA findMpaById(int id) {
        String sqlQuery = "SELECT RATE_ID, NAME FROM MPA_RATE WHERE RATE_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id);
    }

    @Override
    public Collection<MPA> findAll() {
        String sqlQuery = "SELECT RATE_ID, NAME FROM MPA_RATE";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public MPA mapRowToMpa(ResultSet resultSet, int i) throws SQLException {
        return MPA.builder()
                .id(resultSet.getInt("RATE_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }
    @Override
    public boolean isAdded(int id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT RATE_ID, NAME FROM MPA_RATE WHERE RATE_ID = ?", id);

        if (mpaRows.next()) {
            log.info("Найден рейтинг: {} {}", mpaRows.getString("RATE_ID"),
                    mpaRows.getString("NAME"));
            return true;
        } else {
            log.info("Рейтинг с идентификатором {} не найден.", id);
            return false;
        }
    }
}
