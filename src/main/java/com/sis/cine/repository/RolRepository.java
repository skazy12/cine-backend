package com.sis.cine.repository;

import com.sis.cine.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    /**
     * Busca un rol por su nombre.
     * @param nombre El nombre del rol a buscar.
     * @return Un Optional que contiene el Rol si se encuentra, o un Optional vacío si no.
     */
    Optional<Rol> findByNombre(String nombre);

    /**
     * Verifica si existe un rol con el nombre dado.
     * @param nombre El nombre del rol a verificar.
     * @return true si existe un rol con ese nombre, false en caso contrario.
     */
    boolean existsByNombre(String nombre);
}
