package com.sis.cine.repository;

import com.sis.cine.model.Proyeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import com.sis.cine.model.Proyeccion;
import com.sis.cine.model.TipoProyeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProyeccionRepository extends JpaRepository<Proyeccion, Long> {
    List<Proyeccion> findByPeliculaId(Long peliculaId);
    /**
     * Encuentra todos los tipos de proyección distintos para una película
     */
    @Query("SELECT DISTINCT p.tipoProyeccion FROM Proyeccion p " +
            "WHERE p.pelicula.id = :peliculaId " +
            "ORDER BY p.tipoProyeccion.nombre")
    List<TipoProyeccion> findDistinctTipoProyeccionByPeliculaId(@Param("peliculaId") Long peliculaId);

    /**
     * Encuentra proyecciones por película, fecha y tipo de proyección
     */
    @Query("SELECT p FROM Proyeccion p " +
            "WHERE p.pelicula.id = :peliculaId " +
            "AND p.dia = :fecha " +
            "AND p.tipoProyeccion.nombre = :formato " +
            "ORDER BY p.comienzo")
    List<Proyeccion> findByPeliculaIdAndDiaAndTipoProyeccionNombre(
            @Param("peliculaId") Long peliculaId,
            @Param("fecha") LocalDate fecha,
            @Param("formato") String formato
    );
    @Query("SELECT p FROM Proyeccion p WHERE p.id = :proyeccionId AND p.pelicula.id = :peliculaId")
    Optional<Proyeccion> findByIdAndPeliculaId(
            @Param("proyeccionId") Long proyeccionId,
            @Param("peliculaId") Long peliculaId
    );
    @Query("SELECT p FROM Proyeccion p " +
            "WHERE p.sala.id = :salaId " +
            "AND p.dia = :fecha " +
            "ORDER BY p.comienzo")
    List<Proyeccion> findBySalaIdAndDia(
            @Param("salaId") Long salaId,
            @Param("fecha") LocalDate fecha
    );


    /**
     * Encuentra proyecciones por sala
     * @param salaId ID de la sala
     * @return Lista de proyecciones en la sala
     */
    List<Proyeccion> findBySalaId(Long salaId);

    /**
     * Encuentra proyecciones por tipo de proyección
     * @param tipoProyeccionId ID del tipo de proyección
     * @return Lista de proyecciones del tipo especificado
     */
    List<Proyeccion> findByTipoProyeccionId(Long tipoProyeccionId);

    /**
     * Encuentra proyecciones futuras para una película
     * @param peliculaId ID de la película
     * @param fecha Fecha actual
     * @return Lista de proyecciones futuras
     */
    @Query("SELECT p FROM Proyeccion p " +
            "WHERE p.pelicula.id = :peliculaId " +
            "AND p.dia >= :fecha " +
            "ORDER BY p.dia, p.comienzo")
    List<Proyeccion> findFutureProjections(
            @Param("peliculaId") Long peliculaId,
            @Param("fecha") LocalDate fecha
    );

    /**
     * Verifica si existe una proyección en un horario específico
     * @param salaId ID de la sala
     * @param fecha Fecha a verificar
     * @param horaInicio Hora de inicio
     * @param horaFin Hora de fin
     * @return true si existe una proyección en ese horario
     */
    @Query("SELECT COUNT(p) > 0 FROM Proyeccion p " +
            "WHERE p.sala.id = :salaId " +
            "AND p.dia = :fecha " +
            "AND ((p.comienzo >= :horaInicio AND p.comienzo < :horaFin) " +
            "OR (FUNCTION('ADDTIME', p.comienzo, FUNCTION('SEC_TO_TIME', p.pelicula.duracion * 60)) > :horaInicio " +
            "AND p.comienzo <= :horaInicio))")
    boolean existsOverlappingProjection(
            @Param("salaId") Long salaId,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") String horaInicio,
            @Param("horaFin") String horaFin
    );
}