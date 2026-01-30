package com.example.ms_reservas.controller;

import com.example.ms_reservas.dto.ReservaDTO;
import com.example.ms_reservas.model.Reserva;
import com.example.ms_reservas.service.ReservaService;
import com.example.ms_reservas.util.MapperUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "API para gestionar reservas del restaurante")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    @Operation(summary = "Obtener todas las reservas", description = "Devuelve una lista de todas las reservas en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas obtenida exitosamente")
    })
    public ResponseEntity<List<ReservaDTO>> getAllReservas() {
        List<ReservaDTO> reservas = reservaService.getAllReservas().stream()
                .map(MapperUtil::toReservaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reserva por ID", description = "Devuelve los detalles de una reserva específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<ReservaDTO> getReservaById(
            @Parameter(description = "ID de la reserva a buscar", required = true, example = "1")
            @PathVariable Long id) {
        Optional<Reserva> reserva = reservaService.getReservaById(id);
        if (reserva.isPresent()) {
            return ResponseEntity.ok(MapperUtil.toReservaDTO(reserva.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Crear una nueva reserva", description = "Crea una nueva reserva validando disponibilidad de mesa y existencia del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o mesa no disponible"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ReservaDTO> createReserva(
            @Parameter(description = "Datos de la reserva a crear", required = true)
            @RequestBody ReservaDTO reservaDTO) {
        Reserva reserva = MapperUtil.toReservaEntity(reservaDTO);
        Reserva savedReserva = reservaService.saveReserva(reserva);
        return ResponseEntity.ok(MapperUtil.toReservaDTO(savedReserva));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar reserva", description = "Actualiza los datos de una reserva existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ReservaDTO> updateReserva(
            @Parameter(description = "ID de la reserva a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados de la reserva", required = true)
            @RequestBody ReservaDTO reservaDTO) {
        Optional<Reserva> existingReserva = reservaService.getReservaById(id);
        if (existingReserva.isPresent()) {
            Reserva reserva = MapperUtil.toReservaEntity(reservaDTO);
            reserva.setIdReserva(id);
            Reserva updatedReserva = reservaService.saveReserva(reserva);
            return ResponseEntity.ok(MapperUtil.toReservaDTO(updatedReserva));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar reserva", description = "Elimina una reserva del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reserva eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<Void> deleteReserva(
            @Parameter(description = "ID de la reserva a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        reservaService.deleteReserva(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fecha/{fecha}")
    @Operation(summary = "Obtener reservas por fecha", description = "Devuelve una lista de reservas para una fecha específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas obtenida exitosamente")
    })
    public ResponseEntity<List<ReservaDTO>> getReservasByFecha(
            @Parameter(description = "Fecha de las reservas (formato YYYY-MM-DD)", required = true, example = "2026-01-30")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<ReservaDTO> reservas = reservaService.getReservasByFecha(fecha).stream()
                .map(MapperUtil::toReservaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Obtener reservas por usuario", description = "Devuelve una lista de reservas realizadas por un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas obtenida exitosamente")
    })
    public ResponseEntity<List<ReservaDTO>> getReservasByUsuario(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long idUsuario) {
        List<ReservaDTO> reservas = reservaService.getReservasByUsuario(idUsuario).stream()
                .map(MapperUtil::toReservaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener reservas por estado", description = "Devuelve una lista de reservas con un estado específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas obtenida exitosamente")
    })
    public ResponseEntity<List<ReservaDTO>> getReservasByEstado(
            @Parameter(description = "Estado de las reservas", required = true, example = "ACTIVA")
            @PathVariable String estado) {
        List<ReservaDTO> reservas = reservaService.getReservasByEstado(estado).stream()
                .map(MapperUtil::toReservaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reservas);
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar reserva", description = "Cambia el estado de una reserva a CANCELADA")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva cancelada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ReservaDTO> cancelarReserva(
            @Parameter(description = "ID de la reserva a cancelar", required = true, example = "1")
            @PathVariable Long id) {
        Reserva reserva = reservaService.cancelarReserva(id);
        return ResponseEntity.ok(MapperUtil.toReservaDTO(reserva));
    }
}