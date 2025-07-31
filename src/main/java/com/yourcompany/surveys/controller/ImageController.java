package com.yourcompany.surveys.controller;

import com.yourcompany.surveys.enums.ImageType;
import com.yourcompany.surveys.service.SurveyService;
import com.yourcompany.surveys.service.UserImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import static com.yourcompany.surveys.entity.User.extractUsernameFromPrincipal;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Tag(name = "Images")
public class ImageController {
    private final UserImageService userImageService;
    private final SurveyService surveyService;

    @PostMapping("/profile")
    public ResponseEntity<String> uploadProfilePicture(@ModelAttribute @Valid MultipartFile image, Principal principal) {
        String username = extractUsernameFromPrincipal(principal);
        String profilePictureUrl = userImageService.uploadProfilePicture(
                image,
                username,
                ImageType.PROFILE_PICTURE
        );
        return ResponseEntity.ok(profilePictureUrl);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteProfilePicture(Principal principal) {
        String username = extractUsernameFromPrincipal(principal);
        String response = userImageService.deleteProfilePicture(username);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/survey/{surveyId}")
    public ResponseEntity<String> uploadSurveyPicture(
            @PathVariable Long surveyId,
            @ModelAttribute @Valid MultipartFile picture,
            Principal principal
    ) {
        String surveyPictureUrl = surveyService.updateSurveyPicture(surveyId, picture, principal);
        return ResponseEntity.ok(surveyPictureUrl);
    }

    @DeleteMapping("/survey/{surveyId}")
    public ResponseEntity<String> deleteSurveyPicture(
            @PathVariable Long surveyId,
            Principal principal
    ) {
        String response = surveyService.deleteSurveyPicture(surveyId, principal);
        return ResponseEntity.ok(response);
    }
}
