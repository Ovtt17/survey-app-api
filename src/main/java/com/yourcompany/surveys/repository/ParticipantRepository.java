package com.yourcompany.surveys.repository;

import com.yourcompany.surveys.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findBySurveyId(Long surveyId);
}
