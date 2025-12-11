# Cambios Implementados - City y District en Users

## Resumen de Cambios

Se han agregado las tablas `City` (ciudad) y `District` (distrito) al sistema, con relaciones **opcionales** a la tabla de usuarios. Los cambios implementados mantienen las mejores prácticas y no afectan el funcionamiento existente del proyecto.

---

## 📋 Nuevas Entidades

### 1. **City.java** - Entidad Ciudad
- Tabla: `cities`
- Campos:
  - `ciudad_id` (Long) - ID autoincremental
  - `nombre` (String) - Nombre de la ciudad

### 2. **District.java** - Entidad Distrito
- Tabla: `districts`
- Campos:
  - `distrito_id` (Long) - ID autoincremental
  - `nombre` (String) - Nombre del distrito
  - `ciudad_id` (Long) - Relación ManyToOne con City

---

## 🔄 Entidades Modificadas

### **User.java**
- Se agregaron dos nuevas relaciones **opcionales** (@ManyToOne):
  - `city` - Relación opcional con City
  - `district` - Relación opcional con District

---

## 📦 Nuevos Repositorios

### 1. **CityRepository.java**
- Extiende JpaRepository<City, Long>
- Repositorio estándar para ciudades

### 2. **DistrictRepository.java**
- Extiende JpaRepository<District, Long>
- Incluye método: `findByCityId(Long cityId)` - Para obtener distritos por ciudad

---

## 🎯 Nuevos DTOs

### 1. **CityResponse.java**
```java
- id (Long)
- nombre (String)
```

### 2. **DistrictResponse.java**
```java
- id (Long)
- nombre (String)
- ciudadId (Long)
```

### 3. DTOs Modificados:
- **RegisterRequest.java** - Agregados: `cityId`, `districtId` (opcionales)
- **UpdateUserRequest.java** - Agregados: `cityId`, `districtId` (opcionales)
- **UserDTO.java** - Agregados: `cityId`, `cityName`, `districtId`, `districtName` (opcionales)

---

## 🔧 Servicios Modificados/Nuevos

### **LocationService.java** (NUEVO)
Servicio para gestionar ciudades y distritos:
- `getAllCities()` - Obtener todas las ciudades
- `getAllDistricts()` - Obtener todos los distritos
- `getDistrictsByCity(Long cityId)` - Obtener distritos de una ciudad específica

### **AuthService.java** (MODIFICADO)
- Se inyectan `CityRepository` y `DistrictRepository`
- Método `register()` ahora asigna city y district si están presentes en el request
- Método `convertToUserDTO()` incluye información de city y district

### **UserService.java** (MODIFICADO)
- Se inyectan `CityRepository` y `DistrictRepository`
- Método `updateCurrentUser()` ahora permite actualizar city y district
- Método `convertToUserDTO()` incluye información de city y district

---

## 🌐 Nuevos Endpoints (API REST)

### **LocationController.java**
Endpoints públicos para consultar ubicaciones:

```
GET /api/locations/cities
- Obtiene todas las ciudades disponibles
- Acceso: Público

GET /api/locations/districts
- Obtiene todos los distritos
- Acceso: Público

GET /api/locations/cities/{cityId}/districts
- Obtiene distritos de una ciudad específica
- Acceso: Público
```

---

## 🔒 Configuración de Seguridad

### **SecurityConfig.java** (MODIFICADO)
- Se agregó permiso público para: `.requestMatchers(HttpMethod.GET, "/api/locations/**").permitAll()`

---

## 🗄️ Inicialización de Datos

### **DataInitializer.java** (MODIFICADO)
Se agregó el método `initializeCitiesAndDistricts()` que carga automáticamente:

#### **Ciudades** (7 ciudades):
1. Lima
2. Callao
3. Trujillo
4. Chiclayo
5. Piura
6. Ica
7. Huacho

#### **Distritos** (19 distritos):

**Lima (12 distritos):**
1. Comas
2. Ate
3. San Martín de Porres
4. Miraflores
5. Surco
6. Chorrillos
7. La Molina
8. Santa Anita
9. Independencia
10. San Miguel
11. Lurín
12. Cercado de Lima

**Callao (2 distritos):**
13. Callao (Saenz Peña)
14. Callao (Elmer Faucett)

**Otras ciudades (1 distrito c/u):**
15. Trujillo
16. Chiclayo
17. Piura
18. Ica
19. Huacho

---

## ✅ Tests Actualizados

### **AuthControllerTest.java**
- Actualizados constructores de `RegisterRequest` para incluir `cityId` y `districtId` (null en tests)

### **UserControllerTest.java**
- Actualizados constructores de `UpdateUserRequest` para incluir `cityId` y `districtId` (null en tests)

---

## 📝 Notas Importantes

1. **Campos Opcionales**: Los campos `city` y `district` son completamente opcionales en la tabla users. Los usuarios existentes no se ven afectados.

2. **Inicialización Automática**: Los datos de ciudades y distritos se cargan automáticamente al iniciar la aplicación (solo la primera vez).

3. **Sin Breaking Changes**: Todos los endpoints existentes siguen funcionando normalmente. Los nuevos campos solo se agregan si se proporcionan.

4. **Validación**: Si se proporciona un `cityId` o `districtId` inválido, se lanza una RuntimeException con mensaje descriptivo.

5. **Compatibilidad**: Los DTOs retornan `null` para cityId, cityName, districtId y districtName cuando el usuario no tiene estas relaciones.

---

## 🚀 Próximos Pasos Sugeridos

Una vez probado y verificado el funcionamiento:
1. Probar los nuevos endpoints de locations
2. Probar registro de usuario con city y district
3. Probar actualización de perfil con city y district
4. Verificar que usuarios existentes no se vean afectados

---

## 📌 Endpoints de Ejemplo

### Obtener todas las ciudades:
```http
GET http://localhost:8080/api/locations/cities
```

### Obtener distritos de Lima (ciudad_id = 1):
```http
GET http://localhost:8080/api/locations/cities/1/districts
```

### Registrar usuario con ubicación:
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
  "address": "Av. Principal 123",
  "cityId": 1,
  "districtId": 4
}
```

---

**Última actualización**: 11 de diciembre de 2025
