package com.yourcompany.surveys.controller;

import com.yourcompany.surveys.dto.review.ReviewRequestDTO;
import com.yourcompany.surveys.dto.review.ReviewResponse;
import com.yourcompany.surveys.service.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Void> createReview(@RequestBody @Valid ReviewRequestDTO reviewRequest, Principal principal) {
        reviewService.createReview(reviewRequest, principal);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ReviewResponse>> getReviewsBySurveyId (@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewsBySurveyId(id));
    }
}
