# 🍗 El Pollo Empoderado - Backend API

Sistema de delivery completo construido con **Spring Boot 3.5.6**, **MySQL 8**, **JWT**, y **MercadoPago**.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8%2B-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 🚀 Quick Start

```bash
# 1. Crear base de datos
mysql -u root -p
CREATE DATABASE pollo_empoderado_db;

# 2. Configurar credenciales
cp .env.example .env
# Editar .env con tu DB_PASSWORD

# 3. Ejecutar
./mvnw spring-boot:run

# 4. Verificar
curl http://localhost:8080/api/health
```

**✅ Listo!** El backend está corriendo en `http://localhost:8080`

---

## 📋 Tabla de Contenidos

- [Funcionalidades](#-funcionalidades)
- [Stack Tecnológico](#️-stack-tecnológico)
- [Estructura del Proyecto](#️-estructura-del-proyecto)
- [Configuración](#️-configuración)
- [API Endpoints](#-api-endpoints)
- [Testing con Postman](#-testing-con-postman)
- [Seguridad](#-seguridad)
- [Despliegue](#-despliegue)
- [Roadmap](#️-roadmap)

---

## ✨ Funcionalidades

<details>
<summary><b>🔐 Autenticación y Usuarios</b></summary>

- ✅ Registro con validación completa
- ✅ Login con JWT (expiración 24h)
- ✅ Roles: `ROLE_USER` y `ROLE_ADMIN`
- ✅ Gestión de perfil
- ✅ Cambio de contraseña seguro
- ✅ Panel admin para gestión de usuarios
</details>

<details>
<summary><b>📍 Direcciones Múltiples</b></summary>

- ✅ Múltiples direcciones por usuario
- ✅ Dirección predeterminada automática
- ✅ Validación con ciudades/distritos de Perú
- ✅ CRUD completo de direcciones
</details>

<details>
<summary><b>🍽️ Catálogo y Menú</b></summary>

- ✅ Categorías de platos
- ✅ Platos con imágenes, precios, descripciones
- ✅ Endpoint `/api/menu` con menú completo
- ✅ Filtros por categoría
- ✅ Gestión admin (CRUD)
</details>

<details>
<summary><b>🏪 Locales</b></summary>

- ✅ Gestión de locales por ciudad/distrito
- ✅ Información de contacto y ubicación
- ✅ Filtrado por ubicación
</details>

<details>
<summary><b>🛒 Sistema de Pedidos</b></summary>

- ✅ Checkout con validación completa
- ✅ Estados: PENDING → CONFIRMED → PREPARING → ON_DELIVERY → DELIVERED
- ✅ Número de orden único (ORD-timestamp)
- ✅ Seguimiento de pedidos
- ✅ Cancelación (solo si PENDING)
- ✅ Historial completo
- ✅ Panel admin para gestión de pedidos
</details>

<details>
<summary><b>💳 Pagos con MercadoPago</b></summary>

- ✅ Integración completa con SDK 2.1.29
- ✅ Creación de preferencias de pago
- ✅ Webhook automático
- ✅ Estados: PENDING → PROCESSING → APPROVED/REJECTED/REFUNDED
- ✅ Actualización automática de órdenes
- ✅ Emails en cambio de estado
</details>

<details>
<summary><b>📧 Sistema de Emails</b></summary>

- ✅ Templates HTML con Thymeleaf
- ✅ Envío asíncrono (no bloquea API)
- ✅ Confirmación de pedido
- ✅ Notificaciones de estado
- ✅ Boletas de pago
</details>

---

## 🛠️ Stack Tecnológico

| Categoría | Tecnología | Versión |
|-----------|------------|---------|
| **Lenguaje** | Java | 17 |
| **Framework** | Spring Boot | 3.5.6 |
| **Seguridad** | Spring Security + JWT | 6.x + 0.12.3 |
| **ORM** | Spring Data JPA + Hibernate | 3.x |
| **Base de Datos** | MySQL | 8+ |
| **Email** | Spring Mail + Thymeleaf | 3.x |
| **Pagos** | MercadoPago SDK | 2.1.29 |
| **Testing** | H2 Database | 2.3.232 |
| **Build** | Maven | 3.9+ |
| **Utilidades** | Lombok | 1.18.32 |

---

## 🏗️ Estructura del Proyecto

```
📦 el-pollo-empoderado-backend
├── 📂 src/main/java/com/elpolloempoderado/backend/
│   ├── 📂 config/              # Configuraciones Spring
│   │   ├── CorsConfig.java
│   │   ├── DataInitializer.java  # Roles + Admin inicial
│   │   ├── GlobalExceptionHandler.java
│   │   └── PasswordConfig.java
│   ├── 📂 controller/          # 11 REST Controllers
│   │   ├── AuthController.java      # /api/auth/*
│   │   ├── UserController.java      # /api/users, /api/user/me
│   │   ├── AddressController.java   # /api/addresses
│   │   ├── OrderController.java     # /api/orders
│   │   ├── PaymentController.java   # /api/payments
│   │   ├── CategoryController.java
│   │   ├── DishController.java
│   │   ├── LocalController.java
│   │   ├── LocationController.java
│   │   ├── MenuController.java
│   │   └── IndexController.java
│   ├── 📂 dto/                 # Data Transfer Objects
│   ├── 📂 exception/           # Excepciones custom
│   ├── 📂 model/               # 10 Entidades JPA
│   │   ├── User.java
│   │   ├── Role.java
│   │   ├── Address.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── Category.java
│   │   ├── Dish.java
│   │   ├── Local.java
│   │   ├── Ciudad.java
│   │   └── Distrito.java
│   ├── 📂 repository/          # Repositorios JPA
│   ├── 📂 security/            # JWT + Spring Security
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── SecurityConfig.java
│   │   └── CustomUserDetailsService.java
│   ├── 📂 service/             # Lógica de negocio
│   │   ├── impl/
│   │   ├── AuthService.java
│   │   ├── UserService.java
│   │   ├── AddressService.java
│   │   ├── OrderService.java
│   │   ├── MercadoPagoService.java
│   │   ├── EmailService.java
│   │   └── ...
│   └── 📂 util/                # Utilidades (JwtUtil, etc.)
├── 📂 src/main/resources/
│   ├── application.yml              # Config base
│   ├── application-local.yml        # Dev local
│   ├── application-dev.yml          # Dev compartido
│   ├── application-prod.yml         # Producción
│   ├── application-test.yml         # Tests
│   ├── 📂 sql/
│   │   └── init-database.sql       # Script inicial
│   └── 📂 templates/               # Templates email
│       ├── order-confirmation.html
│       └── order-status-update.html
├── 📂 postman/                      # Colección Postman
│   ├── El-Pollo-Empoderado-Complete.postman_collection.json
│   └── README.md
├── 📂 docs/                         # Documentación histórica
│   ├── Sprint1-TareaA.md
│   ├── Sprint1-TareaB.md
│   └── ...
├── 📄 README.md                     # Este archivo
├── 📄 DEVELOPMENT.md                # Guía de desarrollo
├── 📄 API-INTEGRATION.md            # Frontend + MercadoPago
├── 📄 pom.xml
└── 📄 mvnw, mvnw.cmd
```

---

## ⚙️ Configuración

### 1. Variables de Entorno

Crea un archivo `.env` en la raíz con:

```env
# ===== BASE DE DATOS =====
DB_HOST=localhost
DB_PORT=3306
DB_NAME=pollo_empoderado_db
DB_USERNAME=root
DB_PASSWORD=tu_password_aqui

# ===== JWT =====
JWT_SECRET=mySecretKeyForPolloEmpoderadoBackend2024
JWT_EXPIRATION=86400000

# ===== CORS (separados por coma) =====
CORS_ALLOWED_ORIGINS=http://localhost:4200,http://localhost:3000

# ===== EMAIL (Gmail) =====
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=xxxx-xxxx-xxxx-xxxx

# ===== MERCADOPAGO =====
MERCADOPAGO_ACCESS_TOKEN=TEST-6645123649247854-121102-3162dad69521755e2a4dd7dc911d5c89-608720469
MERCADOPAGO_PUBLIC_KEY=TEST-85e01ca4-16a5-4f4b-9335-90dda2c3de08

# ===== FRONTEND URL =====
APP_BASE_URL=http://localhost:4200
```

Ver [DEVELOPMENT.md](DEVELOPMENT.md) para todas las variables disponibles.

### 2. Perfiles de Spring Boot

| Perfil | Comando | Uso |
|--------|---------|-----|
| `local` | `./mvnw spring-boot:run` | Desarrollo local (default) |
| `dev` | `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` | Desarrollo compartido |
| `prod` | `java -jar app.jar --spring.profiles.active=prod` | Producción |
| `test` | `./mvnw test` | Tests con H2 |

### 3. Comandos Útiles

```bash
# Compilar sin tests
./mvnw clean compile -DskipTests

# Compilar con tests
./mvnw clean verify

# Ejecutar
./mvnw spring-boot:run

# Package para producción
./mvnw clean package

# Solo tests
./mvnw test
```

---

## 📡 API Endpoints

### Resumen por Categoría

| Categoría | Endpoints | Público | User | Admin |
|-----------|-----------|---------|------|-------|
| 🏠 Health & Index | 2 | ✅ | ✅ | ✅ |
| 🔐 Authentication | 3 | ✅ | ✅ | ✅ |
| 👤 User Profile | 3 | ❌ | ✅ | ✅ |
| 📍 Locations | 3 | ✅ | ✅ | ✅ |
| 📍 Addresses | 7 | ❌ | ✅ | ✅ |
| 🏪 Locals | 4 | ✅ | ✅ | ✅ |
| 🍗 Categories | 2+3 | 2 públicos, 3 admin | ✅ | ✅ |
| 🍽️ Dishes | 3+3 | 3 públicos, 3 admin | ✅ | ✅ |
| 📖 Menu | 1 | ✅ | ✅ | ✅ |
| 🛒 Orders | 6+5 | 6 user, 5 admin | ❌ | ✅ |
| 💳 Payments | 4 | 2 públicos, 1 user, 1 webhook | ✅ | ✅ |
| 👥 Admin Users | 2 | ❌ | ❌ | ✅ |
| **TOTAL** | **54** | **19** | **35** | **54** |

### Endpoints Principales

<details>
<summary><b>🔐 Authentication (Público)</b></summary>

```http
POST /api/auth/register
POST /api/auth/login
```
</details>

<details>
<summary><b>👤 User Profile (Autenticado)</b></summary>

```http
GET    /api/user/me
PUT    /api/user/me
PUT    /api/user/me/password
```
</details>

<details>
<summary><b>📍 Addresses (Usuario)</b></summary>

```http
GET    /api/addresses
POST   /api/addresses
PUT    /api/addresses/{id}
DELETE /api/addresses/{id}
PATCH  /api/addresses/{id}/default
GET    /api/addresses/default
GET    /api/addresses/{id}
```
</details>

<details>
<summary><b>🛒 Orders (Usuario)</b></summary>

```http
GET    /api/orders?page=0&size=10&sort=createdAt,desc
POST   /api/orders
GET    /api/orders/{id}
GET    /api/orders/{orderNumber}
GET    /api/orders/status/{status}
PATCH  /api/orders/{id}/cancel
```
</details>

<details>
<summary><b>💳 Payments (MercadoPago)</b></summary>

```http
GET  /api/payments/public-key             # Público
GET  /api/payments/test                   # Público
POST /api/payments/create-preference      # Usuario
POST /api/payments/webhook                # Webhook MP
```
</details>

<details>
<summary><b>🔐 Admin Endpoints</b></summary>

```http
# Usuarios
GET    /api/users?page=0&size=10
GET    /api/users/{id}

# Categorías
POST   /api/categories
PUT    /api/categories/{id}
DELETE /api/categories/{id}

# Platos
POST   /api/dishes
PUT    /api/dishes/{id}
DELETE /api/dishes/{id}

# Locales
POST   /api/locals
PUT    /api/locals/{id}
DELETE /api/locals/{id}

# Pedidos
GET    /api/orders/admin/all
GET    /api/orders/admin/{id}
GET    /api/orders/admin/status/{status}
PATCH  /api/orders/admin/{id}/status?status=CONFIRMED
PATCH  /api/orders/admin/{id}/payment?status=APPROVED
```
</details>

**📚 Documentación completa**: Ver [postman/README.md](postman/README.md)

---

## 📬 Testing con Postman

### Importar Colección

```bash
# Archivo único con TODOS los endpoints
postman/El-Pollo-Empoderado-Complete.postman_collection.json
```

### Características

- ✅ **54 endpoints** organizados en 16 secciones
- ✅ **Scripts automáticos** que guardan tokens y IDs
- ✅ **Variables de entorno** auto-configuradas
- ✅ **Ejemplos completos** con payloads
- ✅ **Documentación** en cada request

### Flujo de Testing Típico

```
1. Login Admin → guarda admin_token ✅
2. Login User → guarda user_token ✅
3. Create Address → guarda address_id ✅
4. Get Full Menu → ver productos
5. Create Order → guarda order_id ✅
6. Create Payment Preference → obtener link de pago
7. [Usuario paga en MercadoPago]
8. Webhook actualiza orden automáticamente ✅
9. Get My Orders → ver pedido actualizado
```

### Credenciales de Prueba

**Usuario Normal:**
```json
{
  "email": "juan@example.com",
  "password": "password123"
}
```

**Administrador:**
```json
{
  "email": "admin@empoderado.com",
  "password": "admin123"
}
```

---

## 🔒 Seguridad

### JWT Stateless Authentication

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

- ✅ Tokens expiran en **24 horas**
- ✅ Firmados con **HS256** (JWT_SECRET)
- ✅ Claims: `sub` (email), `roles`, `exp`
- ✅ Validación en cada request protegido

### Control de Acceso

```java
// Ejemplo en código
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/api/users")
public ResponseEntity<Page<UserDTO>> getAllUsers() { ... }

@PreAuthorize("hasRole('USER')")
@GetMapping("/api/user/me")
public ResponseEntity<UserDTO> getMyProfile() { ... }
```

### Configuración de CORS

Permite requests desde Angular:
```yaml
cors:
  allowed-origins: http://localhost:4200,http://localhost:3000
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS,PATCH
  allowed-headers: "*"
  allow-credentials: true
```

---

## 🚀 Despliegue

### Railway (Recomendado)

```bash
# 1. Crear cuenta en Railway.app
# 2. Crear nuevo proyecto
# 3. Agregar MySQL plugin
# 4. Agregar servicio desde GitHub
# 5. Configurar variables de entorno en Railway UI
# 6. Deploy automático ✅
```

**Variables esenciales para Railway:**
```env
DB_HOST=containers-us-west-xxx.railway.app
DB_PORT=3306
DB_NAME=railway
DB_USERNAME=root
DB_PASSWORD=<generado-por-railway>
JWT_SECRET=<tu-secret-seguro>
CORS_ALLOWED_ORIGINS=https://tu-frontend.vercel.app
MERCADOPAGO_ACCESS_TOKEN=<produccion>
APP_BASE_URL=https://tu-frontend.vercel.app
```

### Docker (Alternativo)

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

```bash
./mvnw clean package -DskipTests
docker build -t el-pollo-backend .
docker run -p 8080:8080 --env-file .env el-pollo-backend
```

### Consideraciones de Producción

- ✅ Usar **secrets manager** (no hardcodear credenciales)
- ✅ Habilitar **HTTPS** (certificado SSL)
- ✅ Configurar **rate limiting**
- ✅ Logs centralizados (CloudWatch/Datadog)
- ✅ Monitoreo con **Spring Actuator**
- ✅ **Backup automático** de MySQL
- ✅ **CDN** para imágenes de platos

---

## 🧪 Testing

### Ejecutar Tests

```bash
# Todos los tests
./mvnw test

# Con cobertura
./mvnw clean verify

# Solo compilar (sin tests)
./mvnw clean compile -DskipTests
```

### Tests Implementados

- ✅ `DatabaseConnectionTest` - Conexión a BD
- ✅ Tests unitarios de servicios
- ✅ Tests de integración de controladores
- ✅ Tests de repositorios con H2

---

## 🛣️ Roadmap

### Próximas Funcionalidades

- [ ] Sistema de cupones y descuentos
- [ ] Calificación y reviews de platos
- [ ] Notificaciones push (WebSocket)
- [ ] Dashboard admin con estadísticas
- [ ] Refresh tokens para JWT
- [ ] Sistema de favoritos
- [ ] Reportes PDF/Excel
- [ ] Integración con Yape/Plin
- [ ] Sistema de puntos de fidelidad
- [ ] Chat en tiempo real con soporte

---

## 📚 Documentación Adicional

| Documento | Descripción |
|-----------|-------------|
| **[DEVELOPMENT.md](DEVELOPMENT.md)** | Guía completa de desarrollo y configuración |
| **[API-INTEGRATION.md](API-INTEGRATION.md)** | Integración Frontend + MercadoPago |
| **[postman/README.md](postman/README.md)** | Guía de uso de Postman |
| **[docs/](docs/)** | Documentos históricos del Sprint 1 |

---

## 👥 Equipo

- **Backend Dev A** - Arquitectura, seguridad, y configuración
- **Backend Dev B** - Modelos, servicios, y lógica de negocio
- **Frontend Dev** - Integración con Angular

---

## 🐛 Soporte y Reportar Issues

### Cómo Reportar un Bug

1. Ir a **Issues** en GitHub
2. Crear nuevo issue con:
   - 📝 Descripción clara del problema
   - 🔁 Pasos para reproducir
   - 📋 Logs de error
   - 🖥️ Entorno (local/dev/prod)
   - 📌 Versión del backend

### Logs Útiles

```bash
# Ver logs en tiempo real
tail -f logs/spring-boot-logger.log

# Buscar errores
grep ERROR logs/spring-boot-logger.log

# Logs de SQL
grep "Hibernate:" logs/spring-boot-logger.log
```

---

## 📄 Licencia

MIT License - Ver [LICENSE](LICENSE) para más detalles.

---

## 📊 Estado del Proyecto

### ✅ Sprint 1 Completado (16/10/2024)

Sistema funcional con:
- ✅ Autenticación JWT y roles
- ✅ Gestión de usuarios y perfiles
- ✅ Sistema de direcciones múltiples
- ✅ Carrito y pedidos con estados
- ✅ Integración completa con MercadoPago
- ✅ Sistema de emails automáticos
- ✅ 54 endpoints REST documentados
- ✅ Colección Postman completa

### 📈 Estadísticas

- **Controllers**: 11
- **Endpoints**: 54
- **Entidades JPA**: 10
- **Servicios**: 12
- **Tests**: 15+
- **Líneas de código**: ~8,000

---

## 🌟 Agradecimientos

Gracias a todo el equipo por hacer posible este proyecto.

---

<p align="center">
  <b>🍗 El Pollo Empoderado - Backend API</b><br>
  Última actualización: 11 de diciembre de 2025
</p>
