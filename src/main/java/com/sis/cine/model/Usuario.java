package com.sis.cine.model;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String contrasena;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    // Nuevo campo para el número de teléfono (antes en Cliente)
    @Column(nullable = true)
    private Long tlfn;


}