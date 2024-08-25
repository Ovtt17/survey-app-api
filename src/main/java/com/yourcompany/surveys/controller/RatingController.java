package com.yourcompany.surveys.controller;

import com.yourcompany.surveys.dto.RatingRequestDTO;
import com.yourcompany.surveys.service.RatingService;
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
@RequestMapping("/ratings")
@RequiredArgsConstructor
@Tag(name = "Ratings")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<Void> createRating (@RequestBody @Valid RatingRequestDTO ratingRequest, Principal principal) {
        ratingService.createOrUpdateRating(ratingRequest, principal);
        return ResponseEntity.ok().build();
    }
}
