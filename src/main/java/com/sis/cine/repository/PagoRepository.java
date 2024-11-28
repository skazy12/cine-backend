package com.sis.cine.repository;

import com.sis.cine.model.Pago;
import com.sis.cine.model.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    /**
     * Encuentra el pago asociado a una reserva
     */
    Optional<Pago> findByReservaId(Long reservaId);

    /**
     * Encuentra pagos por estado
     */
    List<Pago> findByEstadoPago(EstadoPago estadoPago);

    /**
     * Encuentra pagos expirados
     */
    @Query("SELECT p FROM Pago p WHERE p.fechaExpiracion < :now AND p.estadoPago = 'PENDIENTE'")
    List<Pago> findExpiredPayments(@Param("now") LocalDateTime now);

    /**
     * Encuentra el último pago de una reserva por estado
     */
    @Query("SELECT p FROM Pago p WHERE p.reserva.id = :reservaId AND p.estadoPago = :estado " +
            "ORDER BY p.fechaGeneracion DESC")
    Optional<Pago> findLastPagoByReservaAndEstado(
            @Param("reservaId") Long reservaId,
            @Param("estado") EstadoPago estado);

    /**
     * Verifica si existe un pago válido para una reserva
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pago p " +
            "WHERE p.reserva.id = :reservaId " +
            "AND p.estadoPago = 'PAGADO' " +
            "AND p.fechaExpiracion > :now")
    boolean existsValidPaymentForReserva(
            @Param("reservaId") Long reservaId,
            @Param("now") LocalDateTime now);
}