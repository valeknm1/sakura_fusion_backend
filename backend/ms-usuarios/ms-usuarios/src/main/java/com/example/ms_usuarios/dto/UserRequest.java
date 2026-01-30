package com.example.ms_usuarios.dto;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserRequest", description = "DTO para crear o actualizar un usuario. " +
        "Usado en endpoints POST /api/auth/register y PUT /api/users/{id}. " +
        "Todos los campos se validan en el servidor.",
        example = "{\"nombre\":\"Juan Perez\",\"email\":\"juan@example.com\",\"password\":\"secret123\",\"rolId\":1}")
public class UserRequest {

    @NotBlank(message = "El nombre es requerido")
    @Schema(description = "Nombre completo del usuario (1-255 caracteres)", 
            example = "Juan Perez",
            minLength = 1,
            maxLength = 255)
    private String nombre;

    @Email(message = "El email debe ser válido")
    @NotBlank(message = "El email es requerido")
    @Schema(description = "Correo electrónico único y válido. Ejemplo: usuario@dominio.com", 
            example = "juan@example.com",
            format = "email")
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener mínimo 6 caracteres")
    @Schema(description = "Contraseña del usuario (mínimo 6 caracteres, máximo 255). Se almacena encriptada con BCrypt.", 
            example = "secret123",
            minLength = 6,
            maxLength = 255)
    private String password;

    @Schema(description = "ID del rol a asignar al usuario. Por defecto se asigna rol ID 1 (ROLE_USER). Solo admin puede cambiar roles.", 
            example = "1",
            minimum = "1")
    private Long rolId;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Long getRolId() { return rolId; }
    public void setRolId(Long rolId) { this.rolId = rolId; }
}
