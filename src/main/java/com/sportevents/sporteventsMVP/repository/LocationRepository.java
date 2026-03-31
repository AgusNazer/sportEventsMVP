package com.sportevents.sporteventsMVP.repository;

import com.sportevents.sporteventsMVP.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {
    List<Location> findByProvinciaIgnoreCase(String provincia);
    List<Location> findByCiudadIgnoreCase(String ciudad);
}
