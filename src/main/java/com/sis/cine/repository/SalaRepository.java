package com.sis.cine.repository;

import com.sis.cine.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {
    /**
     * Busca una sala por su nombre
     * @param nombre el nombre de la sala
     * @return Optional con la sala si existe
     */
    Optional<Sala> findByNombre(String nombre);

    /**
     * Verifica si existe una sala con el nombre especificado
     * @param nombre el nombre a verificar
     * @return true si existe, false si no
     */
    boolean existsByNombre(String nombre);

    /**
     * Busca salas por tipo de sala
     * @param tipoSalaId ID del tipo de sala
     * @return Lista de salas del tipo especificado
     */
    List<Sala> findByTipoSalaId(Long tipoSalaId);

    /**
     * Busca salas con capacidad mayor o igual a la especificada
     * @param capacidad capacidad mínima requerida
     * @return Lista de salas que cumplen con la capacidad
     */
    @Query("SELECT s FROM Sala s WHERE (SELECT COUNT(a) FROM Asiento a WHERE a.sala = s) >= :capacidad")
    List<Sala> findByCapacidadMinima(@Param("capacidad") Integer capacidad);

    /**
     * Obtiene todas las salas con su información de asientos
     * @return Lista de salas con sus asientos
     */
    @Query("SELECT DISTINCT s FROM Sala s LEFT JOIN FETCH s.asientos")
    List<Sala> findAllWithAsientos();

    /**
     * Obtiene una sala específica con su información de asientos
     * @param id ID de la sala
     * @return Optional con la sala y sus asientos
     */
    @Query("SELECT s FROM Sala s LEFT JOIN FETCH s.asientos WHERE s.id = :id")
    Optional<Sala> findByIdWithAsientos(@Param("id") Long id);
}