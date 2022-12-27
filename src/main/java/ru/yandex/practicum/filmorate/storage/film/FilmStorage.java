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

    Collection<Film> getPopular(int count, Optional<Integer> genreId, Optional<Integer> year);

    void deleteById(int filmId);

    List<Film> getCommonFilmsByRating(long userId, long friendId);

    Collection<Film> getFilmRecommendation (int userWantsRecomId, int userWithCommonLikesId);

    Collection<Film> getSearchResults(String query, List<String> by);

    List<Film> getSortedDirectorsFilmsByYears(int id);

    List<Film> getSortedDirectorsFilmsByLikes(int id);
}
