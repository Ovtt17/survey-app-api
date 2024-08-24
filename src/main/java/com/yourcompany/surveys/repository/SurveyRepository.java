package com.yourcompany.surveys.repository;

import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    List<Survey> findByCreator(User creator);
}
