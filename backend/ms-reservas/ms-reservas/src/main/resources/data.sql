-- Insertar mesas de ejemplo
INSERT INTO mesa (numero, capacidad, disponible) VALUES (1, 4, true);
INSERT INTO mesa (numero, capacidad, disponible) VALUES (2, 6, true);
INSERT INTO mesa (numero, capacidad, disponible) VALUES (3, 2, true);
INSERT INTO mesa (numero, capacidad, disponible) VALUES (4, 8, false);

-- Insertar reservas de ejemplo (id_usuario ficticio)
INSERT INTO reserva (fecha, hora, cant_personas, estado, id_usuario, id_mesa) VALUES ('2024-01-30', '19:00:00', 4, 'ACTIVA', 1, 1);
INSERT INTO reserva (fecha, hora, cant_personas, estado, id_usuario, id_mesa) VALUES ('2024-01-31', '20:00:00', 6, 'ACTIVA', 2, 2);