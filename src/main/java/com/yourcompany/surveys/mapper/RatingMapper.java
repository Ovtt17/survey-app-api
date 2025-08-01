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

    public Rating toEntity(RatingRequestDTO ratingRequest) {
        if (ratingRequest == null)
            throw new NullPointerException("La calificación no puede ser nula.");

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

    public RatingRequestDTO toRequestDTO(Rating rating) {
        if (rating == null)
            throw new NullPointerException("La calificación no puede ser nula.");

        return new RatingRequestDTO(
                rating.getId(),
                rating.getRating(),
                rating.getSurvey().getId()
        );
    }

    public RatingResponse toResponse(Rating rating) {
        if (rating == null)
            throw new NullPointerException("La calificación no puede ser nula.");

        return new RatingResponse(
                rating.getId(),
                rating.getRating(),
                rating.getSurvey().getId()
        );
    }
}
