package com.sis.cine.service;

import com.sis.cine.dto.adminDTOs;
import com.sis.cine.model.Precios;
import com.sis.cine.model.TipoProyeccion;
import com.sis.cine.model.TipoSala;
import com.sis.cine.repository.PreciosRepository;
import com.sis.cine.repository.SalaRepository;
import com.sis.cine.repository.TipoProyeccionRepository;
import com.sis.cine.repository.TipoSalaRepository;
import jakarta.transaction.Transactional;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AdminPreciosService {
    @Autowired
    private PreciosRepository preciosRepository;
    @Autowired
    private SalaRepository salaRepository;
    @Autowired
    private TipoSalaRepository tipoSalaRepository;
    @Autowired
    private TipoProyeccionRepository tipoProyeccionRepository;

    public adminDTOs.PreciosDTO crearPrecio(adminDTOs.PreciosDTO preciosDTO) {
        // Validar tipos de sala y proyección
        TipoSala tipoSala = tipoSalaRepository.findById(preciosDTO.getTipoSalaId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de sala no encontrado"));

        TipoProyeccion tipoProyeccion = tipoProyeccionRepository.findById(preciosDTO.getTipoProyeccionId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de proyección no encontrado"));

        // Crear el precio
        Precios precio = new Precios();
        precio.setNombre(preciosDTO.getNombre());
        precio.setCondiciones(preciosDTO.getCondiciones());
        precio.setPrecioFinal(preciosDTO.getPrecioFinal());
        precio.setTipoSala(tipoSala);
        precio.setTipoProyeccion(tipoProyeccion);
        precio.setDiaSemana(preciosDTO.getDiaSemana());

        return convertirADTO(preciosRepository.save(precio));
    }
    /**
     * Convierte una entidad Precios a PreciosDTO
     * @param precio Entidad Precios a convertir
     * @return PreciosDTO con toda la información del precio y sus relaciones
     */
    private adminDTOs.PreciosDTO convertirADTO(Precios precio) {
        adminDTOs.PreciosDTO preciosDTO = new adminDTOs.PreciosDTO();

        // Información básica del precio
        preciosDTO.setId(precio.getId());
        preciosDTO.setNombre(precio.getNombre());
        preciosDTO.setCondiciones(precio.getCondiciones());
        preciosDTO.setPrecioFinal(precio.getPrecioFinal());
        preciosDTO.setDiaSemana(precio.getDiaSemana());

        // Información del tipo de sala
        if (precio.getTipoSala() != null) {
            preciosDTO.setTipoSalaId(precio.getTipoSala().getId());
            preciosDTO.setTipoSalaNombre(precio.getTipoSala().getNombre());
        }

        // Información del tipo de proyección
        if (precio.getTipoProyeccion() != null) {
            preciosDTO.setTipoProyeccionId(precio.getTipoProyeccion().getId());
            preciosDTO.setTipoProyeccionNombre(precio.getTipoProyeccion().getNombre());
        }

        return preciosDTO;
    }

    // ... otros métodos de servicio
}