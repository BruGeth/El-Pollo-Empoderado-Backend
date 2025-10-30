# 🛠️ Guía de Desarrollo - Sprint 1

## Configuración inicial completada ✅

### Dependencias incluidas
- **Spring Boot 3.5.6** - Framework principal
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Persistencia de datos
- **MySQL Connector** - Driver de base de datos
- **JWT (jjwt 0.12.3)** - Tokens de autenticación
- **Lombok** - Reducción de código boilerplate
- **H2** - Base de datos en memoria para tests

### Estructura del proyecto
```
src/main/java/com/elpolloempoderado/backend/
├── config/          # Configuraciones Spring (Security, JWT, etc.)
├── controller/      # REST Controllers
├── dto/             # Data Transfer Objects
├── model/           # Entidades JPA
├── repository/      # Repositorios JPA
├── security/        # Configuración de seguridad
├── service/         # Lógica de negocio
├── util/            # Utilidades (JWT, validaciones, etc.)
└── BackendApplication.java
```

### Comandos útiles
```bash
# Compilar y ejecutar tests
mvnw.cmd clean verify

# Ejecutar aplicación
mvnw.cmd spring-boot:run

# Solo compilar
mvnw.cmd clean compile

# Solo tests
mvnw.cmd test
```

### Próximos pasos (Sprint 1)
- [x] **Tarea A**: Configuración inicial del backend ✅
- [x] **Tarea B**: Crear entidades Usuario y Rol ✅
- [x] **Tarea C**: Configurar Spring Security + JWT ✅
- [x] **Tarea D**: Implementar endpoints de autenticación ✅
- [x] **Tarea E**: CRUD de usuarios ✅
- [x] **Tarea F**: Health check y documentación Postman ✅

### Variables de entorno
| Variable | Descripción | Requerido |
|----------|-------------|-----------|
| `DB_PASSWORD` | Contraseña MySQL | Sí (dev/prod) |
| `DB_USERNAME` | Usuario MySQL | No (default: root) |
| `DB_URL` | URL completa BD | Sí (prod) |

### Perfiles disponibles
- **local** - Desarrollo local (application-local.yml)
- **dev** - Desarrollo compartido (variables de entorno)
- **prod** - Producción (variables de entorno)
- **test** - Tests automatizados (H2 en memoria)