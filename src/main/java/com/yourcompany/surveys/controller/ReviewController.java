package com.yourcompany.surveys.controller;

import com.yourcompany.surveys.dto.ReviewRequestDTO;
import com.yourcompany.surveys.service.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

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
}
