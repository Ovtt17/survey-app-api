package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.answer.AnswerRequestDTO;
import com.yourcompany.surveys.dto.answer.AnswerResponse;
import com.yourcompany.surveys.entity.Answer;
import com.yourcompany.surveys.entity.Participant;
import com.yourcompany.surveys.entity.Survey;
import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.mapper.AnswerMapper;
import com.yourcompany.surveys.repository.AnswerRepository;
import com.yourcompany.surveys.repository.ParticipantRepository;
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
    private final ParticipantRepository participantRepository;

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

    public void save(List<AnswerRequestDTO> answers, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Survey survey = null;
        for (AnswerRequestDTO a : answers) {
            Answer newAnswer = answerMapper.toEntity(a, user);
            answerRepository.save(newAnswer);
            if (survey == null) {
                survey = newAnswer.getSurvey();
            }
        }

        Participant participant = Participant.builder()
                .user(user)
                .survey(survey)
                .build();
        participantRepository.save(participant);
    }

    public AnswerResponse update(Long id, AnswerRequestDTO answer, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Answer answerEntity = answerMapper.toEntity(answer, user);
        answerEntity.setId(id);
        answerRepository.save(answerEntity);
        return answerMapper.toResponse(answerEntity);
    }

    public void delete(Long id) {
        answerRepository.deleteById(id);
    }
}