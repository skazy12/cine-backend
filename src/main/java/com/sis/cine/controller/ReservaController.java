package com.sis.cine.controller;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sis.cine.dto.EntradaDigitalDTO;
import com.sis.cine.dto.ReservaResponseDTO;
import com.sis.cine.exception.UnauthorizedException;
import com.sis.cine.service.ReservaService;
import com.sis.cine.util.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "API para gestión de reservas y entradas")

public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private JwtUtils jwtUtils;

    @Operation(summary = "Crear reserva temporal",
            description = "Crea una reserva temporal con tiempo límite para pago")
    @ApiResponse(responseCode = "200", description = "Reserva creada exitosamente")
    @ApiResponse(responseCode = "409", description = "Asientos no disponibles")
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crearReserva(
            @Parameter(description = "ID de la proyección") @RequestParam Long proyeccionId,
            @Parameter(description = "Lista de asientos (ej: ['A1', 'A2'])") @RequestBody List<String> asientos,
            Authentication authentication) {

        Long usuarioId = obtenerUsuarioIdDeAuthentication(authentication);
        ReservaResponseDTO reserva = reservaService.crearReservaTemporal(proyeccionId, asientos, usuarioId);
        return ResponseEntity.ok(reserva);
    }

    @Operation(summary = "Procesar pago de reserva",
            description = "Procesa el pago de una reserva y genera las entradas digitales")
    @ApiResponse(responseCode = "200", description = "Pago procesado exitosamente")
    @ApiResponse(responseCode = "410", description = "Tiempo de pago expirado")
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/{reservaId}/pago")
    public ResponseEntity<List<EntradaDigitalDTO>> procesarPago(
            @Parameter(description = "ID de la reserva") @PathVariable Long reservaId,
            @Parameter(description = "Datos del comprobante de pago") @RequestBody Map<String, String> datoPago) {

        List<EntradaDigitalDTO> entradas = reservaService.procesarPago(
                reservaId,
                datoPago.get("comprobante")
        );
        return ResponseEntity.ok(entradas);
    }

    @Operation(summary = "Procesar pago simple para pruebas",
            description = "Método simplificado para pruebas de pago")
    @ApiResponse(responseCode = "200", description = "Pago procesado exitosamente")
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/{reservaId}/pago-simple")
    public ResponseEntity<List<EntradaDigitalDTO>> procesarPagoSimple(
            @Parameter(description = "ID de la reserva") @PathVariable Long reservaId) {

        // Generar comprobante simple para pruebas
        String comprobante = "QR_SIMPLE_" + LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + "_" + reservaId;

        List<EntradaDigitalDTO> entradas = reservaService.procesarPago(reservaId, comprobante);
        return ResponseEntity.ok(entradas);
    }

    @Operation(summary = "Verificar entrada digital",
            description = "Verifica la validez de una entrada digital mediante su QR")
    @ApiResponse(responseCode = "200", description = "Entrada verificada")
    @PostMapping("/verificar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> verificarEntrada(
            @Parameter(description = "Contenido del código QR") @RequestBody String qrContent) {

        boolean esValida = reservaService.verificarEntrada(qrContent);
        return ResponseEntity.ok(Map.of("valida", esValida));
    }

    @Operation(summary = "Obtener reservas del usuario",
            description = "Obtiene el historial de reservas del usuario autenticado")
    @GetMapping("/mis-reservas")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerMisReservas(Authentication authentication) {
        Long usuarioId = obtenerUsuarioIdDeAuthentication(authentication);
        List<ReservaResponseDTO> reservas = reservaService.obtenerReservasUsuario(usuarioId);
        return ResponseEntity.ok(reservas);
    }

    @Operation(summary = "Obtener entradas de una reserva",
            description = "Obtiene las entradas digitales asociadas a una reserva")
    @GetMapping("/{reservaId}/entradas")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<EntradaDigitalDTO>> obtenerEntradas(
            @PathVariable Long reservaId,
            Authentication authentication) {

        Long usuarioId = obtenerUsuarioIdDeAuthentication(authentication);
        // Verificar que la reserva pertenezca al usuario
        List<EntradaDigitalDTO> entradas = reservaService.obtenerEntradasReserva(reservaId, usuarioId);
        return ResponseEntity.ok(entradas);
    }

    private Long obtenerUsuarioIdDeAuthentication(Authentication authentication) {
        try {
            String authHeader = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest()
                    .getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new UnauthorizedException("Token no proporcionado o formato inválido");
            }

            String token = authHeader.substring(7);
            DecodedJWT decodedJWT = jwtUtils.validateToken(token);

            // Obtener el userID como String y convertirlo a Long
            String userIdStr = decodedJWT.getClaim("userId").asString();
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                throw new UnauthorizedException("ID de usuario no encontrado en el token");
            }

            try {
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                throw new UnauthorizedException("ID de usuario inválido en el token");
            }

        } catch (Exception e) {
            throw new UnauthorizedException("Error al obtener ID del usuario: " + e.getMessage());
        }
    }
}