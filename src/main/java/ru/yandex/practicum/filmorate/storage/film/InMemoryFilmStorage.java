package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    public int makeID() {
        return ++id;
    }

    @Override
    public Film add(Film film) {
        film.setId(makeID());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film get(int id) {
        return films.get(id);
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public boolean isAdded(int id) {
        return films.containsKey(id);
    }
}
