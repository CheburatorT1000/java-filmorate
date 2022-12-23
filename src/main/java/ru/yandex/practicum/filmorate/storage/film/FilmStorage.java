package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film save(Film film);

    Optional<Film> findFilmById(int id);

    Film update(Film film);

    Collection<Film> findAll();

    void putLike(int filmId, int userId);

    boolean deleteUsersLike(Film film, User user);

    Collection<Film> getPopular(int count);

    List<Film> getSortedDirectorsFilms(int id, String sortBy);

    void deleteById(int filmId);

    List<Film> getCommonFilmsByRating(long userId, long friendId);

    List<Film> getSearchResults(String query, List<String> by);

}
