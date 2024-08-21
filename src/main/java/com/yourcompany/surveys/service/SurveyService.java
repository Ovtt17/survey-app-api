package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.SurveyRequestDTO;
import com.yourcompany.surveys.dto.SurveyResponse;
import com.yourcompany.surveys.dto.UserResponse;
import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.mapper.SurveyMapper;
import com.yourcompany.surveys.repository.SurveyRepository;
import com.yourcompany.surveys.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final SurveyMapper surveyMapper;
    private final UserRepository userRepository;

    public List<SurveyResponse> findAll() {
        List<Survey> surveys = surveyRepository.findAll();
        return surveys.stream()
                .map(surveyMapper::toResponse)
                .toList();
    }

    public Optional<SurveyResponse> findById(Long id) {
        return surveyRepository.findById(id)
                .map(surveyMapper::toResponse);
    }

    @Transactional
    public SurveyResponse save(SurveyRequestDTO surveyRequest, Principal principal) {
        String username = principal.getName();
        Optional<User> user = userRepository.findByEmail(username);
        User creator = user.orElseThrow();
        Survey survey = surveyMapper.toEntity(surveyRequest);
        survey.setCreator(creator);
        survey = surveyRepository.save(survey);
        return surveyMapper.toResponse(survey);
    }

    public Survey update(Survey survey) {
        return surveyRepository.save(survey);
    }

    public void deleteById(Long id) {
        surveyRepository.deleteById(id);
    }
}