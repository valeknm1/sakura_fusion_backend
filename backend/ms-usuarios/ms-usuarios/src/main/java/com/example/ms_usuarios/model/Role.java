package com.example.ms_usuarios.model;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "rol")
@Schema(description = "Rol de usuario (permisiones)")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    @Schema(description = "ID del rol")
    private Long id;

    @Column(name = "nombre_rol", nullable = false, unique = true)
    @Schema(description = "Nombre del rol", example = "ROLE_USER")
    private String nombre;

    public Role() {}

    public Role(String nombre) { this.nombre = nombre; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
