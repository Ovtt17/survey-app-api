package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.rating.RatingGroupResponse;
import com.yourcompany.surveys.dto.rating.RatingRequestDTO;
import com.yourcompany.surveys.entity.Rating;
import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.repository.RatingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {
    @InjectMocks
    private RatingService ratingService;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private SurveyService surveyService;
    @Mock
    private UserService userService;

    @Test
    void should_create_or_update_rating() {
        // Given
        final double initialAverage = 4.0;
        final long initialRatingCount = 2L;
        final double previousRating = 3.0;
        final double newRating = 5.0;

        RatingRequestDTO request = mock(RatingRequestDTO.class);
        when(request.surveyId()).thenReturn(1L);
        when(request.rating()).thenReturn(newRating);

        Survey survey = Survey.builder()
                .id(1L)
                .averageRating(initialAverage)
                .ratingCount(initialRatingCount)
                .build();

        User user = User.builder()
                .id(2L)
                .build();

        Rating existingRating = Rating.builder()
                .rating(previousRating)
                .survey(survey)
                .createdBy(user)
                .build();

        when(surveyService.findByIdOrThrow(1L)).thenReturn(survey);
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(ratingRepository.findBySurveyIdAndCreatedById(1L, 2L)).thenReturn(existingRating);
        when(ratingRepository.save(existingRating)).thenReturn(existingRating);

        // When
        Rating result = ratingService.createOrUpdateRating(request);

        // Then
        double expectedAverage = ((initialAverage * initialRatingCount - previousRating + newRating) / initialRatingCount);
        assertEquals(newRating, result.getRating(), 0.0001);
        assertEquals(expectedAverage, survey.getAverageRating(), 0.0001);

        verify(ratingRepository).save(existingRating);
        verify(surveyService).save(survey);
    }

    @Test
    void should_return_all_ratings_grouped_from_5_to_1_with_missing_defaults() {
        // Given
        Long surveyId = 10L;

        List<RatingGroupResponse> partialResults = List.of(
                new RatingGroupResponse(5L, 3L),
                new RatingGroupResponse(3L, 2L),
                new RatingGroupResponse(1L, 1L)
        );

        when(ratingRepository.getRatingsGroupedByRate(surveyId)).thenReturn(partialResults);

        // When
        List<RatingGroupResponse> results = ratingService.getRatingsGroupedByRate(surveyId);

        // Then
        List<RatingGroupResponse> expected = List.of(
                new RatingGroupResponse(5L, 3L),
                new RatingGroupResponse(4L, 0L),
                new RatingGroupResponse(3L, 2L),
                new RatingGroupResponse(2L, 0L),
                new RatingGroupResponse(1L, 1L)
        );

        assertEquals(expected, results);
        verify(ratingRepository).getRatingsGroupedByRate(surveyId);
    }
}
