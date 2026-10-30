-- =============================================================
-- Test data para PRODUCTO y CATEGORIA_PRODUCTOS
-- =============================================================
-- Entidades analizadas:
--   - productos.CATEGORIA_PRODUCTOS
--   - productos.PRODUCTO
-- Columna/enum relacionada:
--   - PRODUCTO.unidad_medida_peso -> GR, KG
--   - PRODUCTO.unidad_medida_largo -> CM, MT
--   - PRODUCTO.unidad_medida_ancho -> CM, MT
--   - PRODUCTO.unidad_medida_altura -> CM, MT
-- 
-- Nota: este SQL asume PostgreSQL y que el esquema productos existe.
-- =============================================================

BEGIN;

CREATE SCHEMA IF NOT EXISTS productos AUTHORIZATION techplatform;

-- =========================
-- CATEGORIAS RELACIONADAS
-- =========================
TRUNCATE TABLE productos.producto RESTART IDENTITY;
TRUNCATE TABLE productos.categoria_productos RESTART IDENTITY;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_sequences WHERE schemaname = 'public' AND sequencename = 'id_categoria_productos_seq') THEN
        PERFORM setval('public.id_categoria_productos_seq', 1, false);
    END IF;
    IF EXISTS (SELECT 1 FROM pg_sequences WHERE schemaname = 'public' AND sequencename = 'id_producto_seq') THEN
        PERFORM setval('public.id_producto_seq', 1, false);
    END IF;
END $$;

INSERT INTO productos.categoria_productos (
    id_categoria_productos,
    codigo,
    nombre,
    descripcion,
    codigo_padre,
    fecha_hora_creacion,
    fecha_hora_actualizacion,
    usuario_crea,
    usuario_actualiza,
    activo
) VALUES
    (1, 'ELEC', 'Electrónica', 'Productos electrónicos, accesorios y gadgets', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (2, 'SMART', 'Celulares y Smartphones', 'Teléfonos inteligentes y accesorios', 'ELEC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (3, 'HOG', 'Hogar', 'Productos para el hogar y cocina', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (4, 'DEP', 'Deportes', 'Artículos deportivos y recreativos', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (5, 'MODA', 'Moda', 'Ropa, accesorios y estilo personal', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (6, 'OFI', 'Oficina', 'Suministros y equipos de oficina', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (7, 'BEB', 'Bebés', 'Productos para bebés y cuidado infantil', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE);

-- =========================
-- PRODUCTOS (25 registros)
-- =========================
INSERT INTO productos.producto (
    id_producto,
    codigo_categoria,
    codigo,
    nombre,
    descripcion,
    marca,
    precio,
    precio_reducido,
    peso,
    unidad_medida_peso,
    largo,
    unidad_medida_largo,
    ancho,
    unidad_medida_ancho,
    altura,
    unidad_medida_altura,
    tags,
    fecha_hora_creacion,
    fecha_hora_actualizacion,
    usuario_crea,
    usuario_actualiza,
    activo
) VALUES
    (1, 'SMART', 'PROD-001', 'Smartphone Galaxy A54', 'Celular Android de 128 GB con cámara triple y pantalla AMOLED.', 'Samsung', 699.99, 629.99, 0.197, 'KG', 16.40, 'CM', 7.60, 'CM', 0.80, 'CM', 'android,telefono,galaxy,camara', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (2, 'SMART', 'PROD-002', 'Smartphone iPhone 14', 'Smartphone con chip avanzado, pantalla Retina y cámara dual.', 'Apple', 1499.00, 1399.00, 0.206, 'KG', 16.10, 'CM', 7.80, 'CM', 0.78, 'CM', 'iphone,apple,moviles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (3, 'ELEC', 'PROD-003', 'Audífonos Bluetooth Pro', 'Audífonos inalámbricos con cancelación activa de ruido.', 'Sony', 249.90, 219.90, 0.320, 'KG', 20.00, 'CM', 18.00, 'CM', 8.00, 'CM', 'audio,bluetooth,audifonos', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (4, 'ELEC', 'PROD-004', 'Smartwatch Fit Active', 'Reloj inteligente para monitoreo deportivo y notificaciones.', 'Xiaomi', 189.00, 169.00, 0.045, 'KG', 4.50, 'CM', 3.80, 'CM', 1.20, 'CM', 'reloj,fitness,smartwatch', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (5, 'HOG', 'PROD-005', 'Licuadora Multivelocidad 1.5L', 'Licuadora de alta potencia para smoothies y jugos.', 'Oster', 119.50, 99.50, 1.200, 'KG', 25.00, 'CM', 14.00, 'CM', 22.00, 'CM', 'hogar,licuadora,cocina', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (6, 'HOG', 'PROD-006', 'Sarten Antiaderente 28cm', 'Sartén reforzada para cocinar sin esfuerzo y limpieza rápida.', 'Tramontina', 69.90, 59.90, 0.480, 'KG', 28.00, 'CM', 28.00, 'CM', 5.00, 'CM', 'hogar,cocina,sarten', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (7, 'HOG', 'PROD-007', 'Aspiradora Robot', 'Robot aspirador con navegación inteligente y batería recargable.', 'Roomba', 499.00, 449.00, 3.700, 'KG', 34.00, 'CM', 34.00, 'CM', 9.50, 'CM', 'hogar,robot,aspiradora', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (8, 'DEP', 'PROD-008', 'Pelota de Fútbol Profesional', 'Pelota oficial para entrenamiento y partidos recreativos.', 'Nike', 59.90, 49.90, 0.420, 'KG', 22.00, 'CM', 22.00, 'CM', 11.00, 'CM', 'deporte,futbol,pelota', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (9, 'DEP', 'PROD-009', 'Mancuerna Ajustable 20kg', 'Set de pesas ajustables para entrenamiento de fuerza.', 'PowerFit', 159.00, 139.00, 20.000, 'KG', 40.00, 'CM', 15.00, 'CM', 15.00, 'CM', 'gimnasio,mancuernas,fitness', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (10, 'DEP', 'PROD-010', 'Bicicleta Montaña 26', 'Bicicleta resistente para rutas y terrenos irregulares.', 'Rockrider', 899.00, 829.00, 14.300, 'KG', 172.00, 'CM', 52.00, 'CM', 96.00, 'CM', 'bicicleta,deporte,montana', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (11, 'MODA', 'PROD-011', 'Camisa Casual Slim Fit', 'Camisa moderna de algodón para uso diario y oficina.', 'Levi´s', 79.50, 69.50, 0.250, 'KG', 72.00, 'CM', 58.00, 'CM', 1.50, 'CM', 'moda,camisa,ropa', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (12, 'MODA', 'PROD-012', 'Zapatillas Runner Pro', 'Zapatillas ligeras para caminar y entrenar con comodidad.', 'Adidas', 129.99, 109.99, 0.620, 'KG', 28.00, 'CM', 11.50, 'CM', 8.50, 'CM', 'moda,zapatillas,runner', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (13, 'MODA', 'PROD-013', 'Chaqueta Impermeable', 'Chaqueta con tecnología resistente al agua para clima cambiante.', 'North Face', 199.00, 179.00, 0.550, 'KG', 68.00, 'CM', 52.00, 'CM', 7.00, 'CM', 'moda,chaqueta,impermeable', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (14, 'OFI', 'PROD-014', 'Laptop ProBook 14', 'Laptop empresarial con 16 GB RAM, SSD 512 GB y pantalla FHD.', 'HP', 1099.99, 999.99, 1.780, 'KG', 32.50, 'CM', 21.50, 'CM', 1.90, 'CM', 'oficina,laptop,computadora', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (15, 'OFI', 'PROD-015', 'Impresora Multifuncional WiFi', 'Impresora láser de alta velocidad para oficina y hogar.', 'Canon', 329.00, 289.00, 6.500, 'KG', 42.00, 'CM', 38.00, 'CM', 22.00, 'CM', 'oficina,impresora,documentos', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (16, 'OFI', 'PROD-016', 'Escritorio Ejecutivo', 'Escritorio de madera con acabado premium para oficina.', 'HomeLine', 699.00, 649.00, 31.000, 'KG', 120.00, 'CM', 60.00, 'CM', 75.00, 'CM', 'oficina,escritorio,muebles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (17, 'BEB', 'PROD-017', 'Cochecito Compacto', 'Cochecito ligero con ruedas dobles y amplio almacenamiento.', 'BebéGo', 389.00, 349.00, 8.200, 'KG', 101.00, 'CM', 56.00, 'CM', 106.00, 'CM', 'bebe,cochecito,infantil', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (18, 'BEB', 'PROD-018', 'Mamadera Anticolapso', 'Mamadera con sistema anti-colapso para alimentación segura.', 'Mimo', 24.90, 19.90, 0.130, 'KG', 12.00, 'CM', 6.00, 'CM', 8.00, 'CM', 'bebe,mamadera,alimentacion', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (19, 'ELEC', 'PROD-019', 'Tablet 10.9 pulgadas', 'Tableta con batería de larga duración para trabajo y entretenimiento.', 'Lenovo', 549.00, 499.00, 0.480, 'KG', 25.00, 'CM', 17.00, 'CM', 0.70, 'CM', 'tablet,android,entretenimiento', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (20, 'ELEC', 'PROD-020', 'Altavoz Bluetooth Mini', 'Altavoz portátil con sonido claro y resistencia al agua.', 'JBL', 99.00, 89.00, 0.350, 'KG', 14.00, 'CM', 8.00, 'CM', 8.00, 'CM', 'audio,altavoz,bluetooth', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (21, 'HOG', 'PROD-021', 'Cafetera Automática', 'Cafetera con molinillo integrado y capacidad para 12 tazas.', 'Philips', 249.00, 219.00, 4.200, 'KG', 28.00, 'CM', 18.00, 'CM', 30.00, 'CM', 'hogar,cafetera,cocina', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (22, 'DEP', 'PROD-022', 'Mochila Deportiva 35L', 'Mochila resistente con compartimentos para deporte y viajes.', 'Under Armour', 89.90, 75.90, 0.540, 'KG', 52.00, 'CM', 30.00, 'CM', 18.00, 'CM', 'deporte,morral,mochila', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (23, 'OFI', 'PROD-023', 'Monitor 27 pulgadas', 'Monitor Full HD con diseño ultrafino y conectividad HDMI.', 'LG', 399.00, 359.00, 5.300, 'KG', 61.50, 'CM', 47.50, 'CM', 18.00, 'CM', 'oficina,monitor,pantalla', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (24, 'MODA', 'PROD-024', 'Bolso de Cuero Casual', 'Bolso elegante para uso diario y trabajo casual.', 'Mango', 149.90, 129.90, 0.620, 'KG', 28.00, 'CM', 18.00, 'CM', 10.00, 'CM', 'moda,bolso,cuero', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE),
    (25, 'BEB', 'PROD-025', 'Cuna Plegable', 'Cuna portátil para bebés con estructura segura y cómoda.', 'BabySafe', 279.00, 239.00, 6.400, 'KG', 90.00, 'CM', 50.00, 'CM', 80.00, 'CM', 'bebe,cuna,infantil', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin', TRUE);

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_sequences WHERE schemaname = 'public' AND sequencename = 'id_categoria_productos_seq') THEN
        PERFORM setval('public.id_categoria_productos_seq', (SELECT COALESCE(MAX(id_categoria_productos), 0) FROM productos.categoria_productos), true);
    END IF;
    IF EXISTS (SELECT 1 FROM pg_sequences WHERE schemaname = 'public' AND sequencename = 'id_producto_seq') THEN
        PERFORM setval('public.id_producto_seq', (SELECT COALESCE(MAX(id_producto), 0) FROM productos.producto), true);
    END IF;
END $$;

COMMIT;
