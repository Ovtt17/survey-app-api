package com.yourcompany.surveys.dto;

public record UserResponse(
        String username,
        String firstName,
        String fullName,
        String lastName
) {
}
