package com.sis.cine.repository;

import com.sis.cine.model.Proyeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyeccionRepository extends JpaRepository<Proyeccion, Long> {
    List<Proyeccion> findByPeliculaId(Long peliculaId);
}