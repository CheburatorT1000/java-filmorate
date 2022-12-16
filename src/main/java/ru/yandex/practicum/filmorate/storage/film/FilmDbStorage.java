package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final UserDbStorage userDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaService mpaService, GenreService genreService, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
        this.genreService = genreService;
        this.userDbStorage = userDbStorage;
    }

    @Override
    public Film save(Film film) {
        String sqlQuery = "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATE_ID) " +
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
            List<Genre> uniqGenres = film.getGenres().stream()
                    .distinct()
                    .collect(Collectors.toList());

            String sqlQueryForGenres = "INSERT INTO FILM_GENRE(FILM_ID, GENRE_ID) " +
                    "VALUES (?, ?)";
            for (Genre genre : uniqGenres) {
                jdbcTemplate.update(sqlQueryForGenres, film.getId(), genre.getId());
            }
            film.setGenres(uniqGenres);
        }
    }

    @Override
    public Optional<Film> findFilmById(int id) {
        String sqlQuery = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.RATE_ID, " +
                "MR.RATE_ID, MR.NAME " +
                "FROM FILMS AS F " +
                "JOIN MPA_RATE AS MR ON F.RATE_ID = MR.RATE_ID " +
                "WHERE FILM_ID = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if(filmRows.next()) {

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
                    .genres(getGenresByFilmId(filmRows.getInt(1)))
                    .build();

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
                .id(resultSet.getInt("FILM_ID"))
                .name((resultSet.getString("NAME")))
                .description(resultSet.getString("DESCRIPTION"))
                .duration(resultSet.getLong("DURATION"))
                .releaseDate(resultSet.getObject("RELEASE_DATE", LocalDate.class))
                .mpa(mpaService.getMPAById(resultSet.getInt("RATE_ID")))
                .genres(getGenresByFilmId(resultSet.getInt("FILM_ID")))
                .build();
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE FILMS SET " +
                "NAME = ?, " +
                "DESCRIPTION = ?, " +
                "RELEASE_DATE = ?, " +
                "DURATION = ?, " +
                "RATE_ID = ?" +
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

    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "SELECT * FROM FILMS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
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
    public Collection<Film> getPopular(int count) {
        String sqlQuery = "SELECT F.FILM_ID, " +
                "F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.RATE_ID, COUNT(FL.FILM_ID) " +
                "FROM FILMS AS F " +
                "LEFT JOIN FILM_LIKES AS FL ON F.FILM_ID = FL.FILM_ID " +
                "GROUP BY F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.RATE_ID " +
                "ORDER BY COUNT(FL.FILM_ID) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }
}