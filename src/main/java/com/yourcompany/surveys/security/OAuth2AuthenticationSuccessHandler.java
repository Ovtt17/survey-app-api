package com.yourcompany.surveys.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.surveys.dto.user.UserResponse;
import com.yourcompany.surveys.entity.CustomOAuth2User;
import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    @Value("${application.front-end.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = customOAuth2User.user();

        String token = jwtService.generateToken(user);
        UserResponse userResponse = userMapper.toUserResponse(user);
        String userJson = objectMapper.writeValueAsString(userResponse);

        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String encodedUser = URLEncoder.encode(userJson, StandardCharsets.UTF_8);

        String redirectUrl = frontendUrl + "/auth/callback?token=" + encodedToken + "&user=" + encodedUser;
        response.sendRedirect(redirectUrl);
    }
}
