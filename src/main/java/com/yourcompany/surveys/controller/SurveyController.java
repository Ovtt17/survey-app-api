package com.yourcompany.surveys.controller;

import com.yourcompany.surveys.dto.participation.ParticipationResponse;
import com.yourcompany.surveys.dto.survey.SurveyPagedResponse;
import com.yourcompany.surveys.dto.survey.SurveyRequestDTO;
import com.yourcompany.surveys.dto.survey.SurveyResponse;
import com.yourcompany.surveys.dto.survey.SurveySubmissionResponse;
import com.yourcompany.surveys.service.SurveyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/surveys")
@RequiredArgsConstructor
@Tag(name = "Surveys")
public class SurveyController {
    private final SurveyService surveyService;

    @GetMapping
    public ResponseEntity<SurveyPagedResponse> getAllSurveys(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        return ResponseEntity.ok(surveyService.getAllSurveys(page, size));
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
    public ResponseEntity<List<SurveyResponse>> getSurveysByUserForReport(Principal principal) {
        return ResponseEntity.ok(surveyService.getByUserForReport(principal));
    }

    @GetMapping("/user/paged")
    public ResponseEntity<SurveyPagedResponse> getSurveysByUserWithPaging(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return ResponseEntity.ok(surveyService.getByUserWithPaging(principal, page, size));
    }

    @GetMapping("/user/{username}/paged")
    public ResponseEntity<SurveyPagedResponse> getSurveysByUsernameWithPaging(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        return ResponseEntity.ok(surveyService.getByUsernameWithPaging(username, page, size));
    }

    @PostMapping
    public ResponseEntity<String> createSurvey(
            @RequestPart @Valid SurveyRequestDTO surveyRequest,
            @RequestPart(value = "picture", required = false) MultipartFile picture,
            Principal principal
    ) {
        String surveyTitle = surveyService.save(surveyRequest, picture, principal);
        return ResponseEntity.ok("Encuesta con título: " + surveyTitle + " creada exitosamente.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSurvey(
            @PathVariable Long id,
            @RequestPart @Valid SurveyRequestDTO surveyRequest,
            @RequestPart(required = false) MultipartFile picture,
            Principal principal
    ) {
        Long surveyId = surveyService.update(
                id,
                surveyRequest,
                picture,
                principal
        );
        return ResponseEntity.ok("Encuesta con ID: " + surveyId + " actualizada exitosamente.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSurvey(@PathVariable Long id) {
        surveyService.deleteById(id);
        return ResponseEntity.ok("Encuesta eliminada exitosamente.");
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipationResponse>> getSurveyParticipants(@PathVariable Long id) {
        return ResponseEntity.ok(surveyService.getSurveyParticipants(id));
    }
}