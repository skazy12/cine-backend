package com.sis.cine.controller;

import com.sis.cine.dto.PeliculaDTO;
import com.sis.cine.model.Pelicula;
import com.sis.cine.service.PeliculaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/peliculas")
public class PeliculaController {

    @Autowired
    private PeliculaService peliculaService;

    /**
     * Endpoint para filtrar películas por tipo de proyección y mostrar solo estrenos.
     *
     * @param tipoProyeccion El tipo de proyección de la película (2D, 2D Atmos, 3D, o null para todos los tipos)
     * @return Lista de películas filtradas
     */
    @GetMapping("/cartelera")
    public ResponseEntity<List<PeliculaDTO>> getCartelera(@RequestParam(required = false) String tipoProyeccion) {
        List<PeliculaDTO> peliculas = peliculaService.getPeliculasEnCartelera(tipoProyeccion);
        return ResponseEntity.ok(peliculas);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Pelicula> crearPelicula(
            @RequestPart("pelicula") Pelicula pelicula,
            @RequestPart("poster") MultipartFile posterFile) throws IOException {
        Pelicula nuevaPelicula = peliculaService.crearPeliculaConPoster(pelicula, posterFile);
        return ResponseEntity.ok(nuevaPelicula);
    }

    @GetMapping("/signed-url/{fileName}")
    public ResponseEntity<String> getSignedUrl(@PathVariable String fileName) {
        String signedUrl = peliculaService.generateSignedUrl(fileName);
        return ResponseEntity.ok(signedUrl);
    }
}