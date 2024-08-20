package com.yourcompany.surveys.controller;

import com.yourcompany.surveys.entity.Answer;
import com.yourcompany.surveys.service.AnswerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/surveys/answers")
@RequiredArgsConstructor
@Tag(name = "Answers")
public class AnswerController {

    private final AnswerService answerService;

    @GetMapping
    public ResponseEntity<List<Answer>> getAllAnswers() {
        return ResponseEntity.ok(answerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Answer>> getAnswerById(@PathVariable Long id) {
        Optional<Answer> answer = answerService.findById(id);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Answer> createAnswer(@RequestBody Answer answer) {
        Answer newAnswer = answerService.save(answer);
        return new ResponseEntity<>(newAnswer, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Answer> updateAnswer(@PathVariable Long id, @RequestBody Answer answer) {
        Answer updatedAnswer = answerService.update(id, answer);
        return new ResponseEntity<>(updatedAnswer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id) {
        answerService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}