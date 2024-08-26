package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.answer.AnswerRequestDTO;
import com.yourcompany.surveys.dto.answer.AnswerResponse;
import com.yourcompany.surveys.entity.Answer;
import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.mapper.AnswerMapper;
import com.yourcompany.surveys.repository.AnswerRepository;
import com.yourcompany.surveys.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final AnswerMapper answerMapper;
    private final UserRepository userRepository;

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

    public AnswerResponse save(AnswerRequestDTO answer, Principal principal) {
        String username = principal.getName();
        Optional<User> user = userRepository.findByEmail(username);
        User creator = user.orElseThrow();
        Answer newAnswer = answerMapper.toEntity(answer);
        newAnswer.setUser(creator);
        newAnswer = answerRepository.save(newAnswer);
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