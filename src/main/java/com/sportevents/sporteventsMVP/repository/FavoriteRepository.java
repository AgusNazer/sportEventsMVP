package com.sportevents.sporteventsMVP.repository;

import com.sportevents.sporteventsMVP.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {

    List<Favorite> findByUser_Id(UUID userId);

    Optional<Favorite> findByUser_IdAndEvent_Id(UUID userId, UUID eventId);

    boolean existsByUser_IdAndEvent_Id(UUID userId, UUID eventId);

    void deleteByUser_IdAndEvent_Id(UUID userId, UUID eventId);
}
