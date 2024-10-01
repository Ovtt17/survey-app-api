package com.yourcompany.surveys.controller;

import com.yourcompany.surveys.entity.ImageType;
import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.service.ImageService;
import com.yourcompany.surveys.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Tag(name = "Images")
public class ImageController {
    private final ImageService imageService;
    private final UserService userService;

    @PostMapping("/profile")
    public ResponseEntity<String> uploadProfilePicture(
            @ModelAttribute @Valid MultipartFile image,
            Principal principal
    ) {
        String username = getUsername(principal);
        String imageLink = imageService.uploadProfilePicture(
                image,
                username,
                ImageType.PROFILE_PICTURE
        );
        userService.updateUserProfilePicture(username, imageLink);
        return ResponseEntity.ok(imageLink);
    }

    @PostMapping("/survey/{surveyId}")
    public ResponseEntity<String> uploadSurveyPicture(
            @PathVariable Long surveyId,
            @ModelAttribute @Valid MultipartFile image,
            Principal principal
    ) {
        String username = getUsername(principal);
        return ResponseEntity.ok(imageService.uploadSurveyPicture(
                image,
                surveyId,
                username,
                ImageType.SURVEY_PICTURE
        ));
    }

    private String getUsername(Principal principal) {
        Authentication authentication = (Authentication) principal;
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ((User) userDetails).getName();
    }
}
