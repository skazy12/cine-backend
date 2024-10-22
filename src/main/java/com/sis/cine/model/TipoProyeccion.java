package com.sis.cine.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "TipoProyeccion")
public class TipoProyeccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 2)
    private String nombre;
}