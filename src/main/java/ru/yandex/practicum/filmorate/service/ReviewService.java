package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.model.enums.EventType.FRIEND;
import static ru.yandex.practicum.filmorate.model.enums.EventType.REVIEW;
import static ru.yandex.practicum.filmorate.model.enums.Operation.ADD;
import static ru.yandex.practicum.filmorate.model.enums.Operation.REMOVE;
import static ru.yandex.practicum.filmorate.model.enums.Operation.UPDATE;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FeedService feedService;


    public Review save(Review review) {
        if (review != null && review.getUserId() <= 0)
            throw new NotFoundException("Пользователь с таким id не может существовать");

        if (review != null && review.getFilmId() <= 0)
            throw new NotFoundException("Фильм с таким id не может существовать");
        Review rew = reviewStorage.save(review);
        feedService.addFeed(rew.getReviewId(), rew.getUserId(), REVIEW, Operation.ADD);
        return rew;
    }

    public Review update(Review review) {
        Review rew = reviewStorage.getReviewById(review.getReviewId())
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));

        feedService.addFeed(rew.getReviewId(), rew.getUserId(), REVIEW, UPDATE);
        reviewStorage.update(review);
        return getReviewById(review.getReviewId());
    }

    public boolean delete(int id) {
        Review rew = reviewStorage.getReviewById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        feedService.addFeed(rew.getReviewId(), rew.getUserId(), REVIEW, REMOVE);
        return reviewStorage.delete(id);
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
