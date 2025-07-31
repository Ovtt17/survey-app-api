package com.yourcompany.surveys.service;

import com.yourcompany.surveys.handler.exception.ImageDeletionException;
import com.yourcompany.surveys.handler.exception.ImageUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${imgur.url.upload}")
    private String imgur_url;

    @Value("${imgur.access_token}")
    private String accessToken;

    private static final long MAX_IMAGE_SIZE_MB = 5;
    private static final long MAX_IMAGE_SIZE_BYTES = MAX_IMAGE_SIZE_MB * 1024 * 1024;

    public boolean deleteImage(String imageUrl) {
        try {
            String imageHash = getHashFromUrl(imageUrl);
            String deleteUrl = imgur_url + "/" + imageHash;
            HttpHeaders headers = createHeaders();
            HttpEntity<String> request = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, request, new ParameterizedTypeReference<>() {});
            boolean isSuccessful = response.getStatusCode().is2xxSuccessful();
            if (!isSuccessful) {
                throw new ImageDeletionException("Error al eliminar la foto: " + response.getStatusCode());
            }
            return true;
        } catch (ImageDeletionException e) {
            throw e;
        } catch (Exception e) {
            throw new ImageDeletionException("Error al eliminar la foto: " + e.getMessage(), e);
        }
    }

    private String getHashFromUrl(String imageUrl) {
        URI uri = URI.create(imageUrl);
        String path = uri.getPath();
        int lastSlashIndex = path.lastIndexOf('/');
        int dotIndex = path.lastIndexOf('.');
        if (lastSlashIndex == -1 || dotIndex == -1 || dotIndex <= lastSlashIndex) {
            throw new IllegalArgumentException("Invalid picture URL format");
        }
        return path.substring(lastSlashIndex + 1, dotIndex);
    }

    public String uploadImage(MultipartFile image, String imageName) {
        try {
            validateImageType(image);
            validateImageSize(image);
            HttpHeaders headers = createHeaders();
            MultiValueMap<String, Object> body = createRequestBody(image, imageName);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            return uploadImageToServer(request);
        }
        catch (IOException e) {
            throw new ImageUploadException("Error al procesar la imagen: " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new ImageUploadException("Error al subir la imagen al servidor: " + e.getMessage(), e);
        } catch (ImageUploadException e) {
            throw e;
        } catch (Exception e) {
            throw new ImageUploadException("Error inesperado: " + e.getMessage(), e);
        }
    }

    private void validateImageType(MultipartFile image) {
        String contentType = image.getContentType();
        String originalFilename = image.getOriginalFilename();

        // Validación por tipo MIME
        boolean validMimeType = contentType != null &&
                (contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/webp"));

        // Validación por extensión
        boolean validExtension = originalFilename != null &&
                originalFilename.toLowerCase().matches(".*\\.(jpg|jpeg|png|webp)$");

        if (!validMimeType || !validExtension) {
            throw new ImageUploadException("El tipo de archivo no es válido. Solo se permiten archivos JPG, JPEG, PNG y WEBP.");
        }
    }

    private void validateImageSize(MultipartFile image) {
        if (image.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new ImageUploadException("El tamaño de la imagen excede el límite de 5 MB.");
        }
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
        throw new ImageUploadException("Error al subir la imagen al servidor o el enlace no es válido.");
    }
}