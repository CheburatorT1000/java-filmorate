package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {

    private final FilmService filmService;
    private MPA mpa = MPA.builder()
            .id(3)
            .build();
    private Film film = Film.builder()
            .name("какойто фильм")
            .description("nice film")
            .releaseDate(LocalDate.parse("1967-03-03"))
            .duration(Long.valueOf(213))
            .mpa(mpa)
            .build();

    @Test
    void shouldSaveWithId1() {
        filmService.save(film);
        assertEquals(1, filmService.getFilmFromStorage(1).getId());
        assertEquals(1, filmService.findAll().size());
        assertEquals(1, filmService.getPopular(10).size());
        assertEquals(0, filmService.getFilmFromStorage(1).getGenres().size());
    }

    @Test
    void shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> filmService.getFilmFromStorage(999));
        film.setId(345);
        assertThrows(NotFoundException.class, () -> filmService.update(film));
    }

    @Test
    void shouldFailUpdateFilm() {
        film.setId(345);
        assertThrows(NotFoundException.class, () -> filmService.update(film));
    }
}