package com.sis.cine.service;

import com.sis.cine.dto.adminDTOs;
import com.sis.cine.model.Pelicula;
import com.sis.cine.model.Proyeccion;
import com.sis.cine.model.Sala;
import com.sis.cine.model.TipoProyeccion;
import com.sis.cine.repository.PeliculaRepository;
import com.sis.cine.repository.SalaRepository;
import com.sis.cine.repository.ProyeccionRepository;
import com.sis.cine.repository.TipoProyeccionRepository;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AdminProyeccionService {
    @Autowired
    private ProyeccionRepository proyeccionRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private TipoProyeccionRepository tipoProyeccionRepository;

    /**
     * Crea una nueva proyección de película
     * @param proyeccionDTO datos de la proyección a crear
     * @return la proyección creada
     */
    /*
    public adminDTOs.ProyeccionDTO crearProyeccion(adminDTOs.ProyeccionDTO proyeccionDTO) {
        // 1. Validar que existan todos los elementos necesarios
        Pelicula pelicula = peliculaRepository.findById(proyeccionDTO.getPeliculaId())
                .orElseThrow(() -> new ResourceNotFoundException("Película no encontrada"));

        Sala sala = salaRepository.findById(proyeccionDTO.getSalaId())
                .orElseThrow(() -> new ResourceNotFoundException("Sala no encontrada"));

        TipoProyeccion tipoProyeccion = tipoProyeccionRepository.findById(proyeccionDTO.getTipoProyeccionId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de proyección no encontrado"));

        // 2. Validar que la película esté en proyección
        if (!pelicula.getEnProyeccion()) {
            throw new BadRequestException("La película no está disponible para proyección");
        }

        // 3. Validar que no haya superposición de horarios en la misma sala
        validarDisponibilidadHorario(proyeccionDTO.getSalaId(),
                proyeccionDTO.getDia(),
                proyeccionDTO.getComienzo(),
                pelicula.getDuracion());

        // 4. Crear la proyección
        Proyeccion proyeccion = new Proyeccion();
        proyeccion.setPelicula(pelicula);
        proyeccion.setSala(sala);
        proyeccion.setTipoProyeccion(tipoProyeccion);
        proyeccion.setDia(proyeccionDTO.getDia());
        proyeccion.setComienzo(proyeccionDTO.getComienzo());

        // 5. Guardar y convertir a DTO
        Proyeccion savedProyeccion = proyeccionRepository.save(proyeccion);
        return convertirADTO(savedProyeccion);
    }

    /**
     * Valida que no haya superposición de horarios en la sala
     */

//    private void validarDisponibilidadHorario(Long salaId, LocalDate dia, LocalTime horaInicio, Integer duracionMinutos) {
//        // Calcular hora de finalización
//        LocalTime horaFin = horaInicio.plusMinutes(duracionMinutos);
//
//        // Buscar proyecciones en la misma sala y día
//        List<Proyeccion> proyeccionesExistentes = proyeccionRepository
//                .findBySalaIdAndDia(salaId, dia);
//
//        // Verificar superposición
//        for (Proyeccion proyeccionExistente : proyeccionesExistentes) {
//            LocalTime inicioExistente = proyeccionExistente.getComienzo();
//            LocalTime finExistente = inicioExistente.plusMinutes(
//                    proyeccionExistente.getPelicula().getDuracion()
//            );
//
//            if (horaInicio.isBefore(finExistente) && horaFin.isAfter(inicioExistente)) {
//                throw new BadRequestException(
//                        "Existe superposición con otra proyección en la sala. " +
//                                "Horario ocupado: " + inicioExistente + " - " + finExistente
//                );
//            }
//        }
//    }
//
//    /**
//     * Lista las proyecciones con filtros opcionales
//     */
//    public List<ProyeccionDTO> listarProyecciones(Long salaId, Long peliculaId, LocalDate fecha) {
//        List<Proyeccion> proyecciones;
//
//        if (salaId != null && peliculaId != null && fecha != null) {
//            proyecciones = proyeccionRepository.findBySalaIdAndPeliculaIdAndDia(salaId, peliculaId, fecha);
//        } else if (salaId != null && fecha != null) {
//            proyecciones = proyeccionRepository.findBySalaIdAndDia(salaId, fecha);
//        } else if (peliculaId != null && fecha != null) {
//            proyecciones = proyeccionRepository.findByPeliculaIdAndDia(peliculaId, fecha);
//        } else if (fecha != null) {
//            proyecciones = proyeccionRepository.findByDia(fecha);
//        } else {
//            proyecciones = proyeccionRepository.findAll();
//        }
//
//        return proyecciones.stream()
//                .map(this::convertirADTO)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Convierte una Proyeccion a ProyeccionDTO
//     */
//    private ProyeccionDTO convertirADTO(Proyeccion proyeccion) {
//        ProyeccionDTO dto = new ProyeccionDTO();
//        dto.setId(proyeccion.getId());
//        dto.setPeliculaId(proyeccion.getPelicula().getId());
//        dto.setSalaId(proyeccion.getSala().getId());
//        dto.setTipoProyeccionId(proyeccion.getTipoProyeccion().getId());
//        dto.setDia(proyeccion.getDia());
//        dto.setComienzo(proyeccion.getComienzo());
//
//        // Información adicional útil
//        dto.setTituloPelicula(proyeccion.getPelicula().getTitulo());
//        dto.setNombreSala(proyeccion.getSala().getNombre());
//        dto.setDuracionPelicula(proyeccion.getPelicula().getDuracion());
//        dto.setTipoProyeccion(proyeccion.getTipoProyeccion().getNombre());
//
//        return dto;
//    }

}
