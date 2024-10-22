package com.sis.cine.service;

import com.sis.cine.dto.PeliculaDTO;
import com.sis.cine.model.Pelicula;
import com.sis.cine.model.Proyeccion;
import com.sis.cine.model.TipoProyeccion;
import com.sis.cine.repository.PeliculaRepository;
import com.sis.cine.repository.ProyeccionRepository;
import com.sis.cine.repository.TipoProyeccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PeliculaService {

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private ProyeccionRepository proyeccionRepository;

    @Autowired
    private TipoProyeccionRepository tipoProyeccionRepository;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    /**
     * Obtiene las películas en cartelera filtradas por tipo de proyección.
     *
     * @param tipoProyeccionNombre El nombre del tipo de proyección (2D, 2D Atmos, 3D, o null para todos los tipos)
     * @return Lista de PeliculaDTO que cumplen con los criterios
     */
    public List<PeliculaDTO> getPeliculasEnCartelera(String tipoProyeccionNombre) {
        List<Pelicula> peliculasEnProyeccion = peliculaRepository.findByEnProyeccionTrue();

        // Si no se especifica un tipo de proyección, devolvemos todas las películas en proyección
        if (tipoProyeccionNombre == null || tipoProyeccionNombre.equalsIgnoreCase("Todos los formatos")) {
            return peliculasEnProyeccion.stream()
                    .map(this::convertirAPeliculaDTO)
                    .collect(Collectors.toList());
        }

        // Buscamos el tipo de proyección por nombre
        TipoProyeccion tipoProyeccion = tipoProyeccionRepository.findByNombre(tipoProyeccionNombre)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de proyección no encontrado: " + tipoProyeccionNombre));

        // Filtramos las películas que tienen proyecciones del tipo especificado
        return peliculasEnProyeccion.stream()
                .filter(pelicula -> tieneProyeccionDeTipo(pelicula, tipoProyeccion))
                .map(this::convertirAPeliculaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Verifica si una película tiene proyecciones del tipo especificado.
     */
    private boolean tieneProyeccionDeTipo(Pelicula pelicula, TipoProyeccion tipoProyeccion) {
        List<Proyeccion> proyecciones = proyeccionRepository.findByPeliculaId(pelicula.getId());
        return proyecciones.stream()
                .anyMatch(proyeccion -> proyeccion.getTipoProyeccion().equals(tipoProyeccion));
    }
    public Pelicula crearPeliculaConPoster(Pelicula pelicula, MultipartFile posterFile) throws IOException {
        String posterUrl = firebaseStorageService.uploadFile(posterFile);
        pelicula.setPoster(posterUrl);
        return peliculaRepository.save(pelicula);
    }
    public String generateSignedUrl(String fileName) {
        return firebaseStorageService.generateSignedUrl(fileName);
    }

    /**
     * Convierte una entidad Pelicula a un DTO PeliculaDTO.
     */
    private PeliculaDTO convertirAPeliculaDTO(Pelicula pelicula) {
        PeliculaDTO dto = new PeliculaDTO();
        dto.setId(pelicula.getId());
        dto.setTitulo(pelicula.getTitulo());
        dto.setDirector(pelicula.getDirector());
        dto.setDescripcion(pelicula.getDescripcion());
        dto.setDuracion(pelicula.getDuracion());
        dto.setEstudio(pelicula.getEstudio());
        dto.setTrailer(pelicula.getTrailer());

        dto.setPoster(firebaseStorageService.generateSignedUrl(pelicula.getPoster()));
        dto.setEnProyeccion(pelicula.getEnProyeccion()); // Asumimos que todas las películas en proyección son estrenos
        // Aquí puedes agregar más campos según sea necesario
        return dto;
    }
}