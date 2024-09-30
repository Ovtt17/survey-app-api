package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.user.UserResponse;
import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.handler.exception.UserNotFoundException;
import com.yourcompany.surveys.mapper.UserMapper;
import com.yourcompany.surveys.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse getUser(Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no  encontrado con email: " + email));
        return userMapper.toUserResponse(user);
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con nombre de usuario: " + username));
        return userMapper.toUserResponse(user);
    }
}
