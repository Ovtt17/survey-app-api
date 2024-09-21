package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.user.AuthenticationRequest;
import com.yourcompany.surveys.dto.user.AuthenticationResponse;
import com.yourcompany.surveys.dto.user.RegistrationRequest;
import com.yourcompany.surveys.entity.EmailTemplateName;
import com.yourcompany.surveys.entity.Token;
import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.mapper.UserMapper;
import com.yourcompany.surveys.repository.RoleRepository;
import com.yourcompany.surveys.repository.TokenRepository;
import com.yourcompany.surveys.repository.UserRepository;
import com.yourcompany.surveys.security.JwtService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ImageService imageService;
    private final UserMapper userMapper;

    @Value ("${application.security.jwt.mailing.front-end.activation-url}")
    private String activationUrl;

    @Transactional
    public Boolean register(RegistrationRequest request) {
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("ROL 'USER' no encontrado"));
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(true)
                .roles(List.of(userRole))
                .build();

        try {
            if (request.getProfilePicture() != null) {
                String username = user.getName();
                String imageUrl = imageService.uploadImage(
                        request.getProfilePicture(),
                        username,
                        "profile_picture"
                );
                user.setProfilePictureUrl(imageUrl);
            }
            userRepository.save(user);
            sendValidationEmail(user);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error during registration: " + e.getMessage(), e);
        }
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Activa tu Cuenta"
        );

    }

    private String generateAndSaveActivationToken(User user) {
        int tokenLength = 6;
        String generatedToken = generateActivationCode(tokenLength);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int tokenLength) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < tokenLength; i++) {
            int randomIndex = random.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .orElseGet(() -> userRepository.findByEmail(request.getUsernameOrEmail())
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado")));

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        request.getPassword())
        );

        user = (User) auth.getPrincipal();

        var claims = new HashMap<String, Object>();
        claims.put("fullName", user.getFullName());
        var jwtToken = jwtService.generateToken(claims, user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(userMapper.toUserResponse(user))
                .build();
    }

    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("El token ha expirado. Un nuevo token ha sido enviado al mismo correo electrónico");
        }
        User user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    public Boolean checkEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public Boolean checkUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}

