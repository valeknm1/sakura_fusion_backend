package com.example.ms_reservas.controller;

import com.example.ms_reservas.dto.MesaDTO;
import com.example.ms_reservas.model.Mesa;
import com.example.ms_reservas.service.MesaService;
import com.example.ms_reservas.util.MapperUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mesas")
@Tag(name = "Mesas", description = "API para gestionar mesas del restaurante")
public class MesaController {

    @Autowired
    private MesaService mesaService;

    @GetMapping
    @Operation(summary = "Obtener todas las mesas", description = "Devuelve una lista de todas las mesas disponibles en el restaurante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mesas obtenida exitosamente")
    })
    public ResponseEntity<List<MesaDTO>> getAllMesas() {
        List<MesaDTO> mesas = mesaService.getAllMesas().stream()
                .map(MapperUtil::toMesaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mesas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener mesa por ID", description = "Devuelve los detalles de una mesa específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mesa encontrada"),
            @ApiResponse(responseCode = "404", description = "Mesa no encontrada")
    })
    public ResponseEntity<MesaDTO> getMesaById(
            @Parameter(description = "ID de la mesa a buscar", required = true, example = "1")
            @PathVariable Long id) {
        Optional<Mesa> mesa = mesaService.getMesaById(id);
        if (mesa.isPresent()) {
            return ResponseEntity.ok(MapperUtil.toMesaDTO(mesa.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Crear una nueva mesa", description = "Crea una nueva mesa en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mesa creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<MesaDTO> createMesa(
            @Parameter(description = "Datos de la mesa a crear", required = true)
            @RequestBody MesaDTO mesaDTO) {
        Mesa mesa = MapperUtil.toMesaEntity(mesaDTO);
        Mesa savedMesa = mesaService.saveMesa(mesa);
        return ResponseEntity.ok(MapperUtil.toMesaDTO(savedMesa));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar mesa", description = "Actualiza los datos de una mesa existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mesa actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Mesa no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<MesaDTO> updateMesa(
            @Parameter(description = "ID de la mesa a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados de la mesa", required = true)
            @RequestBody MesaDTO mesaDTO) {
        Optional<Mesa> existingMesa = mesaService.getMesaById(id);
        if (existingMesa.isPresent()) {
            Mesa mesa = MapperUtil.toMesaEntity(mesaDTO);
            mesa.setIdMesa(id);
            Mesa updatedMesa = mesaService.saveMesa(mesa);
            return ResponseEntity.ok(MapperUtil.toMesaDTO(updatedMesa));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mesa", description = "Elimina una mesa del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mesa eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Mesa no encontrada")
    })
    public ResponseEntity<Void> deleteMesa(
            @Parameter(description = "ID de la mesa a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        mesaService.deleteMesa(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Obtener mesas disponibles", description = "Devuelve una lista de mesas que están marcadas como disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mesas disponibles obtenida exitosamente")
    })
    public ResponseEntity<List<MesaDTO>> getMesasDisponibles() {
        List<MesaDTO> mesas = mesaService.getMesasDisponibles().stream()
                .map(MapperUtil::toMesaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mesas);
    }

    @GetMapping("/capacidad/{capacidad}")
    @Operation(summary = "Obtener mesas por capacidad mínima", description = "Devuelve mesas con capacidad mayor o igual a la especificada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mesas obtenida exitosamente")
    })
    public ResponseEntity<List<MesaDTO>> getMesasByCapacidad(
            @Parameter(description = "Capacidad mínima requerida", required = true, example = "4")
            @PathVariable Integer capacidad) {
        List<MesaDTO> mesas = mesaService.getMesasByCapacidad(capacidad).stream()
                .map(MapperUtil::toMesaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mesas);
    }
}