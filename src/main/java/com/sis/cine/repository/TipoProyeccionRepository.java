package com.sis.cine.repository;

import com.sis.cine.model.TipoProyeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoProyeccionRepository extends JpaRepository<TipoProyeccion, Long> {
    Optional<TipoProyeccion> findByNombre(String nombre);
}