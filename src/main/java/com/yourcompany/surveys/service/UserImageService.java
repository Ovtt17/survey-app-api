package com.yourcompany.surveys.service;

import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.enums.ImageType;
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
            ImageType imageType
    ) {
        try {
            User user = userService.getAuthenticatedUser();
            String username = user.getName();
            String currentProfilePictureUrl = user.getProfilePictureUrl();

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

    public String deleteProfilePicture() {
        try {
            User user = userService.getAuthenticatedUser();
            String profilePictureUrl = user.getProfilePictureUrl();
            if (profilePictureUrl == null) {
                throw new ImageNoContentException("No tiene foto de perfil.");
            }
            boolean deleted = imageService.deleteImage(profilePictureUrl);
            if (deleted) {
                userService.updateUserProfilePicture(user.getName(), null);
                return "Foto de perfil eliminada correctamente.";
            } else {
                throw new ImageDeletionException("Error al eliminar la foto de perfil para el usuario: " + user.getName());
            }
        } catch (ImageNoContentException | ImageDeletionException e) {
            throw e;
        } catch (Exception e) {
            throw new ImageDeletionException("Error inesperado al eliminar la foto de perfil: " + e.getMessage(), e);
        }
    }
}