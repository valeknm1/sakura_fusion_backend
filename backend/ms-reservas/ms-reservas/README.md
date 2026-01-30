# Microservicio de Reservas y Mesas

Este microservicio gestiona reservas y mesas para el restaurante Sakura Fusion.

## Funcionalidades

### Mesas
- CRUD de mesas
- Consultar mesas disponibles
- Consultar mesas por capacidad

### Reservas
- CRUD de reservas
- Crear reservas (con verificación de disponibilidad y validación de usuario)
- Modificar reservas
- Cancelar reservas
- Consultar reservas por fecha
- Consultar reservas por usuario
- Consultar reservas por estado

## Tecnologías
- Spring Boot
- Spring Data JPA
- Spring Cloud OpenFeign (para comunicación con ms-usuarios)
- SpringDoc OpenAPI (Swagger)
- MySQL
- Lombok

## Documentación API (Swagger)

La documentación completa de la API está disponible a través de Swagger UI.

- **URL de Swagger UI**: `http://localhost:8082/swagger-ui/index.html`
- **URL de OpenAPI JSON**: `http://localhost:8082/v3/api-docs`

### Características de la Documentación
- Descripciones detalladas de todos los endpoints
- Ejemplos de request/response
- Parámetros y tipos de datos documentados
- Códigos de respuesta HTTP
- Agrupación por controladores (Mesas y Reservas)

## Configuración

1. Asegúrate de tener MySQL corriendo en localhost:3306.
2. Crea la base de datos `sakura_fusion_db` o configúrala en `application.properties`.
3. Asegúrate de que el microservicio de Usuarios esté corriendo en `http://localhost:8081`.
4. Ejecuta la aplicación con `./mvnw spring-boot:run`.

## Integración con Microservicio de Usuarios

Este microservicio se conecta con el microservicio de Usuarios para validar que el usuario existe antes de crear una reserva. Usa OpenFeign para llamar al endpoint `/api/usuarios/{id}` del ms-usuarios.

## Endpoints

### Mesas
- GET /api/mesas - Obtener todas las mesas
- GET /api/mesas/{id} - Obtener mesa por ID
- POST /api/mesas - Crear mesa
- PUT /api/mesas/{id} - Actualizar mesa
- DELETE /api/mesas/{id} - Eliminar mesa
- GET /api/mesas/disponibles - Mesas disponibles
- GET /api/mesas/capacidad/{capacidad} - Mesas con capacidad >= capacidad

### Reservas
- GET /api/reservas - Obtener todas las reservas
- GET /api/reservas/{id} - Obtener reserva por ID
- POST /api/reservas - Crear reserva
- PUT /api/reservas/{id} - Actualizar reserva
- DELETE /api/reservas/{id} - Eliminar reserva
- GET /api/reservas/fecha/{fecha} - Reservas por fecha
- GET /api/reservas/usuario/{idUsuario} - Reservas por usuario
- GET /api/reservas/estado/{estado} - Reservas por estado
- PUT /api/reservas/{id}/cancelar - Cancelar reserva

## Base de Datos

Las tablas se crean automáticamente con Hibernate. Datos de prueba se insertan desde `data.sql`.