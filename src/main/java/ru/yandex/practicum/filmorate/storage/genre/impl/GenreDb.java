package ru.yandex.practicum.filmorate.storage.genre.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Slf4j
@Repository
public class GenreDb implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDb(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre findGenreById(int id) {
        String sqlQuery = "SELECT GENRE_ID, NAME FROM GENRE WHERE GENRE_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
    }

    @Override
    public Collection<Genre> findAll() {
        String sqlQuery = "SELECT GENRE_ID, NAME FROM GENRE";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Genre mapRowToGenre(ResultSet resultSet, int i) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }

    @Override
    public boolean isAdded(int id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT GENRE_ID, NAME FROM GENRE WHERE GENRE_ID = ?", id);

        if (mpaRows.next()) {
            log.info("Найден жанр: {} {}", mpaRows.getString("GENRE_ID"),
                    mpaRows.getString("NAME"));
            return true;
        } else {
            log.info("Жанр с идентификатором {} не найден.", id);
            return false;
        }
    }
}
