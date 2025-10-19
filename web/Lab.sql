CREATE DATABASE labdent_sistema;
USE labdent_sistema;
select * from usuarios;
select * from pedidos;
select * from transiciones_estado;
select * from evidencias;
select * from notificaciones;
select * from vista_productividad;

-- Tabla de usuarios con roles
CREATE TABLE usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre_completo VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol ENUM('ODONTOLOGO', 'ADMIN', 'TECNICO', 'CERAMISTA', 'DELIVERISTA') NOT NULL,
    telefono VARCHAR(15),
    direccion VARCHAR(200),
    activo BOOLEAN DEFAULT TRUE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultima_conexion TIMESTAMP NULL,
    INDEX idx_email (email),
    INDEX idx_rol (rol)
);

-- Tabla de pedidos con control FIFO
CREATE TABLE pedidos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo_unico VARCHAR(50) UNIQUE NOT NULL,
    odontologo_id INT NOT NULL,
    nombre_paciente VARCHAR(100) NOT NULL,
    piezas_dentales VARCHAR(100) NOT NULL,
    tipo_protesis VARCHAR(100) NOT NULL,
    material VARCHAR(50) NOT NULL,
    color_shade VARCHAR(50),
    fecha_ingreso TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_compromiso DATE NOT NULL,
    fecha_entrega TIMESTAMP NULL,
    estado_actual ENUM('RECEPCION', 'PARALELIZADO', 'DISENO_CAD', 'PRODUCCION_CAM', 
                       'CERAMICA', 'CONTROL_CALIDAD', 'LISTO_ENTREGA', 'ENTREGADO') DEFAULT 'RECEPCION',
    prioridad ENUM('NORMAL', 'URGENTE', 'EMERGENCIA') DEFAULT 'NORMAL',
    observaciones TEXT,
    archivo_adjunto VARCHAR(255),
    responsable_actual INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (odontologo_id) REFERENCES usuarios(id),
    FOREIGN KEY (responsable_actual) REFERENCES usuarios(id),
    INDEX idx_codigo (codigo_unico),
    INDEX idx_estado (estado_actual),
    INDEX idx_odontologo (odontologo_id),
    INDEX idx_fecha_ingreso (fecha_ingreso)
);

-- Tabla de transiciones de estado (trazabilidad)
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
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    INDEX idx_pedido (pedido_id),
    INDEX idx_fecha (fecha_transicion)
);

-- Tabla de evidencias/archivos
CREATE TABLE evidencias (
    id INT PRIMARY KEY AUTO_INCREMENT,
    pedido_id INT NOT NULL,
    tipo_evidencia ENUM('FOTO_INICIAL', 'FOTO_PROCESO', 'FOTO_FINAL', 'DOCUMENTO', 'CAD_FILE') NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    ruta_archivo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    usuario_id INT NOT NULL,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabla de notificaciones
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
    INDEX idx_usuario_leida (usuario_id, leida)
);

-- Vista para reportes de productividad
CREATE VIEW vista_productividad AS
SELECT 
    u.id as tecnico_id,
    u.nombre_completo,
    u.rol,
    COUNT(DISTINCT t.pedido_id) as trabajos_realizados,
    AVG(t.tiempo_en_estado) as tiempo_promedio_minutos,
    DATE(t.fecha_transicion) as fecha
FROM transiciones_estado t
INNER JOIN usuarios u ON t.usuario_id = u.id
WHERE u.rol IN ('TECNICO', 'CERAMISTA')
GROUP BY u.id, u.nombre_completo, u.rol, DATE(t.fecha_transicion);

-- Datos iniciales
INSERT INTO usuarios (nombre_completo, email, password, rol, telefono) VALUES
('Administrador Sistema', 'admin@labdent.com', 'admin123', 'ADMIN', '942000000'),
('Dr. Carlos Mendoza', 'cmendoza@gmail.com', 'pass123', 'ODONTOLOGO', '942123456'),
('Dra. Ana Pérez', 'aperez@gmail.com', 'pass123', 'ODONTOLOGO', '942987654'),
('Juan Técnico CAD', 'jtecnico@labdent.com', 'pass123', 'TECNICO', '942555111'),
('María Ceramista', 'mceramista@labdent.com', 'pass123', 'CERAMISTA', '942555222'),
('Pedro Deliverista', 'pdeliverista@labdent.com', 'pass123', 'DELIVERISTA', '942555333');

INSERT INTO pedidos (codigo_unico, odontologo_id, nombre_paciente, piezas_dentales, tipo_protesis, material, fecha_compromiso, estado_actual, responsable_actual) VALUES
('LAB-1727890123456', 2, 'María García', '1.6, 1.7', 'Corona', 'Zirconia', '2025-10-15', 'CERAMICA', 5),
('LAB-1727890223456', 3, 'Juan Torres', '2.1, 2.2, 2.3', 'Puente', 'Metal-Porcelana', '2025-10-18', 'DISENO_CAD', 4),
('LAB-1727890323456', 2, 'Rosa López', '1.1', 'Carilla', 'Disilicato de Litio', '2025-10-12', 'CONTROL_CALIDAD', 1);

-- Transiciones para el pedido LAB-1727890123456 (id=1)
INSERT INTO transiciones_estado (pedido_id, estado_anterior, estado_nuevo, usuario_id, observaciones, checklist_completado, tiempo_en_estado)
VALUES
(1, NULL, 'RECEPCION', 5, 'Pedido recibido correctamente.', TRUE, NULL),
(1, 'RECEPCION', 'PARALELIZADO', 5, 'Listo para paralelizado.', TRUE, 60),
(1, 'PARALELIZADO', 'CERAMICA', 5, 'Enviado a ceramista.', TRUE, 180);

-- Transiciones para el pedido LAB-1727890223456 (id=2)
INSERT INTO transiciones_estado (pedido_id, estado_anterior, estado_nuevo, usuario_id, observaciones, checklist_completado, tiempo_en_estado)
VALUES
(2, NULL, 'RECEPCION', 4, 'Recepción en sistema.', TRUE, NULL),
(2, 'RECEPCION', 'DISENO_CAD', 4, 'Diseño CAD iniciado.', TRUE, 120);

-- Transiciones para el pedido LAB-1727890323456 (id=3)
INSERT INTO transiciones_estado (pedido_id, estado_anterior, estado_nuevo, usuario_id, observaciones, checklist_completado, tiempo_en_estado)
VALUES
(3, NULL, 'RECEPCION', 1, 'Recepcionado por admin.', TRUE, NULL),
(3, 'RECEPCION', 'DISENO_CAD', 4, 'Diseño iniciado por técnico CAD.', TRUE, 90),
(3, 'DISENO_CAD', 'PRODUCCION_CAM', 4, 'Diseño completado, enviado a producción.', TRUE, 180),
(3, 'PRODUCCION_CAM', 'CONTROL_CALIDAD', 1, 'Verificación por admin.', TRUE, 60);

-- Evidencias para pedido LAB-1727890123456
INSERT INTO evidencias (pedido_id, tipo_evidencia, nombre_archivo, ruta_archivo, descripcion, usuario_id)
VALUES
(1, 'FOTO_INICIAL', 'foto_inicial_1.jpg', '/uploads/fotos/foto_inicial_1.jpg', 'Foto inicial del modelo', 5),
(1, 'FOTO_FINAL', 'foto_final_1.jpg', '/uploads/fotos/foto_final_1.jpg', 'Trabajo finalizado', 5);

-- Evidencias para pedido LAB-1727890223456
INSERT INTO evidencias (pedido_id, tipo_evidencia, nombre_archivo, ruta_archivo, descripcion, usuario_id)
VALUES
(2, 'CAD_FILE', 'diseno_puente.stl', '/uploads/cad/diseno_puente.stl', 'Diseño CAD en STL', 4);

-- Evidencias para pedido LAB-1727890323456
INSERT INTO evidencias (pedido_id, tipo_evidencia, nombre_archivo, ruta_archivo, descripcion, usuario_id)
VALUES
(3, 'FOTO_PROCESO', 'foto_proceso_3.jpg', '/uploads/fotos/foto_proceso_3.jpg', 'Proceso de fresado', 4),
(3, 'DOCUMENTO', 'orden_trabajo.pdf', '/uploads/docs/orden_trabajo.pdf', 'Orden de trabajo impresa', 1);

-- Notificaciones para técnico y ceramista
INSERT INTO notificaciones (usuario_id, pedido_id, tipo, titulo, mensaje)
VALUES
(5, 1, 'ALERTA', 'Nuevo Trabajo Asignado', 'Se te asignó el trabajo LAB-1727890123456 para proceso cerámico.'),
(4, 2, 'INFO', 'Diseño CAD requerido', 'El pedido LAB-1727890223456 está listo para diseño CAD.'),
(1, 3, 'URGENTE', 'Revisión en Control de Calidad', 'Verifica el trabajo LAB-1727890323456 lo antes posible.');

-- Notificación a odontólogo
INSERT INTO notificaciones (usuario_id, pedido_id, tipo, titulo, mensaje)
VALUES
(2, 1, 'INFO', 'Trabajo en Proceso', 'Tu pedido LAB-1727890123456 se encuentra en la etapa CERÁMICA.');


