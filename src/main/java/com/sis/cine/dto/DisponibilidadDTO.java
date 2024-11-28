package com.sis.cine.dto;

import lombok.Data;

@Data
public class DisponibilidadDTO {
    private Long proyeccionId;
    private int asientosTotales;
    private int asientosDisponibles;
    private boolean disponible;
    private String mensaje;
}