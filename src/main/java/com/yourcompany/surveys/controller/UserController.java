package com.yourcompany.surveys.controller;

import com.yourcompany.surveys.dto.UserResponse;
import com.yourcompany.surveys.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    @GetMapping("/username")
    public ResponseEntity<UserResponse> getUser(
            Principal principal
    ) {
        return ResponseEntity.ok(userService.getUser(principal));
    }
}
