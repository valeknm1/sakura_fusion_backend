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
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Endpoints de autenticación para registro, login y gestión de sesiones JWT. " +
        "Obtén aquí tu token para acceder a endpoints protegidos.")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) { this.userService = userService; }

    @Operation(summary = "Registrar nuevo usuario",
            description = "Crea una nueva cuenta de usuario con email y contraseña. " +
                    "El email debe ser único y válido. La contraseña debe tener mínimo 6 caracteres. " +
                    "Se asigna automáticamente el rol ROLE_USER. La contraseña se encripta con BCrypt.",
            operationId = "registerUser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class),
                            examples = @ExampleObject(name = "Éxito", value = "{\"id\":1,\"nombre\":\"Juan Perez\",\"email\":\"juan@example.com\",\"rolNombre\":\"ROLE_USER\"}"))),
            @ApiResponse(responseCode = "400", description = "Validación fallida - Email duplicado, inválido, o contraseña muy corta",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Email duplicado", value = "{\"error\":\"Email already registered\"}"),
                                    @ExampleObject(name = "Contraseña corta", value = "{\"error\":\"size must be between 6 and 2147483647\"}")
                            }))
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @Operation(summary = "Autenticar usuario y obtener JWT Token",
            description = "Valida las credenciales del usuario. Si son correctas, retorna un JWT Token válido por 24 horas. " +
                    "Usa este token en el header 'Authorization: Bearer <token>' para acceder a endpoints protegidos. " +
                    "Algoritmo: HS256, Expiración: 24 horas (86400 segundos)",
            operationId = "loginUser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa - Token generado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Login exitoso", 
                                    value = "{\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqdWFuQGV4YW1wbGUuY29tIiwiaWF0IjoxNzA5MTIzNDU2LCJleHAiOjE3MDkyMDk4NTZ9.abc123\",\"tokenType\":\"Bearer\",\"expiresIn\":86400}"))),
            @ApiResponse(responseCode = "400", description = "Campos requeridos faltantes",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Campos faltantes", value = "{\"error\":\"email and password are required\"}"))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas - Email no existe o contraseña incorrecta",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Credenciales inválidas", value = "{\"error\":\"Invalid credentials\",\"message\":\"Email or password incorrect\"}")))
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciales del usuario para autenticarse",
                    required = true,
                    content = @Content(examples = @ExampleObject(value = "{\"email\":\"juan@example.com\",\"password\":\"secret123\"}")))
            @RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Email and password are required");
        }
        
        String token = userService.authenticate(email, password);
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("token", token);
        response.put("tokenType", "Bearer");
        response.put("expiresIn", 86400);
        response.put("message", "Token válido por 24 horas");
        return ResponseEntity.ok(response);
    }
}
