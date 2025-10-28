use labdent_sistema;
select * from usuarios;
select * from transiciones_estado;
select * from pedidos;
SHOW TABLES;
SHOW CREATE TABLE pedidos;
SELECT id, usuario_id, estado FROM pedidos;
DESCRIBE pedidos;
SELECT * FROM pedidos where usuario_id = 9;
SELECT id, codigo_unico, usuario_id, estado_actual
FROM pedidos
WHERE usuario_id = 9;

-- Ejecuta esta consulta para ver qué datos realmente existen
-- Verifica los pedidos de tu usuario
SELECT 
    p.id,
    p.codigo_unico,
    p.usuario_id,
    p.nombre_paciente,
    p.estado_actual,
    u.nombre_completo as nombre_usuario
FROM pedidos p
LEFT JOIN usuarios u ON p.usuario_id = u.id
WHERE u.email = 'dayana@gmail.com';
