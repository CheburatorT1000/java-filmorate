package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public interface MpaStorage {
    Optional<MPA> findMpaById(int id);

    Collection<MPA> findAll();

    MPA mapRowToMpa(ResultSet resultSet, int i) throws SQLException;

}
