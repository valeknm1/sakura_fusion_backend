package com.example.ms_usuarios.controller;

import com.example.ms_usuarios.dto.UserRequest;
import com.example.ms_usuarios.dto.UserResponse;
import com.example.ms_usuarios.service.UserService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Endpoints para gestión de usuarios. " +
        "TODOS estos endpoints requieren autenticación JWT. " +
        "Incluye el token en el header: Authorization: Bearer <jwt-token>")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

    @Operation(summary = "Listar todos los usuarios",
            description = "Obtiene una lista paginada de todos los usuarios registrados en el sistema. " +
                    "Requiere autenticación JWT. Solo usuarios autenticados pueden ver esta información.",
            operationId = "listAllUsers",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class),
                            examples = @ExampleObject(name = "Lista de usuarios", 
                                    value = "[{\"id\":1,\"nombre\":\"Juan Perez\",\"email\":\"juan@example.com\",\"rolNombre\":\"ROLE_USER\"}," +
                                            "{\"id\":2,\"nombre\":\"Admin User\",\"email\":\"admin@example.com\",\"rolNombre\":\"ROLE_ADMIN\"}]"))),
            @ApiResponse(responseCode = "401", description = "Token JWT no válido, expirado o ausente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Permisos insuficientes")
    })
    @GetMapping
    public ResponseEntity<List<UserResponse>> list() {
        return ResponseEntity.ok(userService.listAll());
    }

    @Operation(summary = "Obtener usuario por ID",
            description = "Obtiene los detalles de un usuario específico usando su ID. " +
                    "Requiere autenticación JWT. Retorna 404 si el usuario no existe.",
            operationId = "getUserById",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class),
                            examples = @ExampleObject(name = "Usuario encontrado", 
                                    value = "{\"id\":1,\"nombre\":\"Juan Perez\",\"email\":\"juan@example.com\",\"rolNombre\":\"ROLE_USER\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT no válido, expirado o ausente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado con el ID especificado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"User not found\",\"id\":999}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @Operation(summary = "Actualizar datos de un usuario",
            description = "Actualiza el nombre, email y/o rol de un usuario existente. " +
                    "Requiere autenticación JWT. El email debe ser único. " +
                    "Si proporciona una nueva contraseña (6+ caracteres), se actualiza y se encripta.",
            operationId = "updateUser",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class),
                            examples = @ExampleObject(name = "Actualización exitosa", 
                                    value = "{\"id\":1,\"nombre\":\"Juan Perez Updated\",\"email\":\"juan.updated@example.com\",\"rolNombre\":\"ROLE_USER\"}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos - Email duplicado, formato incorrecto, etc.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"Email already registered\",\"email\":\"otro@example.com\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT no válido, expirado o ausente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"User not found\",\"id\":999}")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserRequest req) {
        return ResponseEntity.ok(userService.update(id, req));
    }

    @Operation(summary = "Eliminar un usuario",
            description = "Elimina un usuario del sistema de forma permanente. " +
                    "Requiere autenticación JWT. Retorna 204 No Content si tiene éxito. " +
                    "Nota: Este cambio es irreversible.",
            operationId = "deleteUser",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente (sin contenido en respuesta)"),
            @ApiResponse(responseCode = "401", description = "Token JWT no válido, expirado o ausente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo admin puede eliminar usuarios"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\":\"User not found\",\"id\":999}")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
