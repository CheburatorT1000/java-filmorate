package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReviewService {

    private final ReviewStorage reviewStorage;

    public Review save(Review review) {
        if (review.getUserId() < 1)
            throw new NotFoundException("Пользователь с таким id не может существовать");

        if (review.getFilmId() < 1)
            throw new NotFoundException("Фильм с таким id не может существовать");

        return reviewStorage.save(review);
    }

    public Review update(Review review) {
        reviewStorage.update(review);
        return getReviewById(review.getReviewId());
    }

    public boolean delete(int id) {
        return reviewStorage.delete(id);
    }

    public Review getReviewById(int id) {
        return reviewStorage.getReviewById(id).
                orElseThrow(() -> new NotFoundException("Отзыв не найден!"));
    }

    public List<Review> getAllReviewsByParam(Optional<Integer> filmId, int count) {
        if (filmId.isPresent()) {
            return reviewStorage.getAllReviewsByFilmId(filmId.get(), count);
        } else
            return reviewStorage.getAllReviewsByParam(count);
    }

    public void putLikeToReview(int id, int userId) {
        reviewStorage.putLikeToReview(id, userId);
    }

    public void putDislikeToReview(int id, int userId) {
        reviewStorage.putDislikeToReview(id, userId);
    }

    public void deleteLikeToReview(int id, int userId) {
        reviewStorage.deleteLikeToReview(id, userId);
    }

    public void deleteDislikeToReview(int id, int userId) {
        reviewStorage.deleteDislikeToReview(id, userId);
    }
}
