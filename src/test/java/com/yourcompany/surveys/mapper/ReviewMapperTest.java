package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.rating.RatingRequestDTO;
import com.yourcompany.surveys.dto.review.ReviewRequestDTO;
import com.yourcompany.surveys.dto.review.ReviewResponse;
import com.yourcompany.surveys.entity.Rating;
import com.yourcompany.surveys.entity.Review;
import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReviewMapperTest {
    private ReviewMapper mapper;

    @BeforeEach
    void setUp() {
        RatingMapper ratingMapper = new RatingMapper();
        mapper = new ReviewMapper(ratingMapper);
    }

    @Test
    void shouldMapReviewRequestToEntity() {
        // given
        ReviewRequestDTO request = new ReviewRequestDTO(
                1L,
                "Great Survey",
                "This survey was very helpful.",
                2L,
                new RatingRequestDTO(
                        3L,
                        5.0,
                        2L
                )
        );

        // when
        Review review = mapper.toEntity(request);

        // then
        assertNotNull(review);
        assertEquals(request.id(), review.getId());
        assertEquals(request.title(), review.getTitle());
        assertEquals(request.content(), review.getContent());
        assertNotNull(review.getSurvey());
        assertEquals(request.surveyId(), review.getSurvey().getId());
        assertNotNull(review.getRating());
        assertEquals(request.rating().id(), review.getRating().getId());
        assertEquals(request.rating().rating(), review.getRating().getRating());
        assertEquals(request.rating().surveyId(), review.getRating().getSurvey().getId());
    }

    @Test
    void shouldMapReviewEntityToResponse() {
        // given
        Review review = Review.builder()
                .id(1L)
                .title("Great Survey")
                .content("This survey was very helpful.")
                .survey(
                        Survey.builder()
                                .id(2L)
                                .title("Customer Satisfaction")
                                .build()
                )
                .rating(
                        Rating.builder()
                                .id(3L)
                                .rating(5.0)
                                .survey(
                                        Survey.builder()
                                                .id(2L)
                                                .build()
                                )
                                .build())
                .createdBy(
                        User.builder()
                                .username("John Doe")
                                .profilePictureUrl("https://example.com/johndoe.jpg")
                                .build()
                )
                .createdDate(LocalDateTime.now())
                .build();

        // when
        ReviewResponse response = mapper.toResponse(review);

        // then
        assertNotNull(response);
        assertEquals(review.getId(), response.id());
        assertEquals(review.getTitle(), response.title());
        assertEquals(review.getContent(), response.content());
        assertEquals(review.getSurvey().getId(), response.surveyId());
        assertEquals(review.getSurvey().getTitle(), response.surveyTitle());
        assertNotNull(response.rating());
        assertEquals(review.getRating().getId(), response.rating().id());
        assertEquals(review.getRating().getRating(), response.rating().rating());
        assertEquals(review.getCreatedBy().getName(), response.authorUsername());
        assertEquals(review.getCreatedBy().getProfilePictureUrl(), response.authorPicture());
    }
}