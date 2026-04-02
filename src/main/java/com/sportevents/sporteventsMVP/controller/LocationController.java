package com.sportevents.sporteventsMVP.controller;

import com.sportevents.sporteventsMVP.entity.Location;
import com.sportevents.sporteventsMVP.repository.LocationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationRepository locationRepository;

    public LocationController(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @GetMapping
    public List<Location> getAll() {
        return locationRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> getById(@PathVariable UUID id) {
        return locationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Location> create(@RequestBody Location location) {
        // Si ya existe una con misma ciudad y provincia, la devuelve en lugar de duplicar
        List<Location> existing = locationRepository.findByCiudadIgnoreCase(location.getCiudad());
        for (Location loc : existing) {
            if (loc.getProvincia().equalsIgnoreCase(location.getProvincia())) {
                return ResponseEntity.ok(loc);
            }
        }

        if (location.getPais() == null || location.getPais().isBlank()) {
            location.setPais("Argentina");
        }

        Location saved = locationRepository.save(location);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
