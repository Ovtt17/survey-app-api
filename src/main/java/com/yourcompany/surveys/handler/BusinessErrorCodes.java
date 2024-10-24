package com.yourcompany.surveys.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum BusinessErrorCodes {
    NO_CODE(0, NOT_IMPLEMENTED, "No se ha definido un código de error"),
    INCORRECT_CURRENT_PASSWORD (300, BAD_REQUEST, "La contraseña actual es incorrecta"),
    NEW_PASSWORD_DOES_NOT_MATCH (301, BAD_REQUEST, "Las contraseñas no coinciden"),
    ACCOUNT_LOCKED (302, FORBIDDEN, "La cuenta de usuario se encuentra bloqueada"),
    ACCOUNT_DISABLED (303, FORBIDDEN, "La cuenta de usuario se encuentra deshabilitada"),
    BAD_CREDENTIALS (304, FORBIDDEN, "La combinación de usuario y contraseña es incorrecta"),
    SURVEY_NOT_FOUND (305, NOT_FOUND, "Encuesta no encontrada"),
    USER_NOT_FOUND (306, NOT_FOUND, "Usuario no encontrado"),
    IMAGE_DELETION_ERROR (307, INTERNAL_SERVER_ERROR, "Error al eliminar la imagen"),
    IMAGE_UPLOAD_ERROR (308, INTERNAL_SERVER_ERROR, "Error al subir la imagen"),
    IMAGE_NOT_FOUND (312, NOT_FOUND, "Imagen no encontrada"),
    IMAGE_NO_CONTENT (313, NO_CONTENT, "No hay imagen para eliminar"),
    IMAGE_SIZE_EXCEEDED (309, BAD_REQUEST, "El tamaño de la imagen excede el límite permitido"),
    INVALID_IMAGE_URL (310, BAD_REQUEST, "URL de imagen inválida"),
    INVALID_IMAGE_FORMAT (311, BAD_REQUEST, "Formato de imagen inválido"),
    USER_UNAUTHORIZED(401, UNAUTHORIZED, "No tienes permiso para realizar esta acción"),
    SURVEY_NO_CONTENT (315, NO_CONTENT, "No hay encuestas");

    private final int code;
    private final String description;
    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.description = description;
    }
}
