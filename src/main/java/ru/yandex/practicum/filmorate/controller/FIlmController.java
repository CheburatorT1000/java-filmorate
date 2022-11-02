package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FIlmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    private int makeID() {
        return ++id;
    }


    @GetMapping
    public Collection<Film> findAll() {
        log.info("Выводим список всех фильмов, размер списка: '{}'", films.size());
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {

        log.info("Проверяем film в валидаторах");
        validateExistenceForPOST(film);
        validateDescription(film);
        validateReleaseDate(film);

        log.info("Присваиваем id");
        film.setId(makeID());

        log.info("Создаем объект в билдере");
        Film filmFromCreator = filmCreator(film);

        log.info("Добавляем объект в коллекцию");
        films.put(filmFromCreator.getId(), filmFromCreator);

        return filmFromCreator;
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {

        log.info("Проверяем film в валидаторах");
        validateExistenceForPUT(film);
        validateDescription(film);
        validateReleaseDate(film);

        log.info("Создаем объект в билдере");
        Film filmFromCreator = filmCreator(film);

        log.info("Добавляем объект в коллекцию");
        films.put(filmFromCreator.getId(), filmFromCreator);

        return filmFromCreator;
    }

    public Film filmCreator(Film film) {
        Film filmFromBuilder = Film.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .build();
        log.info("Объект Film создан '{}'", filmFromBuilder.getName());
        return filmFromBuilder;
    }

    public void validateExistenceForPOST(Film film) {
        if (films.containsKey(film.getId())) {
            log.info("Id фильма '{}' ", film.getId());
            throw new ValidationException("Фильм с таким id уже существует!");
        }
    }

    public void validateExistenceForPUT(Film film) {
        if (!films.containsKey(film.getId())) {
            log.info("Id фильма '{}' ", film.getId());
            throw new ValidationException("Фильм с таким id осутствует!");
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
}


