package com.sis.cine.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "MetodoPago")
public class MetodoPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;
}