package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FIlmControllerTest {

    FilmStorage filmStorage;
    FilmService filmService;
    FIlmController fIlmController;
    Film film;

    @BeforeEach
    void preparingForTest() {

        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage);
        fIlmController = new FIlmController(filmService);
        film = Film.builder()
                .id(1)
                .name("Приключения шурика")
                .description("С первого дня покупки шуоповерт сломался и его возят по сервисным центрам 3 месяца")
                .releaseDate(LocalDate.of(2022, 03, 14))
                .duration(20)
                .build();
    }

    @Test
    void shouldReturnAllFilms() {
        fIlmController.create(film);
        assertEquals(1, fIlmController.findAll().size());
    }

    @Test
    void shouldCreate() {
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
    void shouldPut() {
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
    void shuludCreateFilm() {
        Film film1 = fIlmController.create(film);
        Film film2 = Film.builder()
                .id(1)
                .name("Приключения шурика")
                .description("С первого дня покупки шуоповерт сломался и его возят по сервисным центрам 3 месяца")
                .releaseDate(LocalDate.of(2022, 03, 14))
                .duration(20)
                .build();
        assertEquals(film1, film2);
    }

    @Test
    void validateExistenceForPOST() {
        fIlmController.create(film);
        assertThrows(ValidationException.class, () -> fIlmController.create(film));

    }

    @Test
    void shouldFailvalidationExistenceForPUT() {
        fIlmController.create(film);
        film.setId(23);
        assertThrows(ValidationException.class, () -> fIlmController.put(film));
    }

    @Test
    void shouldFailReleaseDateValidation() {
        film.setReleaseDate(LocalDate.of(1880,12,1));
        assertThrows(ValidationException.class, () -> fIlmController.create(film));
    }

    @Test
    void shouldFailDescriptionValidation() {
        film.setDescription("ойойой! айайайа!ойойой! айайайа!ойойой! айайаййайа!ойойой! айайайа!ойойой! айайаййайа!" +
                "йайа!ойойой! айайайа!ойойой! айайаййайа!ойойой! айайайа!ойойой! айайайойойой! айайайа!ойойой! " +
                "йайа!ойойой! айайайа!ойойой! айайаййайа!ойойой! айайайа!ойойой! айайаййайа!ойойой! айайайа!ойойой! " +
                "йайа!ойойой! айайайа!ойойой! айайаййайа!ойойой! айайайа!ойойой! айайайайайайайайайа!ой");
        assertThrows(ValidationException.class, () -> fIlmController.create(film));
    }
}