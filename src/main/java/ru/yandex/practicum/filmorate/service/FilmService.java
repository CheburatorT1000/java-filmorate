package ru.yandex.practicum.filmorate.service;

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
import java.util.Optional;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

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
        User user = userService.findUserById(userId);

        filmStorage.putLike(filmId, userId);
        return film;
    }

    public Film deleteLike(int filmId, int userId) {

        Film film = getFilmFromStorage(filmId);
        User user = userService.findUserById(userId);

        filmStorage.deleteUsersLike(film, user);
        return film;
    }

    public Collection<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }

    public void deleteById(int filmId) {
        filmStorage.deleteById(filmId);
        log.info("Фильм удален с id: '{}'", filmId);
    }
}
