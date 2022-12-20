package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.save(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        reviewService.delete(id);
    }

    @GetMapping("{id}")
    public Review getReview(@PathVariable int id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getAllReviewsByParam(@RequestParam Optional<Integer> filmId,
                                        @RequestParam(defaultValue = "10") int count) {
        return reviewService.getAllReviewsByParam(filmId, count);
    }

    @PutMapping("{id}/like/{userId}")
    public Review putLikeToReview(@PathVariable int id,
                                  @PathVariable int userId) {
        return reviewService.putLikeToReview(id, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public Review putDislikeToReview(@PathVariable int id,
                                  @PathVariable int userId) {
        return reviewService.putDislikeToReview(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLikeToReview(@PathVariable int id,
                                  @PathVariable int userId) {
        return reviewService.deleteLikeToReview(id, userId);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public void deleteDislikeToReview(@PathVariable int id,
                                  @PathVariable int userId) {
        return reviewService.deleteDislikeToReview(id, userId);
    }
}
