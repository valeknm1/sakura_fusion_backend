-- Crear tabla mesa
CREATE TABLE IF NOT EXISTS mesa (
    id_mesa BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero INT NOT NULL,
    capacidad INT NOT NULL,
    disponible BOOLEAN NOT NULL
);

-- Crear tabla reserva
CREATE TABLE IF NOT EXISTS reserva (
    id_reserva BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    cant_personas INT NOT NULL,
    estado VARCHAR(50) NOT NULL,
    id_usuario BIGINT NOT NULL,
    id_mesa BIGINT NOT NULL,
    FOREIGN KEY (id_mesa) REFERENCES mesa(id_mesa)
);