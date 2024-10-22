package com.sis.cine.dto;

import lombok.Data;

@Data
public class PeliculaDTO {
    private Long id;
    private String titulo;
    private String director;
    private String descripcion;
    private Integer duracion;
    private String estudio;
    private String poster;
    private String trailer;
    private Boolean enProyeccion;
}