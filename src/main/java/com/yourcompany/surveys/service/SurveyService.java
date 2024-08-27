package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.participant.ParticipantResponse;
import com.yourcompany.surveys.dto.question.QuestionRequestDTO;
import com.yourcompany.surveys.dto.survey.SurveyRequestDTO;
import com.yourcompany.surveys.dto.survey.SurveyResponse;
import com.yourcompany.surveys.entity.*;
import com.yourcompany.surveys.mapper.ParticipantMapper;
import com.yourcompany.surveys.mapper.QuestionMapper;
import com.yourcompany.surveys.mapper.SurveyMapper;
import com.yourcompany.surveys.mapper.UserMapper;
import com.yourcompany.surveys.repository.AnswerRepository;
import com.yourcompany.surveys.repository.ParticipantRepository;
import com.yourcompany.surveys.repository.SurveyRepository;
import com.yourcompany.surveys.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final SurveyMapper surveyMapper;
    private final UserRepository userRepository;
    private final QuestionMapper questionMapper;
    private final AnswerRepository answerRepository;
    private final UserMapper userMapper;
    private final ParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;

    public List<SurveyResponse> findAll() {
        List<Survey> surveys = surveyRepository.findAll();
        return surveys.stream()
                .map(surveyMapper::toResponse)
                .toList();
    }

    public Optional<SurveyResponse> findById(Long id) {
        Optional<Survey> survey = surveyRepository.findById(id);
        return survey.map(surveyMapper::toResponse);
    }

    public List<SurveyResponse> getByUser(Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );
        List<Survey> surveys = surveyRepository.findByCreator(user);
        return surveys.stream()
                .map(surveyMapper::toResponse)
                .toList();
    }

    @Transactional
    public SurveyResponse save(SurveyRequestDTO surveyRequest, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );
        Survey survey = surveyMapper.toEntity(surveyRequest, user);
        survey = surveyRepository.save(survey);
        return surveyMapper.toResponse(survey);
    }

    public SurveyResponse update(Long id, SurveyRequestDTO surveyRequest) {
        Survey existingSurvey = surveyRepository.findById(id).orElseThrow();

        existingSurvey.setTitle(surveyRequest.title());
        existingSurvey.setDescription(surveyRequest.description());

        Map<Long, QuestionRequestDTO> requestQuestionsMap = surveyRequest.questions().stream()
                .collect(Collectors.toMap(QuestionRequestDTO::id, q -> q));

        Iterator<Question> existingQuestionsIterator = existingSurvey.getQuestions().iterator();
        while (existingQuestionsIterator.hasNext()) {
            Question existingQuestion = existingQuestionsIterator.next();

            if (requestQuestionsMap.containsKey(existingQuestion.getId())) {
                QuestionRequestDTO questionRequest = requestQuestionsMap.get(existingQuestion.getId());
                existingQuestion.setText(questionRequest.text());
                existingQuestion.setType(QuestionType.fromValue(questionRequest.type()));

                requestQuestionsMap.remove(existingQuestion.getId());
            } else {
                existingQuestionsIterator.remove();
            }
        }

        requestQuestionsMap.values().forEach(questionRequest -> {
            Question newQuestion = questionMapper.toEntity(questionRequest);
            newQuestion.setSurvey(existingSurvey);
            existingSurvey.getQuestions().add(newQuestion);
        });

        return surveyMapper.toResponse(surveyRepository.save(existingSurvey));
    }

    public void deleteById(Long id) {
        surveyRepository.deleteById(id);
    }

    public List<ParticipantResponse> getSurveyParticipants(Long id) {
        List<Participant> participants = participantRepository.findBySurveyId(id);
        return participants.stream()
                .map(participantMapper::toResponse)
                .toList();
    }
}