package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.answer.AnswerRequestDTO;
import com.yourcompany.surveys.dto.answer.AnswerResponse;
import com.yourcompany.surveys.entity.Answer;
import com.yourcompany.surveys.entity.Participation;
import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.mapper.AnswerMapper;
import com.yourcompany.surveys.repository.AnswerRepository;
import com.yourcompany.surveys.repository.ParticipationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final AnswerMapper answerMapper;
    private final ParticipationRepository participationRepository;

    public List<AnswerResponse> findAll() {
        List<Answer> answers = answerRepository.findAll();
        return answers.stream()
                .map(answerMapper::toResponse)
                .toList();
    }

    public Optional<AnswerResponse> findById(Long id) {
        Optional<Answer> answer = answerRepository.findById(id);
        return answer.map(answerMapper::toResponse);
    }

    @Transactional
    public void save(List<AnswerRequestDTO> answers) {
        Survey survey = Survey.builder().id(answers.get(0).surveyId()).build();

        Participation participation = Participation.builder()
                .survey(survey)
                .build();
        participationRepository.save(participation);

        for (AnswerRequestDTO a : answers) {
            Answer newAnswer = answerMapper.toEntity(a);
            newAnswer.setParticipation(participation);
            answerRepository.save(newAnswer);
        }
    }

    public AnswerResponse update(Long id, AnswerRequestDTO answer) {
        Answer answerEntity = answerMapper.toEntity(answer);
        answerEntity.setId(id);

        answerRepository.save(answerEntity);
        return answerMapper.toResponse(answerEntity);
    }

    @Transactional
    public void delete(Long id) {
        answerRepository.deleteById(id);
    }

    public List<AnswerResponse> findBySurveyIdAndUserIdAndParticipationId(Long surveyId, Long userId, Long participationId) {
        List<Answer> answers = answerRepository.findBySurveyIdAndCreatedByIdAndParticipationId(surveyId, userId, participationId);
        return answers.stream()
                .map(answerMapper::toResponse)
                .toList();
    }
}