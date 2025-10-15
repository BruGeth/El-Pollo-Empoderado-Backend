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


Si aparece un error de conexión, revisa:

* El puerto y nombre de tu base de datos.
* Que el servidor MySQL esté corriendo.
* Que el timezone sea correcto: `America/Lima`.

---
