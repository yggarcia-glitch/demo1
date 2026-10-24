CREATE TABLE IF NOT EXISTS projects (
    id BIGSERIAL,
    nombre VARCHAR(1500),
    descripcion VARCHAR(10000),
    precio FLOAT,
    PRIMARY KEY (id)
);
