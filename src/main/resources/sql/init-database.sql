-- Script de inicialización de la base de datos
-- Ejecutar manualmente antes de arrancar la aplicación por primera vez

-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS pollo_empoderado_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE pollo_empoderado_db;

-- Las tablas se crearán automáticamente por JPA/Hibernate con ddl-auto: update
-- Este script solo asegura que la base de datos existe

-- Verificar que la base de datos fue creada
SELECT 'Base de datos pollo_empoderado_db creada exitosamente' as mensaje;