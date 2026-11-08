INSERT INTO solicitudes (codigo, marca, tipo_solicitud, fecha_envio, numero_contacto, nombre_contacto, fecha_creacion)
VALUES 
('SOL-20261029-1001', 'Samsung', 'Cotización', '2026-10-25', '987654321', 'Carlos Mendoza', CURRENT_TIMESTAMP),
('SOL-20261029-1002', 'Toyota', 'Soporte Técnico', '2026-10-26', '912345678', 'Lucia Fernandez', CURRENT_TIMESTAMP),
('SOL-20261029-1003', 'LG', 'Garantía', '2026-10-27', '955443322', 'Miguel Rodriguez', CURRENT_TIMESTAMP),
('SOL-20261029-1004', 'Sony', 'Reclamo', '2026-10-28', '944332211', 'Ana Paredes', CURRENT_TIMESTAMP),
('SOL-20261029-1005', 'Hyundai', 'Consulta Comercial', '2026-10-29', '966778899', 'Jorge Alarcon', CURRENT_TIMESTAMP);

INSERT INTO contactos (solicitud_id, nombre_contacto, numero_contacto)
VALUES 
(1, 'Pedro Ramirez', '911223344'),
(1, 'Patricia Gomez', '955667788'),
(2, 'Fernando Castillo', '998877665'),
(3, 'Valeria Torres', '944556677'),
(3, 'David Quispe', '933221100'),
(4, 'Sofia Morales', '977889900'),
(5, 'Roberto Salas', '966554433'),
(5, 'Elena Vasquez', '922334455'),
(5, 'Raul Huaman', '988112233');
