package com.sis.cine.service;

import com.sis.cine.model.Asiento;
import com.sis.cine.model.Sala;
import com.sis.cine.repository.AsientoRepository;
import com.sis.cine.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaService {
    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private AsientoRepository asientoRepository;

    public Sala crearSalaConAsientos(Sala sala, List<Asiento> asientos) {
        // Validar que no exista una sala con el mismo nombre
        if (salaRepository.existsByNombre(sala.getNombre())) {
            throw new IllegalArgumentException("Ya existe una sala con ese nombre");
        }

        // Guardar la sala
        Sala nuevaSala = salaRepository.save(sala);

        // Asignar y guardar los asientos
        asientos.forEach(asiento -> {
            asiento.setSala(nuevaSala);
            asientoRepository.save(asiento);
        });

        return salaRepository.findByIdWithAsientos(nuevaSala.getId())
                .orElseThrow();
    }
}