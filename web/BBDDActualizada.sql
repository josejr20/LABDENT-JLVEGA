-- =====================================================
-- BASE DE DATOS LABDENT - COMPATIBLE CON SISTEMA FIFO
-- =====================================================

DROP DATABASE IF EXISTS labdent_sistema;
CREATE DATABASE labdent_sistema CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE labdent_sistema;

-- =====================================================
-- TABLA DE USUARIOS
-- =====================================================
CREATE TABLE usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre_completo VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol ENUM('ODONTOLOGO', 'ADMIN', 'TECNICO', 'CERAMISTA', 'DELIVERISTA', 'CLIENTE') NOT NULL,
    telefono VARCHAR(15),
    direccion VARCHAR(200),
    activo BOOLEAN DEFAULT TRUE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultima_conexion TIMESTAMP NULL,
    INDEX idx_email (email),
    INDEX idx_rol (rol),
    INDEX idx_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA DE PEDIDOS CON SISTEMA FIFO
-- =====================================================
CREATE TABLE pedidos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo_unico VARCHAR(50) UNIQUE NOT NULL,
    odontologo_id INT NOT NULL,
    usuario_id INT NULL COMMENT 'Cliente/paciente asociado',
    
    -- Información del paciente
    nombre_paciente VARCHAR(100) NOT NULL,
    edad_paciente INT DEFAULT 0 COMMENT 'Edad del paciente',
    piezas_dentales VARCHAR(100) NOT NULL COMMENT 'Dientes a trabajar',
    
    -- Detalles del trabajo
    tipo_protesis VARCHAR(100) NOT NULL,
    material VARCHAR(50) NOT NULL,
    color_shade VARCHAR(50) COMMENT 'Color VITA',
    
    -- Fechas
    fecha_ingreso TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_compromiso DATE NOT NULL,
    fecha_entrega TIMESTAMP NULL,
    
    -- Estado y flujo
    estado_actual ENUM(
        'RECEPCION', 
        'PARALELIZADO', 
        'DISENO_CAD', 
        'PRODUCCION_CAM', 
        'CERAMICA', 
        'CONTROL_CALIDAD', 
        'LISTO_ENTREGA', 
        'ENTREGADO',
        'CANCELADO'
    ) DEFAULT 'RECEPCION',
    
    prioridad ENUM('NORMAL', 'URGENTE', 'EMERGENCIA') DEFAULT 'NORMAL',
    
    -- Información adicional
    observaciones TEXT,
    archivo_adjunto VARCHAR(255),
    responsable_actual INT COMMENT 'Técnico/Ceramista responsable',
    
    -- Metadatos
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Claves foráneas
    FOREIGN KEY (odontologo_id) REFERENCES usuarios(id) ON DELETE RESTRICT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    FOREIGN KEY (responsable_actual) REFERENCES usuarios(id) ON DELETE SET NULL,
    
    -- Índices para optimización
    INDEX idx_codigo (codigo_unico),
    INDEX idx_estado (estado_actual),
    INDEX idx_prioridad (prioridad),
    INDEX idx_odontologo (odontologo_id),
    INDEX idx_usuario (usuario_id),
    INDEX idx_responsable (responsable_actual),
    INDEX idx_fecha_ingreso (fecha_ingreso),
    INDEX idx_fecha_compromiso (fecha_compromiso),
    
    -- Índice compuesto para FIFO con prioridades
    INDEX idx_estado_prioridad_fecha (estado_actual, prioridad, fecha_ingreso)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA DE HISTORIAL DE ESTADOS (Para sistema FIFO)
-- =====================================================
CREATE TABLE historial_estados (
    id INT PRIMARY KEY AUTO_INCREMENT,
    pedido_id INT NOT NULL,
    estado VARCHAR(50) NOT NULL,
    usuario_id INT NOT NULL,
    fecha_cambio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones TEXT,
    tiempo_en_estado INT COMMENT 'Minutos en el estado anterior',
    
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT,
    
    INDEX idx_pedido (pedido_id),
    INDEX idx_usuario (usuario_id),
    INDEX idx_fecha (fecha_cambio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA DE TRANSICIONES (Compatibilidad con código existente)
-- =====================================================
CREATE TABLE transiciones_estado (
    id INT PRIMARY KEY AUTO_INCREMENT,
    pedido_id INT NOT NULL,
    estado_anterior VARCHAR(50),
    estado_nuevo VARCHAR(50) NOT NULL,
    usuario_id INT NOT NULL,
    observaciones TEXT,
    checklist_completado BOOLEAN DEFAULT FALSE,
    tiempo_en_estado INT COMMENT 'Minutos en el estado anterior',
    fecha_transicion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT,
    
    INDEX idx_pedido (pedido_id),
    INDEX idx_fecha (fecha_transicion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA DE EVIDENCIAS/ARCHIVOS
-- =====================================================
CREATE TABLE evidencias (
    id INT PRIMARY KEY AUTO_INCREMENT,
    pedido_id INT NOT NULL,
    tipo_evidencia ENUM('FOTO_INICIAL', 'FOTO_PROCESO', 'FOTO_FINAL', 'DOCUMENTO', 'CAD_FILE') NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    ruta_archivo VARCHAR(500) NOT NULL,
    descripcion TEXT,
    usuario_id INT NOT NULL,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT,
    
    INDEX idx_pedido (pedido_id),
    INDEX idx_tipo (tipo_evidencia),
    INDEX idx_usuario (usuario_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA DE NOTIFICACIONES
-- =====================================================
CREATE TABLE notificaciones (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    pedido_id INT,
    tipo ENUM('INFO', 'ALERTA', 'URGENTE') DEFAULT 'INFO',
    titulo VARCHAR(200) NOT NULL,
    mensaje TEXT NOT NULL,
    leida BOOLEAN DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE SET NULL,
    
    INDEX idx_usuario_leida (usuario_id, leida),
    INDEX idx_fecha (fecha_creacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA DE DELIVERY
-- =====================================================
CREATE TABLE pedido_delivery (
    id INT PRIMARY KEY AUTO_INCREMENT,
    pedido_id INT NOT NULL,
    deliverista_id INT NOT NULL,
    estado_delivery ENUM('SALIO_EMPRESA', 'EN_CURSO', 'LLEGO_DESTINO', 'PEDIDO_ENTREGADO') NOT NULL,
    fecha_salida TIMESTAMP NULL,
    fecha_llegada TIMESTAMP NULL,
    fecha_entrega TIMESTAMP NULL,
    observaciones TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (deliverista_id) REFERENCES usuarios(id) ON DELETE RESTRICT,
    
    INDEX idx_pedido (pedido_id),
    INDEX idx_deliverista (deliverista_id),
    INDEX idx_estado (estado_delivery)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- VISTAS ÚTILES
-- =====================================================

-- Vista de pedidos completos con toda la información
CREATE OR REPLACE VIEW vista_pedidos_completos AS
SELECT 
    p.*,
    o.nombre_completo AS nombre_odontologo,
    o.email AS email_odontologo,
    o.telefono AS telefono_odontologo,
    r.nombre_completo AS nombre_responsable,
    r.email AS email_responsable,
    CASE 
        WHEN p.fecha_compromiso < CURDATE() 
            AND p.estado_actual NOT IN ('ENTREGADO', 'CANCELADO')
        THEN 1
        ELSE 0
    END AS atrasado,
    DATEDIFF(p.fecha_compromiso, CURDATE()) AS dias_restantes,
    CASE p.prioridad
        WHEN 'EMERGENCIA' THEN 1
        WHEN 'URGENTE' THEN 2
        ELSE 3
    END AS orden_prioridad
FROM pedidos p
INNER JOIN usuarios o ON p.odontologo_id = o.id
LEFT JOIN usuarios r ON p.responsable_actual = r.id;

-- Vista de productividad por técnico
CREATE OR REPLACE VIEW vista_productividad AS
SELECT 
    u.id as tecnico_id,
    u.nombre_completo,
    u.rol,
    COUNT(DISTINCT h.pedido_id) as trabajos_realizados,
    AVG(h.tiempo_en_estado) as tiempo_promedio_minutos,
    DATE(h.fecha_cambio) as fecha
FROM historial_estados h
INNER JOIN usuarios u ON h.usuario_id = u.id
WHERE u.rol IN ('TECNICO', 'CERAMISTA')
GROUP BY u.id, u.nombre_completo, u.rol, DATE(h.fecha_cambio);

-- Vista de estadísticas por estado
CREATE OR REPLACE VIEW vista_estadisticas_estados AS
SELECT 
    estado_actual,
    COUNT(*) AS total_pedidos,
    SUM(CASE WHEN prioridad = 'EMERGENCIA' THEN 1 ELSE 0 END) AS emergencias,
    SUM(CASE WHEN prioridad = 'URGENTE' THEN 1 ELSE 0 END) AS urgentes,
    SUM(CASE WHEN prioridad = 'NORMAL' THEN 1 ELSE 0 END) AS normales,
    SUM(CASE WHEN fecha_compromiso < CURDATE() THEN 1 ELSE 0 END) AS atrasados
FROM pedidos
WHERE estado_actual NOT IN ('ENTREGADO', 'CANCELADO')
GROUP BY estado_actual;

-- Vista de pedidos procesables (FIFO)
CREATE OR REPLACE VIEW vista_pedidos_procesables AS
WITH pedidos_ordenados AS (
    SELECT 
        p.*,
        ROW_NUMBER() OVER (
            PARTITION BY p.estado_actual, 
            CASE p.prioridad
                WHEN 'EMERGENCIA' THEN 1
                WHEN 'URGENTE' THEN 2
                ELSE 3
            END
            ORDER BY p.fecha_ingreso ASC
        ) AS posicion_en_cola
    FROM pedidos p
    WHERE p.estado_actual NOT IN ('ENTREGADO', 'CANCELADO')
)
SELECT 
    po.*,
    CASE 
        WHEN po.posicion_en_cola = 1 THEN 1
        ELSE 0
    END AS es_procesable
FROM pedidos_ordenados po;

-- =====================================================
-- TRIGGERS
-- =====================================================

-- Trigger para sincronizar historial_estados y transiciones_estado
DELIMITER //

CREATE TRIGGER trg_sincronizar_historial_transiciones
AFTER INSERT ON historial_estados
FOR EACH ROW
BEGIN
    DECLARE estado_ant VARCHAR(50);
    
    -- Obtener el estado anterior
    SELECT estado INTO estado_ant
    FROM historial_estados
    WHERE pedido_id = NEW.pedido_id 
      AND id < NEW.id
    ORDER BY id DESC
    LIMIT 1;
    
    -- Insertar en transiciones_estado para compatibilidad
    INSERT INTO transiciones_estado (
        pedido_id, 
        estado_anterior, 
        estado_nuevo, 
        usuario_id, 
        observaciones,
        tiempo_en_estado
    ) VALUES (
        NEW.pedido_id,
        estado_ant,
        NEW.estado,
        NEW.usuario_id,
        NEW.observaciones,
        NEW.tiempo_en_estado
    );
END //

DELIMITER ;

-- =====================================================
-- DATOS DE PRUEBA
-- =====================================================

-- Insertar usuarios
INSERT INTO usuarios (nombre_completo, email, password, rol, telefono, direccion) VALUES
('Administrador Sistema', 'admin@labdent.com', 'admin123', 'ADMIN', '942000000', 'Av. Principal 123'),
('Dr. Carlos Mendoza', 'cmendoza@gmail.com', 'pass123', 'ODONTOLOGO', '942123456', 'Consultorio Dental Centro'),
('Dra. Ana Pérez', 'aperez@gmail.com', 'pass123', 'ODONTOLOGO', '942987654', 'Clínica Dental Norte'),
('Juan Técnico CAD', 'jtecnico@labdent.com', 'pass123', 'TECNICO', '942555111', 'Laboratorio LABDENT'),
('María Ceramista', 'mceramista@labdent.com', 'pass123', 'CERAMISTA', '942555222', 'Laboratorio LABDENT'),
('Pedro Deliverista', 'pdeliverista@labdent.com', 'pass123', 'DELIVERISTA', '942555333', 'Área de Entrega'),
('Dayana', 'dayana@gmail.com', 'dayana20', 'CLIENTE', '942111222', 'Dirección Cliente'),
('Jose', 'jose@gmail.com', 'jose20', 'CLIENTE', '946559632', 'Dirección Cliente');

-- Insertar pedidos de ejemplo con diferentes prioridades y estados
INSERT INTO pedidos (
    codigo_unico, 
    odontologo_id, 
    usuario_id, 
    nombre_paciente, 
    edad_paciente,
    piezas_dentales, 
    tipo_protesis, 
    material, 
    color_shade, 
    fecha_compromiso, 
    estado_actual, 
    prioridad, 
    observaciones, 
    responsable_actual
) VALUES
-- Pedidos en RECEPCION (para probar FIFO)
('LAB-2025-001', 2, 7, 'María García López', 35, '1.6, 1.7', 'Corona', 'Zirconio', 'A2', 
 DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'RECEPCION', 'NORMAL', 'Paciente con sensibilidad', NULL),

('LAB-2025-002', 3, 7, 'Juan Carlos Torres', 42, '2.1, 2.2, 2.3', 'Puente', 'Metal-Porcelana', 'B1', 
 DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'RECEPCION', 'URGENTE', 'Paciente solicita pronto', NULL),

('LAB-2025-003', 2, 7, 'Rosa María López', 28, '1.1', 'Carilla', 'Disilicato de Litio', 'A1', 
 DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'RECEPCION', 'EMERGENCIA', 'EVENTO IMPORTANTE - PRIORIDAD', NULL),

('LAB-2025-004', 3, 7, 'Pedro Sánchez', 50, '3.6, 3.7, 4.6, 4.7', 'Prótesis Parcial', 'Cromo-Cobalto', 'A3', 
 DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'RECEPCION', 'NORMAL', 'Diseño complejo', NULL),

-- Pedidos en otros estados
('LAB-2025-005', 2, 7, 'Ana Martínez', 38, '2.4, 2.5', 'Incrustación', 'Zirconio', 'A2', 
 DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'PARALELIZADO', 'NORMAL', 'Listo para CAD', 4),

('LAB-2025-006', 3, 7, 'Luis Fernández', 45, '1.3, 1.4', 'Corona', 'E-max', 'B2', 
 DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'DISENO_CAD', 'URGENTE', 'Diseño en progreso', 4),

('LAB-2025-007', 2, 7, 'Carmen Ruiz', 32, '4.1, 4.2', 'Puente', 'Zirconio', 'A1', 
 DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'PRODUCCION_CAM', 'NORMAL', 'En fresado', 4),

('LAB-2025-008', 3, 7, 'Roberto Díaz', 55, '1.6', 'Corona', 'Zirconio', 'A3', 
 DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'CERAMICA', 'NORMAL', 'Aplicando cerámica', 5),

('LAB-2025-009', 2, 7, 'Elena González', 29, '2.1', 'Carilla', 'Disilicato', 'A2', 
 DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'CONTROL_CALIDAD', 'URGENTE', 'Revisión final', 1),

('LAB-2025-010', 3, 7, 'Miguel Ángel Castro', 48, '3.6, 3.7', 'Corona', 'Zirconio', 'B1', 
 DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'LISTO_ENTREGA', 'NORMAL', 'Listo para recoger', 1),

-- Pedido atrasado para pruebas
('LAB-2025-011', 2, 7, 'Patricia Morales', 36, '1.5', 'Corona', 'E-max', 'A2', 
 DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'DISENO_CAD', 'URGENTE', 'ATRASADO - Priorizar', 4);

-- Insertar historial inicial para algunos pedidos
INSERT INTO historial_estados (pedido_id, estado, usuario_id, observaciones) VALUES
(1, 'RECEPCION', 1, 'Pedido recibido y validado'),
(2, 'RECEPCION', 1, 'Pedido urgente recibido'),
(3, 'RECEPCION', 1, 'EMERGENCIA - Recibido con máxima prioridad'),
(4, 'RECEPCION', 1, 'Pedido estándar recibido'),
(5, 'RECEPCION', 1, 'Recibido'),
(5, 'PARALELIZADO', 4, 'Paralelizado completado'),
(6, 'RECEPCION', 1, 'Recibido'),
(6, 'PARALELIZADO', 4, 'Paralelizado OK'),
(6, 'DISENO_CAD', 4, 'Diseño CAD en progreso');

-- Insertar evidencias de ejemplo
INSERT INTO evidencias (pedido_id, tipo_evidencia, nombre_archivo, ruta_archivo, descripcion, usuario_id) VALUES
(5, 'FOTO_INICIAL', 'foto_inicial_005.jpg', 'uploads/evidencias/foto_inicial_005.jpg', 'Modelo inicial', 4),
(6, 'CAD_FILE', 'diseno_006.stl', 'uploads/cad/diseno_006.stl', 'Diseño CAD completado', 4),
(8, 'FOTO_PROCESO', 'ceramica_proceso_008.jpg', 'uploads/evidencias/ceramica_proceso_008.jpg', 'Aplicación de cerámica', 5);

-- Insertar notificaciones
INSERT INTO notificaciones (usuario_id, pedido_id, tipo, titulo, mensaje) VALUES
(4, 6, 'ALERTA', 'Pedido Urgente', 'El pedido LAB-2025-006 requiere atención urgente'),
(5, 8, 'INFO', 'Trabajo Asignado', 'Se te asignó el pedido LAB-2025-008 para cerámica'),
(1, 9, 'INFO', 'Control de Calidad', 'Pedido LAB-2025-009 listo para revisión'),
(2, 11, 'URGENTE', 'Pedido Atrasado', 'El pedido LAB-2025-011 está atrasado');

-- =====================================================
-- CONSULTAS ÚTILES PARA PRUEBAS
-- =====================================================

-- Ver todos los pedidos en RECEPCION ordenados por FIFO
SELECT * FROM vista_pedidos_completos WHERE estado_actual = 'RECEPCION' 
ORDER BY orden_prioridad, fecha_ingreso;

-- Ver pedidos procesables
SELECT * FROM vista_pedidos_procesables WHERE es_procesable = 1;

-- Ver estadísticas por estado
SELECT * FROM vista_estadisticas_estados;

-- Ver pedidos atrasados
SELECT * FROM vista_pedidos_completos WHERE atrasado = 1;

-- Ver historial de un pedido
SELECT * FROM historial_estados WHERE pedido_id = 1 ORDER BY fecha_cambio;

-- Ver carga de trabajo por técnico
SELECT 
     r.nombre_completo,
     COUNT(*) as pedidos_asignados,
     SUM(CASE WHEN p.prioridad = 'EMERGENCIA' THEN 1 ELSE 0 END) as emergencias
 FROM pedidos p
 JOIN usuarios r ON p.responsable_actual = r.id
 WHERE p.estado_actual NOT IN ('ENTREGADO', 'CANCELADO')
 GROUP BY r.id, r.nombre_completo;
 
 DELIMITER $$

CREATE PROCEDURE registrar_pedido_con_transicion(
    IN p_codigo_unico VARCHAR(50),
    IN p_odontologo_id INT,
    IN p_nombre_paciente VARCHAR(100),
    IN p_piezas_dentales VARCHAR(100),
    IN p_tipo_protesis VARCHAR(100),
    IN p_material VARCHAR(50),
    IN p_color_shade VARCHAR(50),
    IN p_fecha_compromiso DATE,
    IN p_prioridad VARCHAR(20),   -- 🔹 CAMBIADO: antes era ENUM
    IN p_observaciones TEXT,
    OUT p_pedido_id INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_pedido_id = NULL;
    END;

    START TRANSACTION;

    INSERT INTO pedidos (
        codigo_unico,
        odontologo_id,
        usuario_id,
        nombre_paciente,
        edad_paciente,
        piezas_dentales,
        tipo_protesis,
        material,
        color_shade,
        fecha_compromiso,
        estado_actual,
        prioridad,
        observaciones,
        responsable_actual
    ) VALUES (
        p_codigo_unico,
        p_odontologo_id,
        NULL,
        p_nombre_paciente,
        0,
        p_piezas_dentales,
        p_tipo_protesis,
        p_material,
        p_color_shade,
        p_fecha_compromiso,
        'RECEPCION',
        p_prioridad,
        p_observaciones,
        NULL
    );

    SET p_pedido_id = LAST_INSERT_ID();

    COMMIT;
END$$

DELIMITER ;

 