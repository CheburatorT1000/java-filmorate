package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film add(Film film);

    Film get(int id);

    Film update(Film film);

    Collection<Film> findAll();

    boolean isAdded(int id);

}
