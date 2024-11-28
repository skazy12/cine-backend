package com.sis.cine.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Pago")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = true)
    private LocalDateTime fechaPago;

    // Nuevos campos para QR
    @Column(nullable = false)
    private LocalDateTime fechaGeneracion;

    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(name = "datos_qr", columnDefinition = "TEXT", nullable = false)
    private String datosQr;

    @Column(name = "estado_pago", length = 25, nullable = false)
    private String estadoPago;
}