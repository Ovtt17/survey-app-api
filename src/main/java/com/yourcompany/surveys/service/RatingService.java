package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.rating.RatingGroupResponse;
import com.yourcompany.surveys.dto.rating.RatingRequestDTO;
import com.yourcompany.surveys.entity.Rating;
import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.mapper.RatingMapper;
import com.yourcompany.surveys.repository.RatingRepository;
import com.yourcompany.surveys.repository.SurveyRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final SurveyRepository surveyRepository;
    private final UserService userService;

    @Transactional
    public Rating createOrUpdateRating(@Valid RatingRequestDTO ratingRequest) {
        Survey survey = surveyRepository.findById(ratingRequest.surveyId())
                .orElseThrow(() -> new IllegalArgumentException("Survey not found"));

        User user = userService.getAuthenticatedUser();
        Rating existingRating = ratingRepository.findBySurveyIdAndCreatedById(ratingRequest.surveyId(), user.getId());

        if (existingRating != null) {
            Double previousRating = existingRating.getRating();
            existingRating.setRating(ratingRequest.rating());
            survey.setAverageRating(
                    (survey.getAverageRating() * survey.getRatingCount() - previousRating + ratingRequest.rating())
                            / survey.getRatingCount()
            );
            ratingRepository.save(existingRating);
        } else {
            Rating rating = ratingMapper.toEntity(ratingRequest);

            survey.setRatingCount(survey.getRatingCount() + 1);
            survey.setAverageRating(
                    ((survey.getAverageRating() * (survey.getRatingCount() - 1)) + rating.getRating()) / survey.getRatingCount()
            );
            return ratingRepository.save(rating);
        }
        surveyRepository.save(survey);
        return existingRating;
    }

    public List<RatingGroupResponse> getRatingsGroupedByRate(Long surveyId) {
        List<RatingGroupResponse> results = ratingRepository.getRatingsGroupedByRate(surveyId);

        Map<Long, Long> ratingsMap = new HashMap<>();
        for (RatingGroupResponse response : results) {
            ratingsMap.put(response.rating(), response.count());
        }

        List<RatingGroupResponse> completeResults = new ArrayList<>();
        for (long i = 5; i >= 1; i--) {
            completeResults.add(new RatingGroupResponse(i, ratingsMap.getOrDefault(i, 0L)));
        }
        return completeResults;
    }
}