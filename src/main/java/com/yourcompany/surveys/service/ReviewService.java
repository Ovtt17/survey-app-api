package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.RatingRequestDTO;
import com.yourcompany.surveys.dto.ReviewRequestDTO;
import com.yourcompany.surveys.entity.Rating;
import com.yourcompany.surveys.entity.Review;
import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.mapper.RatingMapper;
import com.yourcompany.surveys.mapper.ReviewMapper;
import com.yourcompany.surveys.repository.ReviewRepository;
import com.yourcompany.surveys.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final RatingService ratingService;
    private final RatingMapper ratingMapper;

    @Transactional
    public void createReview(ReviewRequestDTO reviewRequest, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = reviewMapper.toEntity(reviewRequest, user);
        Rating ratingUpdated = ratingService.createOrUpdateRating(
                ratingMapper.toRequestDTO(review.getRating()),
                principal
        );
        review.setRating(ratingUpdated);
        reviewRepository.save(review);
    }
}
