package com.sis.cine.controller;

import com.sis.cine.exception.ResourceNotFoundException;
import com.sis.cine.model.Asiento;
import com.sis.cine.model.Proyeccion;
import com.sis.cine.model.Sala;
import com.sis.cine.repository.AsientoRepository;
import com.sis.cine.repository.EntradaRepository;
import com.sis.cine.repository.ProyeccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proyecciones")
public class AsientosController {

    @Autowired
    private ProyeccionRepository proyeccionRepository;

    @Autowired
    private AsientoRepository asientoRepository;

    @Autowired
    private EntradaRepository entradaRepository;

    /**
     * Obtiene el mapa de asientos para una proyección específica
     * @param proyeccionId ID de la proyección
     * @return Mapa de asientos con su estado (disponible/ocupado)
     */
    @GetMapping("/{proyeccionId}/asientos")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<?> getMapaAsientos(@PathVariable Long proyeccionId) {
        // Obtener la proyección y validar que existe
        Proyeccion proyeccion = proyeccionRepository.findById(proyeccionId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyección no encontrada"));

        // Obtener la sala
        Sala sala = proyeccion.getSala();

        // Obtener todos los asientos ordenados
        List<Asiento> asientos = asientoRepository.findBySalaIdOrderByFilaAndNumero(sala.getId());

        // Obtener asientos ocupados
        List<Long> asientosOcupados = entradaRepository.findAsientosOcupadosByProyeccionId(proyeccionId);

        // Calcular dimensiones de la sala
        int maxFila = 0;
        int maxColumna = 0;
        Map<String, List<Map<String, Object>>> asientosPorFila = new HashMap<>();

        // Agrupar asientos por fila
        for (Asiento asiento : asientos) {
            String fila = asiento.getFila();
            int columna = Integer.parseInt(asiento.getNumero());

            // Actualizar dimensiones máximas
            maxFila = Math.max(maxFila, fila.charAt(0) - 'A' + 1);
            maxColumna = Math.max(maxColumna, columna);

            // Crear info del asiento
            Map<String, Object> asientoInfo = new HashMap<>();
            asientoInfo.put("id", asiento.getId());
            asientoInfo.put("fila", fila);
            asientoInfo.put("numero", asiento.getNumero());
            asientoInfo.put("estado", asientosOcupados.contains(asiento.getId()) ?
                    "OCUPADO" : "DISPONIBLE");

            // Agregar a la lista de la fila correspondiente
            asientosPorFila.computeIfAbsent(fila, k -> new ArrayList<>())
                    .add(asientoInfo);
        }

        // Construir respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("proyeccionId", proyeccionId);
        response.put("sala", new HashMap<String, Object>() {{
            put("id", sala.getId());
            put("nombre", sala.getNombre());
            put("tipo", sala.getTipoSala().getNombre());
        }});
        response.put("dimensiones", Map.of(
                "filas", maxFila,
                "columnas", maxColumna
        ));
        response.put("asientosPorFila", asientosPorFila);
        response.put("leyenda", new HashMap<String, String>() {{
            put("DISPONIBLE", "Asiento disponible para selección");
            put("OCUPADO", "Asiento ya reservado");
            put("ESPECIAL", "Asiento para personas con movilidad reducida");
        }});

        return ResponseEntity.ok(response);
    }
}
