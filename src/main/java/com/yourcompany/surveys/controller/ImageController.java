package com.yourcompany.surveys.controller;

import com.yourcompany.surveys.enums.ImageType;
import com.yourcompany.surveys.service.SurveyService;
import com.yourcompany.surveys.service.UserImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Tag(name = "Images")
public class ImageController {
    private final UserImageService userImageService;
    private final SurveyService surveyService;

    @PostMapping("/profile")
    public ResponseEntity<String> uploadProfilePicture(
            @RequestParam("image") MultipartFile image
    ) {
        String profilePictureUrl = userImageService.uploadProfilePicture(
                image,
                ImageType.PROFILE_PICTURE
        );
        return ResponseEntity.ok(profilePictureUrl);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteProfilePicture() {
        String response = userImageService.deleteProfilePicture();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/survey/{surveyId}")
    public ResponseEntity<String> uploadSurveyPicture(
            @PathVariable Long surveyId,
            @RequestParam("image") MultipartFile picture
    ) {
        String surveyPictureUrl = surveyService.updateSurveyPicture(surveyId, picture);
        return ResponseEntity.ok(surveyPictureUrl);
    }

    @DeleteMapping("/survey/{surveyId}")
    public ResponseEntity<String> deleteSurveyPicture(
            @PathVariable Long surveyId
    ) {
        String response = surveyService.deleteSurveyPicture(surveyId);
        return ResponseEntity.ok(response);
    }
}
