package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.rating.RatingRequestDTO;
import com.yourcompany.surveys.dto.rating.RatingResponse;
import com.yourcompany.surveys.dto.review.ReviewRequestDTO;
import com.yourcompany.surveys.dto.review.ReviewResponse;
import com.yourcompany.surveys.entity.Rating;
import com.yourcompany.surveys.entity.Review;
import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.mapper.RatingMapper;
import com.yourcompany.surveys.mapper.ReviewMapper;
import com.yourcompany.surveys.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @InjectMocks
    private ReviewService reviewService;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReviewMapper reviewMapper;
    @Mock
    private RatingService ratingService;
    @Mock
    private RatingMapper ratingMapper;

    @Test
    void shouldSuccessfullySaveReview() {
        // Arrange
        ReviewRequestDTO request = new ReviewRequestDTO(
                1L,
                "Great Survey",
                "This survey was very helpful.",
                2L,
                new RatingRequestDTO(3L, 5.0, 2L)
        );

        Rating mockInitialRating = Rating.builder()
                .id(3L)
                .rating(5.0)
                .survey(Survey.builder().id(2L).build())
                .build();

        Review mockReview = Review.builder()
                .id(1L)
                .title("Great Survey")
                .content("This survey was very helpful.")
                .survey(Survey.builder().id(2L).title("Customer Satisfaction").build())
                .rating(mockInitialRating)
                .createdBy(User.builder()
                        .username("John Doe")
                        .profilePictureUrl("https://example.com/johndoe.jpg")
                        .build())
                .createdDate(LocalDateTime.now())
                .build();

        RatingRequestDTO expectedRatingDTO = new RatingRequestDTO(3L, 5.0, 2L);

        Rating updatedRating = Rating.builder()
                .id(3L)
                .rating(5.0)
                .survey(Survey.builder().id(2L).build())
                .build();

        Review savedReview = Review.builder()
                .id(1L)
                .title("Great Survey")
                .content("This survey was very helpful.")
                .survey(Survey.builder().id(2L).title("Customer Satisfaction").build())
                .rating(updatedRating)
                .createdBy(mockReview.getCreatedBy())
                .createdDate(mockReview.getCreatedDate())
                .build();

        ReviewResponse expectedResponse = new ReviewResponse(
                savedReview.getId(),
                savedReview.getTitle(),
                savedReview.getContent(),
                savedReview.getSurvey().getId(),
                savedReview.getSurvey().getTitle(),
                new RatingResponse(updatedRating.getId(), updatedRating.getRating(), updatedRating.getSurvey().getId()),
                savedReview.getCreatedBy().getUsername(),
                savedReview.getCreatedBy().getProfilePictureUrl(),
                savedReview.getCreatedDate()
        );

        // Mocking behavior
        when(reviewMapper.toEntity(any())).thenReturn(mockReview);
        when(ratingMapper.toRequestDTO(any())).thenReturn(expectedRatingDTO);
        when(ratingService.createOrUpdateRating(any())).thenReturn(updatedRating);
        when(reviewRepository.save(any())).thenReturn(savedReview);
        when(reviewMapper.toResponse(any())).thenReturn(expectedResponse);

        // Captors
        ArgumentCaptor<ReviewRequestDTO> requestCaptor = ArgumentCaptor.forClass(ReviewRequestDTO.class);
        ArgumentCaptor<Rating> ratingCaptor = ArgumentCaptor.forClass(Rating.class);
        ArgumentCaptor<RatingRequestDTO> ratingRequestCaptor = ArgumentCaptor.forClass(RatingRequestDTO.class);
        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);

        // Act
        ReviewResponse response = reviewService.createReview(request);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse.id(), response.id());
        assertEquals(expectedResponse.title(), response.title());
        assertEquals(expectedResponse.content(), response.content());
        assertEquals(expectedResponse.surveyId(), response.surveyId());
        assertEquals(expectedResponse.surveyTitle(), response.surveyTitle());
        assertEquals(expectedResponse.rating().id(), response.rating().id());
        assertEquals(expectedResponse.rating().rating(), response.rating().rating());
        assertEquals(expectedResponse.rating().surveyId(), response.rating().surveyId());
        assertEquals(expectedResponse.authorUsername(), response.authorUsername());
        assertEquals(expectedResponse.authorPicture(), response.authorPicture());
        assertEquals(expectedResponse.createdDate(), response.createdDate());

        // Verify mocks called
        verify(reviewMapper).toEntity(requestCaptor.capture());
        verify(ratingMapper).toRequestDTO(ratingCaptor.capture());
        verify(ratingService).createOrUpdateRating(ratingRequestCaptor.capture());
        verify(reviewRepository).save(reviewCaptor.capture());
        verify(reviewMapper).toResponse(savedReview);

        // Verify values passed to mocks are correct
        assertEquals(request, requestCaptor.getValue());
        assertEquals(mockReview.getRating().getId(), ratingCaptor.getValue().getId());
        assertEquals(expectedRatingDTO.id(), ratingRequestCaptor.getValue().id());
        assertEquals(savedReview.getRating().getId(), reviewCaptor.getValue().getRating().getId());
    }
}