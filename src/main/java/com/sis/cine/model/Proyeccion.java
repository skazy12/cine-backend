package com.sis.cine.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "Proyeccion")
public class Proyeccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime comienzo;

    @Column(nullable = false)
    private LocalDate dia;

    @ManyToOne
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    @ManyToOne
    @JoinColumn(name = "pelicula_id", nullable = false)
    private Pelicula pelicula;

    @ManyToOne
    @JoinColumn(name = "tipo_proyeccion_id", nullable = false)
    private TipoProyeccion tipoProyeccion;
}