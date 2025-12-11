-- ==========================================
-- SCRIPT DE MIGRACIÓN DE DATOS
-- De: users (address, city, district antiguos)
-- A: addresses (nueva tabla)
-- ==========================================

-- IMPORTANTE: Ejecutar DESPUÉS de verificar que el sistema funciona correctamente
-- Este script migra las direcciones antiguas almacenadas en la tabla users
-- a la nueva tabla addresses

-- ==========================================
-- PASO 1: VERIFICAR DATOS EXISTENTES
-- ==========================================

-- Ver usuarios con datos de dirección en campos antiguos
SELECT 
    id,
    email,
    CONCAT(first_name, ' ', last_name) as nombre_completo,
    address as direccion_antigua,
    ciudad_id,
    distrito_id,
    telefono,
    reference_home
FROM users 
WHERE ciudad_id IS NOT NULL 
  AND distrito_id IS NOT NULL
ORDER BY id;

-- Ver cuántos usuarios tienen direcciones antiguas
SELECT COUNT(*) as usuarios_con_direccion_antigua
FROM users 
WHERE ciudad_id IS NOT NULL AND distrito_id IS NOT NULL;

-- Ver cuántas direcciones ya existen en la nueva tabla
SELECT COUNT(*) as direcciones_nuevas, COUNT(DISTINCT user_id) as usuarios_con_direcciones
FROM addresses;

-- ==========================================
-- PASO 2: MIGRAR DATOS
-- ==========================================

-- Migrar direcciones antiguas de users a addresses
-- Solo para usuarios que NO tienen direcciones en la nueva tabla
INSERT INTO addresses (
    user_id,
    ciudad_id,
    distrito_id,
    street,
    number,
    reference_home,
    telefono,
    label,
    is_default,
    created_at,
    updated_at
)
SELECT 
    u.id,
    u.ciudad_id,
    u.distrito_id,
    COALESCE(u.address, 'Dirección migrada del sistema antiguo'),
    NULL,  -- number (no existía en el sistema antiguo)
    u.reference_home,
    COALESCE(u.telefono, '000000000'),  -- Teléfono por defecto si no tiene
    'Principal',  -- Etiqueta por defecto
    TRUE,  -- Marcar como dirección predeterminada
    NOW(),
    NOW()
FROM users u
WHERE u.ciudad_id IS NOT NULL 
  AND u.distrito_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM addresses a WHERE a.user_id = u.id
  );

-- ==========================================
-- PASO 3: VERIFICAR MIGRACIÓN
-- ==========================================

-- Ver direcciones migradas
SELECT 
    a.id,
    u.email,
    CONCAT(u.first_name, ' ', u.last_name) as usuario,
    c.nombre as ciudad,
    d.nombre as distrito,
    a.street,
    a.telefono as telefono,
    a.label,
    a.is_default,
    a.created_at
FROM addresses a
INNER JOIN users u ON a.user_id = u.id
INNER JOIN cities c ON a.ciudad_id = c.ciudad_id
INNER JOIN districts d ON a.distrito_id = d.distrito_id
ORDER BY a.created_at DESC;

-- Verificar que todos los usuarios con datos antiguos ahora tienen dirección nueva
SELECT 
    u.id,
    u.email,
    CASE 
        WHEN EXISTS (SELECT 1 FROM addresses WHERE user_id = u.id) THEN 'SÍ'
        ELSE 'NO'
    END as tiene_direccion_nueva,
    u.ciudad_id,
    u.distrito_id
FROM users u
WHERE u.ciudad_id IS NOT NULL AND u.distrito_id IS NOT NULL
ORDER BY tiene_direccion_nueva, u.id;

-- Contar usuarios sin dirección migrada (debería ser 0)
SELECT COUNT(*) as usuarios_sin_migrar
FROM users u
WHERE u.ciudad_id IS NOT NULL 
  AND u.distrito_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM addresses WHERE user_id = u.id);

-- ==========================================
-- PASO 4: RESPALDAR DATOS ANTES DE ELIMINAR
-- ==========================================

-- Crear tabla temporal con backup de campos antiguos
CREATE TABLE IF NOT EXISTS users_backup_address (
    user_id BIGINT,
    address_old VARCHAR(255),
    telefono_old VARCHAR(20),
    reference_home_old VARCHAR(255),
    ciudad_id_old BIGINT,
    distrito_id_old BIGINT,
    backup_date DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Insertar backup
INSERT INTO users_backup_address (
    user_id, 
    address_old, 
    telefono_old, 
    reference_home_old, 
    ciudad_id_old, 
    distrito_id_old
)
SELECT 
    id,
    address,
    telefono,
    reference_home,
    ciudad_id,
    distrito_id
FROM users
WHERE ciudad_id IS NOT NULL OR distrito_id IS NOT NULL;

-- Verificar backup
SELECT * FROM users_backup_address ORDER BY user_id;

-- ==========================================
-- PASO 5: ELIMINAR COLUMNAS OBSOLETAS (OPCIONAL)
-- ==========================================

-- ⚠️ ADVERTENCIA: Este paso es IRREVERSIBLE
-- Ejecutar solo después de:
-- 1. Verificar que la migración fue exitosa
-- 2. Probar el sistema completo con las nuevas direcciones
-- 3. Tener un backup completo de la base de datos
-- 4. Confirmar que no hay referencias a estos campos en el código

-- DESCOMENTAR LAS SIGUIENTES LÍNEAS SOLO SI ESTÁS SEGURO:

/*
ALTER TABLE users 
DROP COLUMN address,
DROP COLUMN telefono,
DROP COLUMN reference_home,
DROP COLUMN ciudad_id,
DROP COLUMN distrito_id;
*/

-- ==========================================
-- PASO 6: VERIFICACIÓN FINAL
-- ==========================================

-- Estructura actual de la tabla users (debe mostrar que las columnas fueron eliminadas)
-- DESCRIBE users;

-- Verificar que todos los pedidos tienen direcciones válidas
SELECT 
    o.id,
    o.order_number,
    o.address_id,
    a.street,
    CONCAT(u.first_name, ' ', u.last_name) as cliente
FROM orders o
INNER JOIN addresses a ON o.address_id = a.id
INNER JOIN users u ON o.user_id = u.id
ORDER BY o.created_at DESC
LIMIT 10;

-- ==========================================
-- ROLLBACK (En caso de problemas)
-- ==========================================

-- Si algo sale mal y necesitas restaurar los datos:
/*
-- 1. Restaurar columnas (si fueron eliminadas)
ALTER TABLE users 
ADD COLUMN address VARCHAR(255),
ADD COLUMN telefono VARCHAR(20),
ADD COLUMN reference_home VARCHAR(255),
ADD COLUMN ciudad_id BIGINT,
ADD COLUMN distrito_id BIGINT;

-- 2. Restaurar datos desde el backup
UPDATE users u
INNER JOIN users_backup_address b ON u.id = b.user_id
SET 
    u.address = b.address_old,
    u.telefono = b.telefono_old,
    u.reference_home = b.reference_home_old,
    u.ciudad_id = b.ciudad_id_old,
    u.distrito_id = b.distrito_id_old;

-- 3. Restaurar foreign keys
ALTER TABLE users 
ADD FOREIGN KEY (ciudad_id) REFERENCES cities(ciudad_id),
ADD FOREIGN KEY (distrito_id) REFERENCES districts(distrito_id);
*/

-- ==========================================
-- NOTAS IMPORTANTES
-- ==========================================

/*
1. ANTES DE EJECUTAR:
   - Hacer backup completo de la base de datos
   - Ejecutar en ambiente de pruebas primero
   - Verificar que no hay pedidos activos en proceso

2. ORDEN DE EJECUCIÓN:
   - Ejecutar PASO 1 (verificación)
   - Ejecutar PASO 2 (migración)
   - Ejecutar PASO 3 (verificar migración)
   - Ejecutar PASO 4 (backup)
   - Probar el sistema completo (varios días)
   - Solo entonces ejecutar PASO 5 (eliminar columnas)

3. COMPATIBILIDAD:
   - Mientras las columnas existan, el sistema antiguo y nuevo pueden coexistir
   - Las nuevas direcciones creadas desde el API van solo a la tabla addresses
   - Los campos antiguos quedan como @Deprecated en el código

4. DESPUÉS DE LA MIGRACIÓN:
   - Eliminar anotaciones @Deprecated del código User.java
   - Actualizar tests unitarios
   - Actualizar documentación
   - Notificar al equipo frontend del cambio

5. MONITOREO POST-MIGRACIÓN:
   - Verificar que los pedidos nuevos usan addresses correctamente
   - Verificar que los emails llegan correctamente
   - Monitorear logs por errores relacionados con direcciones
   - Verificar que el dashboard de usuarios funciona correctamente
*/
