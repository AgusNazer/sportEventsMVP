package com.sportevents.sporteventsMVP.repository;

import com.sportevents.sporteventsMVP.entity.Event;
import com.sportevents.sporteventsMVP.enums.EventLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    Optional<Event> findBySlug(String slug);

    List<Event> findByActivoTrueOrderByFechaAsc();

    List<Event> findBySport_SlugAndActivoTrue(String sportSlug);

    List<Event> findByLocation_CiudadIgnoreCaseAndActivoTrue(String ciudad);

    List<Event> findByLocation_ProvinciaIgnoreCaseAndActivoTrue(String provincia);

    List<Event> findByNivelAndActivoTrue(EventLevel nivel);

    @Query("""
        SELECT e FROM Event e
        WHERE e.activo = true
          AND e.fecha BETWEEN :desde AND :hasta
        ORDER BY e.fecha ASC
    """)
    List<Event> findByFechaBetweenAndActivoTrue(
        @Param("desde") LocalDate desde,
        @Param("hasta") LocalDate hasta
    );

    @Query(value = """
    SELECT e.* FROM events e
    JOIN sports s ON s.id = e.sport_id
    JOIN locations l ON l.id = e.location_id
    WHERE e.activo = true
      AND (CAST(:sportSlug AS varchar) IS NULL OR s.slug = :sportSlug)
      AND (CAST(:provincia AS varchar) IS NULL OR l.provincia ILIKE :provincia)
      AND (CAST(:nivel AS varchar) IS NULL OR e.nivel = CAST(:nivel AS varchar))
    ORDER BY e.fecha ASC
""", nativeQuery = true)
    List<Event> findWithFilters(
            @Param("sportSlug") String sportSlug,
            @Param("provincia") String provincia,
            @Param("nivel") String nivel
    );
}
