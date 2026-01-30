package com.example.ms_reservas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO que representa una mesa en el restaurante")
public class MesaDTO {

    @Schema(description = "Identificador único de la mesa", example = "1")
    private Long idMesa;

    @Schema(description = "Número de la mesa", example = "5")
    private Integer numero;

    @Schema(description = "Capacidad máxima de personas en la mesa", example = "4")
    private Integer capacidad;

    @Schema(description = "Indica si la mesa está disponible", example = "true")
    private Boolean disponible;
}