# 🔧 Configuración de Variables de Entorno

Este proyecto utiliza variables de entorno para toda su configuración, permitiendo un despliegue sencillo en plataformas como Railway, Heroku, o cualquier servidor.

## 📋 Configuración Local

1. **Copia el archivo de ejemplo:**
   ```bash
   cp .env.example .env
   ```

2. **Edita el archivo `.env`** con tus valores locales

3. **Carga las variables** (opcional, si usas algún gestor de .env en Spring Boot)

## 🗂️ Variables de Entorno Disponibles

### Base de Datos
| Variable | Descripción | Ejemplo Local | Ejemplo Producción |
|----------|-------------|---------------|-------------------|
| `DB_HOST` | Host del servidor MySQL | `localhost` | `containers-us-west-xxx.railway.app` |
| `DB_PORT` | Puerto de MySQL | `3306` | `3306` |
| `DB_NAME` | Nombre de la base de datos | `pollo_empoderado_db` | `railway` |
| `DB_USERNAME` | Usuario de la base de datos | `root` | `root` |
| `DB_PASSWORD` | Contraseña de la base de datos | _(vacío)_ | `password123` |
| `DB_TIMEZONE` | Zona horaria | `America/Lima` | `UTC` |
| `DB_USE_SSL` | Usar SSL para conexión | `false` | `true` |

### JPA/Hibernate
| Variable | Descripción | Valores | Default |
|----------|-------------|---------|---------|
| `JPA_DDL_AUTO` | Estrategia de creación de tablas | `none`, `validate`, `update`, `create`, `create-drop` | `update` |
| `JPA_SHOW_SQL` | Mostrar SQL en logs | `true`, `false` | `true` |
| `JPA_FORMAT_SQL` | Formatear SQL en logs | `true`, `false` | `true` |

### Spring
| Variable | Descripción | Valores | Default |
|----------|-------------|---------|---------|
| `SPRING_PROFILE` | Perfil activo de Spring | `local`, `dev`, `prod`, `test` | `local` |

### JWT (Autenticación)
| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `JWT_SECRET` | Clave secreta para firmar tokens | `mySecretKeyForPolloEmpoderadoBackend2024` |
| `JWT_EXPIRATION` | Tiempo de expiración en ms | `86400000` (24 horas) |

### CORS (Cross-Origin)
| Variable | Descripción | Ejemplo Local | Ejemplo Producción |
|----------|-------------|---------------|-------------------|
| `CORS_ALLOWED_ORIGINS` | Orígenes permitidos (separados por comas) | `http://localhost:4200,http://localhost:3000` | `https://tu-frontend.vercel.app,https://tu-dominio.com` |
| `CORS_ALLOWED_METHODS` | Métodos HTTP permitidos | `GET,POST,PUT,DELETE,OPTIONS` | Igual |
| `CORS_ALLOWED_HEADERS` | Headers permitidos | `*` | `*` |
| `CORS_ALLOW_CREDENTIALS` | Permitir credenciales | `true` | `true` |

## 🚀 Configuración en Railway

### Opción 1: Variables Individuales (Recomendado)
En el dashboard de Railway, añade las siguientes variables:

```env
DB_HOST=${{MYSQLHOST}}
DB_PORT=${{MYSQLPORT}}
DB_NAME=${{MYSQLDATABASE}}
DB_USERNAME=${{MYSQLUSER}}
DB_PASSWORD=${{MYSQLPASSWORD}}
DB_TIMEZONE=UTC
DB_USE_SSL=true
JPA_DDL_AUTO=validate
JPA_SHOW_SQL=false
SPRING_PROFILE=prod
JWT_SECRET=tu-clave-secreta-muy-segura-y-larga
CORS_ALLOWED_ORIGINS=https://tu-frontend.vercel.app
```

### Opción 2: URL Completa (Legacy)
Si prefieres usar una URL completa:

```env
DB_URL=jdbc:mysql://${{MYSQLHOST}}:${{MYSQLPORT}}/${{MYSQLDATABASE}}?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=${{MYSQLUSER}}
DB_PASSWORD=${{MYSQLPASSWORD}}
```

> **Nota:** Railway automáticamente proporciona las variables `MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, `MYSQLPASSWORD` cuando añades un servicio MySQL.

## 🔒 Seguridad

### ⚠️ IMPORTANTE:
- **NUNCA** subas el archivo `.env` a Git
- **CAMBIA** `JWT_SECRET` en producción por una clave única y segura
- **USA** `JPA_DDL_AUTO=validate` en producción (no `update`)
- **HABILITA** `DB_USE_SSL=true` en producción
- **DESACTIVA** `JPA_SHOW_SQL=false` en producción

## 📝 Ejemplo de `.env` para Desarrollo Local

```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=pollo_empoderado_db
DB_USERNAME=root
DB_PASSWORD=
DB_TIMEZONE=America/Lima
DB_USE_SSL=false

JPA_DDL_AUTO=update
JPA_SHOW_SQL=true
JPA_FORMAT_SQL=true

SPRING_PROFILE=local

JWT_SECRET=mySecretKeyForPolloEmpoderadoBackend2024
JWT_EXPIRATION=86400000

CORS_ALLOWED_ORIGINS=http://localhost:4200
```

## 📝 Ejemplo de Variables para Producción (Railway)

```env
DB_HOST=containers-us-west-xxx.railway.app
DB_PORT=3306
DB_NAME=railway
DB_USERNAME=root
DB_PASSWORD=tu-password-seguro-generado-por-railway
DB_TIMEZONE=UTC
DB_USE_SSL=true

JPA_DDL_AUTO=validate
JPA_SHOW_SQL=false
JPA_FORMAT_SQL=false

SPRING_PROFILE=prod

JWT_SECRET=clave-secreta-larga-y-compleja-para-produccion-123456
JWT_EXPIRATION=86400000

CORS_ALLOWED_ORIGINS=https://tu-frontend.vercel.app,https://tu-dominio.com
```

## 🧪 Perfiles de Spring Boot

El proyecto soporta múltiples perfiles:

- **`local`**: Desarrollo en tu máquina (sin variables de entorno requeridas)
- **`dev`**: Desarrollo en servidor compartido
- **`prod`**: Producción (Railway, Heroku, etc.)
- **`test`**: Tests automatizados (usa H2 en memoria)

Activa un perfil específico:
```bash
# Con variable de entorno
export SPRING_PROFILE=prod

# O con argumento al ejecutar
java -jar target/backend.jar --spring.profiles.active=prod
```

## ❓ Preguntas Frecuentes

### ¿Cómo construyo la URL de la base de datos?
La URL se construye automáticamente con el formato:
```
jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useSSL=${DB_USE_SSL}&allowPublicKeyRetrieval=true&serverTimezone=${DB_TIMEZONE}
```

### ¿Puedo usar múltiples orígenes en CORS?
Sí, sepáralos con comas:
```env
CORS_ALLOWED_ORIGINS=http://localhost:4200,https://frontend1.com,https://frontend2.com
```

### ¿Qué pasa si no defino una variable?
Cada variable tiene un valor por defecto definido en los archivos `application-*.yml`. Si no defines una variable, usará el valor por defecto.
