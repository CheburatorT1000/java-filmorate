package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final GenreService genreService;
    private final UserDbStorage userDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate, GenreService genreService, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.genreService = genreService;
        this.userDbStorage = userDbStorage;
    }

    @Override
    public Film save(Film film) {
        String sqlQuery = "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());

        updateGenres(film);
        return film;
    }

    private void updateGenres(Film film) {
        if (film.getGenres() != null) {
            String sqlQueryForGenres = "INSERT INTO FILM_GENRE(FILM_ID, GENRE_ID) " +
                    "VALUES (?, ?)";
            jdbcTemplate.batchUpdate(
                    sqlQueryForGenres, film.getGenres(), film.getGenres().size(),
                    (ps, genre) -> {
                        ps.setInt(1, film.getId());
                        ps.setInt(2, genre.getId());
                    });
        } else film.setGenres(new LinkedHashSet<>());
    }

    @Override
    public Optional<Film> findFilmById(int id) {
        String sqlQuery = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA_ID, " +
                "M.MPA_ID, M.NAME " +
                "FROM FILMS AS F " +
                "JOIN MPA AS M ON F.MPA_ID = M.MPA_ID " +
                "WHERE FILM_ID = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if (filmRows.next()) {

            MPA mpa = MPA.builder()
                    .id(filmRows.getInt(7))
                    .name(filmRows.getString(8))
                    .build();

            Film film = Film.builder()
                    .id(filmRows.getInt(1))
                    .name(filmRows.getString(2))
                    .description(filmRows.getString(3))
                    .releaseDate(LocalDate.parse(filmRows.getString(4)))
                    .duration(filmRows.getLong(5))
                    .mpa(mpa)
                    .genres(new LinkedHashSet<>())
                    .build();
            loadGenres(Collections.singletonList(film));

            log.info("Найден фильм {} с именем {} ", filmRows.getInt(1),
                    filmRows.getString(2));

            return Optional.of(film);
        } else
            return Optional.empty();
    }

    private List<Genre> getGenresByFilmId(int id) {
        String sqlQuery = "SELECT G.GENRE_ID, G.NAME " +
                "FROM GENRE AS G " +
                "JOIN FILM_GENRE FG on G.GENRE_ID = FG.GENRE_ID " +
                "JOIN FILMS F on F.FILM_ID = FG.FILM_ID " +
                "WHERE F.FILM_ID = ?";

        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) ->
                Genre.builder()
                        .id(resultSet.getInt("GENRE_ID"))
                        .name(resultSet.getString("NAME"))
                        .build(), id);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {

        return Film.builder()
                .id(resultSet.getInt("FILMS.FILM_ID"))
                .name((resultSet.getString("FILMS.NAME")))
                .description(resultSet.getString("FILMS.DESCRIPTION"))
                .duration(resultSet.getLong("FILMS.DURATION"))
                .releaseDate(resultSet.getObject("FILMS.RELEASE_DATE", LocalDate.class))
                .mpa(MPA.builder()
                        .id(resultSet.getInt("MPA.MPA_ID"))
                        .name(resultSet.getString("MPA.NAME"))
                        .build())
                .genres(new LinkedHashSet<>())
                .build();
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE FILMS SET " +
                "NAME = ?, " +
                "DESCRIPTION = ?, " +
                "RELEASE_DATE = ?, " +
                "DURATION = ?, " +
                "MPA_ID = ?" +
                "WHERE FILM_ID = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        String sqlQueryForDeleteGenres = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQueryForDeleteGenres, film.getId());

        updateGenres(film);
        return film;
    }

    private void loadGenres(List<Film> films) {
        String sqlGenres = "SELECT FILM_ID, G.* " +
                "FROM FILM_GENRE " +
                "JOIN GENRE G ON G.GENRE_ID = FILM_GENRE.GENRE_ID " +
                "WHERE FILM_ID IN (:ids)";

        List<Integer> ids = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        Map<Integer, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film, (a, b) -> b));
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        SqlRowSet sqlRowSet = namedJdbcTemplate.queryForRowSet(sqlGenres, parameters);
        while (sqlRowSet.next()) {
            int filmId = sqlRowSet.getInt("FILM_ID");
            int genreId = sqlRowSet.getInt("GENRE_ID");
            String name = sqlRowSet.getString("NAME");
            filmMap.get(filmId).getGenres().add(Genre.builder()
                    .id(genreId)
                    .name(name)
                    .build());
        }
        films.forEach(film -> film.getGenres().addAll(filmMap.get(film.getId()).getGenres()));
    }

    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "SELECT * " +
                "FROM FILMS F " +
                "JOIN MPA M ON F.MPA_ID = M.MPA_ID";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        loadGenres(films);
        return films;
    }

    @Override
    public void putLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery,
                filmId,
                userId);
    }

    @Override
    public boolean deleteUsersLike(Film film, User user) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE USER_ID = ?";

        return jdbcTemplate.update(sqlQuery, user.getId()) > 0;
    }

    @Override
    public List<Film> getPopular(int count) {
        String sqlQuery = "SELECT FILMS.FILM_ID, FILMS.NAME, DESCRIPTION, RELEASE_DATE, DURATION, M.MPA_ID, M.NAME " +
                "FROM FILMS " +
                "LEFT JOIN FILM_LIKES FL ON FILMS.FILM_ID = FL.FILM_ID " +
                "LEFT JOIN MPA M on M.MPA_ID = FILMS.MPA_ID " +
                "GROUP BY FILMS.FILM_ID, FL.FILM_ID IN ( " +
                "SELECT FILM_ID " +
                "FROM FILM_LIKES " +
                ") " +
                "ORDER BY COUNT(FL.FILM_ID) DESC " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
        loadGenres(films);
        return films;
    }

    @Override
    public void deleteById(int filmId) {
        String sqlQuery = "DELETE FROM FILMS WHERE FILM_ID = ?";

        jdbcTemplate.update(sqlQuery, filmId);
    }
}