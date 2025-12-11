-- ==========================================
-- SCRIPT DE CREACIÓN DE TABLAS PARA SISTEMA DE PEDIDOS
-- El Pollo Empoderado - Sistema de Carrito y Ventas
-- ==========================================

-- Tabla de direcciones de envío
-- IMPORTANTE: Usa ciudad_id y distrito_id para coincidir con las tablas cities y districts existentes
CREATE TABLE IF NOT EXISTS addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    ciudad_id BIGINT NOT NULL COMMENT 'FK a cities.ciudad_id',
    distrito_id BIGINT NOT NULL COMMENT 'FK a districts.distrito_id',
    street VARCHAR(255) NOT NULL COMMENT 'Dirección exacta (calle, avenida, jirón)',
    number VARCHAR(50) COMMENT 'Número de casa/departamento (opcional)',
    reference_home VARCHAR(255) COMMENT 'Referencia de ubicación (misma que en users)',
    telefono VARCHAR(20) NOT NULL COMMENT 'Teléfono de contacto (mismo que en users)',
    label VARCHAR(50) COMMENT 'Etiqueta: Casa, Trabajo, etc.',
    is_default BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Dirección predeterminada',
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (ciudad_id) REFERENCES cities(ciudad_id),
    FOREIGN KEY (distrito_id) REFERENCES districts(distrito_id),
    INDEX idx_user_id (user_id),
    INDEX idx_is_default (is_default),
    INDEX idx_ciudad_id (ciudad_id),
    INDEX idx_distrito_id (distrito_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de pedidos
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    address_id BIGINT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    delivery_fee DECIMAL(10,2) NOT NULL DEFAULT 5.00,
    total DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(20) NOT NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_transaction_id VARCHAR(100),
    notes VARCHAR(500),
    receipt_email VARCHAR(150),
    estimated_delivery_time DATETIME(6),
    delivered_at DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (address_id) REFERENCES addresses(id),
    INDEX idx_user_id (user_id),
    INDEX idx_order_number (order_number),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de items del pedido
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    dish_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (dish_id) REFERENCES dish(id),
    INDEX idx_order_id (order_id),
    INDEX idx_dish_id (dish_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- COMENTARIOS SOBRE LAS TABLAS
-- ==========================================

-- ADDRESSES: 
-- Permite a los usuarios tener múltiples direcciones de envío
-- El campo is_default indica cuál es la dirección predeterminada
-- Relación: Un usuario puede tener muchas direcciones (1:N)

-- ORDERS:
-- Almacena la información principal del pedido
-- order_number: Número único generado automáticamente para identificar el pedido
-- status: Estados posibles (PENDING, CONFIRMED, PREPARING, READY, ON_DELIVERY, DELIVERED, CANCELLED)
-- payment_status: Estados de pago (PENDING, PROCESSING, APPROVED, REJECTED, REFUNDED)
-- payment_method: Métodos de pago (MERCADO_PAGO, CASH, CARD)

-- ORDER_ITEMS:
-- Detalle de los productos en cada pedido
-- Guarda el precio al momento de la compra (unit_price) para evitar problemas si el precio cambia después
-- Relación: Un pedido puede tener muchos items (1:N)

-- ==========================================
-- ÍNDICES PARA OPTIMIZACIÓN
-- ==========================================

-- Los índices creados mejoran el rendimiento de:
-- 1. Búsqueda de direcciones por usuario
-- 2. Búsqueda de pedidos por usuario, estado y fecha
-- 3. Búsqueda de items por pedido
-- 4. Búsqueda de pedidos por número de orden
