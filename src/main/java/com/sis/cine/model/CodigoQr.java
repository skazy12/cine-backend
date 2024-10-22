package com.sis.cine.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "CodigoQr")
public class CodigoQr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "pago_id", nullable = false, unique = true)
    private Pago pago;

    @Column(nullable = false)
    private String codigoQr;

    @Column(nullable = false)
    private LocalDateTime fechaGeneracion;

    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;
}