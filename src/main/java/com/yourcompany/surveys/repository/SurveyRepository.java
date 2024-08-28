package com.yourcompany.surveys.repository;

import com.yourcompany.surveys.dto.report.PopularSurveyReportResponse;
import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    List<Survey> findByCreator(User creator);

    @Query("SELECT new com.yourcompany.surveys.dto.report.PopularSurveyReportResponse(" +
            "s.id, s.title, COUNT(p.id)) " +
            "FROM Survey s " +
            "JOIN s.participations p " +
            "WHERE p.user.id = :userId " +
            "GROUP BY s.id, s.title " +
            "ORDER BY COUNT(p.id) DESC"
    )
    List<PopularSurveyReportResponse> findPopularSurveysByUserId(@Param("userId") Long userId);
}
