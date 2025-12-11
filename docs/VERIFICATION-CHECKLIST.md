# ✅ Checklist de Verificación - Sistema de Carrito y Pedidos

Este checklist te ayudará a verificar que todo el sistema funciona correctamente después de las correcciones realizadas.

---

## 🔧 Pre-requisitos

- [ ] Java 17+ instalado
- [ ] MySQL 8+ corriendo
- [ ] Base de datos creada: `pollo_empoderado_db`
- [ ] Variables de entorno configuradas (o `application-local.yml` creado)
- [ ] Postman instalado (opcional, para testing)

---

## 📊 Verificación de Base de Datos

### Tablas Principales

Ejecuta estos comandos en MySQL para verificar que las tablas existen:

```sql
USE pollo_empoderado_db;

-- Verificar tabla users
DESCRIBE users;
-- Debe incluir: id, first_name, last_name, email, password, dni, birth_date, created_at

-- Verificar tabla addresses (NUEVA)
DESCRIBE addresses;
-- Debe incluir: id, user_id, ciudad_id, distrito_id, street, number, reference_home, 
--               telefono, label, is_default, created_at, updated_at

-- Verificar tabla orders (NUEVA)
DESCRIBE orders;
-- Debe incluir: id, order_number, user_id, address_id, subtotal, delivery_fee, 
--               total, status, payment_method, payment_status, notes, 
--               receipt_email, estimated_delivery_time, created_at, updated_at

-- Verificar tabla order_items (NUEVA)
DESCRIBE order_items;
-- Debe incluir: id, order_id, dish_id, quantity, unit_price, subtotal, 
--               created_at, updated_at
```

### Foreign Keys

```sql
-- Verificar relaciones de addresses
SELECT 
    CONSTRAINT_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_NAME = 'addresses' 
  AND CONSTRAINT_SCHEMA = 'pollo_empoderado_db'
  AND REFERENCED_TABLE_NAME IS NOT NULL;

-- Debe mostrar:
-- ✅ user_id → users(id)
-- ✅ ciudad_id → cities(ciudad_id)
-- ✅ distrito_id → districts(distrito_id)

-- Verificar relaciones de orders
SELECT 
    CONSTRAINT_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_NAME = 'orders' 
  AND CONSTRAINT_SCHEMA = 'pollo_empoderado_db'
  AND REFERENCED_TABLE_NAME IS NOT NULL;

-- Debe mostrar:
-- ✅ user_id → users(id)
-- ✅ address_id → addresses(id)

-- Verificar relaciones de order_items
SELECT 
    CONSTRAINT_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_NAME = 'order_items' 
  AND CONSTRAINT_SCHEMA = 'pollo_empoderado_db'
  AND REFERENCED_TABLE_NAME IS NOT NULL;

-- Debe mostrar:
-- ✅ order_id → orders(id)
-- ✅ dish_id → dish(id)
```

**Resultados esperados:**
- [ ] Tabla `addresses` existe con todas las columnas
- [ ] Tabla `orders` existe con todas las columnas
- [ ] Tabla `order_items` existe con todas las columnas
- [ ] Foreign keys de `addresses` están configuradas
- [ ] Foreign keys de `orders` están configuradas
- [ ] Foreign keys de `order_items` están configuradas

---

## 🔨 Compilación del Proyecto

```bash
# Navegar a la raíz del proyecto
cd C:\Github\El-Pollo-Empoderado-Backend

# Limpiar y compilar
.\mvnw.cmd clean compile -DskipTests
```

**Verificar:**
- [ ] Compilación exitosa (BUILD SUCCESS)
- [ ] Sin errores de compilación
- [ ] Solo advertencia: "deprecated API" (esperado)
- [ ] Total de archivos compilados: ~86

---

## 🚀 Inicio de la Aplicación

```bash
# Iniciar con perfil dev
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev

# O con perfil local
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

**Verificar en los logs:**
- [ ] Aplicación inicia correctamente
- [ ] Puerto: 8080 (o el configurado)
- [ ] Sin errores de conexión a BD
- [ ] JPA crea/actualiza las tablas automáticamente
- [ ] Mensaje: "Started BackendApplication in X seconds"

---

## 🧪 Testing con Postman

### 1. Autenticación

**Request:**
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "admin@elpolloempoderado.com",
  "password": "admin123"
}
```

**Verificar:**
- [ ] Status: 200 OK
- [ ] Respuesta incluye `token`
- [ ] Respuesta incluye objeto `user`
- [ ] Copiar token para siguientes requests

---

### 2. Listar Direcciones (Debe estar vacío inicialmente)

**Request:**
```
GET http://localhost:8080/api/addresses
Authorization: Bearer {TOKEN}
```

**Verificar:**
- [ ] Status: 200 OK
- [ ] Respuesta: `[]` (array vacío si es primera vez)

---

### 3. Obtener Ciudades

**Request:**
```
GET http://localhost:8080/api/locations/cities
```

**Verificar:**
- [ ] Status: 200 OK
- [ ] Respuesta incluye lista de ciudades
- [ ] Cada ciudad tiene: `id`, `nombre`
- [ ] Copiar `id` de una ciudad para siguiente paso

---

### 4. Obtener Distritos por Ciudad

**Request:**
```
GET http://localhost:8080/api/locations/districts/by-city/1
```

**Verificar:**
- [ ] Status: 200 OK
- [ ] Respuesta incluye distritos de esa ciudad
- [ ] Cada distrito tiene: `id`, `nombre`, `ciudadId`
- [ ] Copiar `id` de un distrito para siguiente paso

---

### 5. Crear Primera Dirección

**Request:**
```
POST http://localhost:8080/api/addresses
Authorization: Bearer {TOKEN}
Content-Type: application/json

{
  "cityId": 1,
  "districtId": 5,
  "street": "Av. Javier Prado Este 456",
  "number": "Oficina 501",
  "reference": "Edificio corporativo azul",
  "phone": "987654321",
  "label": "Oficina"
}
```

**Verificar:**
- [ ] Status: 201 Created
- [ ] Respuesta incluye dirección creada con `id`
- [ ] `isDefault` es `true` (automático para primera dirección)
- [ ] `city` y `district` vienen poblados

---

### 6. Crear Segunda Dirección

**Request:**
```
POST http://localhost:8080/api/addresses
Authorization: Bearer {TOKEN}
Content-Type: application/json

{
  "cityId": 1,
  "districtId": 3,
  "street": "Calle Los Pinos 123",
  "number": "Dpto 202",
  "reference": "Frente al parque",
  "phone": "999888777",
  "label": "Casa"
}
```

**Verificar:**
- [ ] Status: 201 Created
- [ ] `isDefault` es `false` (primera dirección mantiene default)

---

### 7. Listar Direcciones

**Request:**
```
GET http://localhost:8080/api/addresses
Authorization: Bearer {TOKEN}
```

**Verificar:**
- [ ] Status: 200 OK
- [ ] Respuesta incluye 2 direcciones
- [ ] Primera en la lista tiene `isDefault: true`
- [ ] Segunda tiene `isDefault: false`

---

### 8. Cambiar Dirección Default

**Request:**
```
PATCH http://localhost:8080/api/addresses/2/set-default
Authorization: Bearer {TOKEN}
```

**Verificar:**
- [ ] Status: 200 OK
- [ ] Dirección 2 ahora tiene `isDefault: true`
- [ ] Listar direcciones nuevamente y verificar que solo una tiene `isDefault: true`

---

### 9. Obtener Dirección Default

**Request:**
```
GET http://localhost:8080/api/addresses/default
Authorization: Bearer {TOKEN}
```

**Verificar:**
- [ ] Status: 200 OK
- [ ] Respuesta es la dirección marcada como default
- [ ] `isDefault: true`

---

### 10. Hacer Checkout (Crear Pedido)

**Request:**
```
POST http://localhost:8080/api/orders/checkout
Authorization: Bearer {TOKEN}
Content-Type: application/json

{
  "addressId": 1,
  "items": [
    { "dishId": 1, "quantity": 1 },
    { "dishId": 4, "quantity": 2 }
  ],
  "paymentMethod": "CASH",
  "notes": "Sin ensalada por favor"
}
```

**Verificar:**
- [ ] Status: 201 Created
- [ ] Respuesta incluye `orderNumber` (formato: ORD-...)
- [ ] `status` es "PENDING"
- [ ] `paymentStatus` es "PENDING"
- [ ] `subtotal` calculado correctamente
- [ ] `deliveryFee` es 5.00
- [ ] `total` = subtotal + deliveryFee
- [ ] `estimatedDeliveryTime` es ~45 min desde `createdAt`
- [ ] `items` incluye los platos con precios correctos
- [ ] Verificar en logs: "Email enviado" o error si no está configurado

---

### 11. Listar Mis Pedidos

**Request:**
```
GET http://localhost:8080/api/orders
Authorization: Bearer {TOKEN}
```

**Verificar:**
- [ ] Status: 200 OK
- [ ] Respuesta incluye el pedido creado
- [ ] Ordenados por fecha descendente (más reciente primero)
- [ ] Solo pedidos del usuario autenticado

---

### 12. Obtener Pedido por ID

**Request:**
```
GET http://localhost:8080/api/orders/1
Authorization: Bearer {TOKEN}
```

**Verificar:**
- [ ] Status: 200 OK
- [ ] Respuesta incluye pedido completo con todos los items
- [ ] Dirección de envío incluida
- [ ] Items con detalles del dish

---

### 13. Obtener Pedido por Número

**Request:**
```
GET http://localhost:8080/api/orders/number/ORD-1702341234567
Authorization: Bearer {TOKEN}
```

**Verificar:**
- [ ] Status: 200 OK (si existe)
- [ ] Status: 404 NOT FOUND (si no existe)
- [ ] Solo devuelve si pertenece al usuario autenticado

---

### 14. Filtrar Pedidos por Estado

**Request:**
```
GET http://localhost:8080/api/orders/status/PENDING
Authorization: Bearer {TOKEN}
```

**Verificar:**
- [ ] Status: 200 OK
- [ ] Solo pedidos con estado PENDING
- [ ] Solo pedidos del usuario autenticado

---

### 15. Cancelar Pedido en Estado PENDING

**Request:**
```
PATCH http://localhost:8080/api/orders/1/cancel
Authorization: Bearer {TOKEN}
```

**Verificar:**
- [ ] Status: 200 OK
- [ ] `status` cambia a "CANCELLED"
- [ ] Verificar en logs: "Pedido cancelado: ORD-..."

---

### 16. Intentar Cancelar Pedido en PREPARING (Debe Fallar)

**Primero cambiar el estado manualmente en BD:**
```sql
UPDATE orders SET status = 'PREPARING' WHERE id = 1;
```

**Request:**
```
PATCH http://localhost:8080/api/orders/1/cancel
Authorization: Bearer {TOKEN}
```

**Verificar:**
- [ ] Status: 400 Bad Request
- [ ] Mensaje de error: "No se puede cancelar el pedido en su estado actual"

---

### 17. Intentar Hacer Checkout sin Dirección

**Request:**
```
POST http://localhost:8080/api/orders/checkout
Authorization: Bearer {TOKEN}
Content-Type: application/json

{
  "addressId": 999,
  "items": [
    { "dishId": 1, "quantity": 1 }
  ],
  "paymentMethod": "CASH"
}
```

**Verificar:**
- [ ] Status: 404 Not Found
- [ ] Mensaje: "Dirección no encontrada"

---

### 18. Intentar Usar Dirección de Otro Usuario

**Crear segundo usuario y obtener su dirección ID, luego:**

**Request:**
```
POST http://localhost:8080/api/orders/checkout
Authorization: Bearer {TOKEN_USUARIO_1}
Content-Type: application/json

{
  "addressId": {ID_DIRECCION_USUARIO_2},
  "items": [
    { "dishId": 1, "quantity": 1 }
  ],
  "paymentMethod": "CASH"
}
```

**Verificar:**
- [ ] Status: 403 Forbidden
- [ ] Mensaje: "La dirección no pertenece al usuario"

---

### 19. Hacer Checkout con Carrito Vacío

**Request:**
```
POST http://localhost:8080/api/orders/checkout
Authorization: Bearer {TOKEN}
Content-Type: application/json

{
  "addressId": 1,
  "items": [],
  "paymentMethod": "CASH"
}
```

**Verificar:**
- [ ] Status: 400 Bad Request
- [ ] Mensaje: "El carrito está vacío"

---

### 20. Intentar Eliminar Dirección Usada en Pedido

**Request:**
```
DELETE http://localhost:8080/api/addresses/1
Authorization: Bearer {TOKEN}
```

**Verificar:**
- [ ] Status: 409 Conflict o 400 Bad Request
- [ ] Error de integridad referencial (hay orders usando esa dirección)

---

## 🎯 Testing de Seguridad

### Test 1: Acceso sin Token

**Request:**
```
GET http://localhost:8080/api/addresses
```

**Verificar:**
- [ ] Status: 401 Unauthorized o 403 Forbidden

---

### Test 2: Token Inválido

**Request:**
```
GET http://localhost:8080/api/addresses
Authorization: Bearer token_invalido_123
```

**Verificar:**
- [ ] Status: 401 Unauthorized o 403 Forbidden

---

### Test 3: Token Expirado

**Usar token de hace más de 24 horas (si está configurado así)**

**Verificar:**
- [ ] Status: 401 Unauthorized
- [ ] Mensaje: Token expirado

---

## 📧 Testing de Emails (Opcional)

Si configuraste Gmail o MailHog:

### Verificar Email de Confirmación

**Después de crear un pedido:**

- [ ] Email recibido en el correo del usuario
- [ ] Asunto: "Confirmación de Pedido - El Pollo Empoderado"
- [ ] Incluye número de pedido
- [ ] Incluye listado de items
- [ ] Incluye total
- [ ] Incluye dirección de envío
- [ ] Formato HTML correcto

---

## 🗄️ Verificación de Datos en BD

### Después de crear pedido, verificar en BD:

```sql
USE pollo_empoderado_db;

-- Ver pedido creado
SELECT * FROM orders WHERE id = 1;
-- Verificar: order_number, user_id, address_id, status, total

-- Ver items del pedido
SELECT 
    oi.*,
    d.name as dish_name,
    d.price as current_dish_price
FROM order_items oi
INNER JOIN dish d ON oi.dish_id = d.id
WHERE oi.order_id = 1;
-- Verificar: unit_price puede ser diferente a current_dish_price

-- Ver dirección del pedido
SELECT 
    o.id as order_id,
    o.order_number,
    a.street,
    a.number,
    c.nombre as ciudad,
    d.nombre as distrito
FROM orders o
INNER JOIN addresses a ON o.address_id = a.id
INNER JOIN cities c ON a.city_id = c.ciudad_id
INNER JOIN districts d ON a.district_id = d.distrito_id
WHERE o.id = 1;
```

**Verificar:**
- [ ] Pedido existe en tabla `orders`
- [ ] Items existen en tabla `order_items`
- [ ] `unit_price` en `order_items` es el precio al momento de compra
- [ ] Dirección está correctamente relacionada
- [ ] Ciudad y distrito existen y están relacionados

---

## 🔄 Testing de Flujos Completos

### Flujo 1: Usuario Nuevo Hace su Primer Pedido

- [ ] Registro de usuario (si aplica)
- [ ] Login exitoso
- [ ] Listar direcciones → vacío
- [ ] Crear primera dirección → automáticamente default
- [ ] Hacer checkout con esa dirección
- [ ] Verificar pedido creado
- [ ] Verificar email recibido

---

### Flujo 2: Usuario con Múltiples Direcciones

- [ ] Login de usuario existente
- [ ] Crear 3 direcciones
- [ ] Verificar que solo una es default
- [ ] Cambiar dirección default
- [ ] Hacer checkout con dirección no-default
- [ ] Verificar pedido usa dirección correcta

---

### Flujo 3: Gestión de Pedidos

- [ ] Crear pedido en PENDING
- [ ] Listar mis pedidos
- [ ] Filtrar por estado PENDING
- [ ] Cancelar pedido
- [ ] Verificar estado cambia a CANCELLED
- [ ] Intentar cancelar otro pedido en PREPARING → debe fallar

---

### Flujo 4: Admin Gestiona Pedidos (Si tienes rol admin)

- [ ] Login como admin
- [ ] Listar todos los pedidos (no solo del usuario)
- [ ] Actualizar estado de pedido: PENDING → CONFIRMED
- [ ] Actualizar estado: CONFIRMED → PREPARING
- [ ] Actualizar estado: PREPARING → READY
- [ ] Actualizar estado: READY → ON_DELIVERY
- [ ] Actualizar estado: ON_DELIVERY → DELIVERED
- [ ] Verificar `delivered_at` se registra

---

## 📝 Resumen de Resultados

### Compilación
- [ ] ✅ Proyecto compila sin errores
- [ ] ✅ Total archivos: ~86

### Base de Datos
- [ ] ✅ Tabla `addresses` creada con foreign keys
- [ ] ✅ Tabla `orders` creada con foreign keys
- [ ] ✅ Tabla `order_items` creada con foreign keys
- [ ] ✅ Relaciones bidireccionales funcionan

### Endpoints (17 total)
- [ ] ✅ 7 endpoints de direcciones funcionales
- [ ] ✅ 10 endpoints de pedidos funcionales

### Validaciones
- [ ] ✅ Validación de usuario autenticado
- [ ] ✅ Validación de dirección pertenece a usuario
- [ ] ✅ Validación de distrito pertenece a ciudad
- [ ] ✅ Validación de estado para cancelación
- [ ] ✅ Validación de carrito no vacío

### Cálculos
- [ ] ✅ Subtotal se calcula correctamente
- [ ] ✅ Delivery fee se agrega (5.00)
- [ ] ✅ Total = subtotal + delivery
- [ ] ✅ Tiempo estimado = now + 45 min

### Email
- [ ] ✅ Email se envía asíncronamente
- [ ] ✅ Template HTML se procesa
- [ ] ✅ Si falla email, pedido se crea igual

---

## 🐛 Problemas Comunes

### Error: "Cannot delete or update a parent row"
**Causa:** Intentando eliminar dirección usada en pedidos  
**Solución:** Es correcto, protege datos históricos

### Error: "La dirección no pertenece al usuario"
**Causa:** Intentando usar dirección de otro usuario  
**Solución:** Es correcto, validación de seguridad funciona

### Email no se envía
**Causa:** SMTP no configurado  
**Solución:** Ver SETUP-GUIDE.md o usar MailHog

### "No se puede cancelar el pedido en su estado actual"
**Causa:** Pedido en estado PREPARING o posterior  
**Solución:** Es correcto, solo se puede cancelar PENDING/CONFIRMED

---

## ✅ Criterios de Éxito

El sistema está **100% funcional** si:

- ✅ Todos los endpoints responden correctamente
- ✅ Validaciones de seguridad funcionan
- ✅ Relaciones de BD son correctas
- ✅ Cálculos de totales son precisos
- ✅ Estados del pedido se gestionan correctamente
- ✅ Emails se envían (o se registra el intento)
- ✅ No hay errores en logs (excepto email si no está configurado)

---

¡Si todos los checks están marcados, el sistema está listo para producción! 🎉
