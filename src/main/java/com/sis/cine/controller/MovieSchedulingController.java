package com.sis.cine.controller;

import com.sis.cine.dto.MovieSchedulingDTO;
import com.sis.cine.service.MovieSchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/programacion")
@PreAuthorize("hasRole('ADMIN')")
public class MovieSchedulingController {

    @Autowired
    private MovieSchedulingService movieSchedulingService;

    /**
     * Programa múltiples proyecciones para una película
     * @param schedulingDTO Datos de programación de la película
     * @return Respuesta con detalles de las proyecciones creadas
     */
    @PostMapping("/peliculas")
    public ResponseEntity<MovieSchedulingDTO.SchedulingResponseDTO> programarPelicula(
            @RequestBody MovieSchedulingDTO schedulingDTO) {
        return ResponseEntity.ok(movieSchedulingService.programarPelicula(schedulingDTO));
    }

    /**
     * Obtiene todas las proyecciones programadas para una película
     * @param peliculaId ID de la película
     * @return Lista de proyecciones programadas
     */
    @GetMapping("/peliculas/{peliculaId}/proyecciones")
    public ResponseEntity<List<MovieSchedulingDTO.ProyeccionDetailDTO>> obtenerProgramacion(
            @PathVariable Long peliculaId) {
        return ResponseEntity.ok(movieSchedulingService.obtenerProgramacionPelicula(peliculaId));
    }

    /**
     * Cancela una proyección específica
     * @param proyeccionId ID de la proyección a cancelar
     */
    @DeleteMapping("/proyecciones/{proyeccionId}")
    public ResponseEntity<Void> cancelarProyeccion(@PathVariable Long proyeccionId) {
        movieSchedulingService.cancelarProyeccion(proyeccionId);
        return ResponseEntity.ok().build();
    }
}