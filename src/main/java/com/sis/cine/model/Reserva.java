package com.sis.cine.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Reserva")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean activa;

    @Column(nullable = false)
    private Boolean pagada;

    @Column(nullable = false)
    private Boolean reservada;

    @ManyToOne
    @JoinColumn(name = "proyeccion_id", nullable = false)
    private Proyeccion proyeccion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String estadoPago;
}