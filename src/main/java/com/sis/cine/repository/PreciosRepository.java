package com.sis.cine.repository;

import com.sis.cine.model.Precios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PreciosRepository extends JpaRepository<Precios, Long> {
    /**
     * Busca precios por tipo de sala
     * @param tipoSalaId ID del tipo de sala
     * @return Lista de precios para ese tipo de sala
     */
    List<Precios> findByTipoSalaId(Long tipoSalaId);

    /**
     * Busca precios por tipo de proyección
     * @param tipoProyeccionId ID del tipo de proyección
     * @return Lista de precios para ese tipo de proyección
     */
    List<Precios> findByTipoProyeccionId(Long tipoProyeccionId);

    /**
     * Busca precios por día de la semana
     * @param diaSemana día de la semana (ej: "LUNES", "MARTES", etc.)
     * @return Lista de precios para ese día
     */
    List<Precios> findByDiaSemana(String diaSemana);

    /**
     * Busca un precio específico por todos sus criterios
     * @param tipoSalaId ID del tipo de sala
     * @param tipoProyeccionId ID del tipo de proyección
     * @param diaSemana día de la semana
     * @return Optional con el precio si existe
     */
    Optional<Precios> findByTipoSalaIdAndTipoProyeccionIdAndDiaSemana(
            Long tipoSalaId,
            Long tipoProyeccionId,
            String diaSemana);

    /**
     * Busca precios en un rango específico
     * @param minPrecio precio mínimo
     * @param maxPrecio precio máximo
     * @return Lista de precios en el rango
     */
    @Query("SELECT p FROM Precios p WHERE p.precioFinal BETWEEN :minPrecio AND :maxPrecio")
    List<Precios> findByPrecioRange(
            @Param("minPrecio") BigDecimal minPrecio,
            @Param("maxPrecio") BigDecimal maxPrecio);

    /**
     * Verifica si existe un precio con los mismos criterios
     * @param tipoSalaId ID del tipo de sala
     * @param tipoProyeccionId ID del tipo de proyección
     * @param diaSemana día de la semana
     * @return true si existe, false si no
     */
    boolean existsByTipoSalaIdAndTipoProyeccionIdAndDiaSemana(
            Long tipoSalaId,
            Long tipoProyeccionId,
            String diaSemana);

    /**
     * Encuentra el precio más bajo para un tipo de sala
     * @param tipoSalaId ID del tipo de sala
     * @return El precio más bajo
     */
    @Query("SELECT MIN(p.precioFinal) FROM Precios p WHERE p.tipoSala.id = :tipoSalaId")
    BigDecimal findLowestPriceByTipoSala(@Param("tipoSalaId") Long tipoSalaId);

    /**
     * Encuentra el precio más alto para un tipo de sala
     * @param tipoSalaId ID del tipo de sala
     * @return El precio más alto
     */
    @Query("SELECT MAX(p.precioFinal) FROM Precios p WHERE p.tipoSala.id = :tipoSalaId")
    BigDecimal findHighestPriceByTipoSala(@Param("tipoSalaId") Long tipoSalaId);

    /**
     * Busca precios por nombre o condiciones (búsqueda parcial)
     * @param searchTerm término a buscar
     * @return Lista de precios que coinciden
     */
    @Query("SELECT p FROM Precios p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.condiciones) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Precios> searchByNombreOrCondiciones(@Param("searchTerm") String searchTerm);

    /**
     * Obtiene un resumen de precios por tipo de sala
     * @return Lista de objetos con tipo de sala y estadísticas de precios
     */
    @Query("SELECT p.tipoSala.nombre, " +
            "MIN(p.precioFinal), MAX(p.precioFinal), AVG(p.precioFinal) " +
            "FROM Precios p GROUP BY p.tipoSala.nombre")
    List<Object[]> getPreciosSummaryByTipoSala();

    /**
     * Busca precios activos para una proyección específica
     * @param tipoSalaId ID del tipo de sala
     * @param tipoProyeccionId ID del tipo de proyección
     * @param diaSemana día de la semana
     * @return Lista de precios aplicables
     */
    @Query("SELECT p FROM Precios p " +
            "WHERE p.tipoSala.id = :tipoSalaId " +
            "AND p.tipoProyeccion.id = :tipoProyeccionId " +
            "AND (p.diaSemana = :diaSemana OR p.diaSemana = 'TODOS')")
    List<Precios> findPreciosAplicables(
            @Param("tipoSalaId") Long tipoSalaId,
            @Param("tipoProyeccionId") Long tipoProyeccionId,
            @Param("diaSemana") String diaSemana);
    List<Precios> findByTipoSalaIdAndTipoProyeccionId(Long tipoSalaId, Long tipoProyeccionId);

}