package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.rating.RatingRequestDTO;
import com.yourcompany.surveys.dto.rating.RatingResponse;
import com.yourcompany.surveys.entity.Rating;
import com.yourcompany.surveys.entity.Survey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingMapper {

    public Rating toEntity (RatingRequestDTO ratingRequest) {
        return Rating.builder()
                .id(ratingRequest.id())
                .rating(ratingRequest.rating())
                .survey(
                        Survey.builder()
                                .id(ratingRequest.surveyId())
                                .build()
                )
                .build();
    }

    public RatingRequestDTO toRequestDTO (Rating rating) {
        return new RatingRequestDTO(
                rating.getId(),
                rating.getRating(),
                rating.getSurvey().getId()
        );
    }

    public RatingResponse toResponse(Rating rating) {
        return new RatingResponse(
                rating.getId(),
                rating.getRating(),
                rating.getSurvey().getId()
        );
    }
}
