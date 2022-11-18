package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    public FilmStorage filmStorage;
    public FilmService filmService;
    public FilmController fIlmController;
    public Film film;

    @BeforeEach
    public void preparingForTest() {

        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage);
        fIlmController = new FilmController(filmService);
        film = Film.builder()
                .id(1)
                .name("Приключения шурика")
                .description("С первого дня покупки шуоповерт сломался и его возят по сервисным центрам 3 месяца")
                .releaseDate(LocalDate.of(2022, 03, 14))
                .duration(20)
                .build();
    }

    @Test
    public void shouldReturnAllFilms() {
        fIlmController.create(film);
        assertEquals(1, fIlmController.findAll().size());
    }

    @Test
    public void shouldCreate() {
        fIlmController.create(film);
        Film film2 = Film.builder()
                .name("asdasdasd")
                .description("asdasda")
                .releaseDate(LocalDate.of(2022, 03, 14))
                .duration(20)
                .build();
        fIlmController.create(film2);
        assertEquals(2, fIlmController.findAll().size());
    }

    @Test
    public void shouldPut() {
        fIlmController.create(film);
        Film film2 = Film.builder()
                .id(1)
                .name("asdasdasd")
                .description("asdasda")
                .releaseDate(LocalDate.of(2022, 03, 14))
                .duration(20)
                .build();
        fIlmController.put(film2);
        assertEquals(1,fIlmController.findAll().size());
    }

    @Test
    public void shuludCreateFilm() {
        Film film1 = fIlmController.create(film);
        Film film2 = Film.builder()
                .id(1)
                .name("Приключения шурика")
                .description("С первого дня покупки шуоповерт сломался и его возят по сервисным центрам 3 месяца")
                .releaseDate(LocalDate.of(2022, 03, 14))
                .duration(20)
                .usersLikes(new HashSet<>())
                .build();
        assertEquals(film1, film2);
    }

    @Test
    public void validateExistenceForPOST() {
        fIlmController.create(film);
        assertThrows(ValidationException.class, () -> fIlmController.create(film));

    }

    @Test
    public void shouldFailvalidationExistenceForPUT() {
        fIlmController.create(film);
        film.setId(23);
        assertThrows(NotFoundException.class, () -> fIlmController.put(film));
    }

    @Test
    public void shouldFailReleaseDateValidation() {
        film.setReleaseDate(LocalDate.of(1880,12,1));
        assertThrows(ValidationException.class, () -> fIlmController.create(film));
    }

    @Test
    public void shouldFailDescriptionValidation() {
        film.setDescription("ойойой! айайайа!ойойой! айайайа!ойойой! айайаййайа!ойойой! айайайа!ойойой! айайаййайа!" +
                "йайа!ойойой! айайайа!ойойой! айайаййайа!ойойой! айайайа!ойойой! айайайойойой! айайайа!ойойой! " +
                "йайа!ойойой! айайайа!ойойой! айайаййайа!ойойой! айайайа!ойойой! айайаййайа!ойойой! айайайа!ойойой! " +
                "йайа!ойойой! айайайа!ойойой! айайаййайа!ойойой! айайайа!ойойой! айайайайайайайайайа!ой");
        assertThrows(ValidationException.class, () -> fIlmController.create(film));
    }
}