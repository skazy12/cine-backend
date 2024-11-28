package com.sis.cine.controller;

import com.sis.cine.dto.adminDTOs.*;
import com.sis.cine.service.AdminPreciosService;
import com.sis.cine.service.AdminProyeccionService;
import com.sis.cine.service.PeliculaService;
import com.sis.cine.service.SalaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
//
//    @Autowired
//    private SalaService salaService;
//
//    @Autowired
//    private PeliculaService peliculaService;
//
//    @Autowired
//    private AdminProyeccionService proyeccionService;
//
//
//
//    @Autowired
//    private AdminPreciosService preciosService;
//
//    // ============== GESTIÓN DE SALAS ==============
//
//    @PostMapping("/salas")
//    public ResponseEntity<SalaDTO> crearSala(@RequestBody SalaDTO salaDTO) {
//        return ResponseEntity.ok(salaService.crearSala(salaDTO));
//    }
//
//    @PutMapping("/salas/{id}")
//    public ResponseEntity<SalaDTO> actualizarSala(@PathVariable Long id, @RequestBody SalaDTO salaDTO) {
//        return ResponseEntity.ok(salaService.actualizarSala(id, salaDTO));
//    }
//
//    @DeleteMapping("/salas/{id}")
//    public ResponseEntity<Void> eliminarSala(@PathVariable Long id) {
//        salaService.eliminarSala(id);
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/salas")
//    public ResponseEntity<List<SalaDTO>> listarSalas() {
//        return ResponseEntity.ok(salaService.listarSalas());
//    }
//
//    // ============== GESTIÓN DE PROYECCIONES ==============
//
//    @PostMapping("/proyecciones")
//    public ResponseEntity<ProyeccionDTO> crearProyeccion(@RequestBody ProyeccionDTO proyeccionDTO) {
//        return ResponseEntity.ok(proyeccionService.crearProyeccion(proyeccionDTO));
//    }
//
//    @PutMapping("/proyecciones/{id}")
//    public ResponseEntity<ProyeccionDTO> actualizarProyeccion(@PathVariable Long id, @RequestBody ProyeccionDTO proyeccionDTO) {
//        return ResponseEntity.ok(proyeccionService.actualizarProyeccion(id, proyeccionDTO));
//    }
//
//    @DeleteMapping("/proyecciones/{id}")
//    public ResponseEntity<Void> eliminarProyeccion(@PathVariable Long id) {
//        proyeccionService.eliminarProyeccion(id);
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/proyecciones")
//    public ResponseEntity<List<ProyeccionDTO>> listarProyecciones(
//            @RequestParam(required = false) Long salaId,
//            @RequestParam(required = false) Long peliculaId) {
//        return ResponseEntity.ok(proyeccionService.listarProyecciones(salaId, peliculaId));
//    }
//
//    // ============== GESTIÓN DE PRECIOS ==============
//
//    @PostMapping("/precios")
//    public ResponseEntity<PreciosDTO> crearPrecio(@RequestBody PreciosDTO preciosDTO) {
//        return ResponseEntity.ok(preciosService.crearPrecio(preciosDTO));
//    }
//
//    @PutMapping("/precios/{id}")
//    public ResponseEntity<PreciosDTO> actualizarPrecio(@PathVariable Long id, @RequestBody PreciosDTO preciosDTO) {
//        return ResponseEntity.ok(preciosService.actualizarPrecio(id, preciosDTO));
//    }
//
//    @DeleteMapping("/precios/{id}")
//    public ResponseEntity<Void> eliminarPrecio(@PathVariable Long id) {
//        preciosService.eliminarPrecio(id);
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/precios")
//    public ResponseEntity<List<PreciosDTO>> listarPrecios() {
//        return ResponseEntity.ok(preciosService.listarPrecios());
//    }
}