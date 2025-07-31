package com.yourcompany.surveys.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum AuthProvider {
    LOCAL("local"),
    GOOGLE("google"),
    FACEBOOK("facebook");

    private final String providerId;

    public static Optional<AuthProvider> fromProviderId(String providerId) {
        return Arrays.stream(values())
                .filter(p -> p.providerId.equalsIgnoreCase(providerId))
                .findFirst();
    }
}
