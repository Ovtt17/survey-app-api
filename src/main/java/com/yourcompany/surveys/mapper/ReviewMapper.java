package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.review.ReviewRequestDTO;
import com.yourcompany.surveys.dto.review.ReviewResponse;
import com.yourcompany.surveys.entity.Rating;
import com.yourcompany.surveys.entity.Review;
import com.yourcompany.surveys.entity.Survey;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {
    private final RatingMapper ratingMapper;

    public ReviewMapper(RatingMapper ratingMapper) {
        this.ratingMapper = ratingMapper;
    }

    public Review toEntity(ReviewRequestDTO reviewRequest) {
        return Review.builder()
                .id(reviewRequest.id())
                .title(reviewRequest.title())
                .content(reviewRequest.content())
                .survey(
                        Survey.builder()
                                .id(reviewRequest.surveyId())
                                .build()
                )
                .rating(
                        Rating.builder()
                                .id(reviewRequest.rating().id())
                                .rating(reviewRequest.rating().rating())
                                .survey(
                                        Survey.builder()
                                                .id(reviewRequest.rating().surveyId())
                                                .build()
                                )
                                .build()
                )
                .build();
    }

    public ReviewResponse toResponse (Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getTitle(),
                review.getContent(),
                review.getSurvey().getId(),
                review.getSurvey().getTitle(),
                ratingMapper.toResponse(review.getRating()),
                review.getCreatedBy().getName(),
                review.getCreatedBy().getProfilePictureUrl(),
                review.getCreatedDate()
        );
    }
}
