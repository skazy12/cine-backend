package com.sis.cine.repository;

import com.sis.cine.model.TipoSala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoSalaRepository extends JpaRepository<TipoSala, Long> {
    /**
     * Busca un tipo de sala por su nombre
     * @param nombre nombre del tipo de sala
     * @return Optional con el tipo de sala si existe
     */
    Optional<TipoSala> findByNombre(String nombre);

    /**
     * Verifica si existe un tipo de sala con el nombre especificado
     * @param nombre nombre a verificar
     * @return true si existe, false si no
     */
    boolean existsByNombre(String nombre);

    /**
     * Obtiene todos los tipos de sala activos
     * @return Lista de tipos de sala activos
     */
    List<TipoSala> findByActivoTrue();

    /**
     * Busca tipos de sala que contengan ciertas características
     * @param caracteristica característica a buscar
     * @return Lista de tipos de sala con la característica especificada
     */
    @Query("SELECT t FROM TipoSala t WHERE t.caracteristicas LIKE %:caracteristica%")
    List<TipoSala> findByCaracteristica(@Param("caracteristica") String caracteristica);

    /**
     * Obtiene tipos de sala con el número de salas asociadas
     * @return Lista de tipos de sala con count de salas
     */
    @Query("SELECT t, COUNT(s) FROM TipoSala t LEFT JOIN t.salas s GROUP BY t")
    List<Object[]> findAllWithSalaCount();

    /**
     * Busca tipos de sala que tengan salas asignadas
     * @return Lista de tipos de sala en uso
     */
    @Query("SELECT DISTINCT t FROM TipoSala t INNER JOIN t.salas")
    List<TipoSala> findTiposSalaEnUso();

    /**
     * Verifica si un tipo de sala está en uso (tiene salas asignadas)
     * @param tipoSalaId ID del tipo de sala
     * @return true si está en uso, false si no
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Sala s WHERE s.tipoSala.id = :tipoSalaId")
    boolean isTipoSalaEnUso(@Param("tipoSalaId") Long tipoSalaId);
}