package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreControllerTest {

    private final GenreStorage genreStorage;

    @Test
    void getAllGenres() {
        Collection<Genre> genres = genreStorage.findAll();
        assertEquals(6, genres.size());
    }
    @Test
    void getGenreById() {
        Genre genre = genreStorage.findGenreById(4);
        assertEquals("Триллер", genre.getName());
    }
}