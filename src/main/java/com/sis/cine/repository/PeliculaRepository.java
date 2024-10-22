package com.sis.cine.repository;

import com.sis.cine.model.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {
    List<Pelicula> findByEnProyeccionTrue();
    List<Pelicula> findByEnProyeccionFalse();
}