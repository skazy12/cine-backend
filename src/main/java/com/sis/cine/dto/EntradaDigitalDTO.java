package com.sis.cine.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class EntradaDigitalDTO {
    // Identificadores básicos
    private Long id;
    private Long reservaId;
    private Long peliculaId;
    private Long proyeccionId;  // Agregado para referencia a la proyección

    // Información de la entrada
    private String codigoQR;
    private LocalDateTime fechaEmision;
    private String estado; // ACTIVA, USADA, CANCELADA

    // Información de película y proyección
    private String tituloPelicula;
    private String posterUrl;
    private LocalDateTime fechaProyeccion;
    private String formato; // 2D, 3D

    // Información de sala y asiento
    private String nombreSala;
    private String tipoSala;
    private String asientoFila;
    private String asientoNumero;
    private String asientoCompleto; // Combinación de fila y número (ej: "F12")

    // Información de pago
    private BigDecimal precio;
    private String tipoPrecio;
    private LocalDateTime fechaValidez;
}