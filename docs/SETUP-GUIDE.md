# 🚀 Guía Rápida de Implementación
## Sistema de Carrito y Pedidos - El Pollo Empoderado

## ✅ Lo que se ha implementado

### 📦 Modelos y Base de Datos
- ✅ **Address** - Gestión de múltiples direcciones por usuario
- ✅ **Order** - Pedidos principales con estados y pagos
- ✅ **OrderItem** - Items individuales de cada pedido
- ✅ **Enums**: OrderStatus, PaymentStatus, PaymentMethod

### 🔌 API REST Completa
- ✅ **AddressController** - 7 endpoints para direcciones
- ✅ **OrderController** - 10 endpoints para pedidos
- ✅ Autenticación JWT con roles (USER/ADMIN)
- ✅ Validaciones completas con Bean Validation

### 💼 Servicios de Negocio
- ✅ **AddressService** - CRUD completo de direcciones
- ✅ **OrderService** - Creación, consulta y gestión de pedidos
- ✅ **EmailService** - Envío asíncrono de boletas y notificaciones

### 📧 Sistema de Emails
- ✅ Templates HTML con Thymeleaf
- ✅ Envío asíncrono (no bloquea el API)
- ✅ Plantillas para confirmación y actualizaciones

---

## 🛠️ Pasos para Poner en Funcionamiento

### 1️⃣ Actualizar Base de Datos

Ejecuta el script SQL para crear las nuevas tablas:

```bash
# Archivo: src/main/resources/sql/create-orders-tables.sql
```

O si usas `ddl-auto=update` en JPA, las tablas se crearán automáticamente al iniciar la aplicación.

### 2️⃣ Configurar Email (Gmail)

#### Opción A: Usando Gmail

1. Ir a tu cuenta de Google → Seguridad
2. Activar "Verificación en 2 pasos"
3. Generar una "Contraseña de aplicación"
4. Configurar en `application-dev.yml` o variables de entorno:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: tu-email@gmail.com
    password: xxxx-xxxx-xxxx-xxxx  # App Password
```

O con variables de entorno:
```bash
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=xxxx-xxxx-xxxx-xxxx
```

#### Opción B: Desarrollo Local (MailHog)

Para testing sin enviar emails reales:

```bash
# Instalar MailHog
docker run -d -p 1025:1025 -p 8025:8025 mailhog/mailhog

# Configurar en application-local.yml
spring:
  mail:
    host: localhost
    port: 1025
```

Luego ver los emails en: http://localhost:8025

### 3️⃣ Compilar el Proyecto

```bash
# Limpiar y compilar
./mvnw clean install

# O en Windows
mvnw.cmd clean install
```

### 4️⃣ Ejecutar la Aplicación

```bash
# Modo development
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# O ejecutar directamente
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### 5️⃣ Probar los Endpoints

Importa la colección de Postman:
```
postman/Cart-Orders.postman_collection.json
```

O prueba manualmente:

#### A. Crear una dirección:
```bash
POST http://localhost:8080/api/addresses
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "cityId": 1,
  "districtId": 1,
  "street": "Av. Larco 1234",
  "number": "Dpto 501",
  "reference": "Frente al parque Kennedy",
  "phone": "987654321",
  "label": "Casa",
  "isDefault": true
}
```

#### B. Crear un pedido (Checkout):
```bash
POST http://localhost:8080/api/orders/checkout
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "addressId": 1,
  "items": [
    {"dishId": 1, "quantity": 1},
    {"dishId": 10, "quantity": 2}
  ],
  "paymentMethod": "MERCADO_PAGO",
  "notes": "Sin cebolla por favor"
}
```

#### C. Ver mis pedidos:
```bash
GET http://localhost:8080/api/orders
Authorization: Bearer YOUR_JWT_TOKEN
```

---

## 📊 Estructura de la Base de Datos

### Tabla: addresses
```sql
- id (PK)
- user_id (FK -> users)
- city_id (FK -> cities)
- district_id (FK -> districts)
- street, number, reference
- phone, label
- is_default
- created_at, updated_at
```

### Tabla: orders
```sql
- id (PK)
- order_number (UNIQUE)
- user_id (FK -> users)
- address_id (FK -> addresses)
- subtotal, delivery_fee, total
- status (PENDING, CONFIRMED, etc.)
- payment_method, payment_status
- payment_transaction_id
- notes, receipt_email
- estimated_delivery_time
- created_at, updated_at
```

### Tabla: order_items
```sql
- id (PK)
- order_id (FK -> orders)
- dish_id (FK -> dish)
- quantity
- unit_price
- subtotal
```

---

## 🔐 Seguridad y Permisos

### Endpoints Públicos (Sin autenticación):
- Ninguno en este módulo

### Endpoints USER/ADMIN:
- ✅ Todas las operaciones de direcciones (`/api/addresses/**`)
- ✅ Todas las operaciones de pedidos de usuario (`/api/orders/**`)

### Endpoints Solo ADMIN:
- ✅ Actualizar estado de pedido
- ✅ Actualizar estado de pago

---

## 🎯 Flujo de Usuario Típico

1. **Cliente navega el menú** (endpoints públicos existentes)
2. **Cliente agrega platos al carrito** (gestión en frontend)
3. **Cliente inicia sesión** (POST `/api/auth/login`)
4. **Cliente gestiona direcciones**:
   - Ver direcciones: GET `/api/addresses`
   - Crear nueva: POST `/api/addresses`
   - Establecer default: PATCH `/api/addresses/{id}/set-default`
5. **Cliente hace checkout**:
   - POST `/api/orders/checkout`
   - Sistema crea pedido con estado PENDING
   - Sistema envía email de confirmación
6. **Cliente recibe email con boleta**
7. **Cliente ve sus pedidos**:
   - GET `/api/orders`
   - GET `/api/orders/{id}`
8. **Admin actualiza estados** (desde panel admin):
   - PATCH `/api/orders/{id}/status`

---

## 📧 Estados de Email

### Se envía email cuando:
- ✅ Se crea un pedido nuevo (confirmación/boleta)
- ✅ Se actualiza el estado del pedido (opcional - implementado)

### NO se envía email:
- ❌ Al cancelar un pedido (puedes agregarlo si lo necesitas)

---

## 🧪 Testing

### Endpoints de Prueba Rápida:

```bash
# 1. Login
POST /api/auth/login
{
  "email": "user@test.com",
  "password": "password"
}

# 2. Obtener ciudades (para crear dirección)
GET /api/locations/cities

# 3. Obtener distritos de una ciudad
GET /api/locations/cities/1/districts

# 4. Crear dirección
POST /api/addresses
{
  "cityId": 1,
  "districtId": 1,
  "street": "Av. Test 123",
  "phone": "999888777",
  "isDefault": true
}

# 5. Ver platos disponibles
GET /api/dishes

# 6. Crear pedido
POST /api/orders/checkout
{
  "addressId": 1,
  "items": [{"dishId": 1, "quantity": 1}],
  "paymentMethod": "MERCADO_PAGO"
}
```

---

## 🐛 Solución de Problemas

### Error: "Usuario no autenticado"
- ✅ Verifica que el token JWT esté en el header: `Authorization: Bearer TOKEN`
- ✅ Verifica que el token no haya expirado (24 horas por defecto)

### Error: "Ciudad no encontrada" / "Distrito no encontrado"
- ✅ Asegúrate de tener datos en las tablas `cities` y `districts`
- ✅ Usa los endpoints de locations para obtener IDs válidos

### Error al enviar emails:
- ✅ Verifica las credenciales de Gmail
- ✅ Asegúrate de usar una App Password, no tu contraseña normal
- ✅ Para desarrollo, usa MailHog (ver Opción B arriba)

### Error: "Dirección no encontrada"
- ✅ Crea al menos una dirección antes de hacer checkout
- ✅ Usa el endpoint GET `/api/addresses` para ver tus direcciones

---

## 📚 Documentación Completa

Ver archivo completo de documentación:
```
CART-AND-ORDERS-SYSTEM.md
```

---

## 🎉 ¡Listo!

El sistema está completamente funcional. Ahora puedes:

1. ✅ Gestionar múltiples direcciones por usuario
2. ✅ Crear pedidos con múltiples items
3. ✅ Ver historial de pedidos
4. ✅ Cancelar pedidos (si están en estado válido)
5. ✅ Recibir emails de confirmación
6. ✅ Administrar estados de pedidos (admin)

**Frontend puede empezar a implementar:**
- Side modal del carrito
- Página de checkout con direcciones
- Dashboard de pedidos del usuario
- Integración con MercadoPago

---

**¿Necesitas ayuda?** Revisa:
- 📄 `CART-AND-ORDERS-SYSTEM.md` - Documentación detallada
- 📄 `postman/Cart-Orders.postman_collection.json` - Colección Postman
- 📄 `src/main/resources/sql/create-orders-tables.sql` - Script SQL
