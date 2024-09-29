package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.participation.ParticipationResponse;
import com.yourcompany.surveys.dto.question.QuestionOptionRequestDTO;
import com.yourcompany.surveys.dto.question.QuestionRequestDTO;
import com.yourcompany.surveys.dto.survey.SurveyRequestDTO;
import com.yourcompany.surveys.dto.survey.SurveySubmissionResponse;
import com.yourcompany.surveys.entity.*;
import com.yourcompany.surveys.handler.exception.SurveyNotFoundException;
import com.yourcompany.surveys.handler.exception.UserNotFoundException;
import com.yourcompany.surveys.mapper.ParticipationMapper;
import com.yourcompany.surveys.mapper.QuestionMapper;
import com.yourcompany.surveys.mapper.QuestionOptionMapper;
import com.yourcompany.surveys.mapper.SurveyMapper;
import com.yourcompany.surveys.repository.ParticipationRepository;
import com.yourcompany.surveys.repository.SurveyRepository;
import com.yourcompany.surveys.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final SurveyMapper surveyMapper;
    private final UserRepository userRepository;
    private final QuestionMapper questionMapper;
    private final ParticipationRepository participationRepository;
    private final ParticipationMapper participationMapper;
    private final QuestionOptionMapper questionOptionMapper;

    private User getUserFromPrincipal(Principal principal) {
        String email = principal.getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("Usuario no  encontrado con email: " + email)
        );
    }

    public List<SurveySubmissionResponse> findAll() {
        List<Survey> surveys = surveyRepository.findAll();
        return surveys.stream()
                .map(surveyMapper::toResponse)
                .toList();
    }

    public SurveySubmissionResponse findById(Long id) {
        Optional<Survey> survey = surveyRepository.findById(id);
        if (survey.isEmpty()) {
            throw new SurveyNotFoundException("Encuesta no encontrada.");
        }
        return surveyMapper.toResponse(survey.get());
    }

    public SurveySubmissionResponse findByIdForOwner(Long id, Principal principal) {
        User user = getUserFromPrincipal(principal);
        Survey survey = surveyRepository.findByIdAndCreator(id, user);
        if (survey == null) {
            throw new SurveyNotFoundException("Encuesta no encontrada o no eres el creador.");
        }
        return surveyMapper.toResponse(survey);
    }

    public List<SurveySubmissionResponse> getByUser(Principal principal) {
        User user = getUserFromPrincipal(principal);
        List<Survey> surveys = surveyRepository.findByCreator(user);
        return surveys.stream()
                .map(surveyMapper::toResponse)
                .toList();
    }

    public List<SurveySubmissionResponse> getByUsername(String username) {
        List<Survey> surveys = surveyRepository.findByCreatorUsername(username);
        return surveys.stream()
                .map(surveyMapper::toResponse)
                .toList();
    }

    @Transactional
    public SurveySubmissionResponse save(SurveyRequestDTO surveyRequest, Principal principal) {
        User user = getUserFromPrincipal(principal);
        Survey survey = surveyMapper.toEntity(surveyRequest, user);
        survey = surveyRepository.save(survey);
        return surveyMapper.toResponse(survey);
    }

    public SurveySubmissionResponse update(Long id, SurveyRequestDTO surveyRequest) {
        Survey existingSurvey = surveyRepository.findById(id).orElseThrow();
        updateSurveyDetails(existingSurvey, surveyRequest);
        updateExistingQuestions(existingSurvey, surveyRequest);
        addNewQuestions(existingSurvey, surveyRequest);
        return surveyMapper.toResponse(surveyRepository.save(existingSurvey));
    }

    private void updateSurveyDetails(Survey existingSurvey, SurveyRequestDTO surveyRequest) {
        existingSurvey.setTitle(surveyRequest.title());
        existingSurvey.setDescription(surveyRequest.description());
    }

    private void updateExistingQuestions(Survey existingSurvey, SurveyRequestDTO surveyRequest) {
        Map<Long, QuestionRequestDTO> requestQuestionsMap = surveyRequest.questions().stream()
                .collect(Collectors.toMap(QuestionRequestDTO::id, q -> q));

        Iterator<Question> existingQuestionsIterator = existingSurvey.getQuestions().iterator();
        while (existingQuestionsIterator.hasNext()) {
            Question existingQuestion = existingQuestionsIterator.next();

            if (requestQuestionsMap.containsKey(existingQuestion.getId())) {
                QuestionRequestDTO questionRequest = requestQuestionsMap.get(existingQuestion.getId());
                existingQuestion.setText(questionRequest.text());
                existingQuestion.setType(QuestionType.fromValue(questionRequest.type()));
                updateQuestionOptions(existingQuestion, questionRequest);
                requestQuestionsMap.remove(existingQuestion.getId());
            } else {
                existingQuestionsIterator.remove();
            }
        }
    }

    private void updateQuestionOptions(Question existingQuestion, QuestionRequestDTO questionRequest) {
        Map<Long, QuestionOptionRequestDTO> requestOptionsMap = questionRequest.options().stream()
                .collect(Collectors.toMap(QuestionOptionRequestDTO::id, o -> o));

        Iterator<QuestionOption> existingOptionsIterator = existingQuestion.getOptions().iterator();
        while (existingOptionsIterator.hasNext()) {
            QuestionOption existingOption = existingOptionsIterator.next();

            if (requestOptionsMap.containsKey(existingOption.getId())) {
                QuestionOptionRequestDTO optionRequest = requestOptionsMap.get(existingOption.getId());
                existingOption.setText(optionRequest.text());
                requestOptionsMap.remove(existingOption.getId());
            } else {
                existingOptionsIterator.remove();
            }
        }

        requestOptionsMap.values().forEach(optionRequest -> {
            QuestionOption newOption = questionOptionMapper.toEntity(optionRequest);
            newOption.setQuestion(existingQuestion);
            existingQuestion.getOptions().add(newOption);
        });
    }

    private void addNewQuestions(Survey existingSurvey, SurveyRequestDTO surveyRequest) {
        Map<Long, QuestionRequestDTO> requestQuestionsMap = surveyRequest.questions().stream()
                .collect(Collectors.toMap(QuestionRequestDTO::id, q -> q));

        requestQuestionsMap.values().forEach(questionRequest -> {
            Question newQuestion = questionMapper.toEntity(questionRequest);
            newQuestion.setSurvey(existingSurvey);
            existingSurvey.getQuestions().add(newQuestion);
        });
    }

    public void deleteById(Long id) {
        surveyRepository.deleteById(id);
    }

    public List<ParticipationResponse> getSurveyParticipants(Long id) {
        List<Participation> participations = participationRepository.findBySurveyId(id);
        return participations.stream()
                .map(participationMapper::toResponse)
                .toList();
    }
}