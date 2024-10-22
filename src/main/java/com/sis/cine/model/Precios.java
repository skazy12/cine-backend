package com.sis.cine.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "Precios")
public class Precios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String condiciones;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioFinal;

    @ManyToOne
    @JoinColumn(name = "tipo_sala_id", nullable = false)
    private TipoSala tipoSala;

    @ManyToOne
    @JoinColumn(name = "tipo_proyeccion_id", nullable = false)
    private TipoProyeccion tipoProyeccion;

    private String diaSemana;
}