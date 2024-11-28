package com.sis.cine.controller;

import com.sis.cine.exception.ResourceNotFoundException;
import com.sis.cine.model.Asiento;
import com.sis.cine.model.Sala;
import com.sis.cine.repository.AsientoRepository;
import com.sis.cine.repository.EntradaRepository;
import com.sis.cine.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sala")
public class SalaController {

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private AsientoRepository asientoRepository;

    @Autowired
    private EntradaRepository entradaRepository;

    @GetMapping("/{salaId}/asientos")
    public ResponseEntity<?> getMapaAsientos(
            @PathVariable Long salaId,
            @RequestParam Long proyeccionId) {

        // Obtener la sala y validar que existe
        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sala no encontrada"));

        // Obtener todos los asientos de la sala
        List<Asiento> asientos = asientoRepository.findBySalaIdOrderByFilaAndNumero(salaId);

        // Obtener asientos ocupados para la proyección
        List<Long> asientosOcupados = entradaRepository.findAsientosOcupadosByProyeccionId(proyeccionId);

        // Construir respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("salaId", sala.getId());
        response.put("nombreSala", sala.getNombre());
        response.put("tipoSala", sala.getTipoSala().getNombre());

        List<Map<String, Object>> asientosInfo = asientos.stream()
                .map(asiento -> {
                    Map<String, Object> asientoInfo = new HashMap<>();
                    asientoInfo.put("id", asiento.getId());
                    asientoInfo.put("fila", asiento.getFila());
                    asientoInfo.put("numero", asiento.getNumero());
                    asientoInfo.put("estado", asientosOcupados.contains(asiento.getId())
                            ? "OCUPADO" : "DISPONIBLE");
                    return asientoInfo;
                })
                .collect(Collectors.toList());

        response.put("asientos", asientosInfo);

        return ResponseEntity.ok(response);
    }
}