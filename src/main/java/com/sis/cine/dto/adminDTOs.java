package com.sis.cine.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class adminDTOs {
    @Data
    public static class SalaDTO {
        private Long id;
        private String nombre;
        private Long tipoSalaId;
        private String tipoSalaNombre;  // Nombre del tipo de sala
        private Integer capacidad;      // Cantidad total de asientos
        private List<AsientoDTO> asientos;
    }

    @Data
    public static class AsientoDTO {
        private Long id;
        private String fila;
        private String numero;
        private Boolean disponible;
    }

    @Data
    public static class ProyeccionDTO {
        private Long id;
        private Long peliculaId;
        private Long salaId;
        private Long tipoProyeccionId;
        private LocalDate dia;         // Fecha de la proyección
        private LocalTime comienzo;    // Hora de inicio de la proyección
        private Boolean activa;        // Indica si la proyección está activa

        // Campos adicionales para información
        private String tituloPelicula; // Para mostrar en respuestas
        private String nombreSala;     // Para mostrar en respuestas
        private Integer duracionPelicula; // Duración en minutos
        private String tipoProyeccion; // 2D, 3D, etc.
    }

    @Data
    public static class PreciosDTO {
        private Long id;
        private String nombre;
        private String condiciones;
        private BigDecimal precioFinal;
        private Long tipoSalaId;
        private String tipoSalaNombre;
        private Long tipoProyeccionId;
        private String tipoProyeccionNombre;
        private String diaSemana;
    }
    @Data
    public static class TipoSalaDTO {
        private Long id;
        private String nombre;
        private String descripcion;
        private List<String> caracteristicas; // Lista de características separadas
        private Boolean activo;
        private Long cantidadSalas; // Para mostrar cuántas salas usan este tipo
    }
}
