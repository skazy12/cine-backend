package com.sis.cine.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Reserva")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "proyeccion_id", nullable = false)
    private Proyeccion proyeccion;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDateTime fechaReserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Usuario_id", nullable = false)
    private Usuario usuario;
}