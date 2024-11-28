package com.sis.cine.service;

import com.sis.cine.model.TipoSala;
import com.sis.cine.repository.TipoSalaRepository;
import com.sis.cine.exception.ResourceNotFoundException;
import com.sis.cine.dto.adminDTOs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;


@Service
@Transactional
public class TipoSalaService {

    @Autowired
    private TipoSalaRepository tipoSalaRepository;

    /**
     * Crea un nuevo tipo de sala
     */
    public TipoSalaDTO crearTipoSala(TipoSalaDTO dto) {
        if (tipoSalaRepository.existsByNombre(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un tipo de sala con ese nombre");
        }

        TipoSala tipoSala = new TipoSala();
        tipoSala.setNombre(dto.getNombre());
        tipoSala.setDescripcion(dto.getDescripcion());
        tipoSala.setCaracteristicas(String.join(",", dto.getCaracteristicas()));
        tipoSala.setActivo(true);

        TipoSala savedTipoSala = tipoSalaRepository.save(tipoSala);
        return convertirADTO(savedTipoSala);
    }

    /**
     * Actualiza un tipo de sala existente
     */
    public TipoSalaDTO actualizarTipoSala(Long id, TipoSalaDTO dto) {
        TipoSala tipoSala = tipoSalaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de sala no encontrado"));

        // Validar nombre único si está cambiando
        if (!tipoSala.getNombre().equals(dto.getNombre()) &&
                tipoSalaRepository.existsByNombre(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un tipo de sala con ese nombre");
        }

        tipoSala.setNombre(dto.getNombre());
        tipoSala.setDescripcion(dto.getDescripcion());
        tipoSala.setCaracteristicas(String.join(",", dto.getCaracteristicas()));

        return convertirADTO(tipoSalaRepository.save(tipoSala));
    }

    /**
     * Convierte entidad a DTO incluyendo información adicional
     */
    private TipoSalaDTO convertirADTO(TipoSala tipoSala) {
        TipoSalaDTO dto = new TipoSalaDTO();
        dto.setId(tipoSala.getId());
        dto.setNombre(tipoSala.getNombre());
        dto.setDescripcion(tipoSala.getDescripcion());
        dto.setCaracteristicas(Arrays.asList(tipoSala.getCaracteristicas().split(",")));
        dto.setActivo(tipoSala.getActivo());
        dto.setCantidadSalas((long) tipoSala.getSalas().size());
        return dto;
    }
}