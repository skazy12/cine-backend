package com.sis.cine.repository;

import com.sis.cine.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    /**
     * Encuentra reservas por usuario
     */
    List<Reserva> findByUsuarioId(Long usuarioId);

    /**
     * Encuentra reservas por proyección
     */
    List<Reserva> findByProyeccionId(Long proyeccionId);

    /**
     * Encuentra reservas activas (con pago pendiente) por proyección
     */
    @Query("SELECT r FROM Reserva r WHERE r.proyeccion.id = :proyeccionId " +
            "AND EXISTS (SELECT p FROM Pago p WHERE p.reserva = r AND p.estadoPago = 'PENDIENTE')")
    List<Reserva> findActiveReservasByProyeccion(@Param("proyeccionId") Long proyeccionId);

    /**
     * Encuentra reservas que han expirado
     */
    @Query("SELECT r FROM Reserva r " +
            "JOIN Pago p ON p.reserva = r " +
            "WHERE p.fechaExpiracion < :now AND p.estadoPago = 'PENDIENTE'")
    List<Reserva> findExpiredReservas(@Param("now") LocalDateTime now);

    /**
     * Verifica si un asiento ya está reservado para una proyección
     */
    @Query("SELECT COUNT(r) > 0 FROM Reserva r " +
            "JOIN Entrada e ON e.reserva = r " +
            "WHERE r.proyeccion.id = :proyeccionId " +
            "AND e.asiento.id = :asientoId " +
            "AND EXISTS (SELECT p FROM Pago p WHERE p.reserva = r AND p.estadoPago != 'EXPIRADO')")
    boolean isAsientoReservado(@Param("proyeccionId") Long proyeccionId,
                               @Param("asientoId") Long asientoId);

    /**
     * Cuenta las reservas activas para una proyección
     */
    @Query("SELECT COUNT(r) FROM Reserva r " +
            "WHERE r.proyeccion.id = :proyeccionId " +
            "AND EXISTS (SELECT p FROM Pago p WHERE p.reserva = r AND p.estadoPago IN ('PENDIENTE', 'PAGADO'))")
    int countActiveReservasByProyeccion(@Param("proyeccionId") Long proyeccionId);
}