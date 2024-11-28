package com.sis.cine.repository;

import com.sis.cine.model.Entrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntradaRepository extends JpaRepository<Entrada, Long> {

    /**
     * Cuenta el número de entradas vendidas para una proyección
     */
    @Query("SELECT COUNT(e) FROM Entrada e WHERE e.reserva.proyeccion.id = :proyeccionId")
    int countByReservaProyeccionId(@Param("proyeccionId") Long proyeccionId);

    /**
     * Encuentra las entradas para una reserva específica
     */
    List<Entrada> findByReservaId(Long reservaId);

    /**
     * Encuentra las entradas de una proyección con sus asientos
     */
    @Query("SELECT e FROM Entrada e " +
            "JOIN FETCH e.asiento a " +
            "WHERE e.reserva.proyeccion.id = :proyeccionId")
    List<Entrada> findByProyeccionIdWithAsientos(@Param("proyeccionId") Long proyeccionId);

    /**
     * Verifica si un asiento está ocupado en una proyección
     */
    @Query("SELECT COUNT(e) > 0 FROM Entrada e " +
            "WHERE e.reserva.proyeccion.id = :proyeccionId " +
            "AND e.asiento.id = :asientoId")
    boolean isAsientoOcupado(
            @Param("proyeccionId") Long proyeccionId,
            @Param("asientoId") Long asientoId
    );

    /**
     * Encuentra todas las entradas de una sala para una fecha específica
     */
    @Query("SELECT e FROM Entrada e " +
            "WHERE e.reserva.proyeccion.sala.id = :salaId " +
            "AND e.reserva.proyeccion.dia = :fecha")
    List<Entrada> findBySalaIdAndFecha(
            @Param("salaId") Long salaId,
            @Param("fecha") LocalDate fecha
    );

    /**
     * Obtiene los IDs de los asientos ocupados para una proyección
     */
    @Query("SELECT e.asiento.id FROM Entrada e " +
            "WHERE e.reserva.proyeccion.id = :proyeccionId")
    List<Long> findAsientosOcupadosByProyeccionId(@Param("proyeccionId") Long proyeccionId);

    /**
     * Verifica si hay entradas disponibles para una proyección
     */
    @Query("SELECT (COUNT(a) - COUNT(e)) > 0 FROM Proyeccion p " +
            "JOIN p.sala s " +
            "JOIN s.asientos a " +
            "LEFT JOIN Entrada e ON e.asiento.id = a.id AND e.reserva.proyeccion.id = p.id " +
            "WHERE p.id = :proyeccionId " +
            "GROUP BY s.id")
    boolean hayEntradasDisponibles(@Param("proyeccionId") Long proyeccionId);

    /**
     * Verifica si existe un pago completado para una entrada
     */
    @Query("SELECT COUNT(p) > 0 FROM Pago p " +
            "WHERE p.reserva.id = " +
            "(SELECT e.reserva.id FROM Entrada e WHERE e.id = :entradaId) " +
            "AND p.estadoPago = 'PAGADO'")
    boolean tienePagoCompletado(@Param("entradaId") Long entradaId);

    /**
     * Verifica la validez completa de una entrada (existencia y pago)
     */
    @Query("SELECT COUNT(e) > 0 FROM Entrada e " +
            "WHERE e.id = :entradaId " +
            "AND e.reserva.proyeccion.id = :proyeccionId " +
            "AND EXISTS (SELECT p FROM Pago p " +
            "           WHERE p.reserva = e.reserva " +
            "           AND p.estadoPago = 'PAGADO')")
    boolean isEntradaValida(
            @Param("entradaId") Long entradaId,
            @Param("proyeccionId") Long proyeccionId
    );

    /**
     * Obtiene el estado del pago de una entrada
     */
    @Query("SELECT p.estadoPago FROM Pago p " +
            "WHERE p.reserva.id = " +
            "(SELECT e.reserva.id FROM Entrada e WHERE e.id = :entradaId) " +
            "ORDER BY p.fechaPago DESC")
    Optional<String> getEstadoPagoEntrada(@Param("entradaId") Long entradaId);

    /**
     * Verifica si una entrada está dentro del tiempo válido para su uso
     * (verifica fecha de proyección y que el pago no esté expirado)
     */
    @Query("SELECT COUNT(e) > 0 FROM Entrada e " +
            "JOIN e.reserva r " +
            "JOIN r.proyeccion p " +
            "WHERE e.id = :entradaId " +
            "AND p.dia >= CURRENT_DATE " +
            "AND EXISTS (SELECT pg FROM Pago pg " +
            "           WHERE pg.reserva = r " +
            "           AND pg.estadoPago = 'PAGADO' " +
            "           AND pg.fechaExpiracion > CURRENT_TIMESTAMP)")
    boolean isEntradaVigente(@Param("entradaId") Long entradaId);
}