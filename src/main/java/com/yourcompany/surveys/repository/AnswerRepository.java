package com.yourcompany.surveys.repository;

import com.yourcompany.surveys.dto.report.SurveyReportResponse;
import com.yourcompany.surveys.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findBySurveyId(Long surveyId);

    @Query("SELECT new com.yourcompany.surveys.dto.report.SurveyReportResponse(" +
            "q.id, q.text, a.id, a.answerText, u.id, u.username) " +
            "FROM Answer a " +
            "JOIN a.question q " +
            "JOIN a.user u " +
            "WHERE a.survey.id = :surveyId " +
            "ORDER BY u.id, q.id "
    )
    List<SurveyReportResponse> findByAnswerBySurveyId(@Param("surveyId") Long surveyId);
}