package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> findAll() {
        log.info("Выводим список всех фильмов");
        return filmStorage.findAll();
    }

    public Film create(Film film) {

        log.info("Проверяем film в валидаторах");
        validateExistenceForPOST(film);
        validateDescription(film);
        validateReleaseDate(film);

        log.info("Создаем объект в билдере");
        Film filmFromCreator = filmCreator(film);

        log.info("Добавляем объект в коллекцию");
        return filmStorage.add(filmFromCreator);
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

    Film filmCreator(Film film) {
        Film filmFromBuilder = Film.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .usersLikes(new HashSet<>())
                .build();
        log.info("Объект Film создан '{}'", filmFromBuilder.getName());
        return filmFromBuilder;
    }

    void validateExistenceForPOST(Film film) {
        if (filmStorage.isAdded(film.getId())) {
            log.info("Id фильма '{}' ", film.getId());
            throw new ValidationException("Фильм с таким id уже существует!");
        }
    }

    void validateExistenceForPUT(Film film) {
        if (!filmStorage.isAdded(film.getId())) {
            log.info("Id фильма '{}' ", film.getId());
            throw new NotFoundException("Фильм с таким id осутствует!");
        }
    }

    void validateDescription(Film film) {
        if (film.getDescription().length() > 200) {
            log.info("Размер описания '{}' ", film.getDescription().length());
            throw new ValidationException("Длина описания не может превышать 200 символов!");
        }
    }

    void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Дата релиза '{}' ", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895!");
        }
    }

    public Film getFilmFromStorage(int id) {

        if(filmStorage.isAdded(id))
            return filmStorage.get(id);
        else
            throw new NotFoundException("Фильм не найден!");
    }

    public Film giveLike(int filmId, int userId) {

        Film film = getFilmFromStorage(filmId);
        film.getUsersLikes().add(userId);

        return film;
    }

    public Film deleteLike(int filmId, int userId) {

        Film film = getFilmFromStorage(filmId);

        if (film.getUsersLikes().contains(userId))
           film.getUsersLikes().remove(userId);
        else
            throw new NotFoundException("Лайк от пользователя отсутствует!");

        return film;
    }

    public Collection<Film> getPopular(int count) {
        return filmStorage.findAll().stream()
                .sorted((o1, o2) -> o2.getUsersLikes().size() - o1.getUsersLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
