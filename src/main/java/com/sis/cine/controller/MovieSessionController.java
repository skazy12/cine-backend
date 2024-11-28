package com.sis.cine.controller;

import com.sis.cine.dto.DisponibilidadDTO;
import com.sis.cine.dto.ErrorResponse;
import com.sis.cine.dto.MovieSessionDTO;
import com.sis.cine.exception.ResourceNotFoundException;
import com.sis.cine.service.MovieSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import shaded_package.io.swagger.v3.oas.annotations.Operation;
import shaded_package.io.swagger.v3.oas.annotations.Parameter;
import shaded_package.io.swagger.v3.oas.annotations.media.Content;
import shaded_package.io.swagger.v3.oas.annotations.media.Schema;
import shaded_package.io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/peliculas")
public class MovieSessionController {

    @Autowired
    private MovieSessionService movieSessionService;

    /**
     * Obtiene la información de sesiones de la película
     * Si no se especifica fecha o formato, se usan valores por defecto
     */

    @Operation(
            summary = "Obtener sesiones de una película",
            description = "Obtiene todas las sesiones disponibles para una película específica, " +
                    "permitiendo filtrar por fecha y formato de proyección. " +
                    "Si no se especifica fecha, se usa la fecha actual. " +
                    "Si no se especifica formato, se usa el primer formato disponible."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Sesiones encontradas correctamente",
            content = @Content(schema = @Schema(implementation = MovieSessionDTO.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Película no encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )

    @GetMapping("/{id}/sesiones")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
    public ResponseEntity<MovieSessionDTO> getMovieSessions(
            @Parameter(description = "ID de la película", required = true)
            @PathVariable Long id,

            @Parameter(description = "Fecha para buscar sesiones (formato: yyyy-MM-dd)", example = "2024-11-24")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,

            @Parameter(description = "Formato de proyección (2D o 3D)", example = "2D")
            @RequestParam(required = false) String formato) {

        try {
            MovieSessionDTO sessions = movieSessionService.getMovieSessions(id, fecha, formato);
            return ResponseEntity.ok(sessions);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las sesiones de la película", e);
        }
    }

    @Operation(summary = "Verificar disponibilidad de sesión")
    @GetMapping("/{id}/sesiones/{proyeccionId}/disponibilidad")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENTE')")
    public ResponseEntity<DisponibilidadDTO> checkSessionAvailability(
            @PathVariable Long id,
            @PathVariable Long proyeccionId) {

        DisponibilidadDTO disponibilidad = movieSessionService.checkSessionAvailability(id, proyeccionId);
        return ResponseEntity.ok(disponibilidad);
    }
}