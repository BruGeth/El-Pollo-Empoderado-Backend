drop database pollo_empoderado_db;

CREATE DATABASE IF NOT EXISTS pollo_empoderado_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
-- ==========================================
-- Dataloader (No subir)
-- ==========================================
-- INSERTAR CIUDADES
-- ==========================================
-- INSERT INTO cities (nombre) VALUES 
-- ('Lima'),
-- ('Callao'),
-- ('Trujillo'),
-- ('Chiclayo'),
-- ('Piura'),
-- ('Ica'),
-- ('Huacho');

-- ==========================================
-- INSERTAR DISTRITOS
-- ==========================================
-- Distritos de Lima
-- INSERT INTO districts (nombre, ciudad_id) VALUES 
-- ('Comas', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
-- ('Ate', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
-- ('San Martín de Porres', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
-- ('Miraflores', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
-- ('Surco', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
-- ('Chorrillos', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
-- ('La Molina', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
-- ('Santa Anita', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
-- ('Independencia', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
-- ('San Miguel', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
-- ('Lurín', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima')),
-- ('Cercado de Lima', (SELECT ciudad_id FROM cities WHERE nombre = 'Lima'));

-- Distritos de Callao
-- INSERT INTO districts (nombre, ciudad_id) VALUES 
-- ('Callao (Saenz Peña)', (SELECT ciudad_id FROM cities WHERE nombre = 'Callao')),
-- ('Callao (Elmer Faucett)', (SELECT ciudad_id FROM cities WHERE nombre = 'Callao'));

-- Otras ciudades
-- INSERT INTO districts (nombre, ciudad_id) VALUES 
-- ('Trujillo', (SELECT ciudad_id FROM cities WHERE nombre = 'Trujillo')),
-- ('Chiclayo', (SELECT ciudad_id FROM cities WHERE nombre = 'Chiclayo')),
-- ('Piura', (SELECT ciudad_id FROM cities WHERE nombre = 'Piura')),
-- ('Ica', (SELECT ciudad_id FROM cities WHERE nombre = 'Ica')),
-- ('Huacho', (SELECT ciudad_id FROM cities WHERE nombre = 'Huacho'));

-- ==========================================
-- Subir
-- ==========================================
-- INSERTAR USUARIOS
-- ==========================================
-- Nota: La tabla users ya NO tiene los campos: address, telefono, reference_home, ciudad_id, distrito_id
-- Esos campos ahora están en la tabla 'addresses'
INSERT INTO users (first_name, last_name, email, password, dni, birth_date, created_at) 
VALUES 
('Juan', 'Pérez', 'juan.perez@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J0lR8u7CQSf.jxlE4M4Y8c9FmXqI8a', '12345678', '1990-05-15', NOW()),
('María', 'García', 'maria.garcia@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J0lR8u7CQSf.jxlE4M4Y8c9FmXqI8a', '87654321', '1995-08-20', NOW()),
('Carlos', 'López', 'carlos.lopez@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J0lR8u7CQSf.jxlE4M4Y8c9FmXqI8a', '11223344', '1988-12-10', NOW());

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
-- INSERTAR DIRECCIONES DE LOS USUARIOS
-- ==========================================
-- Nota: Ahora las direcciones están en una tabla separada
INSERT INTO addresses (user_id, ciudad_id, distrito_id, street, number, telefono, reference_home, label, is_default, created_at, updated_at) 
VALUES 
-- Dirección de Juan Pérez
((SELECT id FROM users WHERE email = 'juan.perez@test.com'), 
 (SELECT ciudad_id FROM cities WHERE nombre = 'Lima'), 
 (SELECT distrito_id FROM districts WHERE nombre = 'Miraflores'), 
 'Av. Lima 123', 'San Isidro', '987654321', 'Cerca al parque principal', 'Casa', TRUE, NOW(), NOW()),

-- Dirección de María García
((SELECT id FROM users WHERE email = 'maria.garcia@test.com'), 
 (SELECT ciudad_id FROM cities WHERE nombre = 'Lima'), 
 (SELECT distrito_id FROM districts WHERE nombre = 'Miraflores'), 
 'Jr. Arequipa 456', '', '912345678', 'Frente a la iglesia', 'Casa', TRUE, NOW(), NOW()),

-- Dirección de Carlos López
((SELECT id FROM users WHERE email = 'carlos.lopez@test.com'), 
 (SELECT ciudad_id FROM cities WHERE nombre = 'Lima'), 
 (SELECT distrito_id FROM districts WHERE nombre = 'Surco'), 
 'Calle Los Pinos 789', '', '998877665', 'Al lado del colegio', 'Casa', TRUE, NOW(), NOW());

-- ==========================================
-- INSERTAR LOCALES/TIENDAS
-- ==========================================
-- Total: 25 locales en 19 distritos/ciudades de Perú
INSERT INTO locals (nombre, distrito_id, direccion, telefono, horario, imagen_url, maps_url, created_at) VALUES

-- LIMA (12 distritos) - 15 locales
('El Pollo Empoderado - Comas Centro', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Comas'), 
  'Av. Túpac Amaru 850, Comas', '01-5381200', 
  'Lun-Dom: 10:00 AM - 11:00 PM', 
  'https://example.com/local-comas.jpg', 
  'https://maps.google.com/?q=Comas+Lima', NOW()),

('El Pollo Empoderado - Comas Retablo', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Comas'), 
  'Av. Retablo 2150, Comas', '01-5381201', 
  'Lun-Dom: 11:00 AM - 10:30 PM', 
  'https://example.com/local-comas-2.jpg', 
  'https://maps.google.com/?q=Retablo+Comas', NOW()),

('El Pollo Empoderado - Ate Vitarte', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Ate'), 
  'Av. Nicolás Ayllón 4500, Ate', '01-3514000', 
  'Lun-Dom: 11:00 AM - 10:00 PM', 
  'https://example.com/local-ate.jpg', 
  'https://maps.google.com/?q=Ate+Lima', NOW()),

('El Pollo Empoderado - SMP Norte', 
  (SELECT distrito_id FROM districts WHERE nombre = 'San Martín de Porres'), 
  'Av. Perú 2800, San Martín de Porres', '01-5674800', 
  'Lun-Dom: 10:00 AM - 11:00 PM', 
  'https://example.com/local-smp.jpg', 
  'https://maps.google.com/?q=San+Martin+Porres', NOW()),

('El Pollo Empoderado - SMP Universitaria', 
  (SELECT distrito_id FROM districts WHERE nombre = 'San Martín de Porres'), 
  'Av. Universitaria 1950, San Martín de Porres', '01-5674801', 
  'Lun-Dom: 11:00 AM - 10:30 PM', 
  'https://example.com/local-smp-2.jpg', 
  'https://maps.google.com/?q=Universitaria+SMP', NOW()),

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

('El Pollo Empoderado - Surco Chacarilla', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Surco'), 
  'Av. Primavera 1580, Chacarilla', '01-4494201', 
  'Lun-Dom: 11:30 AM - 11:00 PM', 
  'https://example.com/local-surco-2.jpg', 
  'https://maps.google.com/?q=Chacarilla+Surco', NOW()),

('El Pollo Empoderado - Chorrillos', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Chorrillos'), 
  'Av. Huaylas 1850, Chorrillos', '01-2514700', 
  'Lun-Dom: 10:30 AM - 10:30 PM', 
  'https://example.com/local-chorrillos.jpg', 
  'https://maps.google.com/?q=Chorrillos+Lima', NOW()),

('El Pollo Empoderado - La Molina', 
  (SELECT distrito_id FROM districts WHERE nombre = 'La Molina'), 
  'Av. Javier Prado Este 4950, La Molina', '01-3654100', 
  'Lun-Dom: 11:00 AM - 11:00 PM', 
  'https://example.com/local-lamolina.jpg', 
  'https://maps.google.com/?q=La+Molina+Lima', NOW()),

('El Pollo Empoderado - Santa Anita', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Santa Anita'), 
  'Av. Carretera Central Km 10.5, Santa Anita', '01-3624900', 
  'Lun-Dom: 10:00 AM - 10:00 PM', 
  'https://example.com/local-santaanita.jpg', 
  'https://maps.google.com/?q=Santa+Anita+Lima', NOW()),

('El Pollo Empoderado - Independencia', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Independencia'), 
  'Av. Túpac Amaru 3200, Independencia', '01-5321400', 
  'Lun-Dom: 10:30 AM - 10:30 PM', 
  'https://example.com/local-independencia.jpg', 
  'https://maps.google.com/?q=Independencia+Lima', NOW()),

('El Pollo Empoderado - San Miguel', 
  (SELECT distrito_id FROM districts WHERE nombre = 'San Miguel'), 
  'Av. La Marina 9012, San Miguel', 
  '01-3456789', 
  'Lun-Sab: 11:30 AM - 10:30 PM', 
  'https://example.com/local-sanmiguel.jpg', 
  'https://maps.google.com/?q=San+Miguel+Lima', 
  NOW()),

('El Pollo Empoderado - Lurín', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Lurín'), 
  'Av. San Pedro 450, Lurín', '01-4301500', 
  'Lun-Dom: 11:00 AM - 10:00 PM', 
  'https://example.com/local-lurin.jpg', 
  'https://maps.google.com/?q=Lurin+Lima', NOW()),

('El Pollo Empoderado - Centro de Lima', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Cercado de Lima'), 
  'Jr. Ucayali 380, Cercado de Lima', '01-4276800', 
  'Lun-Sab: 10:00 AM - 9:00 PM', 
  'https://example.com/local-cercado.jpg', 
  'https://maps.google.com/?q=Centro+Lima', NOW()),

-- CALLAO (2 locales)
('El Pollo Empoderado - Callao Sáenz Peña', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Callao (Saenz Peña)'), 
  'Av. Sáenz Peña 250, Callao', '01-4201000', 
  'Lun-Dom: 10:30 AM - 10:30 PM', 
  'https://example.com/local-callao-1.jpg', 
  'https://maps.google.com/?q=Callao+Peru', NOW()),

('El Pollo Empoderado - Callao Aeropuerto', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Callao (Elmer Faucett)'), 
  'Av. Elmer Faucett 3100, Callao', '01-4201001', 
  'Lun-Dom: 8:00 AM - 11:00 PM', 
  'https://example.com/local-callao-2.jpg', 
  'https://maps.google.com/?q=Aeropuerto+Callao', NOW()),

-- OTRAS CIUDADES (6 locales)
('El Pollo Empoderado - Trujillo Centro', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Trujillo'), 
  'Av. España 1200, Trujillo', '044-481000', 
  'Lun-Dom: 11:00 AM - 10:00 PM', 
  'https://example.com/local-trujillo.jpg', 
  'https://maps.google.com/?q=Trujillo+Peru', NOW()),

('El Pollo Empoderado - Trujillo Mall', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Trujillo'), 
  'Av. América Oeste 750, Trujillo', '044-481001', 
  'Lun-Dom: 11:00 AM - 10:30 PM', 
  'https://example.com/local-trujillo-2.jpg', 
  'https://maps.google.com/?q=Mall+Aventura+Trujillo', NOW()),

('El Pollo Empoderado - Chiclayo', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Chiclayo'), 
  'Av. Balta 605, Chiclayo', '074-201000', 
  'Lun-Dom: 11:00 AM - 10:00 PM', 
  'https://example.com/local-chiclayo.jpg', 
  'https://maps.google.com/?q=Chiclayo+Peru', NOW()),

('El Pollo Empoderado - Piura Centro', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Piura'), 
  'Av. Grau 450, Piura', '073-351000', 
  'Lun-Dom: 11:00 AM - 10:00 PM', 
  'https://example.com/local-piura.jpg', 
  'https://maps.google.com/?q=Piura+Peru', NOW()),

('El Pollo Empoderado - Piura Open Plaza', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Piura'), 
  'Carretera Piura-Sullana Km 5, Piura', '073-351001', 
  'Lun-Dom: 11:00 AM - 10:30 PM', 
  'https://example.com/local-piura-2.jpg', 
  'https://maps.google.com/?q=Open+Plaza+Piura', NOW()),

('El Pollo Empoderado - Ica', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Ica'), 
  'Av. Grau 280, Ica', '056-231000', 
  'Lun-Dom: 11:00 AM - 10:00 PM', 
  'https://example.com/local-ica.jpg', 
  'https://maps.google.com/?q=Ica+Peru', NOW()),

('El Pollo Empoderado - Huacho', 
  (SELECT distrito_id FROM districts WHERE nombre = 'Huacho'), 
  'Jr. 28 de Julio 350, Huacho', '01-2321000', 
  'Lun-Dom: 11:00 AM - 9:30 PM', 
  'https://example.com/local-huacho.jpg', 
  'https://maps.google.com/?q=Huacho+Peru', NOW());

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
-- Nota: original_price es opcional, se usa para mostrar descuentos (precio tachado)
-- Formato: (name, description, price, original_price, image_url, category_id)

-- Pollos a la Brasa
INSERT INTO dish (name, description, price, original_price, image_url, category_id) VALUES
('Pollo Entero a la Brasa', 'Pollo entero marinado con especias secretas, acompañado de papas fritas y ensalada', 55.00, NULL, 'https://images.unsplash.com/photo-1598103442097-8b74394b95c6', 
  (SELECT id FROM category WHERE name = 'Pollos a la Brasa')),
('1/2 Pollo a la Brasa', 'Medio pollo con papas fritas y ensalada fresca', 30.00, NULL, 'https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58', 
  (SELECT id FROM category WHERE name = 'Pollos a la Brasa')),
('1/4 Pollo a la Brasa', 'Cuarto de pollo con papas y ensalada', 18.00, NULL, 'https://images.unsplash.com/photo-1594221708779-94832f4320d1', 
  (SELECT id FROM category WHERE name = 'Pollos a la Brasa'));

-- Parrillas
INSERT INTO dish (name, description, price, original_price, image_url, category_id) VALUES
('Anticuchos de Corazón', 'Brochetas de corazón marinadas con ají panca, incluye papa y choclo', 25.00, NULL, 'https://images.unsplash.com/photo-1529042410759-befb1204b468', 
  (SELECT id FROM category WHERE name = 'Parrillas')),
('Parrilla Mixta', 'Chorizo, costilla, pollo y anticuchos con guarniciones', 45.00, NULL, 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1', 
  (SELECT id FROM category WHERE name = 'Parrillas')),
('Costillas BBQ', 'Costillas de cerdo con salsa barbacoa casera', 38.00, 42.00, 'https://images.unsplash.com/photo-1544025162-d76694265947', 
  (SELECT id FROM category WHERE name = 'Parrillas'));  -- ¡EN OFERTA! S/ 42.00 → S/ 38.00

-- Entradas
INSERT INTO dish (name, description, price, original_price, image_url, category_id) VALUES
('Tequeños', '6 unidades de tequeños de queso con salsas', 12.00, NULL, 'https://images.unsplash.com/photo-1601050690597-df0568f70950', 
  (SELECT id FROM category WHERE name = 'Entradas')),
('Causa Limeña', 'Causa tradicional rellena de pollo o atún', 15.00, NULL, 'https://images.unsplash.com/photo-1626200419199-391ae4be7a41', 
  (SELECT id FROM category WHERE name = 'Entradas')),
('Alitas Picantes', '8 alitas de pollo fritas con salsa picante', 19.90, 25.00, 'https://images.unsplash.com/photo-1527477396000-e27163b481c2', 
  (SELECT id FROM category WHERE name = 'Entradas')),  -- ¡EN OFERTA! S/ 25.00 → S/ 19.90
('Ensalada Mixta', 'Ensalada fresca de lechuga, tomate, cebolla y palta', 10.00, NULL, 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd', 
  (SELECT id FROM category WHERE name = 'Entradas'));

-- Bebidas
INSERT INTO dish (name, description, price, original_price, image_url, category_id) VALUES
('Inca Kola 1.5L', 'Gaseosa Inca Kola familiar', 8.00, NULL, 'https://images.unsplash.com/photo-1581006852262-e4307cf6283a', 
  (SELECT id FROM category WHERE name = 'Bebidas')),
('Chicha Morada 1L', 'Chicha morada casera con frutas', 10.00, NULL, 'https://images.unsplash.com/photo-1546173159-315724a31696', 
  (SELECT id FROM category WHERE name = 'Bebidas')),
('Limonada Frozen', 'Limonada helada con hierbabuena', 7.00, NULL, 'https://images.unsplash.com/photo-1523677011781-c91d1bbe2f0f', 
  (SELECT id FROM category WHERE name = 'Bebidas')),
('Coca Cola 1.5L', 'Gaseosa Coca Cola familiar', 8.00, NULL, 'https://images.unsplash.com/photo-1554866585-cd94860890b7', 
  (SELECT id FROM category WHERE name = 'Bebidas'));

-- Postres
INSERT INTO dish (name, description, price, original_price, image_url, category_id) VALUES
('Suspiro Limeño', 'Postre tradicional peruano con manjar y merengue', 12.00, NULL, 'https://images.unsplash.com/photo-1488477181946-6428a0291777', 
  (SELECT id FROM category WHERE name = 'Postres')),
('Mazamorra Morada', 'Mazamorra tradicional con arroz con leche', 8.00, NULL, 'https://images.unsplash.com/photo-1563805042-7684c019e1cb', 
  (SELECT id FROM category WHERE name = 'Postres')),
('Picarones', '4 picarones con miel de chancaca', 10.00, NULL, 'https://images.unsplash.com/photo-1509440159596-0249088772ff', 
  (SELECT id FROM category WHERE name = 'Postres'));

-- Combos Familiares
INSERT INTO dish (name, description, price, original_price, image_url, category_id) VALUES
('Combo Familiar 1', '1 Pollo entero + papas grandes + ensalada + gaseosa 1.5L', 65.00, NULL, 'https://images.unsplash.com/photo-1598103442097-8b74394b95c6', 
  (SELECT id FROM category WHERE name = 'Combos Familiares')),
('Combo Familiar 2', '1.5 Pollos + papas extra + ensalada + 2 gaseosas 1.5L', 89.90, 99.00, 'https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58', 
  (SELECT id FROM category WHERE name = 'Combos Familiares')),  -- ¡SUPER OFERTA! S/ 99.00 → S/ 89.90
('Combo Personal', '1/4 Pollo + papas + ensalada + gaseosa 500ml', 22.00, NULL, 'https://images.unsplash.com/photo-1594221708779-94832f4320d1', 
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

-- Ver usuarios con sus direcciones
-- SELECT u.id, u.first_name, u.last_name, u.email, 
--        a.street, a.number, a.phone, a.reference, a.label, a.is_default,
--        c.nombre as ciudad, d.nombre as distrito 
-- FROM users u 
-- LEFT JOIN addresses a ON u.id = a.user_id
-- LEFT JOIN cities c ON a.ciudad_id = c.ciudad_id 
-- LEFT JOIN districts d ON a.distrito_id = d.distrito_id;

-- Ver direcciones con información completa
-- SELECT a.id, u.email, u.first_name, u.last_name,
--        c.nombre as ciudad, d.nombre as distrito,
--        a.street, a.number, a.phone, a.reference, a.label, a.is_default
-- FROM addresses a
-- JOIN users u ON a.user_id = u.id
-- JOIN cities c ON a.ciudad_id = c.ciudad_id
-- JOIN districts d ON a.distrito_id = d.distrito_id
-- ORDER BY u.email, a.is_default DESC;

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
