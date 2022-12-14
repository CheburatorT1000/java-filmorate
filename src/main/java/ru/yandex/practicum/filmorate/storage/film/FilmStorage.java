package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FilmStorage {

    Film save(Film film);

    Film findFilmById(int id);

    Film update(Film film);

    Collection<Film> findAll();

    boolean isAdded(int id);

    void getUsersLike(int filmId, int userId);

    boolean isUsersLikeAdded(Film film, User user);

    boolean deleteUsersLike(Film film, User user);

    Collection<Film> getPopular(int count);

    Collection<Genre> getAllGenres();

    Genre getGenreById(int id);

    Collection<MPA> getAllMPA();

    MPA getMPAById(int id);

    boolean isMpaAdded(int id);

    boolean isGenreAdded(int id);
}
