package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import java.time.LocalDate;
import java.util.Collection;

import static ru.yandex.practicum.filmorate.model.enums.EventType.LIKE;
import static ru.yandex.practicum.filmorate.model.enums.Operation.ADD;
import static ru.yandex.practicum.filmorate.model.enums.Operation.REMOVE;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_=@Autowired)

public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final FeedService feedService;
    private final DirectorService directorService;

    public Collection<Film> findAll() {
        log.info("Выводим список всех фильмов");
        return filmStorage.findAll();
    }

    public Film save(Film film) {

        log.info("Проверяем film в валидаторах");
        validateDescription(film);
        validateReleaseDate(film);

        log.info("Создаем объект в билдере");
        Film filmFromCreator = filmCreator(film);

        log.info("Добавляем объект в коллекцию");
        return filmStorage.save(filmFromCreator);
    }

    public Film update(Film film) {

        log.info("Проверяем film в валидаторах");
        validateDescription(film);
        validateReleaseDate(film);

        log.info("Создаем объект в билдере");
        Film filmFromCreator = filmCreator(film);

        log.info("Добавляем объект в коллекцию");
        getFilmFromStorage(filmFromCreator.getId());
        return filmStorage.update(filmFromCreator);
    }

    public Film filmCreator(Film film) {

        Film filmFromBuilder = Film.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(film.getMpa())
                .genres(film.getGenres())
                .directors(film.getDirectors())
                .build();
        log.info("Объект Film создан '{}'", filmFromBuilder.getName());
        return filmFromBuilder;
    }

    public void validateDescription(Film film) {
        if (film.getDescription().length() > 200) {
            log.info("Размер описания '{}' ", film.getDescription().length());
            throw new ValidationException("Длина описания не может превышать 200 символов!");
        }
    }

    public void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Дата релиза '{}' ", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895!");
        }
    }

    public Film getFilmFromStorage(int id) {
        return filmStorage.findFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден!"));
    }

    public Film putLike(int filmId, int userId) {

        Film film = getFilmFromStorage(filmId);
        userService.findUserById(userId);

        filmStorage.putLike(filmId, userId);
        feedService.addFeed(filmId, userId, LIKE, ADD);
        return film;
    }

    public Film deleteLike(int filmId, int userId) {

        Film film = getFilmFromStorage(filmId);
        User user = userService.findUserById(userId);

        filmStorage.deleteUsersLike(film, user);
        feedService.addFeed(filmId, userId, LIKE, REMOVE);
        return film;
    }

    public List<Film> getCommonFilmsByRating(Long userId, Long friendId) {

        return filmStorage.getCommonFilmsByRating(userId, friendId);
    }

    public Collection<Film> getPopular(int count, Optional<Integer> genreId, Optional<Integer> year) {
        return filmStorage.getPopular(count, genreId, year);
    }

    public List<Film> getSortedDirectorsFilms(int id, String sortBy) {
        List<Film> films;
        directorService.findDirectorById(id);
        films = filmStorage.getSortedDirectorsFilms(id, sortBy);
        return films;
    }
    public void deleteById(int filmId) {
        filmStorage.deleteById(filmId);
        log.info("Фильм удален с id: '{}'", filmId);

    }
}
