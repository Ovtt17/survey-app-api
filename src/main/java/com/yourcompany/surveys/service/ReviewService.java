package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.rating.RatingRequestDTO;
import com.yourcompany.surveys.dto.review.ReviewRequestDTO;
import com.yourcompany.surveys.dto.review.ReviewResponse;
import com.yourcompany.surveys.entity.Rating;
import com.yourcompany.surveys.entity.Review;
import com.yourcompany.surveys.mapper.RatingMapper;
import com.yourcompany.surveys.mapper.ReviewMapper;
import com.yourcompany.surveys.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final RatingService ratingService;
    private final RatingMapper ratingMapper;

    @Transactional
    public ReviewResponse createReview(ReviewRequestDTO reviewRequest) {
        Review review = reviewMapper.toEntity(reviewRequest);
        RatingRequestDTO ratingRequestDTO = ratingMapper.toRequestDTO(review.getRating());
        Rating ratingUpdated = ratingService.createOrUpdateRating(ratingRequestDTO);
        review.setRating(ratingUpdated);

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toResponse(savedReview);
    }

    public List<ReviewResponse> getReviewsBySurveyId (@PathVariable Long id) {
        List<Review> reviews = reviewRepository.findBySurveyId(id);
        return reviews.stream()
                .map(reviewMapper::toResponse)
                .toList();
    }
}
