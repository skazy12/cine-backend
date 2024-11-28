package com.sis.cine.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class MovieSessionDTO {
    // Información de la película
    private Long id;
    private String titulo;
    private String genero;
    private Integer duracion;
    private String clasificacion;
    private String poster;
    private String trailer;
    private boolean esEstreno;

    // Información de formatos y fechas
    private List<FormatoDTO> formatos;
    private String formatoSeleccionado; // Formato actual seleccionado
    private List<FechaDTO> fechasDisponibles;
    private LocalDate fechaSeleccionada; // Fecha actual seleccionada
    private List<HorarioDTO> horarios; // Horarios para el formato y fecha seleccionados

    @Data
    public static class FormatoDTO {
        private String nombre; // "2D" o "3D"
        private boolean activo;
    }

    @Data
    public static class FechaDTO {
        private LocalDate fecha;
        private String nombreDia;
        private String numeroDia;
        private String nombreMes;
        private boolean activo;
    }

    @Data
    public static class HorarioDTO {
        private Long proyeccionId;
        private LocalTime hora;
        private int asientosDisponibles;
        private boolean disponible;
        private String salaNombre;    // Nombre de la sala (ej: "Sala 1")
        private String tipoSala;
    }
}