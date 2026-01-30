package com.example.ms_usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserResponse", description = "Representación pública de un usuario. " +
        "Se retorna en endpoints GET /api/users, GET /api/users/{id}, POST /api/auth/register y PUT /api/users/{id}. " +
        "Nota: La contraseña nunca se incluye en la respuesta por seguridad.",
        example = "{\"id\":1,\"nombre\":\"Juan Perez\",\"email\":\"juan@example.com\",\"rolNombre\":\"ROLE_USER\"}")
public class UserResponse {
    
    @Schema(description = "Identificador único del usuario. Generado automáticamente por la base de datos.", 
            example = "1",
            type = "integer",
            minimum = "1")
    private Long id;

    @Schema(description = "Nombre completo del usuario. Se valida para no estar vacío.", 
            example = "Juan Perez",
            minLength = 1,
            maxLength = 255)
    private String nombre;

    @Schema(description = "Correo electrónico único del usuario. No se puede duplicar en el sistema.", 
            example = "juan@example.com",
            format = "email")
    private String email;

    @Schema(description = "Nombre del rol asociado al usuario. Valores posibles: ROLE_USER, ROLE_ADMIN, etc.", 
            example = "ROLE_USER",
            allowableValues = {"ROLE_USER", "ROLE_ADMIN"})
    private String rolNombre;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRolNombre() { return rolNombre; }
    public void setRolNombre(String rolNombre) { this.rolNombre = rolNombre; }
}
