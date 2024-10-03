package com.yourcompany.surveys.controller;

import com.yourcompany.surveys.entity.ImageType;
import com.yourcompany.surveys.service.SurveyImageService;
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
    private final SurveyImageService surveyImageService;

    @PostMapping("/profile")
    public ResponseEntity<String> uploadProfilePicture(
            @ModelAttribute @Valid MultipartFile image,
            Principal principal
    ) {
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
}
