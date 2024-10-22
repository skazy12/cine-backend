-- Insertar roles
INSERT INTO rol (nombre) VALUES ('ADMIN');
INSERT INTO rol (nombre) VALUES ('CLIENTE');

-- Insertar tipos de sala
INSERT INTO tipo_sala (nombre) VALUES ('Normal');
INSERT INTO tipo_sala (nombre) VALUES ('VIP');

-- Insertar tipos de proyección
INSERT INTO tipo_proyeccion (nombre) VALUES ('2D');
INSERT INTO tipo_proyeccion (nombre) VALUES ('3D');

-- Insertar salas
INSERT INTO sala (nombre, tipo_sala_id) VALUES ('Sala 1', 1);
INSERT INTO sala (nombre, tipo_sala_id) VALUES ('Sala 2', 1);
INSERT INTO sala (nombre, tipo_sala_id) VALUES ('Sala VIP', 2);

-- Insertar películas
INSERT INTO pelicula (titulo, director, descripcion, duracion, estudio, poster, trailer, enproyeccion) VALUES ('Inception', 'Christopher Nolan', 'Un ladrón con la rara habilidad de "extracción"...', 148, 'Warner Bros.', 'https://ejemplo.com/inception.jpg', 'https://ejemplo.com/inception-trailer.mp4', true);

INSERT INTO pelicula (titulo, director, descripcion, duracion, estudio, poster, trailer, enproyeccion) VALUES ('The Shawshank Redemption', 'Frank Darabont', 'Dos hombres encarcelados se unen a lo largo de varios años...', 142, 'Castle Rock Entertainment', 'https://ejemplo.com/shawshank.jpg', 'https://ejemplo.com/shawshank-trailer.mp4', true);

INSERT INTO pelicula (titulo, director, descripcion, duracion, estudio, poster, trailer, enproyeccion) VALUES ('Pulp Fiction', 'Quentin Tarantino', 'Las vidas de dos mafiosos, un boxeador, la esposa de un gángster...', 154, 'Miramax', 'https://ejemplo.com/pulpfiction.jpg', 'https://ejemplo.com/pulpfiction-trailer.mp4', true);

INSERT INTO pelicula (titulo, director, descripcion, duracion, estudio, poster, trailer, enproyeccion) VALUES ('The Godfather', 'Francis Ford Coppola', 'El patriarca de una dinastía del crimen organizado...', 175, 'Paramount Pictures', 'https://ejemplo.com/godfather.jpg', 'https://ejemplo.com/godfather-trailer.mp4', false);

-- Insertar proyecciones
INSERT INTO proyeccion (comienzo, dia, sala_id, pelicula_id, tipo_proyeccion_id) VALUES ('19:00:00', CURRENT_DATE, 1, 1, 1);

INSERT INTO proyeccion (comienzo, dia, sala_id, pelicula_id, tipo_proyeccion_id) VALUES ('21:30:00', CURRENT_DATE, 2, 2, 1);

INSERT INTO proyeccion (comienzo, dia, sala_id, pelicula_id, tipo_proyeccion_id) VALUES ('20:00:00', CURRENT_DATE, 3, 3, 2);

-- Insertar precios
INSERT INTO precios (nombre, condiciones, precio_final, tipo_sala_id, tipo_proyeccion_id, dia_semana) VALUES ('Normal 2D', 'Entrada estándar', 10.00, 1, 1, NULL);

INSERT INTO precios (nombre, condiciones, precio_final, tipo_sala_id, tipo_proyeccion_id, dia_semana) VALUES ('VIP 2D', 'Entrada VIP', 15.00, 2, 1, NULL);

INSERT INTO precios (nombre, condiciones, precio_final, tipo_sala_id, tipo_proyeccion_id, dia_semana) VALUES ('Normal 3D', 'Entrada 3D', 12.00, 1, 2, NULL);

INSERT INTO precios (nombre, condiciones, precio_final, tipo_sala_id, tipo_proyeccion_id, dia_semana) VALUES ('VIP 3D', 'Entrada VIP 3D', 18.00, 2, 2, NULL);

-- Insertar un usuario de prueba (contraseña: password123)
INSERT INTO usuario (nombre, apellido, email, contrasena, rol_id, tlfn) VALUES ('Usuario', 'Prueba', 'usuario@test.com', '$2a$12$m7q77KaZJ1IuknGdu8USPez9FZugsf5qe/qcr1YydT5SvXnNLoYWe', 2, 1234567890);

-- Insertar un administrador de prueba (contraseña: admin123)
INSERT INTO usuario (nombre, apellido, email, contrasena, rol_id, departamento) VALUES ('Admin', 'Prueba', 'admin@test.com', '$2a$12$m7q77KaZJ1IuknGdu8USPez9FZugsf5qe/qcr1YydT5SvXnNLoYWe', 1, 'IT');