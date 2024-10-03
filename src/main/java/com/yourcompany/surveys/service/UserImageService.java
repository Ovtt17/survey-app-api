package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.user.UserResponse;
import com.yourcompany.surveys.entity.ImageType;
import com.yourcompany.surveys.handler.exception.ImageDeletionException;
import com.yourcompany.surveys.handler.exception.ImageNoContentException;
import com.yourcompany.surveys.handler.exception.ImageUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserImageService {
    private final ImageService imageService;
    private final UserService userService;

    public String uploadProfilePicture(
            MultipartFile image,
            String username,
            ImageType imageType
    ) {
        try {
            UserResponse user = userService.getUserByUsername(username);
            String currentProfilePictureUrl = user.profilePictureUrl();

            if (currentProfilePictureUrl != null) {
                imageService.deleteImage(currentProfilePictureUrl);
            }
            String profilePictureName = username + "_" + imageType.getType();
            String newProfilePictureUrl = imageService.uploadImage(image, profilePictureName);
            userService.updateUserProfilePicture(username, newProfilePictureUrl);
            return newProfilePictureUrl;
        } catch (Exception e) {
            throw new ImageUploadException("Error al subir la foto de perfil: " + e.getMessage());
        }
    }

    public String deleteProfilePicture(String username) {
        try {
            UserResponse user = userService.getUserByUsername(username);
            String profilePictureUrl = user.profilePictureUrl();
            if (profilePictureUrl == null) {
                throw new ImageNoContentException("No tiene foto de perfil.");
            }
            boolean deleted = imageService.deleteImage(profilePictureUrl);
            if (deleted) {
                userService.updateUserProfilePicture(username, null);
                return "Foto de perfil eliminada correctamente.";
            } else {
                throw new ImageDeletionException("Error al eliminar la foto de perfil para el usuario: " + username);
            }
        } catch (ImageNoContentException | ImageDeletionException e) {
            throw e;
        } catch (Exception e) {
            throw new ImageDeletionException("Error inesperado al eliminar la foto de perfil: " + e.getMessage(), e);
        }
    }
}