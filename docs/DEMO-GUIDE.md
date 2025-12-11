# 🎯 Guía de Demo - Sprint 1 (16 de Octubre)

## 📋 Preparación Previa

### 1. Levantar el Backend
```bash
# Asegurarse de tener MySQL corriendo
# Crear base de datos si no existe
mysql -u root -p
CREATE DATABASE IF NOT EXISTS pollo_empoderado_db;

# Ejecutar la aplicación
mvnw.cmd spring-boot:run
```

### 2. Verificar que funciona
- Abrir: http://localhost:8080/api/health
- Debe mostrar: `{"status": "ok", "version": "0.1", "time": "...", "service": "El Pollo Empoderado Backend"}`

---

## 🚀 Flujo de Demo (10-12 minutos)

### **Paso 1: Health Check** (1 min)
```http
GET http://localhost:8080/api/health
```
**Resultado esperado**: Status 200, JSON con información del servicio

---

### **Paso 2: Registrar Usuario** (2 min)
```http
POST http://localhost:8080/api/auth/register
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
**Resultado esperado**: Status 201, usuario creado con token JWT

---

### **Paso 3: Login Usuario** (1 min)
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "juan@example.com",
  "password": "password123"
}
```
**Resultado esperado**: Status 200, token JWT válido
**Guardar**: `user_token` para siguientes requests

---

### **Paso 4: Login Admin** (1 min)
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "admin@empoderado.com",
  "password": "admin123"
}
```
**Resultado esperado**: Status 200, token JWT de admin
**Guardar**: `admin_token` para siguientes requests

---

### **Paso 5: Demostrar Seguridad** (3 min)

#### 5a. Sin token → 401
```http
GET http://localhost:8080/api/users
```
**Resultado esperado**: Status 401 Unauthorized

#### 5b. Con token de usuario → 403/400
```http
GET http://localhost:8080/api/users
Authorization: Bearer {user_token}
```
**Resultado esperado**: Status 403 Forbidden o 400 Bad Request

#### 5c. Con token de admin → 200
```http
GET http://localhost:8080/api/users
Authorization: Bearer {admin_token}
```
**Resultado esperado**: Status 200, lista de usuarios

---

### **Paso 6: Gestión de Perfil** (2 min)

#### 6a. Ver mi perfil
```http
GET http://localhost:8080/api/user/me
Authorization: Bearer {user_token}
```

#### 6b. Actualizar perfil
```http
PUT http://localhost:8080/api/user/me
Authorization: Bearer {user_token}
Content-Type: application/json

{
  "firstName": "Juan Carlos",
  "lastName": "Pérez García",
  "address": "Nueva dirección 456"
}
```

---

### **Paso 7: Cambio de Contraseña** (1 min)
```http
PUT http://localhost:8080/api/user/me/password
Authorization: Bearer {user_token}
Content-Type: application/json

{
  "oldPassword": "password123",
  "newPassword": "newpassword456"
}
```

---

## 📊 Puntos Clave a Destacar

### ✅ **Funcionalidades Completadas**:
1. **Autenticación JWT** - Registro y login seguros
2. **Control de Acceso** - Roles USER y ADMIN
3. **Gestión de Usuarios** - CRUD básico y perfil
4. **Seguridad** - Contraseñas hasheadas, tokens firmados
5. **Validaciones** - Entrada de datos y permisos

### 🔒 **Seguridad Implementada**:
- Contraseñas hasheadas con BCrypt
- Tokens JWT con expiración (24h)
- Control de acceso por roles
- Rutas protegidas vs públicas
- Validación de entrada

### 🧪 **Calidad del Código**:
- 16 tests automatizados (100% passing)
- Arquitectura limpia (Controller → Service → Repository)
- Manejo centralizado de errores
- Documentación completa

---

## 🛠️ Uso de Postman

### Importar Colección:
1. Abrir Postman
2. Import → File → Seleccionar `postman/El-Pollo-Empoderado-API.postman_collection.json`
3. La colección incluye 10 requests ordenados para la demo

### Variables de Entorno:
- `base_url`: http://localhost:8080
- `user_token`: Se guarda automáticamente al hacer login
- `admin_token`: Se guarda automáticamente al hacer login admin

### Orden de Ejecución:
1. Health Check
2. Register New User
3. Login User (guarda token)
4. Login Admin (guarda token)
5. Get My Profile
6. Get All Users (Admin)
7. Casos de error (sin token, token incorrecto)
8. Update Profile
9. Change Password

---

## 🚨 Troubleshooting

### Problema: "Connection refused"
**Solución**: Verificar que MySQL esté corriendo y la app iniciada

### Problema: "User not found" en login admin
**Solución**: El usuario admin se crea automáticamente al iniciar la app

### Problema: Token inválido
**Solución**: Hacer login nuevamente, los tokens expiran en 24h

### Problema: 403 en lugar de 401
**Solución**: Es comportamiento normal del sistema de autorización

---

## 📈 Métricas de Éxito

- ✅ Todos los endpoints responden correctamente
- ✅ Seguridad funciona (401/403 cuando corresponde)
- ✅ Tokens JWT válidos y funcionales
- ✅ CRUD de usuarios operativo
- ✅ Tests pasando (16/16)
- ✅ Documentación completa

**Estado**: ✅ **LISTO PARA DEMO 16/10**