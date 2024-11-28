package com.sis.cine.dto;


import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;

@Data
public class EntradaDTO {
    // Identificadores y datos básicos
    private Long id;
    private Long reservaId;
    private String codigoQR;
    private LocalDateTime fechaGeneracion;
    private String estadoEntrada; // ACTIVA, USADA, CANCELADA

    // Información de la película y proyección
    private Long peliculaId;
    private String tituloPelicula;
    private String posterPelicula;
    private LocalDate fechaProyeccion;
    private LocalTime horaProyeccion;
    private String formatoProyeccion; // 2D, 3D
    private Integer duracionMinutos;

    // Información de la sala y asiento
    private String nombreSala;
    private String tipoSala; // Normal, VIP
    private String asientoFila;
    private String asientoNumero;
    private String asientoCompleto; // Ejemplo: "F12"

    // Información de precios
    private BigDecimal precioBase;
    private BigDecimal precioFinal;
    private String tipoPrecio; // Regular, Estudiante, Adulto Mayor
    private String condicionesPrecio;

    // Datos del usuario
    private Long usuarioId;
    private String nombreUsuario;
    private String emailUsuario;

    // Datos para validación
    private Long pagoCodigo;
    private LocalDateTime fechaExpiracion;
}