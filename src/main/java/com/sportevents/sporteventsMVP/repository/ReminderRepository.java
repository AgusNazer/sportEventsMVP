package com.sportevents.sporteventsMVP.repository;
import com.sportevents.sporteventsMVP.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReminderRepository extends JpaRepository<Reminder, UUID> {

    List<Reminder> findByUser_Id(UUID userId);

    Optional<Reminder> findByUser_IdAndEvent_Id(UUID userId, UUID eventId);

    boolean existsByUser_IdAndEvent_Id(UUID userId, UUID eventId);

    void deleteByUser_IdAndEvent_Id(UUID userId, UUID eventId);

    @Query("""
        SELECT r FROM Reminder r
        JOIN FETCH r.user u
        JOIN FETCH r.event e
        WHERE r.enviado = false
          AND FUNCTION('DATE', e.fecha) = :fechaObjetivo
    """)
    List<Reminder> findPendingForDate(LocalDate fechaObjetivo);
}