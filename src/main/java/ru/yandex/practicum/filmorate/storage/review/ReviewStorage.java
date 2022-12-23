package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review save(Review review);

    Review update(Review review);

    boolean delete(int id);

    Optional<Review> getReviewById(int id);

    List<Review> getAllReviewsByFilmId(int filmId, int count);

    List<Review> getAllReviewsByParam(int count);

    void putLikeToReview(int id, int userId);

    void putDislikeToReview(int id, int userId);

    void deleteLikeToReview(int id, int userId);

    void deleteDislikeToReview(int id, int userId);
}
