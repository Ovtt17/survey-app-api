package com.yourcompany.surveys.controller;

import com.yourcompany.surveys.dto.user.AuthenticationRequest;
import com.yourcompany.surveys.dto.user.AuthenticationResponse;
import com.yourcompany.surveys.dto.user.RegistrationRequest;
import com.yourcompany.surveys.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {
    private final AuthenticationService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register (
            @ModelAttribute @Valid RegistrationRequest request
    ) throws MessagingException {
        Boolean isRegistered = authService.register(request);
        return ResponseEntity.accepted().body(isRegistered);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate (
            @RequestBody @Valid AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping ("/activate-account")
    public ResponseEntity<Void> confirm (
            @RequestParam String token
    ) throws MessagingException {
        authService.activateAccount(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Boolean> checkEmail (
            @PathVariable @Valid String email
    ) {
        return ResponseEntity.ok(authService.checkEmail(email));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Boolean> checkUsername (
            @PathVariable @Valid String username
    ) {
        return ResponseEntity.ok(authService.checkUsername(username));
    }
}
