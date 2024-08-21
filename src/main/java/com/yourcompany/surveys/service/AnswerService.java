package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.AnswerRequestDTO;
import com.yourcompany.surveys.dto.AnswerResponse;
import com.yourcompany.surveys.entity.Answer;
import com.yourcompany.surveys.entity.Question;
import com.yourcompany.surveys.mapper.AnswerMapper;
import com.yourcompany.surveys.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final AnswerMapper answerMapper;

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

    public AnswerResponse save(AnswerRequestDTO answer) {
        Answer newAnswer = Answer.builder()
                .question(
                        Question.builder()
                                .id(answer.questionId())
                                .build()
                )
                .answerText(answer.answerText())
                .build();
        answerRepository.save(newAnswer);
        return answerMapper.toResponse(newAnswer);
    }

    public AnswerResponse update(Long id, AnswerRequestDTO answer) {
        Answer answerEntity = answerMapper.toEntity(answer);
        answerEntity.setId(id);
        answerRepository.save(answerEntity);
        return answerMapper.toResponse(answerEntity);
    }

    public void delete(Long id) {
        answerRepository.deleteById(id);
    }
}