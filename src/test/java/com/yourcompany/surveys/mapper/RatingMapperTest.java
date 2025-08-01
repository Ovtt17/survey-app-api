package com.yourcompany.surveys.mapper;

import com.yourcompany.surveys.dto.rating.RatingRequestDTO;
import com.yourcompany.surveys.dto.rating.RatingResponse;
import com.yourcompany.surveys.entity.Rating;
import com.yourcompany.surveys.entity.Survey;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RatingMapperTest {

    private final RatingMapper ratingMapper = new RatingMapper();

    @Test
    void shouldMapRatingRequestDTOToEntity() {
        // given
        RatingRequestDTO requestDTO = new RatingRequestDTO(1L, 5.0, 10L);
        // when
        Rating entity = ratingMapper.toEntity(requestDTO);
        // then
        assertEquals(requestDTO.id(), entity.getId());
        assertEquals(requestDTO.rating(), entity.getRating());
        assertNotNull(entity.getSurvey());
        assertEquals(requestDTO.surveyId(), entity.getSurvey().getId());
    }

    @Test
    void shouldMapEntityToRatingRequestDTO() {
        // given
        Survey survey = Survey.builder().id(10L).build();
        Rating entity = Rating.builder().id(1L).rating(5.0).survey(survey).build();
        // when
        RatingRequestDTO requestDTO = ratingMapper.toRequestDTO(entity);
        // then
        assertEquals(entity.getId(), requestDTO.id());
        assertEquals(entity.getRating(), requestDTO.rating());
        assertEquals(entity.getSurvey().getId(), requestDTO.surveyId());
    }

    @Test
    void shouldMapEntityToRatingResponse() {
        // given
        Survey survey = Survey.builder().id(10L).build();
        Rating entity = Rating.builder().id(1L).rating(5.0).survey(survey).build();
        // when
        RatingResponse response = ratingMapper.toResponse(entity);
        // then
        assertEquals(entity.getId(), response.id());
        assertEquals(entity.getRating(), response.rating());
        assertEquals(entity.getSurvey().getId(), response.surveyId());
    }
}
