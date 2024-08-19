package com.yourcompany.surveys.service;

import com.yourcompany.surveys.entity.Answer;
import com.yourcompany.surveys.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;

    public List<Answer> findAll() {
        return answerRepository.findAll();
    }

    public Optional<Answer> findById(Long id) {
        return answerRepository.findById(id);
    }

    public Answer save(Answer answer) {
        return answerRepository.save(answer);
    }

    public Answer update(Long id, Answer answer) {
        answer.setId(id);
        return answerRepository.save(answer);
    }

    public void delete(Long id) {
        answerRepository.deleteById(id);
    }
}