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
            "p.id, p.createdBy.id, p.createdBy.username, p.createdBy.profilePictureUrl, p.survey.id, p.survey.title, p.createdDate" +
            ") " +
            "FROM Participation p WHERE p.createdBy.id = :userId")
    List<ParticipationResponse> findAllUserParticipationsByUserId(@Param("userId") Long userId);
}
