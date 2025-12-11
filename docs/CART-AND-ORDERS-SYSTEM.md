# 🛒 Sistema de Carrito y Gestión de Pedidos
## El Pollo Empoderado - Backend

Este documento describe la implementación completa del sistema de carrito de compras y gestión de pedidos para la pollería.

## 📋 Tabla de Contenidos

- [Arquitectura](#arquitectura)
- [Modelos de Datos](#modelos-de-datos)
- [API Endpoints](#api-endpoints)
- [Configuración](#configuración)
- [Flujo de Pedido](#flujo-de-pedido)
- [Integración con Email](#integración-con-email)

---

## 🏗️ Arquitectura

### Nuevas Tablas Creadas

1. **addresses** - Direcciones de envío del usuario
2. **orders** - Pedidos principales
3. **order_items** - Items/productos de cada pedido

### Relaciones

```
User 1:N Address
User 1:N Order
Order 1:N OrderItem
Order N:1 Address
OrderItem N:1 Dish
Address N:1 City
Address N:1 District
```

---

## 📊 Modelos de Datos

### Address (Dirección)
- **Propósito**: Gestionar múltiples direcciones de envío por usuario
- **Campos principales**:
  - `city_id`, `district_id` - Ubicación
  - `street`, `number`, `reference` - Dirección detallada
  - `phone` - Teléfono de contacto
  - `is_default` - Dirección predeterminada

### Order (Pedido)
- **Propósito**: Representa un pedido completo del cliente
- **Campos principales**:
  - `order_number` - Número único de orden (ORD-timestamp)
  - `subtotal`, `delivery_fee`, `total` - Montos
  - `status` - Estado del pedido (PENDING, CONFIRMED, PREPARING, etc.)
  - `payment_method` - Método de pago
  - `payment_status` - Estado del pago
  - `estimated_delivery_time` - Tiempo estimado (45 min por defecto)

### OrderItem
- **Propósito**: Productos individuales dentro de un pedido
- **Campos principales**:
  - `dish_id` - Plato ordenado
  - `quantity` - Cantidad
  - `unit_price` - Precio al momento de la compra
  - `subtotal` - Total del item

---

## 🌐 API Endpoints

### Direcciones (`/api/addresses`)

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|---------------|
| GET | `/api/addresses` | Listar direcciones del usuario | USER/ADMIN |
| GET | `/api/addresses/{id}` | Obtener dirección específica | USER/ADMIN |
| GET | `/api/addresses/default` | Obtener dirección por defecto | USER/ADMIN |
| POST | `/api/addresses` | Crear nueva dirección | USER/ADMIN |
| PUT | `/api/addresses/{id}` | Actualizar dirección | USER/ADMIN |
| PATCH | `/api/addresses/{id}/set-default` | Establecer como predeterminada | USER/ADMIN |
| DELETE | `/api/addresses/{id}` | Eliminar dirección | USER/ADMIN |

### Pedidos (`/api/orders`)

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|---------------|
| POST | `/api/orders/checkout` | Crear pedido (checkout) | USER/ADMIN |
| GET | `/api/orders` | Listar pedidos del usuario | USER/ADMIN |
| GET | `/api/orders/paginated` | Listar con paginación | USER/ADMIN |
| GET | `/api/orders/{id}` | Obtener pedido por ID | USER/ADMIN |
| GET | `/api/orders/number/{orderNumber}` | Obtener por número de orden | USER/ADMIN |
| GET | `/api/orders/status/{status}` | Filtrar por estado | USER/ADMIN |
| PATCH | `/api/orders/{id}/cancel` | Cancelar pedido | USER/ADMIN |
| GET | `/api/orders/statistics` | Estadísticas de pedidos | USER/ADMIN |
| PATCH | `/api/orders/{id}/status` | Actualizar estado (admin) | ADMIN |
| PATCH | `/api/orders/{id}/payment-status` | Actualizar pago (admin) | ADMIN |

---

## ⚙️ Configuración

### 1. Agregar configuración de email en `application-dev.yml`:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

### 2. Variables de entorno requeridas:

```bash
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-app-password
```

### 3. Para Gmail, generar App Password:
1. Ir a Google Account → Security
2. Activar 2-Step Verification
3. Crear App Password
4. Usar ese password en `MAIL_PASSWORD`

---

## 🔄 Flujo de Pedido

### 1. Cliente agrega items al carrito (Frontend)
```json
{
  "dishId": 1,
  "quantity": 2
}
```

### 2. Cliente procede al checkout
**Request**: `POST /api/orders/checkout`
```json
{
  "addressId": 1,
  "items": [
    {"dishId": 1, "quantity": 2},
    {"dishId": 3, "quantity": 1}
  ],
  "paymentMethod": "MERCADO_PAGO",
  "notes": "Sin cebolla por favor"
}
```

### 3. Sistema procesa:
- ✅ Valida usuario y dirección
- ✅ Valida existencia de platos
- ✅ Calcula subtotal y total
- ✅ Crea pedido con estado PENDING
- ✅ Envía email de confirmación (asíncrono)
- ✅ Retorna pedido creado

### 4. Respuesta:
```json
{
  "id": 1,
  "orderNumber": "ORD-1702345678901",
  "status": "PENDING",
  "paymentStatus": "PENDING",
  "subtotal": 85.00,
  "deliveryFee": 5.00,
  "total": 90.00,
  "estimatedDeliveryTime": "2024-12-11T16:15:00",
  ...
}
```

### 5. Estados del pedido:
```
PENDING → CONFIRMED → PREPARING → READY → ON_DELIVERY → DELIVERED
                                      ↓
                                  CANCELLED
```

---

## 📧 Integración con Email

### EmailService
Envía correos automáticamente para:
- ✉️ **Confirmación de pedido** (boleta)
- ✉️ **Actualizaciones de estado**

### Templates HTML (Thymeleaf)
Ubicación: `src/main/resources/templates/email/`
- `order-confirmation.html` - Boleta con detalle completo
- `order-status-update.html` - Notificación de cambios

### Método asíncrono
Los correos se envían en background para no bloquear la respuesta del API.

---

## 🎯 Estados y Enums

### OrderStatus
- `PENDING` - Pendiente de confirmación
- `CONFIRMED` - Confirmado por el sistema
- `PREPARING` - En cocina
- `READY` - Listo para entrega
- `ON_DELIVERY` - En camino
- `DELIVERED` - Entregado
- `CANCELLED` - Cancelado

### PaymentStatus
- `PENDING` - Pendiente
- `PROCESSING` - En proceso
- `APPROVED` - Aprobado
- `REJECTED` - Rechazado
- `REFUNDED` - Reembolsado

### PaymentMethod
- `MERCADO_PAGO` - MercadoPago
- `CASH` - Efectivo
- `CARD` - Tarjeta

---

## 📝 Notas Importantes

### Gestión de Direcciones
- Un usuario puede tener múltiples direcciones
- Solo una puede ser predeterminada
- No se puede eliminar la dirección predeterminada si hay otras
- Al crear la primera dirección, automáticamente es predeterminada

### Precios Históricos
Los precios se guardan en `OrderItem.unit_price` al momento de la compra, así si cambia el precio del plato después, el pedido mantiene el precio original.

### Costo de Envío
Actualmente fijo en S/ 5.00, definido en `OrderService.DELIVERY_FEE`

### Tiempo de Entrega
Por defecto: 45 minutos desde la creación del pedido

---

## 🚀 Próximos Pasos

### Para el Frontend:
1. Implementar Side Modal del carrito
2. Página de resumen y checkout
3. Gestión de direcciones en perfil de usuario
4. Dashboard de pedidos del usuario
5. Integración con MercadoPago

### Para el Backend:
1. Webhook de MercadoPago para actualizar estado de pago
2. Sistema de notificaciones en tiempo real (WebSocket)
3. Panel admin para gestionar pedidos
4. Reportes y analytics de ventas

---

## 📚 Recursos Adicionales

- [Spring Boot Mail](https://spring.io/guides/gs/sending-email/)
- [Thymeleaf Templates](https://www.thymeleaf.org/)
- [MercadoPago API](https://www.mercadopago.com.pe/developers/es/docs)

---

**Desarrollado con ❤️ para El Pollo Empoderado**
