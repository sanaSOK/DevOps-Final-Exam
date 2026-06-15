package com.example.football_rental.controller;

import com.example.football_rental.model.Review;
import com.example.football_rental.repository.ReviewRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;

    /** GET /api/reviews — list all reviews */
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewRepository.findAll());
    }

    /** GET /api/reviews/{id} — get a single review */
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        return reviewRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /api/reviews/terrain/{terrainId} — get reviews for a terrain */
    @GetMapping("/terrain/{terrainId}")
    public ResponseEntity<List<Review>> getReviewsByTerrain(@PathVariable Long terrainId) {
        return ResponseEntity.ok(reviewRepository.findByTerrainId(terrainId));
    }

    /** GET /api/reviews/user/{userId} — get reviews by a user */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewRepository.findByUserId(userId));
    }

    /** GET /api/reviews/terrain/{terrainId}/average-rating */
    @GetMapping("/terrain/{terrainId}/average-rating")
    public ResponseEntity<Map<String, Object>> getAverageRating(@PathVariable Long terrainId) {
        Double avg = reviewRepository.findAverageRatingByTerrainId(terrainId);
        return ResponseEntity.ok(Map.of(
                "terrainId", terrainId,
                "averageRating", avg != null ? avg : 0.0
        ));
    }

    /** POST /api/reviews — create a new review */
    @PostMapping
    public ResponseEntity<Review> createReview(@Valid @RequestBody Review review) {
        Review saved = reviewRepository.save(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /** PUT /api/reviews/{id} — update a review */
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody Review updatedReview) {
        return reviewRepository.findById(id)
                .map(existing -> {
                    updatedReview.setId(existing.getId());
                    return ResponseEntity.ok(reviewRepository.save(updatedReview));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /** DELETE /api/reviews/{id} — delete a review */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        if (!reviewRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        reviewRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
