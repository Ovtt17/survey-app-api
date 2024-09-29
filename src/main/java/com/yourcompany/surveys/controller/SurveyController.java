package com.yourcompany.surveys.controller;

import com.yourcompany.surveys.dto.participation.ParticipationResponse;
import com.yourcompany.surveys.dto.survey.SurveyRequestDTO;
import com.yourcompany.surveys.dto.survey.SurveyResponse;
import com.yourcompany.surveys.dto.survey.SurveySubmissionResponse;
import com.yourcompany.surveys.service.SurveyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/surveys")
@RequiredArgsConstructor
@Tag(name = "Surveys")
public class SurveyController {
    private final SurveyService surveyService;

    @GetMapping
    public ResponseEntity<List<SurveyResponse>> getAllSurveys() {
        return ResponseEntity.ok(surveyService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SurveyResponse> getSurveyById(@PathVariable Long id) {
        SurveyResponse survey = surveyService.findById(id);
        return new ResponseEntity<>(survey, HttpStatus.OK);
    }

    @GetMapping("/{id}/submission")
    public ResponseEntity<SurveySubmissionResponse> getSurveyByIdForSubmission(@PathVariable Long id) {
        SurveySubmissionResponse survey = surveyService.findByIdForSubmission(id);
        return new ResponseEntity<>(survey, HttpStatus.OK);
    }

    @GetMapping("/{id}/owner")
    public ResponseEntity<SurveySubmissionResponse> getSurveyByIdForOwner(@PathVariable Long id, Principal principal) {
        SurveySubmissionResponse survey = surveyService.findByIdForOwner(id, principal);
        return new ResponseEntity<>(survey, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<SurveyResponse>> getSurveysByUser(Principal principal) {
        return ResponseEntity.ok(surveyService.getByUser(principal));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<SurveyResponse>> getSurveysByUsername(@PathVariable String username) {
        return ResponseEntity.ok(surveyService.getByUsername(username));
    }

    @PostMapping
    public ResponseEntity<Void> createSurvey(@RequestBody SurveyRequestDTO surveyRequest, Principal principal) {
        surveyService.save(surveyRequest, principal);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateSurvey(@PathVariable Long id, @RequestBody SurveyRequestDTO surveyRequest) {
        surveyService.update(id, surveyRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable Long id) {
        surveyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipationResponse>> getSurveyParticipants(@PathVariable Long id) {
        return ResponseEntity.ok(surveyService.getSurveyParticipants(id));
    }
}