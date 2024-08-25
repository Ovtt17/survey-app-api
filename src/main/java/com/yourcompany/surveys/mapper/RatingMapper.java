package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.RatingRequestDTO;
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
}
