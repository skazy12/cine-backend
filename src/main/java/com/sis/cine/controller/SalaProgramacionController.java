package com.sis.cine.controller;

import com.sis.cine.dto.SalaProgramadaDTO;
import com.sis.cine.repository.ProyeccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/salas")
@PreAuthorize("hasRole('ADMIN')")
public class SalaProgramacionController {

    @Autowired
    private ProyeccionRepository proyeccionRepository;

    @GetMapping("/programadas")
    public ResponseEntity<List<SalaProgramadaDTO>> obtenerSalasProgramadas() {
        List<SalaProgramadaDTO> salasProgramadas = proyeccionRepository.obtenerSalasConProyecciones();
        return ResponseEntity.ok(salasProgramadas);
    }
}