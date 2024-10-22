package com.sis.cine.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Pelicula")
public class Pelicula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String titulo;

    @Column(nullable = false)
    private String director;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Integer duracion;

    private String estudio;
    @Column(nullable = false)
    private String trailer;

    @Column(nullable = false)
    private String poster;

    @Column(name = "enproyeccion", nullable = false)
    private Boolean enProyeccion;
}