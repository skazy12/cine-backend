package com.sis.cine.controller;

import com.sis.cine.dto.TipoProyeccionDTO;
import com.sis.cine.model.TipoProyeccion;
import com.sis.cine.repository.TipoProyeccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/proyecciones")
@PreAuthorize("hasRole('ADMIN')")
public class TipoProyeccionController {

    @Autowired
    private TipoProyeccionRepository tipoProyeccionRepository;

    @GetMapping("/tipos")
    public ResponseEntity<List<TipoProyeccionDTO>> obtenerTiposDeProyeccion() {
        List<TipoProyeccion> tiposProyeccion = tipoProyeccionRepository.findAll();
        List<TipoProyeccionDTO> dtos = tiposProyeccion.stream()
                .map(tipo -> new TipoProyeccionDTO(tipo.getId(), tipo.getNombre()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
