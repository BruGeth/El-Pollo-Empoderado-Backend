# Cambios Implementados - Tabla Locals (Locales/Tiendas)

## Resumen de Cambios

Se ha creado la tabla `locals` para gestionar los locales/tiendas del restaurante "El Pollo Empoderado". Cada local está asociado a un distrito específico y contiene toda la información necesaria para que los usuarios puedan ubicar y contactar las tiendas.

---

## 📋 Nueva Entidad

### **Local.java** - Entidad Local/Tienda
- Tabla: `locals`
- Campos:
  - `id` (Long) - ID autoincremental
  - `nombre` (String, max 200) - Nombre del local
  - `distrito_id` (Long) - Relación ManyToOne con District (requerido)
  - `direccion` (String, max 255) - Dirección completa del local (requerido)
  - `telefono` (String, max 20) - Número telefónico (opcional)
  - `horario` (String, max 100) - Horario de atención (opcional)
  - `imagen_url` (String, max 500) - URL de imagen del local (opcional)
  - `maps_url` (String, max 500) - Link de Google Maps (opcional)
  - `created_at` (LocalDateTime) - Fecha de creación (auto-generado)

---

## 📦 Nuevo Repositorio

### **LocalRepository.java**
- Extiende JpaRepository<Local, Long>
- Métodos personalizados:
  - `findByDistrictId(Long districtId)` - Obtener locales por distrito
  - `findByDistrictCityId(Long cityId)` - Obtener locales por ciudad

---

## 🎯 Nuevos DTOs

### 1. **LocalRequest.java**
DTO para crear/actualizar un local:
```java
- nombre (String)
- distritoId (Long)
- direccion (String)
- telefono (String)
- horario (String)
- imagenUrl (String)
- mapsUrl (String)
```

### 2. **LocalResponse.java**
DTO para respuesta con información completa:
```java
- id (Long)
- nombre (String)
- distritoId (Long)
- distritoNombre (String)
- ciudadId (Long)
- ciudadNombre (String)
- direccion (String)
- telefono (String)
- horario (String)
- imagenUrl (String)
- mapsUrl (String)
- createdAt (LocalDateTime)
```

---

## 🔧 Nuevo Servicio

### **LocalService.java**
Servicio completo con operaciones CRUD:
- `getAllLocals()` - Obtener todos los locales
- `getLocalById(Long id)` - Obtener local por ID
- `getLocalsByDistrict(Long districtId)` - Obtener locales de un distrito
- `getLocalsByCity(Long cityId)` - Obtener locales de una ciudad
- `createLocal(LocalRequest)` - Crear nuevo local
- `updateLocal(Long id, LocalRequest)` - Actualizar local existente
- `deleteLocal(Long id)` - Eliminar local

---

## 🌐 Nuevos Endpoints (API REST)

### **LocalController.java**

#### Endpoints Públicos (GET):
```
GET /api/locals
- Obtiene todos los locales
- Acceso: Público

GET /api/locals/{id}
- Obtiene un local específico por ID
- Acceso: Público

GET /api/locals/district/{districtId}
- Obtiene locales de un distrito específico
- Acceso: Público

GET /api/locals/city/{cityId}
- Obtiene locales de una ciudad específica
- Acceso: Público
```

#### Endpoints Protegidos (requieren ROLE_ADMIN):
```
POST /api/locals
- Crear un nuevo local
- Acceso: Solo ADMIN
- Body: LocalRequest

PUT /api/locals/{id}
- Actualizar un local existente
- Acceso: Solo ADMIN
- Body: LocalRequest

DELETE /api/locals/{id}
- Eliminar un local
- Acceso: Solo ADMIN
```

---

## 🔒 Configuración de Seguridad

### **SecurityConfig.java** (MODIFICADO)
- Se agregó: `.requestMatchers(HttpMethod.GET, "/api/locals/**").permitAll()`
- Los endpoints GET son públicos, POST/PUT/DELETE requieren autenticación y rol ADMIN

---

## 🗄️ Inicialización de Datos

### **DataInitializer.java** (MODIFICADO)
Se agregó el método `initializeLocals()` que carga 3 locales de ejemplo:

1. **El Pollo Empoderado - Miraflores**
   - Distrito: Miraflores (ID: 4)
   - Dirección: Av. Larco 1234, Miraflores
   - Teléfono: 01-4567890
   - Horario: Lun-Dom: 11:00 AM - 11:00 PM

2. **El Pollo Empoderado - Surco**
   - Distrito: Surco (ID: 5)
   - Dirección: Av. Primavera 5678, Surco
   - Teléfono: 01-7654321
   - Horario: Lun-Dom: 12:00 PM - 10:00 PM

3. **El Pollo Empoderado - San Miguel**
   - Distrito: San Miguel (ID: 10)
   - Dirección: Av. La Marina 9012, San Miguel
   - Teléfono: 01-3456789
   - Horario: Lun-Sab: 11:30 AM - 10:30 PM

---

## 📝 Archivos Creados (6 nuevos)

1. [Local.java](c:\Github\El-Pollo-Empoderado-Backend\src\main\java\com\elpolloempoderado\backend\model\Local.java) - Entidad
2. [LocalRepository.java](c:\Github\El-Pollo-Empoderado-Backend\src\main\java\com\elpolloempoderado\backend\repository\LocalRepository.java) - Repositorio
3. [LocalRequest.java](c:\Github\El-Pollo-Empoderado-Backend\src\main\java\com\elpolloempoderado\backend\dto\LocalRequest.java) - DTO Request
4. [LocalResponse.java](c:\Github\El-Pollo-Empoderado-Backend\src\main\java\com\elpolloempoderado\backend\dto\LocalResponse.java) - DTO Response
5. [LocalService.java](c:\Github\El-Pollo-Empoderado-Backend\src\main\java\com\elpolloempoderado\backend\service\LocalService.java) - Servicio
6. [LocalController.java](c:\Github\El-Pollo-Empoderado-Backend\src\main\java\com\elpolloempoderado\backend\controller\LocalController.java) - Controlador

## 🔄 Archivos Modificados (2 archivos)

1. [SecurityConfig.java](c:\Github\El-Pollo-Empoderado-Backend\src\main\java\com\elpolloempoderado\backend\security\SecurityConfig.java) - Permisos públicos
2. [DataInitializer.java](c:\Github\El-Pollo-Empoderado-Backend\src\main\java\com\elpolloempoderado\backend\config\DataInitializer.java) - Datos iniciales

---

## ✅ Características Implementadas

- ✅ CRUD completo para locales
- ✅ Relación con District (distrito)
- ✅ Consultas por distrito y ciudad
- ✅ Información completa: nombre, dirección, teléfono, horario
- ✅ Links para imagen y Google Maps
- ✅ Endpoints públicos para consulta (GET)
- ✅ Endpoints protegidos para gestión (POST/PUT/DELETE - solo ADMIN)
- ✅ 3 locales de ejemplo pre-cargados
- ✅ Sin errores de compilación
- ✅ Uso de ResourceNotFoundException del proyecto
- ✅ Respuestas incluyen información del distrito y ciudad

---

## 📌 Ejemplos de Uso

### Consultar todos los locales (Público):
```http
GET http://localhost:8080/api/locals
```

### Consultar locales de una ciudad (Público):
```http
GET http://localhost:8080/api/locals/city/1
```
*(1 = Lima)*

### Consultar locales de un distrito (Público):
```http
GET http://localhost:8080/api/locals/district/4
```
*(4 = Miraflores)*

### Crear un nuevo local (Requiere ADMIN):
```http
POST http://localhost:8080/api/locals
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "El Pollo Empoderado - Callao",
  "distritoId": 13,
  "direccion": "Av. Saenz Peña 456, Callao",
  "telefono": "01-5551234",
  "horario": "Lun-Dom: 10:00 AM - 11:00 PM",
  "imagenUrl": "https://example.com/local-callao.jpg",
  "mapsUrl": "https://maps.google.com/?q=Callao+Peru"
}
```

### Actualizar un local (Requiere ADMIN):
```http
PUT http://localhost:8080/api/locals/1
Authorization: Bearer {token}
Content-Type: application/json

{
  "telefono": "01-9999999",
  "horario": "Lun-Dom: 10:00 AM - 12:00 AM"
}
```

### Eliminar un local (Requiere ADMIN):
```http
DELETE http://localhost:8080/api/locals/1
Authorization: Bearer {token}
```

---

## 🎯 Respuesta de Ejemplo

```json
{
  "id": 1,
  "nombre": "El Pollo Empoderado - Miraflores",
  "distritoId": 4,
  "distritoNombre": "Miraflores",
  "ciudadId": 1,
  "ciudadNombre": "Lima",
  "direccion": "Av. Larco 1234, Miraflores",
  "telefono": "01-4567890",
  "horario": "Lun-Dom: 11:00 AM - 11:00 PM",
  "imagenUrl": "https://example.com/local-miraflores.jpg",
  "mapsUrl": "https://maps.google.com/?q=Miraflores+Lima",
  "createdAt": "2025-12-11T10:30:00"
}
```

---

## 📋 Notas Importantes

1. **Relación Obligatoria**: Cada local DEBE tener un distrito asignado. No puede existir un local sin distrito.

2. **Campos Opcionales**: telefono, horario, imagenUrl y mapsUrl son opcionales y pueden ser null.

3. **Validación de Distrito**: Al crear o actualizar un local, se valida que el distrito exista. Si no existe, lanza `ResourceNotFoundException`.

4. **Información Completa**: Las respuestas incluyen automáticamente el nombre del distrito y la ciudad, no solo los IDs.

5. **Seguridad**: 
   - Cualquiera puede VER los locales (no requiere autenticación)
   - Solo ADMIN puede crear, actualizar o eliminar locales

6. **Inicialización**: Los 3 locales de ejemplo solo se crean si la tabla está vacía (primera vez que se ejecuta la app).

---

**Última actualización**: 11 de diciembre de 2025
