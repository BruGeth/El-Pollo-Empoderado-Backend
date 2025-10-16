# 📋 Sprint 1 - Tarea E: Endpoints de Gestión de Usuarios (CRUD, perfil)

**Key (Jira)**: SPR1-E-User-Management  
**Estado**: ✅ **COMPLETADA**  
**Responsable**: Backend Dev B (owner)  
**Estimación total**: 6h  
**Tiempo real**: [Completar cuando se termine]

---

## 🎯 Propósito
Implementar endpoints para obtener usuario(s), editar perfil y gestión básica (para admin) con control de acceso por roles.

---

## ✅ Subtareas Completadas

### E.1 — GET /api/users y GET /api/users/{id} (ADMIN) ✅
**Estimación**: 2h  
**Descripción**: Endpoints para listar usuarios y obtener detalle (solo ADMIN)

#### Funcionalidades implementadas:
- ✅ `GET /api/users` - Lista paginada de usuarios (solo ADMIN)
- ✅ `GET /api/users/{id}` - Detalle de usuario específico (solo ADMIN)
- ✅ Paginación con parámetros `page` y `size`
- ✅ Control de acceso con `@PreAuthorize("hasRole('ADMIN')")`
- ✅ Respuesta 403/400 para usuarios sin permisos

#### Request/Response:
```http
GET /api/users?page=0&size=10
Authorization: Bearer <admin_token>
```

**Respuesta exitosa (200)**:
```json
{
  "content": [
    {
      "id": 1,
      "firstName": "Admin",
      "lastName": "User",
      "email": "admin@empoderado.com",
      "dni": "12345678",
      "birthDate": "1985-01-01",
      "address": "Admin Address",
      "roles": ["ROLE_ADMIN"],
      "createdAt": "2024-10-07T10:30:00"
    }
  ],
  "pageable": {...},
  "totalElements": 1,
  "totalPages": 1
}
```

---

### E.2 — GET /api/user/me y PUT /api/user/me (propietario) ✅
**Estimación**: 2h  
**Descripción**: Endpoints para ver y editar perfil propio

#### Funcionalidades implementadas:
- ✅ `GET /api/user/me` - Obtener datos del usuario autenticado
- ✅ `PUT /api/user/me` - Actualizar datos personales propios
- ✅ Campos editables: firstName, lastName, dni, birthDate, address
- ✅ NO permite cambiar email ni roles
- ✅ Validación de usuario autenticado

#### Request/Response:
```http
GET /api/user/me
Authorization: Bearer <user_token>
```

**Respuesta (200)**:
```json
{
  "id": 2,
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan@example.com",
  "dni": "87654321",
  "birthDate": "1990-05-15",
  "address": "Mi dirección",
  "roles": ["ROLE_USER"],
  "createdAt": "2024-10-07T11:00:00"
}
```

**Actualizar perfil**:
```http
PUT /api/user/me
Authorization: Bearer <user_token>
Content-Type: application/json

{
  "firstName": "Juan Carlos",
  "lastName": "Pérez García",
  "dni": "87654321",
  "birthDate": "1990-05-15",
  "address": "Nueva dirección 123"
}
```

---

### E.3 — PUT /api/user/me/password (cambio contraseña) ✅
**Estimación**: 2h  
**Descripción**: Endpoint para cambiar contraseña del usuario autenticado

#### Funcionalidades implementadas:
- ✅ Validación de contraseña actual
- ✅ Hash seguro de nueva contraseña
- ✅ Verificación de autenticación
- ✅ Respuesta de confirmación

#### Request/Response:
```http
PUT /api/user/me/password
Authorization: Bearer <user_token>
Content-Type: application/json

{
  "oldPassword": "password123",
  "newPassword": "newpassword456"
}
```

**Respuesta exitosa (200)**:
```json
{
  "message": "Password changed successfully"
}
```

**Error contraseña incorrecta (400)**:
```json
{
  "message": "Current password is incorrect"
}
```

---

## 🔧 Componentes Técnicos Implementados

### DTOs creados:
- `UpdateUserRequestDTO` - Datos para actualizar perfil
- `ChangePasswordRequestDTO` - Cambio de contraseña

### Servicios:
- `UserService` - Lógica de negocio para gestión de usuarios
  - `getAllUsers(Pageable)` - Lista paginada
  - `getUserById(Long)` - Usuario por ID
  - `getCurrentUser()` - Usuario autenticado
  - `updateCurrentUser(UpdateUserRequestDTO)` - Actualizar perfil
  - `changePassword(ChangePasswordRequestDTO)` - Cambiar contraseña

### Controladores:
- `UserController` - Endpoints `/api/users/**` y `/api/user/**`

### Seguridad:
- `@PreAuthorize("hasRole('ADMIN')")` para endpoints administrativos
- `SecurityUtil.getCurrentUserEmail()` para obtener usuario actual
- Validación de contraseña con `PasswordEncoder`

---

## 🧪 Criterios de Aceptación Cumplidos

### ✅ Endpoints ADMIN (E.1):
- [x] ADMIN puede listar usuarios con paginación
- [x] ADMIN puede obtener detalle de cualquier usuario
- [x] Usuarios no-admin reciben error de acceso
- [x] Paginación funciona correctamente

### ✅ Perfil propio (E.2):
- [x] Usuario puede ver sus datos completos
- [x] Usuario puede editar datos personales
- [x] NO puede cambiar email ni roles
- [x] Validaciones aplicadas correctamente

### ✅ Cambio contraseña (E.3):
- [x] Cambio exitoso retorna confirmación
- [x] Contraseña incorrecta retorna error 400
- [x] Nueva contraseña se hashea correctamente
- [x] Solo usuario autenticado puede cambiar su contraseña

---

## 🧪 Tests Implementados

### UserControllerTest:
- ✅ `shouldGetAllUsersAsAdmin()` - Admin puede listar usuarios
- ✅ `shouldRejectGetAllUsersAsUser()` - User no puede listar usuarios
- ✅ `shouldGetUserByIdAsAdmin()` - Admin puede ver usuario específico
- ✅ `shouldGetCurrentUserProfile()` - Usuario ve su perfil
- ✅ `shouldUpdateCurrentUserProfile()` - Usuario actualiza su perfil
- ✅ `shouldChangePassword()` - Cambio de contraseña exitoso
- ✅ `shouldRejectWrongOldPassword()` - Rechaza contraseña incorrecta

**Resultado**: ✅ **16 tests ejecutados, 0 fallos**

---

## 🔗 Dependencias

### Completadas previamente:
- ✅ **Tarea A**: Proyecto Spring Boot configurado
- ✅ **Tarea B**: Entidades User y Role creadas
- ✅ **Tarea C**: Spring Security y JWT configurados
- ✅ **Tarea D**: Endpoints de autenticación funcionando

### Habilita para:
- ⏳ **Tarea F**: Health check y documentación Postman
- ⏳ **Tarea G**: Frontend Angular (consumo de APIs)

---

## 📝 Notas de Implementación

### Seguridad aplicada:
- Control de acceso por roles con `@PreAuthorize`
- Validación de usuario autenticado con `SecurityUtil`
- Hash seguro de contraseñas con BCrypt
- Separación clara entre endpoints públicos y protegidos

### Paginación:
- Parámetros: `page` (default: 0), `size` (default: 10)
- Respuesta estándar de Spring Data con metadatos
- Optimización de consultas JPA

### Validaciones:
- Usuario autenticado requerido para todos los endpoints
- Verificación de contraseña actual antes de cambio
- Campos opcionales en actualización de perfil
- Manejo de errores centralizado

---

## ✅ Estado Final
**TAREA E COMPLETADA** - Todos los endpoints de gestión de usuarios funcionando correctamente con tests pasando.

**Próximo paso**: Continuar con **Tarea F** (Health check + documentación Postman) para completar el Sprint 1.