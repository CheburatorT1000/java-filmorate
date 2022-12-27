package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.model.enums.EventType.REVIEW;
import static ru.yandex.practicum.filmorate.model.enums.Operation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FeedService feedService;


    public Review save(Review review) {
        if (review != null && review.getUserId() <= 0)
            throw new NotFoundException("Пользователь с таким id не может существовать");

        if (review != null && review.getFilmId() <= 0)
            throw new NotFoundException("Фильм с таким id не может существовать");
        Review rew = reviewStorage.save(review);
        feedService.addFeed(rew.getReviewId(), rew.getUserId(), REVIEW, ADD);
        return rew;
    }

    public Review update(Review review) {
        Review rew = reviewStorage.getReviewById(review.getReviewId())
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));

        reviewStorage.update(review);
        feedService.addFeed(rew.getReviewId(), rew.getUserId(), REVIEW, UPDATE);
        return getReviewById(review.getReviewId());
    }

    public boolean delete(int id) {
        Review rew = reviewStorage.getReviewById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));

        boolean isDeleted = reviewStorage.delete(id);
        feedService.addFeed(rew.getReviewId(), rew.getUserId(), REVIEW, REMOVE);

        return isDeleted;
    }

    public Review getReviewById(int id) {
        return reviewStorage.getReviewById(id).
                orElseThrow(() -> new NotFoundException("Отзыв не найден!"));
    }

    public List<Review> getAllReviewsByParam(Optional<Integer> filmId, int count) {
        if (filmId.isPresent()) {
            return reviewStorage.getReviewsByFilmId(filmId.get(), count);
        } else
            return reviewStorage.getAllReviewsByParam(count);
    }

    public void putLikeOrDislikeToReview(int id, int userId, int vote) {
        reviewStorage.putLikeOrDislikeToReview(id, userId, vote);
    }

    public void deleteLikeOrDislikeToReview(int id, int userId, int vote) {
        reviewStorage.deleteLikeOrDislikeToReview(id, userId, vote);
    }
}
