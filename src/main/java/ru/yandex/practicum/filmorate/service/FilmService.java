package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;

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
        validateExistenceForPOST(film);
        validateDescription(film);
        validateReleaseDate(film);

        log.info("Создаем объект в билдере");
        Film filmFromCreator = filmCreator(film);

        log.info("Добавляем объект в коллекцию");
        return filmStorage.save(filmFromCreator);
    }

    public Film update(Film film) {

        log.info("Проверяем film в валидаторах");
        validateExistenceForPUT(film);
        validateDescription(film);
        validateReleaseDate(film);

        log.info("Создаем объект в билдере");
        Film filmFromCreator = filmCreator(film);

        log.info("Добавляем объект в коллекцию");

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

    public void validateExistenceForPOST(Film film) {
        if (filmStorage.isAdded(film.getId())) {
            log.info("Id фильма '{}' ", film.getId());
            throw new ValidationException("Фильм с таким id уже существует!");
        }
    }

    public void validateExistenceForPUT(Film film) {
        if (!filmStorage.isAdded(film.getId())) {
            log.info("Id фильма '{}' ", film.getId());
            throw new NotFoundException("Фильм с таким id осутствует!");
        }
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

        if (filmStorage.isAdded(id))
            return filmStorage.findFilmById(id);
        else
            throw new NotFoundException("Фильм не найден!");
    }

    public Film giveLike(int filmId, int userId) {

        Film film = getFilmFromStorage(filmId);
        User user = userService.findUserById(userId);

        if (filmStorage.isUsersLikeAdded(film, user))
            throw new ValidationException("Лайк от пользователя уже добавлен!");
        else
            filmStorage.getUsersLike(filmId, userId);

        return film;
    }

    public Film deleteLike(int filmId, int userId) {

        Film film = getFilmFromStorage(filmId);
        User user = userService.findUserById(userId);

        if (filmStorage.isUsersLikeAdded(film, user))
            filmStorage.deleteUsersLike(film, user);
        else
            throw new NotFoundException("Лайк от пользователя отсутствует!");

        return film;
    }

    public Collection<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }

    public Collection<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }


    public Collection<MPA> getAllMPA() {
        return filmStorage.getAllMPA();
    }

    public MPA getMPAById(int id) {

        if (filmStorage.isMpaAdded(id))
            return filmStorage.getMPAById(id);
        else
            throw new NotFoundException("Рейтинг с таким id отсутствует");
    }
    public Genre getGenreById(int id) {

        if (filmStorage.isGenreAdded(id))
            return filmStorage.getGenreById(id);
        else
            throw new NotFoundException("Жанр с таким id отсутствует");
    }
}
