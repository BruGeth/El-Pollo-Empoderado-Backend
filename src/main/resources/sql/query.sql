drop database pollo_empoderado_db;

CREATE DATABASE IF NOT EXISTS pollo_empoderado_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- ==========================================
-- INSERTAR CIUDADES
-- ==========================================
INSERT INTO cities (nombre) VALUES 
('Lima'),
('Callao'),
('Trujillo'),
('Chiclayo'),
('Piura'),
('Ica'),
('Huacho');

-- ==========================================
-- INSERTAR DISTRITOS
-- ==========================================
-- Distritos de Lima
INSERT INTO districts (nombre, ciudad_id) VALUES 
('Comas', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
('Ate', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
('San Martín de Porres', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
('Miraflores', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
('Surco', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
('Chorrillos', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
('La Molina', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
('Santa Anita', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
('Independencia', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
('San Miguel', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
('Lurín', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
('Cercado de Lima', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima'));

-- Distritos de Callao
INSERT INTO districts (nombre, ciudad_id) VALUES 
('Callao (Saenz Peña)', (SELECT ciudad_id FROM cities WHERE nombre = 'Callao')),
('Callao (Elmer Faucett)', (SELECT ciudad_id FROM cities WHERE nombre = 'Callao'));

-- Otras ciudades
INSERT INTO districts (nombre, ciudad_id) VALUES 
('Trujillo', (SELECT ciudad_id FROM cities WHERE nombre = 'Trujillo')),
('Chiclayo', (SELECT ciudad_id FROM cities WHERE nombre = 'Chiclayo')),
('Piura', (SELECT ciudad_id FROM cities WHERE nombre = 'Piura')),
('Ica', (SELECT ciudad_id FROM cities WHERE nombre = 'Ica')),
('Huacho', (SELECT ciudad_id FROM cities WHERE nombre = 'Huacho'));

-- ==========================================
-- INSERTAR USUARIOS
-- ==========================================
INSERT INTO users (first_name, last_name, email, password, dni, birth_date, address, telefono, reference_home, ciudad_id, distrito_id, created_at) 
VALUES 
('Juan', 'Pérez', 'juan.perez@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J0lR8u7CQSf.jxlE4M4Y8c9FmXqI8a', '12345678', '1990-05-15', 'Av. Lima 123, San Isidro', '987654321', 'Cerca al parque principal', 1, 4, NOW()),
('María', 'García', 'maria.garcia@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J0lR8u7CQSf.jxlE4M4Y8c9FmXqI8a', '87654321', '1995-08-20', 'Jr. Arequipa 456, Miraflores', '912345678', 'Frente a la iglesia', 1, 4, NOW()),
('Carlos', 'López', 'carlos.lopez@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J0lR8u7CQSf.jxlE4M4Y8c9FmXqI8a', '11223344', '1988-12-10', 'Calle Los Pinos 789, Surco', '998877665', 'Al lado del colegio', 1, 5, NOW());

-- ==========================================
-- ASIGNAR ROLES A USUARIOS
-- ==========================================
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.email = 'juan.perez@test.com' AND r.name = 'ROLE_USER';

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.email = 'maria.garcia@test.com' AND r.name = 'ROLE_USER';

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.email = 'carlos.lopez@test.com' AND r.name = 'ROLE_USER';

-- ==========================================
-- INSERTAR LOCALES/TIENDAS
-- ==========================================
INSERT INTO locals (nombre, distrito_id, direccion, telefono, horario, imagen_url, maps_url, created_at) VALUES
('El Pollo Empoderado - Miraflores', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Miraflores'), 
  'Av. Larco 1234, Miraflores', 
  '01-4567890', 
  'Lun-Dom: 11:00 AM - 11:00 PM', 
  'https://example.com/local-miraflores.jpg', 
  'https://maps.google.com/?q=Miraflores+Lima', 
  NOW()),

('El Pollo Empoderado - Surco', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Surco'), 
  'Av. Primavera 5678, Surco', 
  '01-7654321', 
  'Lun-Dom: 12:00 PM - 10:00 PM', 
  'https://example.com/local-surco.jpg', 
  'https://maps.google.com/?q=Surco+Lima', 
  NOW()),

('El Pollo Empoderado - San Miguel', 
  (SELECT distrito_id FROM districts WHERE nombre = 'San Miguel'), 
  'Av. La Marina 9012, San Miguel', 
  '01-3456789', 
  'Lun-Sab: 11:30 AM - 10:30 PM', 
  'https://example.com/local-sanmiguel.jpg', 
  'https://maps.google.com/?q=San+Miguel+Lima', 
  NOW());

-- ==========================================
-- INSERTAR CATEGORÍAS
-- ==========================================
INSERT INTO category (name, description) VALUES 
('Pollos a la Brasa', 'Deliciosos pollos a la brasa con guarniciones tradicionales'),
('Parrillas', 'Carnes a la parrilla y anticuchos'),
('Entradas', 'Aperitivos y entradas para compartir'),
('Bebidas', 'Bebidas frías y calientes'),
('Postres', 'Dulces tradicionales y postres caseros'),
('Combos Familiares', 'Combos económicos para toda la familia');

-- ==========================================
-- INSERTAR PLATOS/DISHES
-- ==========================================

-- Pollos a la Brasa
INSERT INTO dish (name, description, price, image_url, category_id) VALUES
('Pollo Entero a la Brasa', 'Pollo entero marinado con especias secretas, acompañado de papas fritas y ensalada', 55.00, 'https://images.unsplash.com/photo-1598103442097-8b74394b95c6', 
  (SELECT id FROM category WHERE name = 'Pollos a la Brasa')),
('1/2 Pollo a la Brasa', 'Medio pollo con papas fritas y ensalada fresca', 30.00, 'https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58', 
  (SELECT id FROM category WHERE name = 'Pollos a la Brasa')),
('1/4 Pollo a la Brasa', 'Cuarto de pollo con papas y ensalada', 18.00, 'https://images.unsplash.com/photo-1594221708779-94832f4320d1', 
  (SELECT id FROM category WHERE name = 'Pollos a la Brasa'));

-- Parrillas
INSERT INTO dish (name, description, price, image_url, category_id) VALUES
('Anticuchos de Corazón', 'Brochetas de corazón marinadas con ají panca, incluye papa y choclo', 25.00, 'https://images.unsplash.com/photo-1529042410759-befb1204b468', 
  (SELECT id FROM category WHERE name = 'Parrillas')),
('Parrilla Mixta', 'Chorizo, costilla, pollo y anticuchos con guarniciones', 45.00, 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1', 
  (SELECT id FROM category WHERE name = 'Parrillas')),
('Costillas BBQ', 'Costillas de cerdo con salsa barbacoa casera', 38.00, 'https://images.unsplash.com/photo-1544025162-d76694265947', 
  (SELECT id FROM category WHERE name = 'Parrillas'));

-- Entradas
INSERT INTO dish (name, description, price, image_url, category_id) VALUES
('Tequeños', '6 unidades de tequeños de queso con salsas', 12.00, 'https://images.unsplash.com/photo-1601050690597-df0568f70950', 
  (SELECT id FROM category WHERE name = 'Entradas')),
('Causa Limeña', 'Causa tradicional rellena de pollo o atún', 15.00, 'https://images.unsplash.com/photo-1626200419199-391ae4be7a41', 
  (SELECT id FROM category WHERE name = 'Entradas')),
('Alitas Picantes', '8 alitas de pollo fritas con salsa picante', 22.00, 'https://images.unsplash.com/photo-1527477396000-e27163b481c2', 
  (SELECT id FROM category WHERE name = 'Entradas')),
('Ensalada Mixta', 'Ensalada fresca de lechuga, tomate, cebolla y palta', 10.00, 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd', 
  (SELECT id FROM category WHERE name = 'Entradas'));

-- Bebidas
INSERT INTO dish (name, description, price, image_url, category_id) VALUES
('Inca Kola 1.5L', 'Gaseosa Inca Kola familiar', 8.00, 'https://images.unsplash.com/photo-1581006852262-e4307cf6283a', 
  (SELECT id FROM category WHERE name = 'Bebidas')),
('Chicha Morada 1L', 'Chicha morada casera con frutas', 10.00, 'https://images.unsplash.com/photo-1546173159-315724a31696', 
  (SELECT id FROM category WHERE name = 'Bebidas')),
('Limonada Frozen', 'Limonada helada con hierbabuena', 7.00, 'https://images.unsplash.com/photo-1523677011781-c91d1bbe2f0f', 
  (SELECT id FROM category WHERE name = 'Bebidas')),
('Coca Cola 1.5L', 'Gaseosa Coca Cola familiar', 8.00, 'https://images.unsplash.com/photo-1554866585-cd94860890b7', 
  (SELECT id FROM category WHERE name = 'Bebidas'));

-- Postres
INSERT INTO dish (name, description, price, image_url, category_id) VALUES
('Suspiro Limeño', 'Postre tradicional peruano con manjar y merengue', 12.00, 'https://images.unsplash.com/photo-1488477181946-6428a0291777', 
  (SELECT id FROM category WHERE name = 'Postres')),
('Mazamorra Morada', 'Mazamorra tradicional con arroz con leche', 8.00, 'https://images.unsplash.com/photo-1563805042-7684c019e1cb', 
  (SELECT id FROM category WHERE name = 'Postres')),
('Picarones', '4 picarones con miel de chancaca', 10.00, 'https://images.unsplash.com/photo-1509440159596-0249088772ff', 
  (SELECT id FROM category WHERE name = 'Postres'));

-- Combos Familiares
INSERT INTO dish (name, description, price, image_url, category_id) VALUES
('Combo Familiar 1', '1 Pollo entero + papas grandes + ensalada + gaseosa 1.5L', 65.00, 'https://images.unsplash.com/photo-1598103442097-8b74394b95c6', 
  (SELECT id FROM category WHERE name = 'Combos Familiares')),
('Combo Familiar 2', '1.5 Pollos + papas extra + ensalada + 2 gaseosas 1.5L', 95.00, 'https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58', 
  (SELECT id FROM category WHERE name = 'Combos Familiares')),
('Combo Personal', '1/4 Pollo + papas + ensalada + gaseosa 500ml', 22.00, 'https://images.unsplash.com/photo-1594221708779-94832f4320d1', 
  (SELECT id FROM category WHERE name = 'Combos Familiares'));

-- ==========================================
-- CONSULTAS DE VERIFICACIÓN
-- ==========================================

-- Ver todas las ciudades
-- SELECT * FROM cities;

-- Ver todos los distritos con sus ciudades
-- SELECT d.distrito_id, d.nombre as distrito, c.nombre as ciudad 
-- FROM districts d 
-- JOIN cities c ON d.ciudad_id = c.ciudad_id 
-- ORDER BY c.nombre, d.nombre;

-- Ver todos los locales con ubicación completa
-- SELECT l.id, l.nombre, d.nombre as distrito, c.nombre as ciudad, l.direccion, l.telefono 
-- FROM locals l 
-- JOIN districts d ON l.distrito_id = d.distrito_id 
-- JOIN cities c ON d.ciudad_id = c.ciudad_id;

-- Ver usuarios con su ubicación
-- SELECT u.id, u.first_name, u.last_name, u.email, u.telefono, u.reference_home,
--        c.nombre as ciudad, d.nombre as distrito 
-- FROM users u 
-- LEFT JOIN cities c ON u.ciudad_id = c.ciudad_id 
-- LEFT JOIN districts d ON u.distrito_id = d.distrito_id;

-- Ver todas las categorías
-- SELECT * FROM category;

-- Ver todos los platos
-- SELECT * FROM dish;

-- Ver platos por categoría
-- SELECT d.name, d.price, c.name as category 
-- FROM dish d 
-- JOIN category c ON d.category_id = c.id 
-- ORDER BY c.name, d.name;

-- Ver usuarios junto con sus roles
-- SELECT u.id, u.first_name, u.last_name, u.email, r.name AS role
-- FROM users u
-- JOIN user_roles ur ON u.id = ur.user_id
-- JOIN roles r ON ur.role_id = r.id
-- ORDER BY u.id;

-- ==========================================
-- CREDENCIALES DE PRUEBA
-- ==========================================
-- 
-- Usuario Administrador (Ya existente en el sistema):
-- Email: admin@empoderado.com
-- Password: admin123
--
-- Usuarios de Prueba:
-- Email: juan.perez@test.com
-- Password: password123
--
-- Email: maria.garcia@test.com
-- Password: password123
--
-- Email: carlos.lopez@test.com
-- Password: password123
--
-- ==========================================
-- ESTRUCTURA DE DATOS
-- ==========================================
-- Ciudades: 7
-- Distritos: 19 (Lima: 12, Callao: 2, Otras: 5)
-- Locales: 3 (Miraflores, Surco, San Miguel)
-- Usuarios: 3 + 1 Admin
-- Categorías: 6
-- Platos: 20
