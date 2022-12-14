package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public interface GenreStorage {
    Genre findGenreById(int id);

    Collection<Genre> findAll();

    Genre mapRowToGenre(ResultSet resultSet, int i) throws SQLException;

    boolean isAdded(int id);
}
