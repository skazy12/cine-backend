package com.sis.cine.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class MovieSchedulingDTO {
    // Información básica de la programación
    private Long peliculaId;
    private List<ProyeccionProgramadaDTO> proyecciones;

    @Data
    public static class ProyeccionProgramadaDTO {
        private Long salaId;
        private Long tipoProyeccionId; // 2D, 3D
        private LocalDate fechaInicio;  // Fecha de inicio de proyección
        private LocalDate fechaFin;     // Fecha de fin de proyección
        private List<LocalTime> horarios; // Horarios diarios
    }

    @Data
    public static class SchedulingResponseDTO {
        private String mensaje;
        private int proyeccionesCreadas;
        private List<String> errores;
        private List<ProyeccionDetailDTO> detallesProyecciones;
    }

    @Data
    public static class ProyeccionDetailDTO {
        private Long proyeccionId;
        private String sala;
        private String formato;
        private LocalDate fecha;
        private LocalTime horario;
        private String estado;
    }
}