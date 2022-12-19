package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public interface GenreStorage {
    Optional<Genre> findGenreById(int id);

    Collection<Genre> findAll();

    Genre mapRowToGenre(ResultSet resultSet, int i) throws SQLException;
}
