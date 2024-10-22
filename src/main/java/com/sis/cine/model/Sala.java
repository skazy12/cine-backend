package com.sis.cine.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Sala")
public class Sala {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "tipo_sala_id", nullable = false)
    private TipoSala tipoSala;
}