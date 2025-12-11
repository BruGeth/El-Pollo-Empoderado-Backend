# 🛠️ Guía de Desarrollo - El Pollo Empoderado Backend

## 📋 Índice
1. [Estado del Proyecto](#estado-del-proyecto)
2. [Configuración del Entorno](#configuración-del-entorno)
3. [Variables de Entorno](#variables-de-entorno)
4. [Perfiles de Aplicación](#perfiles-de-aplicación)
5. [Comandos de Desarrollo](#comandos-de-desarrollo)
6. [Estructura del Proyecto](#estructura-del-proyecto)
7. [Configuración de Base de Datos](#configuración-de-base-de-datos)
8. [Testing](#testing)
9. [Troubleshooting](#troubleshooting)

---

## ✅ Estado del Proyecto

### Funcionalidades Implementadas
- ✅ **Autenticación y Usuarios** - JWT, roles (USER/ADMIN), registro, login
- ✅ **Gestión de Categorías** - CRUD completo con manejo de platos asociados
- ✅ **Gestión de Platos/Dishes** - CRUD con imágenes, disponibilidad, precio
- ✅ **Gestión de Locales** - CRUD con ubicaciones (ciudad/distrito), horarios
- ✅ **Sistema de Direcciones** - Múltiples direcciones por usuario, validación
- ✅ **Sistema de Órdenes** - Checkout, seguimiento, estados, historial
- ✅ **Sistema de Pagos** - Integración completa con MercadoPago
- ✅ **Email Service** - Confirmaciones de registro y órdenes
- ✅ **Ubicaciones** - Ciudades y distritos de Perú precargados
- ✅ **Dashboard Admin** - Estadísticas, gestión de órdenes

### Stack Tecnológico
```
Backend Framework:    Spring Boot 3.5.6
Java Version:         Java 17 (LTS)
Base de Datos:        MySQL 8.0+
Autenticación:        JWT (jjwt 0.12.3)
Pagos:                MercadoPago SDK 2.1.29
Email:                Spring Boot Mail + Thymeleaf
Testing:              JUnit 5, H2 in-memory
Build Tool:           Maven 3.9+
```

### Dependencias Principales
```xml
<!-- Spring Boot -->
<spring-boot.version>3.5.6</spring-boot.version>

<!-- Security & JWT -->
<jjwt.version>0.12.3</jjwt.version>

<!-- Database -->
<mysql.version>8.0.33</mysql.version>

<!-- Payments -->
<mercadopago.version>2.1.29</mercadopago.version>

<!-- Utils -->
<lombok.version>1.18.32</lombok.version>
```

---

## 🔧 Configuración del Entorno

### Prerrequisitos
1. **Java 17 (LTS)** - Descargar de [Adoptium](https://adoptium.net/)
2. **Maven 3.9+** - O usar wrapper incluido (mvnw.cmd)
3. **MySQL 8.0+** - Server instalado y corriendo
4. **Git** - Para control de versiones

### Setup Inicial

#### 1. Clonar el Repositorio
```bash
git clone <repository-url>
cd El-Pollo-Empoderado-Backend
```

#### 2. Crear Base de Datos MySQL
```sql
-- Conectar a MySQL
mysql -u root -p

-- Crear base de datos
CREATE DATABASE el_pollo_empoderado CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Crear usuario (opcional, para producción)
CREATE USER 'polloapp'@'localhost' IDENTIFIED BY 'tu_password_seguro';
GRANT ALL PRIVILEGES ON el_pollo_empoderado.* TO 'polloapp'@'localhost';
FLUSH PRIVILEGES;
```

#### 3. Configurar Variables de Entorno

**Para desarrollo local** (no requiere variables):
```bash
# application-local.yml tiene valores por defecto
# Solo ejecuta: mvnw.cmd spring-boot:run
```

**Para desarrollo compartido (dev/prod)**:
```bash
# Windows (PowerShell)
$env:DB_PASSWORD = "tu_password"
$env:DB_USERNAME = "root"
$env:JWT_SECRET = "tu-secreto-jwt-minimo-32-caracteres-aqui"
$env:EMAIL_USERNAME = "tu-email@gmail.com"
$env:EMAIL_PASSWORD = "tu-app-password"
$env:MERCADOPAGO_ACCESS_TOKEN = "APP_USR-..."

# Linux/Mac
export DB_PASSWORD="tu_password"
export DB_USERNAME="root"
export JWT_SECRET="tu-secreto-jwt-minimo-32-caracteres-aqui"
export EMAIL_USERNAME="tu-email@gmail.com"
export EMAIL_PASSWORD="tu-app-password"
export MERCADOPAGO_ACCESS_TOKEN="APP_USR-..."
```

#### 4. Ejecutar la Aplicación
```bash
# Con perfil local (por defecto)
mvnw.cmd spring-boot:run

# Con perfil dev
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev

# O con Maven instalado
./mvnw spring-boot:run
```

---

## 🔐 Variables de Entorno

### Variables Obligatorias (dev/prod)

| Variable | Descripción | Ejemplo | Requerido En |
|----------|-------------|---------|--------------|
| `DB_PASSWORD` | Contraseña de MySQL | `mySecurePass123` | dev, prod |
| `JWT_SECRET` | Clave secreta JWT (mínimo 32 chars) | `mi-super-secreto-jwt-2024-pollo` | dev, prod |
| `EMAIL_PASSWORD` | App password de Gmail | `abcd efgh ijkl mnop` | dev, prod |

### Variables Opcionales

| Variable | Descripción | Valor por Defecto | Requerido En |
|----------|-------------|-------------------|--------------|
| `DB_USERNAME` | Usuario de MySQL | `root` | - |
| `DB_URL` | URL completa de BD | `jdbc:mysql://localhost:3306/el_pollo_empoderado` | prod |
| `EMAIL_USERNAME` | Email del sistema | - | dev, prod |
| `MERCADOPAGO_ACCESS_TOKEN` | Token de MercadoPago | - | prod |
| `FRONTEND_URL` | URL del frontend | `http://localhost:4200` | prod |

### Configuración de Email (Gmail)

1. Habilitar autenticación de 2 factores en tu cuenta Google
2. Generar App Password:
   - Ir a [https://myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords)
   - Crear nueva app password
   - Copiar el password de 16 caracteres (sin espacios)
3. Configurar variables:
```bash
$env:EMAIL_USERNAME = "tu-email@gmail.com"
$env:EMAIL_PASSWORD = "abcdefghijklmnop"  # 16 caracteres sin espacios
```

### Configuración de MercadoPago

1. Crear cuenta en [MercadoPago Developers](https://www.mercadopago.com.ar/developers)
2. Obtener credenciales de prueba (TEST) o producción (PROD)
3. Configurar Access Token:
```bash
# Test
$env:MERCADOPAGO_ACCESS_TOKEN = "TEST-1234567890123456-123456-1234567890abcdef1234567890abcdef-123456789"

# Producción
$env:MERCADOPAGO_ACCESS_TOKEN = "APP_USR-1234567890123456-123456-1234567890abcdef1234567890abcdef-123456789"
```

---

## 📁 Perfiles de Aplicación

### Perfil `local` (Por Defecto)
**Archivo**: `application-local.yml`
```yaml
# Base de datos local con valores hardcoded
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/el_pollo_empoderado
    username: root
    password: admin  # ⚠️ Solo para desarrollo local

# No requiere variables de entorno
# Ideal para desarrollo rápido
```

**Uso**: 
```bash
mvnw.cmd spring-boot:run
# O simplemente correr desde IDE
```

### Perfil `dev`
**Archivo**: `application-dev.yml`
```yaml
# Requiere variables de entorno
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/el_pollo_empoderado
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD}  # Obligatorio

# Seguridad intermedia
# Logs detallados para debugging
```

**Uso**:
```bash
$env:DB_PASSWORD = "tu_password"
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

### Perfil `prod`
**Archivo**: `application-prod.yml`
```yaml
# Todas las credenciales por variable de entorno
spring:
  datasource:
    url: ${DB_URL}  # URL completa incluye host, puerto, nombre BD
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

# Máxima seguridad
# Logs solo errores
# SSL habilitado
```

**Uso**:
```bash
$env:DB_URL = "jdbc:mysql://prod-server:3306/el_pollo_empoderado"
$env:DB_USERNAME = "polloapp"
$env:DB_PASSWORD = "SuperSecurePass123!"
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=prod
```

### Perfil `test`
**Archivo**: `application-test.yml`
```yaml
# Base de datos H2 en memoria
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb

# Usada automáticamente por @SpringBootTest
# No requiere MySQL instalado
```

---

## 🚀 Comandos de Desarrollo

### Compilación y Build

```bash
# Limpiar y compilar
mvnw.cmd clean compile

# Compilar sin tests
mvnw.cmd clean install -DskipTests

# Compilar con tests
mvnw.cmd clean verify

# Generar JAR ejecutable
mvnw.cmd clean package
```

### Ejecución

```bash
# Ejecutar con perfil por defecto (local)
mvnw.cmd spring-boot:run

# Ejecutar con perfil específico
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev

# Ejecutar JAR compilado
java -jar target/backend-0.0.1-SNAPSHOT.jar

# Ejecutar con perfil específico (JAR)
java -jar -Dspring.profiles.active=dev target/backend-0.0.1-SNAPSHOT.jar
```

### Testing

```bash
# Ejecutar todos los tests
mvnw.cmd test

# Ejecutar test específico
mvnw.cmd test -Dtest=UserServiceTest

# Tests con cobertura
mvnw.cmd clean verify

# Ver reporte de cobertura
# Abrir: target/site/jacoco/index.html
```

### Debugging

```bash
# Ejecutar en modo debug (puerto 5005)
mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# En IDE (IntelliJ/Eclipse)
# - Click derecho en BackendApplication.java
# - Run 'BackendApplication' con Debug
```

### Limpieza

```bash
# Limpiar target/
mvnw.cmd clean

# Limpiar dependencias y recompilar
mvnw.cmd clean install -U

# Limpiar base de datos (drop + create)
# Ver: src/main/resources/sql/init-database.sql
```

---

## 📦 Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/elpolloempoderado/backend/
│   │   ├── BackendApplication.java          # Main class
│   │   ├── config/
│   │   │   ├── CorsConfig.java              # CORS configuration
│   │   │   ├── DataInitializer.java         # Data seed (locations, admin)
│   │   │   ├── GlobalExceptionHandler.java  # Global error handling
│   │   │   └── SecurityConfig.java          # Spring Security config
│   │   ├── controller/
│   │   │   ├── AddressController.java       # /api/addresses/**
│   │   │   ├── AuthController.java          # /api/auth/**
│   │   │   ├── CategoryController.java      # /api/categories/**
│   │   │   ├── DishController.java          # /api/dishes/**
│   │   │   ├── HealthController.java        # /health
│   │   │   ├── LocalController.java         # /api/locals/**
│   │   │   ├── LocationController.java      # /api/locations/**
│   │   │   ├── MenuController.java          # /api/menu/**
│   │   │   ├── OrderAdminController.java    # /api/admin/orders/**
│   │   │   ├── OrderController.java         # /api/orders/**
│   │   │   ├── PaymentController.java       # /api/payments/**
│   │   │   └── UserController.java          # /api/users/**
│   │   ├── dto/                             # Request/Response DTOs
│   │   ├── exception/                       # Custom exceptions
│   │   ├── model/                           # JPA entities
│   │   │   ├── Address.java
│   │   │   ├── Category.java
│   │   │   ├── City.java
│   │   │   ├── Dish.java
│   │   │   ├── District.java
│   │   │   ├── Local.java
│   │   │   ├── Order.java
│   │   │   ├── OrderItem.java
│   │   │   ├── Payment.java
│   │   │   └── User.java
│   │   ├── repository/                      # Spring Data JPA repositories
│   │   ├── security/
│   │   │   ├── JwtAuthenticationFilter.java # JWT filter
│   │   │   └── UserDetailsServiceImpl.java  # User loading
│   │   ├── service/                         # Business logic interfaces
│   │   │   └── impl/                        # Service implementations
│   │   └── util/
│   │       ├── JwtUtil.java                 # JWT generation/validation
│   │       └── ValidationUtil.java          # Input validation
│   └── resources/
│       ├── application.yml                  # Main config
│       ├── application-local.yml            # Local profile
│       ├── application-dev.yml              # Dev profile
│       ├── application-prod.yml             # Prod profile
│       ├── application-test.yml             # Test profile
│       ├── sql/
│       │   └── init-database.sql            # DB initialization
│       └── templates/
│           ├── order-confirmation.html      # Email template
│           └── registration-confirmation.html
└── test/
    └── java/com/elpolloempoderado/backend/
        ├── BackendApplicationTests.java     # Context load test
        ├── DatabaseConnectionTest.java      # DB connectivity test
        ├── controller/                      # Controller integration tests
        ├── repository/                      # Repository tests
        ├── service/                         # Service unit tests
        └── util/                            # Utility tests
```

---

## 💾 Configuración de Base de Datos

### Esquema Principal

La aplicación crea automáticamente las siguientes tablas:

```
users              - Usuarios del sistema (USER/ADMIN)
├── addresses      - Direcciones de entrega (1:N)
└── orders         - Órdenes realizadas (1:N)
    ├── order_items - Items de la orden (1:N)
    └── payments    - Pagos de la orden (1:1)

categories         - Categorías de platos
└── dishes         - Platos del menú (N:1)

locals             - Locales/restaurantes
├── city           - Ciudad del local (N:1)
└── district       - Distrito del local (N:1)

cities             - Ciudades de Perú
└── districts      - Distritos por ciudad (1:N)
```

### Inicialización de Datos

**DataInitializer.java** ejecuta al inicio:

1. **Ciudades y Distritos**: Precarga 25 distritos de Lima
2. **Usuario Admin**: 
   - Email: `admin@elpollo.com`
   - Password: `admin123`
   - Rol: `ADMIN`
3. **Categorías de ejemplo**: Si la BD está vacía

### Propiedades JPA

```yaml
# application.yml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # Crea/actualiza tablas automáticamente
    show-sql: false     # true para ver queries SQL
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQLDialect
```

**Opciones de ddl-auto**:
- `none`: No hace nada
- `validate`: Solo valida esquema
- `update`: Crea/actualiza tablas (recomendado para dev)
- `create`: Borra y crea tablas (⚠️ pérdida de datos)
- `create-drop`: Borra al cerrar app

### Conexión a Base de Datos

**Verificar conexión**:
```bash
# Test de conexión
mvnw.cmd test -Dtest=DatabaseConnectionTest

# O ejecutar aplicación y verificar logs
mvnw.cmd spring-boot:run

# Buscar en logs:
# "HikariPool-1 - Start completed" ✅
# "Failed to obtain JDBC Connection" ❌
```

**Problemas comunes**:
```
Error: Communications link failure
→ Verificar que MySQL está corriendo
→ netstat -ano | findstr :3306

Error: Access denied for user
→ Verificar DB_USERNAME y DB_PASSWORD
→ Revisar permisos en MySQL

Error: Unknown database
→ Crear base de datos: CREATE DATABASE el_pollo_empoderado;
```

---

## 🧪 Testing

### Estructura de Tests

```
src/test/java/
├── controller/          # Integration tests
│   ├── AuthControllerTest.java
│   ├── CategoryControllerTest.java
│   └── OrderControllerTest.java
├── service/            # Unit tests
│   └── impl/
│       ├── UserServiceImplTest.java
│       └── OrderServiceImplTest.java
├── repository/         # Repository tests
│   ├── UserRepositoryTest.java
│   └── OrderRepositoryTest.java
└── util/              # Utility tests
    └── JwtUtilTest.java
```

### Ejecutar Tests

```bash
# Todos los tests
mvnw.cmd test

# Test específico
mvnw.cmd test -Dtest=UserServiceTest

# Tests de un paquete
mvnw.cmd test -Dtest="com.elpolloempoderado.backend.service.*"

# Tests con cobertura
mvnw.cmd clean verify

# Ver reporte: target/site/jacoco/index.html
```

### Configuración de Tests

Los tests usan **H2 in-memory** (no requiere MySQL):

```yaml
# application-test.yml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop  # Nueva BD para cada test
```

### Escribir Tests

**Test de Service**:
```java
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    @Test
    void testFindByEmail() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail(anyString()))
            .thenReturn(Optional.of(user));
        
        // When
        Optional<User> result = userService.findByEmail("test@example.com");
        
        // Then
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }
}
```

**Test de Controller**:
```java
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testCreateOrder() throws Exception {
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"items\":[{\"dishId\":1,\"quantity\":2}]}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }
}
```

---

## 🔧 Troubleshooting

### Problemas Comunes

#### 1. Error: "Failed to configure a DataSource"
```
***************************
APPLICATION FAILED TO START
***************************

Description:
Failed to configure a DataSource: 'url' attribute is not specified
```

**Solución**:
```bash
# Verificar que MySQL está corriendo
net start MySQL80

# Configurar variables de entorno
$env:DB_PASSWORD = "tu_password"

# O usar perfil local
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

#### 2. Error: "Port 8080 already in use"
```
***************************
APPLICATION FAILED TO START
***************************

Web server failed to start. Port 8080 was already in use.
```

**Solución**:
```bash
# Opción 1: Matar proceso en puerto 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Opción 2: Cambiar puerto en application.yml
server:
  port: 8081
```

#### 3. Error: "JWT secret key is too short"
```
InvalidKeyException: The specified key byte array is 128 bits which is not secure enough
```

**Solución**:
```bash
# JWT_SECRET debe tener mínimo 32 caracteres
$env:JWT_SECRET = "mi-super-secreto-jwt-minimo-32-caracteres-aqui"
```

#### 4. Error: "Access denied for user 'root'@'localhost'"
```
java.sql.SQLException: Access denied for user 'root'@'localhost' (using password: YES)
```

**Solución**:
```bash
# Verificar password en MySQL
mysql -u root -p

# Resetear password si es necesario
ALTER USER 'root'@'localhost' IDENTIFIED BY 'nuevo_password';
FLUSH PRIVILEGES;

# Configurar variable
$env:DB_PASSWORD = "nuevo_password"
```

#### 5. Tests fallan con "Table not found"
```
org.h2.jdbc.JdbcSQLSyntaxErrorException: Table "USERS" not found
```

**Solución**:
```yaml
# Verificar application-test.yml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop  # Debe estar en create-drop
```

### Logs y Debugging

**Habilitar logs detallados**:
```yaml
# application-dev.yml
logging:
  level:
    root: INFO
    com.elpolloempoderado.backend: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

**Ver queries SQL**:
```yaml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

**Debugging en IntelliJ**:
1. Poner breakpoint en código
2. Click derecho en `BackendApplication.java`
3. Seleccionar "Debug 'BackendApplication'"
4. Ejecutar request que active el breakpoint

---

## 📞 Soporte

### Recursos
- **Documentación API**: Ver [README.md](README.md) para todos los endpoints
- **Integración Frontend**: Ver [API-INTEGRATION.md](API-INTEGRATION.md)
- **Postman Collection**: Ver [postman/README.md](postman/README.md)

### Contacto
- **Issues**: Reportar bugs en GitHub Issues
- **Equipo**: Contactar al lead developer

---

**Última actualización**: Enero 2025  
**Versión**: 1.0.0
