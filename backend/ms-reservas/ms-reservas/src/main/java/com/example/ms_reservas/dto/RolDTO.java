package com.example.ms_reservas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO que representa un rol en el sistema")
public class RolDTO {

    @Schema(description = "Identificador único del rol", example = "1")
    private Long idRol;

    @Schema(description = "Nombre del rol", example = "ADMIN", allowableValues = {"ADMIN", "USER", "CLIENTE"})
    private String nombreRol;
}