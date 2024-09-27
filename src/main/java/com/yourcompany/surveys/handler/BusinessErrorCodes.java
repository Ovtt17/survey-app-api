package com.yourcompany.surveys.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Getter
public enum BusinessErrorCodes {
    NO_CODE(0, NOT_IMPLEMENTED, "No se ha definido un código de error"),
    INCORRECT_CURRENT_PASSWORD (300, BAD_REQUEST, "La contraseña actual es incorrecta"),
    NEW_PASSWORD_DOES_NOT_MATCH (301, BAD_REQUEST, "Las contraseñas no coinciden"),
    ACCOUNT_LOCKED (302, FORBIDDEN, "La cuenta de usuario se encuentra bloqueada"),
    ACCOUNT_DISABLED (303, FORBIDDEN, "La cuenta de usuario se encuentra deshabilitada"),
    BAD_CREDENTIALS (304, FORBIDDEN, "La combinación de usuario y contraseña es incorrecta"),
    SURVEY_NOT_FOUND (305, NOT_FOUND, "Encuesta no encontrada o no eres el creador"),
    ;
    private final int code;
    private final String description;
    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.description = description;
    }
}
