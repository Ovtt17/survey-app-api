package com.yourcompany.surveys.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class RegistrationRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    @NotNull (message = "La fecha de nacimiento es obligatoria")
    private LocalDate dateOfBirth;

    @NotNull (message = "El teléfono es obligatorio")
    @Digits(integer = 8, fraction = 0, message = "El teléfono debe tener exactamente 8 dígitos")
    @Positive (message = "El teléfono debe ser un número positivo")
    private Integer phone;

    @Email (message = "El email no es válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size (min = 4, message = "El nombre de usuario debe tener al menos 4 caracteres")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size (min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
}
