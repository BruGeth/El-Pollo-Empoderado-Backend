# 🔄 Diagrama de Flujo Completo - Sistema de Carrito y Pedidos

## 📊 Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                         FRONTEND (Angular)                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐│
│  │ Promociones│  │   Carta    │  │Acompañamien│  │  Bebidas   ││
│  │   Page     │  │   Page     │  │  tos Page  │  │   Page     ││
│  └──────┬─────┘  └──────┬─────┘  └──────┬─────┘  └──────┬─────┘│
│         │                │                │                │      │
│         └────────────────┴────────────────┴────────────────┘      │
│                              │                                    │
│                      [Click "Agregar"]                            │
│                              ↓                                    │
│                   ┌──────────────────────┐                        │
│                   │   CartService        │                        │
│                   │   (localStorage)     │                        │
│                   └──────────────────────┘                        │
│                              │                                    │
│                              ↓                                    │
│         ┌────────────────────────────────────────┐               │
│         │     Side Modal (Carrito)               │               │
│         │  ┌────────────────────────────────┐   │               │
│         │  │ [X]                            │   │               │
│         │  │ Card 1: Pollo 1/2   [-][2][+] │   │               │
│         │  │ Card 2: Inca Kola   [-][1][+] │   │               │
│         │  │                                │   │               │
│         │  │ Total: S/ 38.00                │   │               │
│         │  │ [Proceder al Pago]             │   │               │
│         │  └────────────────────────────────┘   │               │
│         └────────────────────────────────────────┘               │
│                              │                                    │
│                              ↓                                    │
│         ┌────────────────────────────────────────┐               │
│         │   Página /carrito (Resumen)            │               │
│         │  ┌────────────────────────────────┐   │               │
│         │  │ Resumen Items                  │   │               │
│         │  │ Card 1: Pollo 1/2              │   │               │
│         │  │ Card 2: Inca Kola              │   │               │
│         │  │                                │   │               │
│         │  │ Acompañamientos Sugeridos:     │   │               │
│         │  │ [Papas] [Ensalada] [Tequeños] │   │               │
│         │  │                                │   │               │
│         │  │ Subtotal: S/ 38.00             │   │               │
│         │  │ Envío: S/ 5.00                 │   │               │
│         │  │ Total: S/ 43.00                │   │               │
│         │  │ [Continuar con Envío] ────────┐│   │               │
│         │  └────────────────────────────────┘   │               │
│         └────────────────────────────────────────┘               │
│                              │                                    │
│                              ↓                                    │
│         ┌────────────────────────────────────────┐               │
│         │   Página /envio                        │               │
│         │  ┌────────────────┐ ┌────────────────┐│               │
│         │  │ Direcciones    │ │ Resumen        ││               │
│         │  │ ┌────────────┐ │ │ Items          ││               │
│         │  │ │[✓]Casa     │ │ │ Subtotal:38.00 ││               │
│         │  │ │Lima-Mirafl.│ │ │ Envío: 5.00    ││               │
│         │  │ └────────────┘ │ │ Total: 43.00   ││               │
│         │  │ ┌────────────┐ │ │                ││               │
│         │  │ │[ ]Oficina  │ │ │ Método Pago:   ││               │
│         │  │ │Lima-San Is.│ │ │ ○ MercadoPago  ││               │
│         │  │ └────────────┘ │ │ ● Efectivo     ││               │
│         │  │ [+ Nueva Dir.] │ │                ││               │
│         │  │                │ │ Boleta a:      ││               │
│         │  │ Boleta:        │ │ user@email.com ││               │
│         │  │ user@email.com │ │                ││               │
│         │  └────────────────┘ │ [Confirmar]────┼┼──┐            │
│         └────────────────────────────────────────┘  │            │
└─────────────────────────────────────────────────────┼────────────┘
                                                       │
                              ┌────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                      BACKEND (Spring Boot)                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  POST /api/orders/checkout                                       │
│  Headers: Authorization: Bearer {JWT}                            │
│  Body: { addressId, items[], paymentMethod, notes }             │
│                              │                                    │
│                              ↓                                    │
│         ┌────────────────────────────────────────┐               │
│         │     OrderService.createOrder()         │               │
│         └────────────────────────────────────────┘               │
│                              │                                    │
│         ┌────────────────────┴────────────────────┐              │
│         ↓                                          ↓              │
│  ┌──────────────┐                          ┌──────────────┐     │
│  │ Validaciones │                          │  Cálculos    │     │
│  │ • Usuario    │                          │ • Subtotal   │     │
│  │ • Dirección  │                          │ • Delivery   │     │
│  │ • Dishes     │                          │ • Total      │     │
│  │ • Permisos   │                          │ • Est.Time   │     │
│  └──────┬───────┘                          └──────┬───────┘     │
│         │                                          │              │
│         └────────────────────┬────────────────────┘              │
│                              ↓                                    │
│              ┌───────────────────────────────┐                   │
│              │  Guardar en Base de Datos     │                   │
│              ├───────────────────────────────┤                   │
│              │  orders                       │                   │
│              │  • id: 10                     │                   │
│              │  • order_number: ORD-170...   │                   │
│              │  • user_id: 1                 │                   │
│              │  • address_id: 1              │                   │
│              │  • status: PENDING            │                   │
│              │  • payment_status: PENDING    │                   │
│              │  • total: 43.00               │                   │
│              │                               │                   │
│              │  order_items                  │                   │
│              │  • order_id: 10, dish_id: 2   │                   │
│              │  • quantity: 2, price: 30.00  │                   │
│              │  • order_id: 10, dish_id: 12  │                   │
│              │  • quantity: 1, price: 8.00   │                   │
│              └───────────────┬───────────────┘                   │
│                              │                                    │
│                              ↓                                    │
│              ┌───────────────────────────────┐                   │
│              │  EmailService (Async)         │                   │
│              │  sendOrderConfirmation()      │                   │
│              └───────────────┬───────────────┘                   │
│                              │                                    │
│                              ↓                                    │
│                       SMTP Server                                │
│                              │                                    │
└──────────────────────────────┼───────────────────────────────────┘
                               │
                               ↓
                    ┌──────────────────┐
                    │   Email Client   │
                    │  ┌────────────┐  │
                    │  │ De: Pollo  │  │
                    │  │ Para: user │  │
                    │  │            │  │
                    │  │ Pedido     │  │
                    │  │ #ORD-170.. │  │
                    │  │            │  │
                    │  │ Total:     │  │
                    │  │ S/ 43.00   │  │
                    │  └────────────┘  │
                    └──────────────────┘
```

---

## 🗄️ Modelo de Base de Datos

```
┌─────────────┐           ┌─────────────┐
│    roles    │◄─────────┐│    users    │
│─────────────│           ││─────────────│
│ id          │    M:M    ││ id          │
│ name        │           ││ first_name  │
└─────────────┘           ││ last_name   │
                          ││ email       │
      ┌───────────────────┘│ password    │
      │                    ││ dni         │
      │  ┌─────────────────┼│ birth_date  │
      │  │                 └┼────────────┬┘
      │  │                  │            │
      │  │                  │            │
      │  │                  │ 1:N        │ 1:N
      │  │                  ↓            ↓
      │  │         ┌─────────────┐  ┌─────────────┐
      │  │         │  addresses  │  │   orders    │
      │  │         │─────────────│  │─────────────│
      │  │         │ id          │  │ id          │
      │  └────────►│ user_id (FK)│  │ order_number│
      │            │ city_id (FK)│◄─┤ user_id (FK)│
      │            │ district_id │  │ address_id  │
      │            │ street      │  │ subtotal    │
      │            │ number      │  │ delivery_fee│
      │            │ reference   │  │ total       │
      │            │ phone       │  │ status      │
      │            │ label       │  │ payment_... │
      │            │ is_default  │  │ notes       │
      │            └─────────────┘  │ receipt_... │
      │                             │ estimated...│
      ↓ 1:N                         │ created_at  │
┌─────────────┐                     └──────┬──────┘
│user_roles   │                            │
│─────────────│                            │ 1:N
│ user_id (FK)│                            ↓
│ role_id (FK)│                   ┌─────────────┐
└─────────────┘                   │order_items  │
                                  │─────────────│
┌─────────────┐                   │ id          │
│   cities    │                   │ order_id(FK)│
│─────────────│                   │ dish_id (FK)│
│ ciudad_id   │                   │ quantity    │
│ nombre      │                   │ unit_price  │◄───┐
└──────┬──────┘                   │ subtotal    │    │
       │ 1:N                      └─────────────┘    │
       ↓                                              │
┌─────────────┐           ┌─────────────┐            │
│ districts   │           │  category   │            │
│─────────────│           │─────────────│            │
│ distrito_id │           │ id          │            │
│ nombre      │           │ name        │            │
│ ciudad_id   │           │ description │            │
└─────────────┘           └──────┬──────┘            │
                                 │ 1:N               │
                                 ↓                   │
                          ┌─────────────┐            │
                          │    dish     │────────────┘
                          │─────────────│
                          │ id          │
                          │ name        │
                          │ description │
                          │ price       │
                          │ image_url   │
                          │ category_id │
                          └─────────────┘
```

---

## 🔄 Estados del Pedido (Order Status)

```
    ┌──────────┐
    │ PENDING  │  ← Usuario hace checkout
    └────┬─────┘
         │
         ↓ (Admin confirma)
    ┌──────────┐
    │CONFIRMED │
    └────┬─────┘
         │
         ↓ (Cocina empieza)
    ┌──────────┐
    │PREPARING │
    └────┬─────┘
         │
         ↓ (Listo para enviar)
    ┌──────────┐
    │  READY   │
    └────┬─────┘
         │
         ↓ (Repartidor sale)
    ┌──────────┐
    │ON_DELIVERY│
    └────┬─────┘
         │
         ↓ (Cliente recibe)
    ┌──────────┐
    │DELIVERED │
    └──────────┘

         │
         └──→ (Usuario cancela en PENDING/CONFIRMED)
              ┌──────────┐
              │CANCELLED │
              └──────────┘
```

---

## 🔒 Flujo de Seguridad (JWT)

```
┌──────────────────────────────────────────────────────────────┐
│                      AUTENTICACIÓN                            │
└──────────────────────────────────────────────────────────────┘

Frontend                          Backend
   │                                 │
   │  POST /api/auth/login           │
   │  { email, password }            │
   ├────────────────────────────────>│
   │                                 │ Validar credenciales
   │                                 │ Generar JWT Token
   │                                 │
   │  { token: "eyJhbG...",         │
   │    user: {...} }                │
   │<────────────────────────────────┤
   │                                 │
   │  Guardar token en localStorage  │
   │                                 │

┌──────────────────────────────────────────────────────────────┐
│                   REQUESTS AUTENTICADOS                       │
└──────────────────────────────────────────────────────────────┘

Frontend                          Backend
   │                                 │
   │  GET /api/orders                │
   │  Authorization: Bearer {TOKEN}  │
   ├────────────────────────────────>│
   │                                 │ Validar JWT
   │                                 │ Extraer userId
   │                                 │ Verificar permisos
   │                                 │ Filtrar por userId
   │                                 │
   │  [OrderResponse, ...]           │
   │<────────────────────────────────┤
   │                                 │
```

---

## 📧 Flujo de Emails

```
┌───────────────────────────────────────────────────────────────┐
│                      EMAIL ASÍNCRONO                           │
└───────────────────────────────────────────────────────────────┘

OrderService                 EmailService              SMTP Server
     │                            │                          │
     │ createOrder()              │                          │
     │  ↓                         │                          │
     │ save(order)                │                          │
     │  ↓                         │                          │
     │ sendOrderConfirmation() ──>│                          │
     │  ↓                         │                          │
     │ return OrderResponse       │                          │
     │  (No espera email)         │                          │
     │                            │                          │
     │                            │ @Async                   │
     │                            │ sendOrderConfirmation()  │
     │                            │  ↓                       │
     │                            │ Cargar template          │
     │                            │ Thymeleaf                │
     │                            │  ↓                       │
     │                            │ Procesar variables       │
     │                            │  ↓                       │
     │                            │ send(email) ────────────>│
     │                            │                          │ Enviar
     │                            │                          │ a destinatario
     │                            │<─────────────────────────┤
     │                            │ Email enviado            │
     │                            │                          │
```

---

## 🎯 Endpoints del Sistema

### 🏠 Direcciones (AddressController)

```
GET    /api/addresses                  → Listar mis direcciones
GET    /api/addresses/default          → Obtener dirección default
POST   /api/addresses                  → Crear nueva dirección
GET    /api/addresses/{id}             → Obtener dirección específica
PUT    /api/addresses/{id}             → Actualizar dirección
PATCH  /api/addresses/{id}/set-default → Marcar como default
DELETE /api/addresses/{id}             → Eliminar dirección
```

### 🛒 Pedidos (OrderController)

```
POST   /api/orders/checkout                  → Crear pedido (checkout)
GET    /api/orders                           → Listar mis pedidos
GET    /api/orders/paginated                 → Listar con paginación
GET    /api/orders/{id}                      → Obtener pedido específico
GET    /api/orders/number/{orderNumber}      → Obtener por número
GET    /api/orders/status/{status}           → Filtrar por estado
PATCH  /api/orders/{id}/cancel               → Cancelar pedido
PATCH  /api/orders/{id}/status               → Actualizar estado (Admin)
PATCH  /api/orders/{id}/payment-status       → Actualizar pago (Admin/Webhook)
GET    /api/orders/statistics                → Estadísticas (Admin)
```

### 📍 Ubicaciones (LocationController)

```
GET    /api/locations/cities                 → Listar ciudades
GET    /api/locations/districts/by-city/{id} → Listar distritos de ciudad
```

---

## ⚡ Casos de Uso Principales

### 1️⃣ Usuario Hace su Primer Pedido

```
1. Usuario agrega items al carrito (localStorage)
2. Click "Proceder al Pago"
3. Página /envio → No tiene direcciones
4. Click "Nueva Dirección"
5. Selecciona ciudad → Se cargan distritos
6. Llena formulario → POST /api/addresses
7. Dirección se marca automáticamente como default
8. Selecciona método de pago
9. Click "Confirmar Pedido" → POST /api/orders/checkout
10. Backend valida, crea Order, envía email
11. Usuario ve confirmación con número de pedido
```

### 2️⃣ Usuario con Múltiples Direcciones

```
1. Usuario entra a /envio
2. GET /api/addresses → 3 direcciones
3. Frontend selecciona automáticamente la default
4. Usuario puede cambiar selección
5. Click "Confirmar Pedido"
6. Backend valida que dirección pertenece al usuario
7. Crea pedido con dirección seleccionada
```

### 3️⃣ Usuario Cancela Pedido

```
1. Usuario entra a /mis-pedidos
2. GET /api/orders → Lista de pedidos
3. Ve pedido en estado PENDING
4. Click "Cancelar"
5. Confirmación → PATCH /api/orders/{id}/cancel
6. Backend valida estado (PENDING o CONFIRMED)
7. Actualiza status = CANCELLED
8. Frontend actualiza lista
```

### 4️⃣ Admin Actualiza Estado de Pedido

```
1. Admin dashboard
2. GET /api/orders → Todos los pedidos
3. Selecciona pedido en CONFIRMED
4. Cambio a PREPARING
5. PATCH /api/orders/{id}/status
6. Backend actualiza estado
7. EmailService notifica al cliente (opcional)
```

---

## 🧪 Testing Checklist

- ✅ Crear usuario y login
- ✅ Crear primera dirección (debe ser default automáticamente)
- ✅ Crear segunda dirección (primera mantiene default)
- ✅ Cambiar dirección default
- ✅ Hacer checkout sin dirección seleccionada (debe fallar)
- ✅ Hacer checkout con dirección de otro usuario (debe fallar)
- ✅ Hacer checkout exitoso con CASH
- ✅ Verificar que el email llegó
- ✅ Listar mis pedidos
- ✅ Filtrar por estado PENDING
- ✅ Cancelar pedido PENDING (debe funcionar)
- ✅ Cancelar pedido PREPARING (debe fallar)
- ✅ Obtener pedido por número
- ✅ Actualizar estado como admin
- ✅ Verificar que los precios en OrderItem son del momento de compra
- ✅ Eliminar dirección que está en un pedido (debe fallar por FK)
- ✅ Verificar cascada: eliminar usuario elimina addresses

---

Este diagrama muestra el flujo completo desde que el usuario agrega items al carrito hasta que recibe su pedido y la boleta por email. 🚀
