package com.sis.cine.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "TipoSala")
public class TipoSala {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "caracteristicas")
    private String caracteristicas;  // Por ejemplo: "Sonido Dolby,Butacas reclinables"

    @OneToMany(mappedBy = "tipoSala")
    private List<Sala> salas;

    @Column(name = "activo", nullable = true)
    private Boolean activo = true;
}