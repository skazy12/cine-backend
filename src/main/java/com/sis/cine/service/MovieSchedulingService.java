package com.sis.cine.service;

import com.sis.cine.dto.MovieSchedulingDTO;
import com.sis.cine.exception.ResourceNotFoundException;
import com.sis.cine.model.*;
import com.sis.cine.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MovieSchedulingService {

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private ProyeccionRepository proyeccionRepository;

    @Autowired
    private TipoProyeccionRepository tipoProyeccionRepository;
    @Autowired
    private EntradaRepository entradaRepository;

    /**
     * Programa múltiples proyecciones para una película
     * @param schedulingDTO Datos de programación
     * @return Respuesta con detalles de las proyecciones creadas
     */
    public MovieSchedulingDTO.SchedulingResponseDTO programarPelicula(MovieSchedulingDTO schedulingDTO) {
        // Inicializar respuesta
        MovieSchedulingDTO.SchedulingResponseDTO response = new MovieSchedulingDTO.SchedulingResponseDTO();
        List<String> errores = new ArrayList<>();
        List<MovieSchedulingDTO.ProyeccionDetailDTO> detalles = new ArrayList<>();

        // Validar película
        Pelicula pelicula = peliculaRepository.findById(schedulingDTO.getPeliculaId())
                .orElseThrow(() -> new ResourceNotFoundException("Película no encontrada"));

        // Procesar cada programación
        for (MovieSchedulingDTO.ProyeccionProgramadaDTO prog : schedulingDTO.getProyecciones()) {
            try {
                // Validar sala y tipo de proyección
                Sala sala = salaRepository.findById(prog.getSalaId())
                        .orElseThrow(() -> new ResourceNotFoundException("Sala no encontrada"));

                TipoProyeccion tipoProyeccion = tipoProyeccionRepository.findById(prog.getTipoProyeccionId())
                        .orElseThrow(() -> new ResourceNotFoundException("Tipo de proyección no encontrado"));

                // Crear proyecciones para cada día y horario
                LocalDate currentDate = prog.getFechaInicio();
                while (!currentDate.isAfter(prog.getFechaFin())) {
                    for (LocalTime horario : prog.getHorarios()) {
                        // Validar disponibilidad de sala
                        if (isSalaDisponible(sala.getId(), currentDate, horario, pelicula.getDuracion())) {
                            // Crear nueva proyección
                            Proyeccion proyeccion = new Proyeccion();
                            proyeccion.setPelicula(pelicula);
                            proyeccion.setSala(sala);
                            proyeccion.setTipoProyeccion(tipoProyeccion);
                            proyeccion.setDia(currentDate);
                            proyeccion.setComienzo(horario);

                            Proyeccion saved = proyeccionRepository.save(proyeccion);
                            detalles.add(crearProyeccionDetail(saved));
                        } else {
                            errores.add(String.format(
                                    "Sala %s no disponible para fecha %s horario %s",
                                    sala.getNombre(), currentDate, horario
                            ));
                        }
                    }
                    currentDate = currentDate.plusDays(1);
                }
            } catch (Exception e) {
                errores.add("Error procesando programación: " + e.getMessage());
            }
        }

        // Preparar respuesta
        response.setMensaje("Programación completada");
        response.setProyeccionesCreadas(detalles.size());
        response.setErrores(errores);
        response.setDetallesProyecciones(detalles);

        return response;
    }

    /**
     * Verifica si una sala está disponible para un horario específico
     */
    private boolean isSalaDisponible(Long salaId, LocalDate fecha, LocalTime horario, int duracionMinutos) {
        LocalTime horaFin = horario.plusMinutes(duracionMinutos);

        // Obtener proyecciones existentes para la sala en esa fecha
        List<Proyeccion> proyeccionesExistentes = proyeccionRepository.findBySalaIdAndDia(salaId, fecha);

        // Verificar superposición con otras proyecciones
        for (Proyeccion p : proyeccionesExistentes) {
            LocalTime pInicio = p.getComienzo();
            LocalTime pFin = pInicio.plusMinutes(p.getPelicula().getDuracion());

            // Si hay superposición
            if (!(horario.isAfter(pFin) || horaFin.isBefore(pInicio))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Crea un DTO con detalles de la proyección
     */
    private MovieSchedulingDTO.ProyeccionDetailDTO crearProyeccionDetail(Proyeccion proyeccion) {
        MovieSchedulingDTO.ProyeccionDetailDTO detail = new MovieSchedulingDTO.ProyeccionDetailDTO();
        detail.setProyeccionId(proyeccion.getId());
        detail.setSala(proyeccion.getSala().getNombre());
        detail.setFormato(proyeccion.getTipoProyeccion().getNombre());
        detail.setFecha(proyeccion.getDia());
        detail.setHorario(proyeccion.getComienzo());
        detail.setEstado("PROGRAMADA");
        return detail;
    }

    /**
     * Obtiene todas las proyecciones programadas para una película
     */
    public List<MovieSchedulingDTO.ProyeccionDetailDTO> obtenerProgramacionPelicula(Long peliculaId) {
        return proyeccionRepository.findByPeliculaId(peliculaId)
                .stream()
                .map(this::crearProyeccionDetail)
                .collect(Collectors.toList());
    }

    /**
     * Cancela una proyección específica
     */
    public void cancelarProyeccion(Long proyeccionId) {
        Proyeccion proyeccion = proyeccionRepository.findById(proyeccionId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyección no encontrada"));

        // Verificar si hay entradas vendidas
        boolean hayEntradas = entradaRepository.countByReservaProyeccionId(proyeccionId) > 0;
        if (hayEntradas) {
            throw new IllegalStateException("No se puede cancelar una proyección con entradas vendidas");
        }

        proyeccionRepository.delete(proyeccion);
    }
}