package com.yourcompany.surveys.repository;

import com.yourcompany.surveys.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findBySurveyId(Long id);

    List<Review> findBySurveyIdAndUserId(Long surveyId, Long userId);
}
