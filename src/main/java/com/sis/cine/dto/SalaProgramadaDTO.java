package com.sis.cine.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class SalaProgramadaDTO {
    private Long salaId;
    private String nombreSala;
    private LocalDate fechaProyeccion;
    private LocalTime horaProyeccion;
}
