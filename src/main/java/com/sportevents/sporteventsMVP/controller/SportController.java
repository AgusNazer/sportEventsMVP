package com.sportevents.sporteventsMVP.controller;

import com.sportevents.sporteventsMVP.entity.Sport;
import com.sportevents.sporteventsMVP.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sports")
@RequiredArgsConstructor
public class SportController {

    private final SportRepository sportRepository;

    @GetMapping
    public List<Sport> getAll() {
        return sportRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sport> getById(@PathVariable UUID id) {
        return sportRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Sport create(@RequestBody Sport sport) {
        return sportRepository.save(sport);
    }
}
