package com.sis.cine.service;

import com.sis.cine.dto.DisponibilidadDTO;
import com.sis.cine.dto.MovieSessionDTO;
import com.sis.cine.exception.ResourceNotFoundException;
import com.sis.cine.model.*;
import com.sis.cine.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MovieSessionService {

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private ProyeccionRepository proyeccionRepository;

    @Autowired
    private EntradaRepository entradaRepository;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    public MovieSessionDTO getMovieSessions(Long peliculaId, LocalDate fechaParam, String formatoParam) {
        Pelicula pelicula = peliculaRepository.findById(peliculaId)
                .orElseThrow(() -> new ResourceNotFoundException("Película no encontrada"));

        MovieSessionDTO sessionDTO = new MovieSessionDTO();
        mapPeliculaToDTO(pelicula, sessionDTO);

        // Obtener fecha actual si no se especifica
        LocalDate fechaActual = fechaParam != null ? fechaParam : LocalDate.now();
        sessionDTO.setFechaSeleccionada(fechaActual);

        // Obtener formatos disponibles primero
        List<MovieSessionDTO.FormatoDTO> formatos = getFormatosDisponibles(peliculaId);

        // Determinar formato seleccionado
        String formatoSeleccionado;
        if (formatoParam != null) {
            formatoSeleccionado = formatoParam;
        } else {
            formatoSeleccionado = formatos.isEmpty() ? null : formatos.get(0).getNombre();
        }
        sessionDTO.setFormatoSeleccionado(formatoSeleccionado);

        // Actualizar estado activo de formatos
        formatos = actualizarFormatosActivos(formatos, formatoSeleccionado);
        sessionDTO.setFormatos(formatos);

        // Obtener y actualizar fechas disponibles
        List<MovieSessionDTO.FechaDTO> fechasDisponibles = getNextSevenDays(fechaActual);
        actualizarFechasActivas(fechasDisponibles, fechaActual);
        sessionDTO.setFechasDisponibles(fechasDisponibles);

        // Obtener horarios si hay formato seleccionado
        if (formatoSeleccionado != null) {
            List<MovieSessionDTO.HorarioDTO> horarios = getHorarios(peliculaId, fechaActual, formatoSeleccionado);
            sessionDTO.setHorarios(horarios);
        }

        return sessionDTO;
    }

    private void mapPeliculaToDTO(Pelicula pelicula, MovieSessionDTO dto) {
        dto.setId(pelicula.getId());
        dto.setTitulo(pelicula.getTitulo());
        dto.setDuracion(pelicula.getDuracion());
        dto.setGenero(pelicula.getGenero());
        dto.setClasificacion(pelicula.getClasificacion());
        dto.setPoster(firebaseStorageService.generateSignedUrl(pelicula.getPoster()));
        dto.setTrailer(pelicula.getTrailer());
        dto.setEsEstreno(pelicula.getEnProyeccion()); // Usar el valor real de la BD
    }

    private List<MovieSessionDTO.FormatoDTO> getFormatosDisponibles(Long peliculaId) {
        return proyeccionRepository.findDistinctTipoProyeccionByPeliculaId(peliculaId)
                .stream()
                .map(tipo -> {
                    MovieSessionDTO.FormatoDTO formatoDTO = new MovieSessionDTO.FormatoDTO();
                    formatoDTO.setNombre(tipo.getNombre());
                    formatoDTO.setActivo(false);
                    return formatoDTO;
                })
                .collect(Collectors.toList());
    }

    private List<MovieSessionDTO.FormatoDTO> actualizarFormatosActivos(
            List<MovieSessionDTO.FormatoDTO> formatos,
            String formatoSeleccionado) {
        return formatos.stream()
                .peek(formato ->
                        formato.setActivo(formato.getNombre().equals(formatoSeleccionado)))
                .collect(Collectors.toList());
    }

    private void actualizarFechasActivas(
            List<MovieSessionDTO.FechaDTO> fechas,
            LocalDate fechaSeleccionada) {
        fechas.forEach(fecha ->
                fecha.setActivo(fecha.getFecha().equals(fechaSeleccionada)));
    }

    private List<MovieSessionDTO.FechaDTO> getNextSevenDays(LocalDate startDate) {
        List<MovieSessionDTO.FechaDTO> fechas = new ArrayList<>();
        Locale spanish = new Locale("es", "ES");

        for (int i = 0; i < 7; i++) {
            LocalDate fecha = startDate.plusDays(i);
            MovieSessionDTO.FechaDTO fechaDTO = new MovieSessionDTO.FechaDTO();
            fechaDTO.setFecha(fecha);
            fechaDTO.setNombreDia(fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, spanish).toLowerCase());
            fechaDTO.setNumeroDia(String.valueOf(fecha.getDayOfMonth()));
            fechaDTO.setNombreMes(fecha.getMonth().getDisplayName(TextStyle.SHORT, spanish).toLowerCase());
            fechaDTO.setActivo(false); // Se actualizará después
            fechas.add(fechaDTO);
        }

        return fechas;
    }

    private List<MovieSessionDTO.HorarioDTO> getHorarios(Long peliculaId, LocalDate fecha, String formato) {
        return proyeccionRepository.findByPeliculaIdAndDiaAndTipoProyeccionNombre(peliculaId, fecha, formato)
                .stream()
                .map(proyeccion -> {
                    MovieSessionDTO.HorarioDTO horarioDTO = new MovieSessionDTO.HorarioDTO();
                    horarioDTO.setProyeccionId(proyeccion.getId());
                    horarioDTO.setHora(proyeccion.getComienzo());

                    // Calcular disponibilidad
                    int asientosTotales = proyeccion.getSala().getAsientos().size();
                    int asientosOcupados = entradaRepository.countByReservaProyeccionId(proyeccion.getId());
                    int disponibles = asientosTotales - asientosOcupados;

                    horarioDTO.setAsientosDisponibles(disponibles);
                    horarioDTO.setDisponible(disponibles > 0);

                    // Agregar información de la sala
                    horarioDTO.setSalaNombre(proyeccion.getSala().getNombre());
                    horarioDTO.setTipoSala(proyeccion.getSala().getTipoSala().getNombre());

                    return horarioDTO;
                })
                .sorted(Comparator.comparing(MovieSessionDTO.HorarioDTO::getHora))
                .collect(Collectors.toList());
    }
    public DisponibilidadDTO checkSessionAvailability(Long peliculaId, Long proyeccionId) {
        Proyeccion proyeccion = proyeccionRepository.findByIdAndPeliculaId(proyeccionId, peliculaId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyección no encontrada"));

        DisponibilidadDTO dto = new DisponibilidadDTO();
        dto.setProyeccionId(proyeccionId);

        int asientosTotales = proyeccion.getSala().getAsientos().size();
        int asientosOcupados = entradaRepository.countByReservaProyeccionId(proyeccionId);
        int disponibles = asientosTotales - asientosOcupados;

        dto.setAsientosTotales(asientosTotales);
        dto.setAsientosDisponibles(disponibles);
        dto.setDisponible(disponibles > 0);

        if (disponibles > 0) {
            dto.setMensaje(String.format("Hay %d asientos disponibles", disponibles));
        } else {
            dto.setMensaje("No hay asientos disponibles para esta función");
        }

        return dto;
    }
}