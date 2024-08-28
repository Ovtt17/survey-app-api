package com.yourcompany.surveys.repository;

import com.yourcompany.surveys.dto.participation.ParticipationResponse;
import com.yourcompany.surveys.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findBySurveyId(Long surveyId);

    @Query("SELECT new com.yourcompany.surveys.dto.participation.ParticipationResponse(" +
            "p.id, p.user.id, p.user.username, p.survey.id, p.survey.title, p.participatedDate) " +
            "FROM Participation p WHERE p.user.id = :userId")
    List<ParticipationResponse> findAllUserParticipationsByUserId(@Param("userId") Long userId);
}
