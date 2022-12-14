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
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    private final UserDbStorage userDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaStorage mpaStorage, GenreStorage genreStorage, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
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
        return film;
    }

    @Override
    public Film findFilmById(int id) {
        String sqlQuery = "SELECT FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATE_ID " +
                "FROM FILMS " +
                "WHERE FILM_ID = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
    }

    private List<Genre> getGenresByFilmId(int id) {
        String sqlQuery = "SELECT GENRE_ID, NAME " +
                "FROM GENRE " +
                "WHERE GENRE_ID IN (" +
                "SELECT GENRE_ID " +
                "FROM FILM_GENRE " +
                "WHERE FILM_ID = ?)";
        return jdbcTemplate.query(sqlQuery, genreStorage::mapRowToGenre, id);
    }

    private MPA getMpa(int id) {
        String sqlQuery = "SELECT * FROM MPA_RATE WHERE RATE_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, mpaStorage::mapRowToMpa, id);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("FILM_ID"))
                .name((resultSet.getString("NAME")))
                .description(resultSet.getString("DESCRIPTION"))
                .duration(resultSet.getLong("DURATION"))
                .releaseDate(resultSet.getObject("RELEASE_DATE", LocalDate.class))
                .mpa(getMpa(resultSet.getInt("RATE_ID")))
                .genres(getGenresByFilmId(resultSet.getInt("FILM_ID")))
                .build();
    }

    @Override
    public boolean isAdded(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILMS WHERE FILM_ID = ?", id);

        if (userRows.next()) {
            log.info("Найден фильм: {} {}", userRows.getString("FILM_ID"),
                    userRows.getString("NAME"));
            return true;
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            return false;
        }
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

        if (film.getGenres() != null) {

            List<Genre> uniqGenres = film.getGenres().stream()
                    .distinct()
                    .collect(Collectors.toList());
            System.out.println(uniqGenres);

            String sqlQueryForGenres = "INSERT INTO FILM_GENRE(FILM_ID, GENRE_ID) " +
                    "VALUES (?, ?)";
            for (Genre genre : uniqGenres) {
                jdbcTemplate.update(sqlQueryForGenres, film.getId(), genre.getId());
            }
            film.setGenres(uniqGenres);
        }
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "SELECT * FROM FILMS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public void getUsersLike(int filmId, int userId) {

        String sqlQuery = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery,
                filmId,
                userId);
    }

    @Override
    public boolean isUsersLikeAdded(Film film, User user) {
        SqlRowSet filmLikesRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILM_LIKES WHERE FILM_ID = ? AND " +
                "USER_ID = ?", film.getId(), user.getId());

        if (filmLikesRows.next()) {
            log.info("Пользователь {} уже поставил лайк фильму {}", filmLikesRows.getString("USER_ID"),
                    filmLikesRows.getString("FILM_ID"));
            return true;
        } else {
            log.info("Добавляем лайк пользователя с идентификатором {} к фильму с идентификатором {}.",
                    user.getId(), film.getId());
            return false;
        }
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

    @Override
    public Collection<Genre> getAllGenres() {
        return genreStorage.findAll();
    }

    @Override
    public Genre getGenreById(int id) {
        return genreStorage.findGenreById(id);
    }

    @Override
    public Collection<MPA> getAllMPA() {
        return mpaStorage.findAll();
    }

    @Override
    public MPA getMPAById(int id) {
        return mpaStorage.findMpaById(id);
    }

    @Override
    public boolean isMpaAdded(int id) {
        return mpaStorage.isAdded(id);
    }

    @Override
    public boolean isGenreAdded(int id) {
        return genreStorage.isAdded(id);
    }

}