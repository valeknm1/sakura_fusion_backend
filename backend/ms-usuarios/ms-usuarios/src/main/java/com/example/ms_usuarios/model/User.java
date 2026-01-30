package com.example.ms_usuarios.model;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "usuario")
@Schema(description = "Entidad de usuario persistida")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    @Schema(description = "ID del usuario")
    private Long id;

    @Column(name = "nombre", nullable = false)
    @Schema(description = "Nombre completo del usuario", example = "Juan Perez")
    private String nombre;

    @Column(name = "email", nullable = false, unique = true)
    @Schema(description = "Correo electrónico", example = "juan@example.com")
    private String email;

    @Column(name = "password", nullable = false)
    @Schema(description = "Contraseña encriptada (no se expone en las respuestas)")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol")
    @Schema(description = "Rol asociado al usuario")
    private Role rol;

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRol() { return rol; }
    public void setRol(Role rol) { this.rol = rol; }
}
