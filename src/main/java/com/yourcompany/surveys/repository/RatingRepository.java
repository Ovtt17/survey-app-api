package com.yourcompany.surveys.repository;

import com.yourcompany.surveys.dto.rating.RatingGroupResponse;
import com.yourcompany.surveys.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Rating findBySurveyIdAndCreatedById(Long survey_id, Long user_id);

    @Query("SELECT new com.yourcompany.surveys.dto.rating.RatingGroupResponse(CAST(FLOOR(r.rating) AS long), COUNT(r.id)) " +
            "FROM Rating r " +
            "JOIN r.survey s " +
            "WHERE s.id = :surveyId " +
            "GROUP BY CAST(FLOOR(r.rating) AS long)"
    )
    List<RatingGroupResponse> getRatingsGroupedByRate(@Param("surveyId") Long surveyId);
}
