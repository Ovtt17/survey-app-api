package com.yourcompany.surveys.controller;

import com.yourcompany.surveys.dto.rating.RatingGroupResponse;
import com.yourcompany.surveys.dto.rating.RatingRequestDTO;
import com.yourcompany.surveys.service.RatingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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

    @GetMapping("/grouped/{surveyId}")
    public ResponseEntity<List<RatingGroupResponse>> getRatingsGroupedByRate(@PathVariable @Valid Long surveyId) {
        List<RatingGroupResponse> ratings = ratingService.getRatingsGroupedByRate(surveyId);
        return ResponseEntity.ok(ratings);
    }
}
