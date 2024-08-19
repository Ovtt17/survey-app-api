package com.yourcompany.surveys.service;

import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final SurveyRepository surveyRepository;

    public List<Survey> findAll() {
        return surveyRepository.findAll();
    }

    public Optional<Survey> findById(Long id) {
        return surveyRepository.findById(id);
    }

    public Survey save(Survey survey) {
        return surveyRepository.save(survey);
    }

    public Survey update(Survey survey) {
        return surveyRepository.save(survey);
    }

    public void deleteById(Long id) {
        surveyRepository.deleteById(id);
    }
}