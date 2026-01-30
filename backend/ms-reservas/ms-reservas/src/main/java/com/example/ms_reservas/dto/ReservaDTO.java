package com.example.ms_reservas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "DTO que representa una reserva en el restaurante")
public class ReservaDTO {

    @Schema(description = "Identificador único de la reserva", example = "1")
    private Long idReserva;

    @Schema(description = "Fecha de la reserva", example = "2026-01-30")
    private LocalDate fecha;

    @Schema(description = "Hora de la reserva", example = "19:00")
    private LocalTime hora;

    @Schema(description = "Cantidad de personas para la reserva", example = "4")
    private Integer cantPersonas;

    @Schema(description = "Estado de la reserva", example = "ACTIVA", allowableValues = {"ACTIVA", "CANCELADA", "COMPLETADA"})
    private String estado;

    @Schema(description = "Identificador del usuario que realiza la reserva", example = "1")
    private Long idUsuario;

    @Schema(description = "Información de la mesa reservada")
    private MesaDTO mesa;
}