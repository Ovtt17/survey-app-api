package com.yourcompany.surveys.repository;

import com.yourcompany.surveys.entity.Rating;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Rating findBySurveyIdAndUserId(@NotNull(message = "El ID de la encuesta no puede estar vacío") Long surveyId, Long userId);
}
