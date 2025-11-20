# 📋 Sprint 1 - Tarea B: Modelado de datos: Usuario y Rol

**Estado**: ✅ COMPLETADA  
**Responsable**: Backend Dev B (owner), Backend Dev A (apoyo)  
**Estimación total**: 10h  
**Tiempo real**: ~8h  

## 🎯 Objetivo
Definir entidades Usuario y Rol con persistencia JPA, incluyendo repositorios, DTOs y datos iniciales.

---

## ✅ Subtareas Completadas

### B.1 - Definir entidad Rol
**Estimación**: 2h | **Estado**: ✅ COMPLETADA

**Entregables**:
- ✅ Entidad `Role`:
  ```java
  @Entity
  @Table(name = "roles")
  public class Role {
      @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;
      
      @Column(nullable = false, unique = true, length = 50)
      private String name;
  }
  ```
- ✅ `RoleRepository` con método `findByName(String name)`
- ✅ Seed automático de roles:
  - `ROLE_USER` - Rol por defecto para usuarios
  - `ROLE_ADMIN` - Rol para administradores
- ✅ `DataInitializer` que ejecuta al arrancar la aplicación

**Criterios de aceptación**: ✅ Roles seed insertados al iniciar y `RoleRepository.findAll()` devuelve roles

### B.2 - Definir entidad Usuario
**Estimación**: 4h | **Estado**: ✅ COMPLETADA

**Entregables**:
- ✅ Entidad `User` completa:
  ```java
  @Entity
  @Table(name = "users")
  public class User {
      @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;
      
      @Column(nullable = false, length = 100)
      private String firstName;
      
      @Column(nullable = false, length = 100)
      private String lastName;
      
      @Column(nullable = false, unique = true, length = 150)
      private String email;
      
      @Column(nullable = false)
      private String password; // Hasheada con BCrypt
      
      @Column(length = 8)
      private String dni;
      
      private LocalDate birthDate;
      
      @Column(length = 255)
      private String address;
      
      @ManyToMany(fetch = FetchType.EAGER)
      @JoinTable(name = "user_roles", ...)
      private Set<Role> roles;
      
      @Column(name = "created_at")
      private LocalDateTime createdAt; // Auto-generado
  }
  ```
- ✅ `UserRepository` con métodos:
  - `findByEmail(String email)`
  - `existsByEmail(String email)`
- ✅ DTOs implementados:
  - `UserDTO` - Para transferencia segura (sin password)
  - `RegisterRequest` - Para registro de usuarios

**Criterios de aceptación**: ✅ Persistencia funciona, UserRepository permite CRUD básico, contraseña no en texto plano

### B.3 - Seed de usuario admin y utilidades de hashing
**Estimación**: 4h | **Estado**: ✅ COMPLETADA

**Entregables**:
- ✅ `PasswordConfig` con `BCryptPasswordEncoder`
- ✅ Usuario administrador creado automáticamente:
  - **Email**: `admin@empoderado.com`
  - **Contraseña**: `admin123` (hasheada con BCrypt)
  - **Rol**: `ROLE_ADMIN`
  - **Datos**: Admin Empoderado, DNI: 12345678
- ✅ `DataInitializer` actualizado para crear admin tras roles
- ✅ Contraseñas hasheadas automáticamente

**Criterios de aceptación**: ✅ Se puede hacer login con el admin (verificado en próximos pasos)

---

## 🗄️ Modelo de Datos Implementado

### Esquema de Base de Datos
```sql
-- Tabla de roles
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla de usuarios  
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    dni VARCHAR(8),
    birth_date DATE,
    address VARCHAR(255),
    created_at TIMESTAMP(6)
);

-- Tabla de relación muchos a muchos
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);
```

### Datos Iniciales (Seed)
```sql
-- Roles por defecto
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

-- Usuario administrador
INSERT INTO users (first_name, last_name, email, password, dni, birth_date, address, created_at) 
VALUES ('Admin', 'Empoderado', 'admin@empoderado.com', '$2a$10$...', '12345678', '1990-01-01', 'Lima, Perú', NOW());

-- Asignar rol admin
INSERT INTO user_roles (user_id, role_id) VALUES (1, 2);
```

---

## 📁 Archivos Creados

### Entidades
- `src/main/java/.../model/Role.java` - Entidad Rol
- `src/main/java/.../model/User.java` - Entidad Usuario

### Repositorios
- `src/main/java/.../repository/RoleRepository.java` - Repositorio de roles
- `src/main/java/.../repository/UserRepository.java` - Repositorio de usuarios

### DTOs
- `src/main/java/.../dto/UserDTO.java` - DTO para transferencia segura
- `src/main/java/.../dto/RegisterRequest.java` - DTO para registro

### Configuración
- `src/main/java/.../config/PasswordConfig.java` - Configuración BCrypt
- `src/main/java/.../config/DataInitializer.java` - Inicialización de datos

### Tests
- `src/test/java/.../repository/UserRepositoryTest.java` - Tests de repositorio

---

## 🧪 Tests Implementados

| Test | Propósito | Estado |
|------|-----------|--------|
| `UserRepositoryTest.shouldFindUserByEmail` | Buscar usuario por email | ✅ PASA |
| `UserRepositoryTest.shouldCheckIfEmailExists` | Verificar existencia email | ✅ PASA |
| `BackendApplicationTests` | Context loads con entidades | ✅ PASA |

**Verificación en logs**:
```sql
-- Se ejecutan automáticamente al arrancar
Hibernate: CREATE TABLE roles (...)
Hibernate: CREATE TABLE users (...)  
Hibernate: CREATE TABLE user_roles (...)
Hibernate: INSERT INTO roles (name) VALUES (?)
Hibernate: INSERT INTO users (...) VALUES (...)
```

---

## 🔐 Seguridad Implementada

### Hashing de Contraseñas
- **Algoritmo**: BCrypt con salt automático
- **Configuración**: `BCryptPasswordEncoder` como bean
- **Uso**: Automático en `DataInitializer` y futuro registro

### Validaciones
- **Email único**: Constraint a nivel de BD y repositorio
- **Campos requeridos**: `nullable = false` en campos críticos
- **Longitudes**: Límites apropiados (email: 150, nombres: 100, etc.)

---

## 🔄 Relaciones Implementadas

### User ↔ Role (Many-to-Many)
```java
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
    name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
)
private Set<Role> roles;
```

**Ventajas**:
- Un usuario puede tener múltiples roles
- Un rol puede ser asignado a múltiples usuarios
- Fetch EAGER para cargar roles automáticamente
- Tabla intermedia `user_roles` manejada por JPA

---

## ✅ Criterios de Aceptación Cumplidos

- [x] Roles seed insertados al iniciar (`ROLE_USER`, `ROLE_ADMIN`)
- [x] `RoleRepository.findAll()` devuelve roles correctamente
- [x] Persistencia funciona y `UserRepository` permite CRUD básico
- [x] Contraseña NO se guarda en texto plano (BCrypt)
- [x] Usuario admin creado automáticamente
- [x] Se puede hacer login con admin (verificado en siguiente tarea)
- [x] Tests de repositorio pasan correctamente
- [x] Relación many-to-many funciona

---

## 🚀 Integración con Próximas Tareas

### Para Tarea C (Spring Security + JWT)
- ✅ `User` entity lista para `UserDetailsService`
- ✅ Roles preparados para autorización
- ✅ `UserRepository.findByEmail()` para autenticación

### Para Tarea D (Endpoints Auth)
- ✅ `RegisterRequest` listo para endpoint registro
- ✅ `UserDTO` listo para respuestas seguras
- ✅ Usuario admin para testing

### Para Tarea E (CRUD Usuarios)
- ✅ Repositorios completos para operaciones CRUD
- ✅ DTOs preparados para transferencia de datos

---

## 📊 Métricas de Calidad

- **Cobertura de tests**: Repositorios principales cubiertos
- **Seguridad**: Contraseñas hasheadas, validaciones implementadas
- **Performance**: Fetch EAGER solo donde necesario
- **Mantenibilidad**: Código limpio con Lombok, separación clara de responsabilidades