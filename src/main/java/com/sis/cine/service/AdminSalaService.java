package com.sis.cine.service;

import com.sis.cine.dto.adminDTOs.*;
import com.sis.cine.model.*;
import com.sis.cine.repository.*;
import com.sis.cine.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminSalaService {
    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private AsientoRepository asientoRepository;

    @Autowired
    private TipoSalaRepository tipoSalaRepository;

    public SalaDTO crearSala(SalaDTO salaDTO) {
        // Validar tipo de sala
        TipoSala tipoSala = tipoSalaRepository.findById(salaDTO.getTipoSalaId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de sala no encontrado"));

        // Crear la sala
        Sala sala = new Sala();
        sala.setNombre(salaDTO.getNombre());
        sala.setTipoSala(tipoSala);
        sala = salaRepository.save(sala);

        // Crear asientos
        for (AsientoDTO asientoDTO : salaDTO.getAsientos()) {
            Asiento asiento = new Asiento();
            asiento.setFila(asientoDTO.getFila());
            asiento.setNumero(asientoDTO.getNumero());
            asiento.setSala(sala);
            asientoRepository.save(asiento);
        }

        return convertirADTO(sala);
    }
    /**
     * Convierte una entidad Sala a SalaDTO incluyendo sus asientos
     * @param sala Entidad Sala a convertir
     * @return SalaDTO con toda la información de la sala y sus asientos
     */
    private SalaDTO convertirADTO(Sala sala) {
        // Crear el DTO base de la sala
        SalaDTO salaDTO = new SalaDTO();
        salaDTO.setId(sala.getId());
        salaDTO.setNombre(sala.getNombre());
        salaDTO.setTipoSalaId(sala.getTipoSala().getId());

        // Obtener y convertir los asientos
        List<AsientoDTO> asientosDTO = sala.getAsientos().stream()
                .map(asiento -> {
                    AsientoDTO asientoDTO = new AsientoDTO();
                    asientoDTO.setId(asiento.getId());
                    asientoDTO.setFila(asiento.getFila());
                    asientoDTO.setNumero(asiento.getNumero());
                    asientoDTO.setDisponible(true); // Por defecto al crear
                    return asientoDTO;
                })
                .sorted((a1, a2) -> { // Ordenar asientos por fila y letra
                    int compareFilas = a1.getFila().compareTo(a2.getFila());
                    if (compareFilas == 0) {
                        return a1.getNumero().compareTo(a2.getNumero());
                    }
                    return compareFilas;
                })
                .collect(Collectors.toList());

        salaDTO.setAsientos(asientosDTO);

        // Agregar información adicional útil
        salaDTO.setCapacidad(asientosDTO.size());
        salaDTO.setTipoSalaNombre(sala.getTipoSala().getNombre());

        return salaDTO;
    }


    // ... otros métodos de servicio
}