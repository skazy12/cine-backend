package com.sis.cine.model;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Asiento")
public class Asiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1)
    private String fila;

    @Column(nullable = false, length = 1)
    private String letra;

    @ManyToOne
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;
}