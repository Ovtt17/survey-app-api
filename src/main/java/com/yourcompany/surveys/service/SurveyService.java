package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.participation.ParticipationResponse;
import com.yourcompany.surveys.dto.question.QuestionOptionRequestDTO;
import com.yourcompany.surveys.dto.question.QuestionRequestDTO;
import com.yourcompany.surveys.dto.survey.SurveyPagedResponse;
import com.yourcompany.surveys.dto.survey.SurveyRequestDTO;
import com.yourcompany.surveys.dto.survey.SurveyResponse;
import com.yourcompany.surveys.dto.survey.SurveySubmissionResponse;
import com.yourcompany.surveys.entity.*;
import com.yourcompany.surveys.handler.exception.SurveyNotFoundException;
import com.yourcompany.surveys.mapper.ParticipationMapper;
import com.yourcompany.surveys.mapper.QuestionMapper;
import com.yourcompany.surveys.mapper.QuestionOptionMapper;
import com.yourcompany.surveys.mapper.SurveyMapper;
import com.yourcompany.surveys.repository.ParticipationRepository;
import com.yourcompany.surveys.repository.SurveyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final SurveyMapper surveyMapper;
    private final QuestionMapper questionMapper;
    private final ParticipationRepository participationRepository;
    private final ParticipationMapper participationMapper;
    private final QuestionOptionMapper questionOptionMapper;
    private final UserService userService;

    public SurveyPagedResponse getAllSurveys(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Survey> surveys = surveyRepository.findAll(pageable);
        return surveyMapper.toPagedResponse(surveys);
    }

    public SurveyResponse findById(Long id) {
        Optional<Survey> survey = surveyRepository.findById(id);
        if (survey.isEmpty()) {
            throw new SurveyNotFoundException("Encuesta no encontrada.");
        }
        return surveyMapper.toResponse(survey.get());
    }

    public SurveySubmissionResponse findByIdForSubmission(Long id) {
        Optional<Survey> survey = surveyRepository.findById(id);
        if (survey.isEmpty()) {
            throw new SurveyNotFoundException("Encuesta no encontrada.");
        }
        return surveyMapper.toSubmissionResponse(survey.get());
    }

    public SurveySubmissionResponse findByIdForOwner(Long id, Principal principal) {
        User user = userService.getUserFromPrincipal(principal);
        Survey survey = surveyRepository.findByIdAndCreator(id, user);
        if (survey == null) {
            throw new SurveyNotFoundException("Encuesta no encontrada.");
        }
        return surveyMapper.toSubmissionResponse(survey);
    }

    public List<SurveyResponse> getByUserForReport(Principal principal) {
        User user = userService.getUserFromPrincipal(principal);
        List<Survey> surveys = surveyRepository.findByCreator(user);
        return surveys.stream()
                .map(surveyMapper::toResponse)
                .toList();
    }

    public SurveyPagedResponse getByUserWithPaging(
            Principal principal,
            int page,
            int size
    ) {
        User user = userService.getUserFromPrincipal(principal);
        Pageable pageable = PageRequest.of(page, size);
        Page<Survey> surveys = surveyRepository.findByCreator(user, pageable);
        return surveyMapper.toPagedResponse(surveys);
    }

    public SurveyPagedResponse getByUsernameWithPaging(
            String username,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Survey> surveys = surveyRepository.findByCreatorUsername(username, pageable);
        return surveyMapper.toPagedResponse(surveys);
    }

    @Transactional
    public void save(SurveyRequestDTO surveyRequest, Principal principal) {
        User user = userService.getUserFromPrincipal(principal);
        Survey survey = surveyMapper.toEntity(surveyRequest, user);
        survey = surveyRepository.save(survey);
        surveyMapper.toSubmissionResponse(survey);
    }

    public void update(Long id, SurveyRequestDTO surveyRequest) {
        Survey existingSurvey = surveyRepository.findById(id).orElseThrow(
                () -> new SurveyNotFoundException("Encuesta no encontrada.")
        );
        updateSurveyDetails(existingSurvey, surveyRequest);
        updateExistingQuestions(existingSurvey, surveyRequest);
        addNewQuestions(existingSurvey, surveyRequest);
        surveyMapper.toSubmissionResponse(surveyRepository.save(existingSurvey));
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