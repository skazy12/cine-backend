package com.sis.cine.repository;

import com.sis.cine.model.Asiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsientoRepository extends JpaRepository<Asiento, Long> {
    /**
     * Recupera todos los asientos asociados a una sala específica.
     * Útil para visualizar la disposición completa de asientos en una sala.
     *
     * @param salaId Identificador único de la sala
     * @return Lista de todos los asientos en la sala especificada
     */
    List<Asiento> findBySalaId(Long salaId);




    /**
     * Verifica la existencia de un asiento específico en una sala.
     * Útil para validaciones rápidas sin necesidad de recuperar el objeto completo.
     *
     * @param salaId Identificador único de la sala
     * @param fila Identificador de la fila (carácter único)
     * @param numero Número del asiento en la fila
     * @return true si el asiento existe, false en caso contrario
     */
    boolean existsBySalaIdAndFilaAndNumero(Long salaId, String fila, String numero);

    /**
     * Obtiene el conteo total de asientos en una sala específica.
     * Útil para cálculos de capacidad y estadísticas.
     *
     * @param salaId Identificador único de la sala
     * @return Cantidad total de asientos en la sala
     */
    long countBySalaId(Long salaId);

    /**
     * Recupera todos los asientos de una fila específica en una sala.
     * Útil para gestionar reservas por fila o verificar disponibilidad.
     *
     * @param salaId Identificador único de la sala
     * @param fila Identificador de la fila (carácter único)
     * @return Lista de asientos en la fila especificada
     */
    List<Asiento> findBySalaIdAndFila(Long salaId, String fila);



    /**
     * Verifica si una sala tiene asientos registrados.
     * Útil para validaciones de configuración de sala.
     *
     * @param salaId Identificador único de la sala
     * @return true si la sala tiene asientos asignados, false en caso contrario
     */
    boolean existsBySalaId(Long salaId);

    /**
     * Recupera los asientos disponibles para una sala específica.
     * Útil para mostrar asientos que pueden ser reservados.
     *
     * @param salaId Identificador único de la sala
     * @return Lista de asientos disponibles
     */
    @Query("SELECT a FROM Asiento a WHERE a.sala.id = :salaId " +
            "AND a.id NOT IN (SELECT e.asiento.id FROM Entrada e WHERE e.reserva.proyeccion.sala.id = :salaId)")
    List<Asiento> findAvailableSeatsInSala(@Param("salaId") Long salaId);

    /**
     * Busca un asiento específico en una sala usando la fila y el número.
     * Utilizado para localizar un asiento particular, por ejemplo, al validar una reserva.
     *
     * @param salaId Identificador único de la sala
     * @param fila Identificador de la fila (carácter único)
     * @param numero Número del asiento en la fila
     * @return Optional conteniendo el asiento si existe
     */
    @Query("SELECT a FROM Asiento a WHERE a.sala.id = :salaId " +
            "AND a.fila = :fila AND a.numero = :numero")
    Optional<Asiento> findBySalaIdAndFilaAndNumero(
            @Param("salaId") Long salaId,
            @Param("fila") String fila,
            @Param("numero") String numero);

    /**
     * Recupera todos los asientos de una sala ordenados por fila y número.
     * Útil para mostrar un mapa ordenado de asientos o generar reportes.
     *
     * @param salaId Identificador único de la sala
     * @return Lista ordenada de asientos
     */

    @Query("SELECT a FROM Asiento a WHERE a.sala.id = :salaId " +
            "ORDER BY a.fila, CAST(a.numero AS int)")
    List<Asiento> findBySalaIdOrderByFilaAndNumero(@Param("salaId") Long salaId);
}