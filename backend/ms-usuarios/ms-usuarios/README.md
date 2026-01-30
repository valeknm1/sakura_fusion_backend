# MS-Usuarios - Microservicio de Autenticación

Microservicio REST para gestión de usuarios, autenticación con JWT, roles y más.

**Tecnologías:** Spring Boot 4.0.2 · Spring Security · JWT · JPA/Hibernate · MySQL/H2 · OpenAPI/Swagger

---

## 🚀 Comenzar Rápidamente

```bash
# 1. Ejecutar aplicación
mvnw.cmd spring-boot:run

# 2. Abrir Swagger UI
# http://localhost:8081/swagger-ui/index.html

# 3. Registrar usuario (en Swagger)
POST /api/auth/register

# 4. Login (obtener JWT)
POST /api/auth/login

# 5. Usar endpoints protegidos
GET /api/users (con header Authorization: Bearer <token>)
```

---

## 📚 Documentación

- **[GUIA_RAPIDA.md](GUIA_RAPIDA.md)** — 5 minutos para estar funcionando
- **[DOCUMENTACION_COMPLETA.md](DOCUMENTACION_COMPLETA.md)** — Guía detallada y exhaustiva
- **[EJEMPLOS_CURL_POSTMAN.md](EJEMPLOS_CURL_POSTMAN.md)** — Ejemplos prácticos con cURL y Postman

---

## 📋 Endpoints Principales

### Autenticación (sin JWT)
- `POST /api/auth/register` — Registrar usuario
- `POST /api/auth/login` — Obtener token JWT

### Usuarios (requieren JWT)
- `GET /api/users` — Listar todos
- `GET /api/users/{id}` — Obtener por ID
- `PUT /api/users/{id}` — Actualizar
- `DELETE /api/users/{id}` — Eliminar

### Documentación
- `GET /swagger-ui/index.html` — Swagger UI interactivo
- `GET /h2-console` — Consola BD (desarrollo)

---

## ✨ Características

✅ Autenticación con JWT  
✅ Gestión completa de usuarios (CRUD)  
✅ Gestión de roles  
✅ Encriptación BCrypt  
✅ BD H2 en desarrollo, MySQL en producción  
✅ Documentación OpenAPI/Swagger  
✅ Pruebas unitarias con Mockito  
✅ Validaciones de entrada  

---

## 📝 Ejemplo Rápido

### Registrar
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Juan","email":"juan@test.com","password":"pass123"}'
```

### Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"juan@test.com","password":"pass123"}'
```

### Usar Token
```bash
curl -X GET http://localhost:8081/api/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## 🔧 Configuración

### BD (por defecto H2)
- URL: `jdbc:h2:mem:testdb`
- Usuario: `sa`
- Contraseña: (vacío)

### JWT Secret (⚠️ cambiar en producción)
```properties
app.jwt.secret=TuClaveSeguraAqui
app.jwt.expiration-ms=86400000  # 24 horas
```

---

## 🧪 Tests

```bash
mvnw.cmd test
```

Resultado: 9 tests ✅

---

## ⚠️ Problemas

| Problema | Solución |
|----------|----------|
| Puerto 8081 en uso | `server.port=8082` en `application.properties` |
| Error 403 sin token | Incluye `Authorization: Bearer <token>` |
| Email duplicado | Cada usuario necesita email único |

Ver [DOCUMENTACION_COMPLETA.md](DOCUMENTACION_COMPLETA.md#-troubleshooting) para más.

---

## 📂 Estructura

```
src/main/java/com/example/ms_usuarios/
├── controller/       # Endpoints REST
├── service/          # Lógica de negocio
├── model/            # Entidades (User, Role)
├── repository/       # Acceso a BD
├── dto/              # DTOs (UserRequest, UserResponse)
├── security/         # JWT, filtros
├── config/           # Configuración (Security, OpenAPI)
└── init/             # Inicializadores (roles por defecto)
Para usar MySQL en producción, actualiza `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sakura_fusion_db
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=tu_password
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

Luego crea la base de datos en MySQL:
```sql
CREATE DATABASE sakura_fusion_db;
```

## Swagger / OpenAPI

Una vez levantada la aplicación, la documentación Swagger UI estará disponible en:

- http://localhost:8081/swagger-ui/index.html

La especificación OpenAPI JSON se sirve en:

- http://localhost:8081/v3/api-docs

H2 Console (si usas H2):
- http://localhost:8081/h2-console

## Endpoints principales

### Registro
POST `/api/auth/register`
Body ejemplo:

```json
{
  "nombre": "Juan",
  "email": "juan@example.com",
  "password": "secret123"
}
```

### Login
POST `/api/auth/login`
Body ejemplo:

```json
{ "email": "juan@example.com", "password": "secret123" }
```
Respuesta ejemplo:

```json
{ "token": "<jwt-token>" }
```

### Usar token
Incluye en las peticiones protegidas el header:

```
Authorization: Bearer <jwt-token>
```

### Usuarios
- GET `/api/users` - Lista usuarios (protegido)
- GET `/api/users/{id}` - Obtener usuario (protegido)
- PUT `/api/users/{id}` - Actualizar usuario (protegido)
- DELETE `/api/users/{id}` - Eliminar usuario (protegido)

## Ejemplos curl

Registrar:

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Juan","email":"juan@example.com","password":"secret123"}'
```

Login:

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"juan@example.com","password":"secret123"}'
```

Listar usuarios (con token):

```bash
curl -H "Authorization: Bearer <jwt-token>" \
  http://localhost:8081/api/users
```

## Notas
- El proyecto incluye roles por defecto (`ROLE_USER`, `ROLE_ADMIN`).
- JWT expira en 24 horas (configurable en `application.properties`).
- Todos los tests pasan: ejecuta `mvnw test` para validar.
- Para ver ejemplos más detallados, abre Swagger UI.

 
## Ejecutar sin MySQL (perfil `dev`)

Si no quieres usar MySQL en desarrollo puedes ejecutar la app con una base de datos en memoria H2:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
# Windows PowerShell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

La consola H2 estará disponible en `http://localhost:8081/h2-console` (JDBC URL: `jdbc:h2:mem:ms_usuarios`).

## Crear la base de datos MySQL

Si prefieres usar MySQL crea la base de datos que indica `application.properties` (o ajusta el nombre):

```sql
CREATE DATABASE sakura_fusion_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Luego actualiza `spring.datasource.username` y `spring.datasource.password` en `src/main/resources/application.properties`.
