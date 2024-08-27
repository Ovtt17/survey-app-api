package com.yourcompany.surveys.repository;

import com.yourcompany.surveys.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findBySurveyId(Long surveyId);
}
