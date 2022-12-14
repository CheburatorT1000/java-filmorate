package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {

    @Autowired
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.save(film);
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping("{id}")
    public Film getById(@PathVariable int id) {
        return filmService.getFilmFromStorage(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film putLike(@PathVariable("id") int filmId,
                        @PathVariable int userId) {
        return filmService.putLike(filmId, userId);
    }
    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") int filmId,
                           @PathVariable int userId) {
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getMostRatedFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopular(count);
    }
}


