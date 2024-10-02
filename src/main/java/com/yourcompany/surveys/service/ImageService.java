package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.user.UserResponse;
import com.yourcompany.surveys.entity.ImageType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final UserService userService;
    @Value("${imgur.url.upload}")
    private String imgur_url;

    @Value("${imgur.access_token}")
    private String accessToken;

    private static final long MAX_IMAGE_SIZE_MB = 5;
    private static final long MAX_IMAGE_SIZE_BYTES = MAX_IMAGE_SIZE_MB * 1024 * 1024;

    public String uploadProfilePicture(
            MultipartFile image,
            String username,
            ImageType imageType
    ) {
        String profilePictureName = username + "_" + imageType.getType();
        return uploadImage(image, profilePictureName);
    }

    public String uploadSurveyPicture(
            MultipartFile image,
            Long surveyId,
            String username,
            ImageType imageType
    ) {
        String surveyPictureName = "survey_" + surveyId + "_" + username + "_" + imageType.getType();
        return uploadImage(image, surveyPictureName);
    }

    public ResponseEntity<String> deleteImage(String username) {
        try {
            UserResponse user = userService.getUserByUsername(username);
            if (user.profilePictureUrl() == null) {
                return ResponseEntity.noContent().build();
            }
            String imageHash = getHashFromUrl(user.profilePictureUrl());
            String deleteUrl = imgur_url + "/" + imageHash;
            HttpHeaders headers = createHeaders();
            HttpEntity<String> request = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, request, new ParameterizedTypeReference<>() {});
            if (response.getStatusCode().is2xxSuccessful()) {
                userService.updateUserProfilePicture(username, null);
                return ResponseEntity.ok("Foto de perfil eliminada correctamente.");
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Error al eliminar la foto de perfil.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    private String getHashFromUrl (String imageUrl) {
        URI uri = URI.create(imageUrl);
        String path = uri.getPath();
        int lastSlashIndex = path.lastIndexOf('/');
        int dotIndex = path.lastIndexOf('.');
        if (lastSlashIndex == -1 || dotIndex == -1 || dotIndex <= lastSlashIndex) {
            throw new IllegalArgumentException("Invalid image URL format");
        }
        return path.substring(lastSlashIndex + 1, dotIndex);
    }

    private String uploadImage(
            MultipartFile image,
            String imageName
    ) {
        try {
            if (isImageSizeExceeded(image)) {
                return "El tamaño de la imagen excede el límite de 5 MB.";
            }
            HttpHeaders headers = createHeaders();
            MultiValueMap<String, Object> body = createRequestBody(image, imageName);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            return uploadImageToServer(request);
        } catch (IOException e) {
            return "Error al procesar la imagen: " + e.getMessage();
        } catch (Exception e) {
            return "Error inesperado: " + e.getMessage();
        }
    }

    private boolean isImageSizeExceeded(MultipartFile image) {
        return image.getSize() > MAX_IMAGE_SIZE_BYTES;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }

    private MultiValueMap<String, Object> createRequestBody(MultipartFile image, String imageName) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        });
        body.add("type", "file");
        body.add("name", imageName);
        body.add("title", imageName);
        return body;
    }

    private String uploadImageToServer(HttpEntity<MultiValueMap<String, Object>> request) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(imgur_url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {});

        Map<String, Object> responseData = response.getBody();
        if (responseData != null) {
            Object data = responseData.get("data");
            if (data instanceof Map) {
                Object imageLink = ((Map<?, ?>) data).get("link");
                if (imageLink instanceof String) {
                    return (String) imageLink;
                }
            }
        }
        return "Error al subir la imagen a Imgur o el enlace no es válido.";
    }


}