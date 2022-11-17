package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FIlmController {

    private final FilmService filmService;

    public FIlmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    Collection<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    Film put(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping("{id}")
    public Film getById(@PathVariable int id) {
        return filmService.getFilmFromStorage(id);
    }

    @PutMapping("/{id}/like/{userId}")
    Film putLike(@PathVariable("id") int filmId,
                 @PathVariable int userId) {
        return filmService.giveLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    Film deleteLike(@PathVariable("id") int filmId,
                    @PathVariable int userId) {
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    Collection<Film> getMostRatedFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopular(count);
    }

}


