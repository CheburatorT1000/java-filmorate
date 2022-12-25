package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmController {

    private final FilmService filmService;

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
    public Collection<Film> getMostRatedFilms(@RequestParam(defaultValue = "10") int count,
                                              @RequestParam Optional<Integer> genreId,
                                              @RequestParam Optional<Integer> year) {
        return filmService.getPopular(count, genreId, year);
    }


    @GetMapping("/director/{directorId}")
    public List<Film> getByDirectorId(@PathVariable Integer directorId,
                                      @RequestParam String sortBy) {
        return filmService.getSortedDirectorsFilms(directorId, sortBy);
    }
    @DeleteMapping("/{filmId}")
    public void deleteById(@PathVariable int filmId) {
        filmService.deleteById(filmId);

    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam long userId, @RequestParam long friendId) {
        return filmService.getCommonFilmsByRating(userId, friendId);
    }

    @GetMapping("/search")
    public Collection<Film> getSearchResults(@RequestParam String query, @RequestParam Optional<List<String>> by) {
        if (by.isPresent()) {
            return filmService.getSearchResults(query, by.get());
        } else {
            return filmService.getPopular(10, Optional.empty(), Optional.empty());
        }
    }

}


