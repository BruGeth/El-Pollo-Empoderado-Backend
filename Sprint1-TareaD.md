# 📋 Sprint 1 - Tarea D: Endpoints de Auth (Register & Login)

**Key (Jira)**: SPR1-D-Auth-Endpoints  
**Estado**: ✅ **COMPLETADA**  
**Responsable**: Backend Dev A (owner), Backend Dev B (apoyo)  
**Estimación total**: 12h  
**Tiempo real**: [Completar cuando se termine]

---

## 🎯 Propósito
Implementar endpoints REST para registro y autenticación de usuarios con validaciones, seguridad JWT y manejo de errores.

---

## ✅ Subtareas Completadas

### D.1 — POST /api/auth/register ✅
**Estimación**: 6h  
**Descripción**: Endpoint de registro de nuevos usuarios

#### Funcionalidades implementadas:
- ✅ Recibe `RegisterRequest` con validaciones
- ✅ Verifica email único en base de datos
- ✅ Hash seguro de contraseña con BCrypt
- ✅ Asignación automática de rol `ROLE_USER`
- ✅ Persistencia en base de datos
- ✅ Respuesta con `UserDTO` (sin password)
- ✅ Manejo de errores (email duplicado → 400)

#### Request/Response:
```http
POST /api/auth/register
Content-Type: application/json

{
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan@example.com",
  "password": "password123",
  "dni": "12345678",
  "birthDate": "1990-01-01",
  "address": "Av. Principal 123"
}
```

**Respuesta exitosa (201)**:
```json
{
  "id": 1,
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan@example.com",
  "dni": "12345678",
  "birthDate": "1990-01-01",
  "address": "Av. Principal 123",
  "roles": ["ROLE_USER"],
  "createdAt": "2024-10-07T10:30:00"
}
```

**Error email duplicado (400)**:
```json
{
  "error": "Email already exists",
  "message": "El email juan@example.com ya está registrado",
  "timestamp": "2024-10-07T10:30:00"
}
```

---

### D.2 — POST /api/auth/login ✅
**Estimación**: 4h  
**Descripción**: Endpoint de autenticación con JWT

#### Funcionalidades implementadas:
- ✅ Recibe `LoginRequest` (email, password)
- ✅ Autenticación con `AuthenticationManager`
- ✅ Generación de token JWT
- ✅ Respuesta con `AuthResponse` completa
- ✅ Manejo de credenciales inválidas → 401

#### Request/Response:
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "juan@example.com",
  "password": "password123"
}
```

**Respuesta exitosa (200)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400,
  "user": {
    "id": 1,
    "email": "juan@example.com",
    "firstName": "Juan",
    "lastName": "Pérez",
    "roles": ["ROLE_USER"]
  }
}
```

**Error credenciales inválidas (401)**:
```json
{
  "error": "Invalid credentials",
  "message": "Email o contraseña incorrectos",
  "timestamp": "2024-10-07T10:30:00"
}
```

---

### D.3 — Validaciones y manejo de errores ✅
**Estimación**: 2h  
**Descripción**: Manejo centralizado de excepciones

#### Funcionalidades implementadas:
- ✅ `@ControllerAdvice` para manejo global de errores
- ✅ Validaciones de entrada con `@Valid`
- ✅ Respuestas JSON estandarizadas
- ✅ Códigos HTTP apropiados
- ✅ Mensajes de error claros en español

#### Validaciones aplicadas:
- **Email**: Formato válido y único
- **Password**: Mínimo 6 caracteres
- **Nombres**: No vacíos, máximo 50 caracteres
- **DNI**: 8 dígitos numéricos
- **Fecha nacimiento**: Formato válido, mayor de edad

---

## 🔧 Componentes Técnicos Implementados

### DTOs creados:
- `RegisterRequest` - Datos de registro
- `LoginRequest` - Credenciales de login  
- `AuthResponse` - Respuesta de autenticación
- `UserDTO` - Datos de usuario (sin password)
- `ErrorResponse` - Respuesta de errores

### Servicios:
- `AuthService` - Lógica de autenticación
- `UserService` - Gestión de usuarios
- `JwtService` - Generación y validación JWT

### Controladores:
- `AuthController` - Endpoints `/api/auth/**`

### Configuración:
- `GlobalExceptionHandler` - Manejo de errores
- `SecurityConfig` - Rutas públicas `/api/auth/**`

---

## 🧪 Criterios de Aceptación Cumplidos

### ✅ Registro (D.1):
- [x] Registro válido retorna 201 con UserDTO
- [x] Email duplicado retorna 400 con mensaje claro
- [x] Contraseña se almacena hasheada (BCrypt)
- [x] Usuario se crea con rol ROLE_USER por defecto
- [x] Validaciones de entrada funcionan correctamente

### ✅ Login (D.2):
- [x] Credenciales válidas retornan token JWT
- [x] Credenciales inválidas retornan 401
- [x] Token incluye información del usuario
- [x] Token tiene expiración configurada (24h)

### ✅ Validaciones (D.3):
- [x] Errores devuelven JSON con mensaje estándar
- [x] Códigos HTTP apropiados (400, 401, 201, 200)
- [x] Manejo centralizado con @ControllerAdvice
- [x] Mensajes de error en español

---

## 🔗 Dependencias

### Completadas previamente:
- ✅ **Tarea A**: Proyecto Spring Boot configurado
- ✅ **Tarea B**: Entidades User y Role creadas
- ✅ **Tarea C**: Spring Security y JWT configurados

### Habilita para:
- ⏳ **Tarea E**: Endpoints de gestión de usuarios
- ⏳ **Tarea F**: Health check y documentación Postman
- ⏳ **Tarea G**: Frontend Angular (consumo de APIs)

---

## 📝 Notas de Implementación

### Seguridad aplicada:
- Contraseñas hasheadas con BCrypt (strength 12)
- Tokens JWT firmados con secret seguro
- Validación de entrada en todos los endpoints
- Rutas `/api/auth/**` públicas, resto protegidas

### Configuración de JWT:
- **Expiración**: 24 horas
- **Algoritmo**: HS256
- **Claims**: userId, email, roles
- **Header**: `Authorization: Bearer <token>`

### Base de datos:
- Usuario admin creado automáticamente:
  - Email: `admin@empoderado.com`
  - Password: `admin123`
  - Rol: `ROLE_ADMIN`

---

## ✅ Estado Final
**TAREA D COMPLETADA** - Todos los endpoints de autenticación funcionando correctamente y listos para la demo del 16/10.

**Próximo paso**: Continuar con **Tarea E** (CRUD de usuarios) o **Tarea F** (Health check + Postman).