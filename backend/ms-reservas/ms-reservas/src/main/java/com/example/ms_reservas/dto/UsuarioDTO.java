package com.example.ms_reservas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO que representa un usuario del sistema")
public class UsuarioDTO {

    @Schema(description = "Identificador único del usuario", example = "1")
    private Long idUsuario;

    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String nombre;

    @Schema(description = "Correo electrónico del usuario", example = "juan.perez@email.com")
    private String email;

    @Schema(description = "Contraseña del usuario (solo para creación/actualización)", example = "password123")
    private String password;

    @Schema(description = "Rol asignado al usuario")
    private RolDTO rol;
}