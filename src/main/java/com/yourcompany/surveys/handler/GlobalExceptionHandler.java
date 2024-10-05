package com.yourcompany.surveys.handler;

import com.yourcompany.surveys.handler.exception.*;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static com.yourcompany.surveys.handler.BusinessErrorCodes.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleLockedException (LockedException e) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ACCOUNT_LOCKED.getCode())
                                .businessErrorDescription(ACCOUNT_LOCKED.getDescription())
                                .error(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleDisabledException (DisabledException e) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ACCOUNT_DISABLED.getCode())
                                .businessErrorDescription(ACCOUNT_DISABLED.getDescription())
                                .error(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentialsException (BadCredentialsException e) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(BAD_CREDENTIALS.getCode())
                                .businessErrorDescription(BAD_CREDENTIALS.getDescription())
                                .error(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleMessagingException (MessagingException e) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .error(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException (MethodArgumentNotValidException e) {
        Set<String> errors = new HashSet<>();
        e.getBindingResult()
                .getAllErrors()
                .forEach((error) -> {
                    String errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .validationErrors(errors)
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGeneralException (Exception e) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorDescription("Ha ocurrido un error inesperado, contacta con el administrador del sistema")
                                .error(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(SurveyNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleSurveyNotFoundException (SurveyNotFoundException e) {
        return ResponseEntity
                .status(SURVEY_NOT_FOUND.getHttpStatus())
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(SURVEY_NOT_FOUND.getCode())
                                .businessErrorDescription(SURVEY_NOT_FOUND.getDescription())
                                .error(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFoundException (UserNotFoundException e) {
        return ResponseEntity
                .status(USER_NOT_FOUND.getHttpStatus())
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(USER_NOT_FOUND.getCode())
                                .businessErrorDescription(USER_NOT_FOUND.getDescription())
                                .error(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(ImageDeletionException.class)
    public ResponseEntity<ExceptionResponse> handleImageDeletionException (ImageDeletionException e) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(IMAGE_DELETION_ERROR.getCode())
                                .businessErrorDescription(IMAGE_DELETION_ERROR.getDescription())
                                .error(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ExceptionResponse> handleImageUploadException (ImageUploadException e) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(IMAGE_UPLOAD_ERROR.getCode())
                                .businessErrorDescription(IMAGE_UPLOAD_ERROR.getDescription())
                                .error(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(ImageNoContentException.class)
    public ResponseEntity<ExceptionResponse> handleImageNoContentException(ImageNoContentException e) {
        return ResponseEntity
                .status(NO_CONTENT)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(IMAGE_NO_CONTENT.getCode())
                                .businessErrorDescription(IMAGE_NO_CONTENT.getDescription())
                                .error(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionResponse> handleUnauthorizedException(UnauthorizedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(BusinessErrorCodes.USER_UNAUTHORIZED.getCode())
                                .businessErrorDescription(BusinessErrorCodes.USER_UNAUTHORIZED.getDescription())
                                .error(e.getMessage())
                                .build()
                );
    }
}
