# 📬 Colecciones Postman - El Pollo Empoderado

## 📁 Archivo Principal

### **El-Pollo-Empoderado-Complete.postman_collection.json**
Colección **COMPLETA** con todos los endpoints del sistema organizados por funcionalidad.

#### 📂 Estructura de Secciones:

1. **🏠 Health & Index** (2 endpoints)
   - Health Check
   - Index

2. **🔐 Authentication** (3 endpoints)
   - Register User
   - Login User
   - Login Admin

3. **👤 User Profile** (3 endpoints)
   - Get My Profile
   - Update My Profile
   - Change Password

4. **📍 Locations (Public)** (3 endpoints)
   - Get All Cities
   - Get All Districts
   - Get Districts By City

5. **📍 Addresses (User)** (7 endpoints)
   - Get My Addresses
   - Get Address by ID
   - Create Address
   - Update Address
   - Set Default Address
   - Get Default Address
   - Delete Address

6. **🏪 Locals (Public)** (4 endpoints)
   - Get All Locals
   - Get Local By ID
   - Get Locals By City
   - Get Locals By District

7. **🍗 Categories (Public)** (2 endpoints)
   - Get All Categories
   - Get Category By ID

8. **🍽️ Dishes (Public)** (3 endpoints)
   - Get All Dishes
   - Get Dish By ID
   - Get Dishes By Category

9. **📖 Menu (Public)** (1 endpoint)
   - Get Full Menu

10. **🛒 Orders (User)** (6 endpoints)
    - Get My Orders
    - Get Order by ID
    - Get Order by Number
    - Create Order (Checkout)
    - Get Orders by Status
    - Cancel Order

11. **💳 Payments (MercadoPago)** (4 endpoints)
    - Get Public Key
    - Test Configuration
    - Create Payment Preference
    - Webhook (MercadoPago callback)

12. **🔐 ADMIN - Users** (2 endpoints)
    - Get All Users
    - Get User By ID

13. **🔐 ADMIN - Categories** (3 endpoints)
    - Create Category
    - Update Category
    - Delete Category

14. **🔐 ADMIN - Dishes** (3 endpoints)
    - Create Dish
    - Update Dish
    - Delete Dish

15. **🔐 ADMIN - Locals** (3 endpoints)
    - Create Local
    - Update Local
    - Delete Local

16. **🔐 ADMIN - Orders** (5 endpoints)
    - Get All Orders
    - Get Order By ID
    - Get Orders By Status
    - Update Order Status
    - Update Payment Status

---

## 📊 Resumen

| Categoría | Endpoints | Autenticación |
|-----------|-----------|---------------|
| Health & Index | 2 | ❌ No |
| Authentication | 3 | ❌ No |
| User Profile | 3 | ✅ User |
| Locations (Public) | 3 | ❌ No |
| Addresses (User) | 7 | ✅ User |
| Locals (Public) | 4 | ❌ No |
| Categories (Public) | 2 | ❌ No |
| Dishes (Public) | 3 | ❌ No |
| Menu (Public) | 1 | ❌ No |
| Orders (User) | 6 | ✅ User |
| Payments | 4 | 2 públicos, 1 user, 1 webhook |
| ADMIN - Users | 2 | 🔐 Admin |
| ADMIN - Categories | 3 | 🔐 Admin |
| ADMIN - Dishes | 3 | 🔐 Admin |
| ADMIN - Locals | 3 | 🔐 Admin |
| ADMIN - Orders | 5 | 🔐 Admin |
| **TOTAL** | **54** | - |

---

## 🚀 Cómo Usar

### 1. Importar la Colección
1. Abrir Postman
2. Click en **Import**
3. Seleccionar `El-Pollo-Empoderado-Complete.postman_collection.json`
4. Click **Import**

### 2. Configurar Variables de Entorno

#### Variables de Colección (ya configuradas):
```json
{
  "base_url": "http://localhost:8080",
  "user_token": "",
  "admin_token": "",
  "address_id": "",
  "order_id": "",
  "order_number": ""
}
```

#### Variables Auto-guardadas:
- **user_token**: Se guarda automáticamente al hacer Register o Login User
- **admin_token**: Se guarda automáticamente al hacer Login Admin
- **address_id**: Se guarda al crear una dirección
- **order_id**: Se guarda al crear un pedido
- **order_number**: Se guarda al crear un pedido

### 3. Flujo Típico de Testing

#### Para Usuario Normal:
```
1. Register User (guarda token automáticamente)
   ↓
2. Create Address (guarda address_id automáticamente)
   ↓
3. Get Full Menu
   ↓
4. Create Order (guarda order_id y order_number)
   ↓
5. Create Payment Preference
   ↓
6. Get My Orders
```

#### Para Admin:
```
1. Login Admin (guarda admin_token)
   ↓
2. Get All Orders
   ↓
3. Update Order Status
   ↓
4. Create Category/Dish/Local
```

---

## 🔑 Credenciales de Prueba

### Usuario Normal:
```json
{
  "email": "juan@example.com",
  "password": "password123"
}
```

### Administrador:
```json
{
  "email": "admin@empoderado.com",
  "password": "admin123"
}
```

---

## 📝 Notas Importantes

1. **Autenticación**: Los endpoints protegidos requieren el header `Authorization: Bearer <token>`
2. **Tokens automáticos**: Los scripts post-request guardan automáticamente los tokens
3. **IDs dinámicos**: Los IDs se guardan automáticamente al crear recursos
4. **MercadoPago**: El webhook es llamado por MercadoPago, no por el frontend
5. **Order States**: PENDING → CONFIRMED → PREPARING → ON_DELIVERY → DELIVERED (o CANCELLED)
6. **Payment States**: PENDING → PROCESSING → APPROVED (o REJECTED/REFUNDED)

---

## 🗑️ Archivos Anteriores (Eliminados)

Los siguientes archivos fueron consolidados en la nueva colección completa:

❌ `Address-and-Orders.postman_collection.json`  
❌ `Admin-Dashboard.postman_collection.json`  
❌ `Cart-Orders.postman_collection.json`  
❌ `Category-postman.json`  
❌ `Dish-postman.json`  
❌ `El-Pollo-Empoderado-API.postman_collection.json`  
❌ `Payments.postman_collection.json`  
❌ `Public-Endpoints.postman_collection.json`  

**Todos los endpoints de estos archivos están incluidos en `El-Pollo-Empoderado-Complete.postman_collection.json`**

---

## 🔍 Búsqueda Rápida

Para encontrar un endpoint específico en Postman:
1. Abrir la colección
2. Usar `Ctrl+F` (Windows) o `Cmd+F` (Mac)
3. Buscar por nombre o ruta del endpoint

---

## 📞 Soporte

Para reportar problemas o sugerir mejoras en la documentación de la API, contactar al equipo de desarrollo.
