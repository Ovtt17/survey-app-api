package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.user.UserResponse;
import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.handler.exception.UserNotFoundException;
import com.yourcompany.surveys.mapper.UserMapper;
import com.yourcompany.surveys.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con nombre de usuario: " + username));
        return userMapper.toUserResponse(user);
    }

    public void updateUserProfilePicture(String username, String imageLink) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con nombre de usuario: " + username));
        user.setProfilePictureUrl(imageLink);
        userRepository.save(user);
    }
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    public UserResponse getUserResponseFromAuthenticatedUser() {
        return userMapper.toUserResponse(getAuthenticatedUser());
    }
}
