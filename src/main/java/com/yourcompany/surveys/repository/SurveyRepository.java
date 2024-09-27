package com.yourcompany.surveys.repository;

import com.yourcompany.surveys.dto.report.PopularSurveyReportResponse;
import com.yourcompany.surveys.dto.report.UserSatisfactionReportResponse;
import com.yourcompany.surveys.dto.report.UserSurveyParticipationCountResponse;
import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Survey findByIdAndCreator(Long id, User creator);
    List<Survey> findByCreator(User creator);

    @Query("SELECT new com.yourcompany.surveys.dto.report.PopularSurveyReportResponse(" +
            "s.id, s.title, COUNT(p.id)) " +
            "FROM Survey s " +
            "JOIN s.participations p " +
            "WHERE s.creator.id = :userId " +
            "GROUP BY s.id, s.title " +
            "ORDER BY COUNT(p.id) DESC"
    )
    List<PopularSurveyReportResponse> findPopularSurveysByUserId(@Param("userId") Long userId);

    @Query("SELECT new com.yourcompany.surveys.dto.report.UserSurveyParticipationCountResponse(" +
            "s.id, s.title, u.id, u.username, COUNT(p.id)) " +
            "FROM Survey s " +
            "JOIN s.participations p " +
            "JOIN p.user u " +
            "WHERE s.creator.id = :creatorId " +
            "GROUP BY s.id, s.title, u.id, u.username " +
            "ORDER BY s.id, COUNT(p.id) DESC"
    )
    List<UserSurveyParticipationCountResponse> findParticipationCountByCreatorId(@Param("creatorId") Long creatorId);

    @Query("SELECT new com.yourcompany.surveys.dto.report.UserSatisfactionReportResponse(" +
            "s.id, s.title, AVG(r.rating)) " +
            "FROM Survey s " +
            "JOIN Rating r ON s.id = r.survey.id " +
            "WHERE s.creator.id = :creatorId " +
            "GROUP BY s.id, s.title " +
            "ORDER BY AVG(r.rating) DESC"
    )
    List<UserSatisfactionReportResponse> findUserSatisfactionByCreatorId(@Param("creatorId") Long creatorId);
}
