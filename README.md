# 🍗 Pollo Empoderado — Backend

Backend del sistema **Pollo Empoderado**, desarrollado con **Spring Boot** y **MySQL**.

---

## ⚙️ Configuración del entorno

### 1. Requisitos previos

Asegúrate de tener instalado:

* **Java 17+**
* **Maven 3.9+**
* **MySQL 8+**

---

### 2. Estructura de configuración

El proyecto usa archivos `YAML` con **perfiles de entorno**:

```
src/main/resources/
├── application.yml               # Configuración base
├── application-dev.yml           # Entorno de desarrollo (usa variables de entorno)
├── application-prod.yml          # Entorno de producción
└── application-local.yml         # ⚠️ Solo local (no se sube al repo)
```

---

### 3. Creación del archivo `application-local.yml`

Este archivo **no está incluido en el repositorio** (está en el `.gitignore`), por lo que debes crearlo manualmente para ejecutar el backend localmente.

📄 **Ruta:**

```
src/main/resources/application-local.yml
```

✍️ **Contenido de ejemplo:**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pollo_empoderado_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima
    username: root
    password: tu_password_local # modifica por tu contraseña real 
    driver-class-name: com.mysql.cj.jdbc.Driver

spring:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

💡 *Este archivo contiene tus credenciales locales, por lo que nunca debe subirse al repositorio.*

---

### 4. Perfiles disponibles

| Perfil    | Archivo                 | Descripción                                                                           |
| :-------- | :---------------------- | :------------------------------------------------------------------------------------ |
| **local** | `application-local.yml` | Tu configuración local (no versionada).                                               |
| **dev**   | `application-dev.yml`   | Entorno de desarrollo compartido (usa variables `${DB_USERNAME}` y `${DB_PASSWORD}`). |
| **prod**  | `application-prod.yml`  | Entorno de producción, seguro y validado.                                             |

---

### 5. Activar un perfil

Por defecto, el perfil activo es `local`.
Puedes cambiarlo al ejecutar la aplicación con Maven:

```bash
# Local (por defecto)
mvn spring-boot:run

# Desarrollo
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Producción
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

### 6. Variables de entorno (para dev/prod)

Si usas los perfiles `dev` o `prod`, define tus credenciales en el entorno del sistema:

```bash
export DB_URL=jdbc:mysql://servidor:3306/pollo_empoderado_db?useSSL=true&serverTimezone=America/Lima
export DB_USERNAME=usuario
export DB_PASSWORD=contraseña
```

> En Windows (PowerShell):
>
> ```powershell
> setx DB_URL "jdbc:mysql://servidor:3306/pollo_empoderado_db?useSSL=true&serverTimezone=America/Lima"
> setx DB_USERNAME "usuario"
> setx DB_PASSWORD "contraseña"
> ```

---

## 🧪 Ejecución y pruebas locales

### Pasos para ejecutar el proyecto

1. **Crear la base de datos** (solo la primera vez):
   ```sql
   CREATE DATABASE pollo_empoderado_db;
   ```

2. **Crear el archivo de configuración local** `src/main/resources/application-local.yml`:
   ```yaml
   spring:
     datasource:
       password: tu_password_mysql
   ```

3. **Verificar compilación y tests**:
   ```bash
   # Con Maven Wrapper (recomendado)
   mvnw.cmd clean verify    # Windows
   ./mvnw clean verify      # Linux/Mac
   ```

4. **Ejecutar la aplicación**:
   ```bash
   mvnw.cmd spring-boot:run    # Windows
   ./mvnw spring-boot:run      # Linux/Mac
   ```

5. **Verificar que funciona**:
   - Abrir: http://localhost:8080/api/index
   - Deberías ver: `{"message": "API de Pollería El Empoderado funcionando correctamente", "status": "OK"}`

### Usuario administrador por defecto

El sistema crea automáticamente un usuario administrador:
- **Email**: `admin@empoderado.com`
- **Contraseña**: `admin123`
- **Rol**: `ROLE_ADMIN`

> ⚠️ **Importante**: Cambiar estas credenciales en producción.

### Variables de entorno necesarias

| Variable | Descripción | Ejemplo |
|----------|-------------|----------|
| `DB_PASSWORD` | Contraseña MySQL (solo para dev/prod) | `mi_password` |
| `DB_USERNAME` | Usuario MySQL (opcional, default: root) | `root` |
| `DB_URL` | URL completa de BD (solo para prod) | `jdbc:mysql://...` |

---

## 🤖 Integración continua (CI)

El proyecto incluye un **workflow de GitHub Actions** que valida automáticamente la compilación y ejecución de tests cuando se abre un Pull Request o se hace push en `main` o `develop`.

📄 Archivo: `.github/workflows/ci.yml`

Ejemplo del pipeline:

```yaml
name: CI - Build & Test

on:
  push:
    branches: [ "main", "develop" ]
  pull_request:
    branches: [ "main", "develop" ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout del código
        uses: actions/checkout@v4

      - name: Configurar Java
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17

      - name: Compilar y ejecutar tests
        run: mvn clean verify
```

💡 Esto garantiza que las builds sean estables antes de fusionar cualquier cambio.

---

## 📜 Estructura del proyecto

```
src/main/java/com/elpolloempoderado/backend/
├── config/          # Configuraciones de Spring
├── controller/      # Controladores REST
├── dto/             # Data Transfer Objects
├── model/           # Entidades JPA
├── repository/      # Repositorios de datos
├── security/        # Configuración de seguridad
├── service/         # Lógica de negocio
├── util/            # Utilidades
└── BackendApplication.java

src/main/resources/
├── sql/             # Scripts de base de datos
├── application.yml  # Configuración base
├── application-dev.yml
├── application-prod.yml
└── application-local.yml  # (crear manualmente)
```

---

## 🗄️ Configuración de Base de Datos

### Inicialización de la base de datos

Antes de ejecutar la aplicación por primera vez, ejecuta el script de inicialización:

```bash
# Conectarse a MySQL
mysql -u root -p

# Ejecutar el script de inicialización
source src/main/resources/sql/init-database.sql
```

O manualmente:

```sql
CREATE DATABASE IF NOT EXISTS pollo_empoderado_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

---

## 🐛 Solución de problemas comunes

| Problema                                    | Posible causa                             | Solución                                                         |
| ------------------------------------------- | ----------------------------------------- | ---------------------------------------------------------------- |
| `Access denied for user 'root'@'localhost'` | Contraseña incorrecta o MySQL no iniciado | Verifica tus credenciales y que el servicio MySQL esté activo.   |
| `Unknown database 'pollo_empoderado_db'`    | Base de datos no creada                   | Ejecuta el script `src/main/resources/sql/init-database.sql`     |
| `Timezone issue`                            | Configuración de zona horaria incorrecta  | Usa `serverTimezone=America/Lima` en tu URL JDBC.                |

---
## 👤 Usuario admin de prueba

Al iniciar la aplicación en entorno local, se crea automáticamente el usuario admin para pruebas:

- **Email:** `admin@empoderado.com`
- **Contraseña:** `ContraseñaSegura123!`
- **Rol:** `ROLE_ADMIN`

> ⚠️ **Importante:**  
> Este usuario y contraseña son solo para desarrollo y pruebas.  
> **No uses estas credenciales en producción** y elimina el inicializador antes de desplegar.

---