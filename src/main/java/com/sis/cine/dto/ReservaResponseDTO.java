package com.sis.cine.dto;

import com.sis.cine.model.EstadoPago;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReservaResponseDTO {
    private Long reservaId;
    private LocalDateTime fechaExpiracion;
    private String codigoPago;
    private EstadoPago estadoPago;
    private List<String> asientosSeleccionados;
    private Double montoTotal;
}