package com.sportevents.sporteventsMVP.controller;

import com.sportevents.sporteventsMVP.dto.EventFilterRequest;
import com.sportevents.sporteventsMVP.dto.EventRequest;
import com.sportevents.sporteventsMVP.dto.EventResponse;
import com.sportevents.sporteventsMVP.enums.EventLevel;
import com.sportevents.sporteventsMVP.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public List<EventResponse> getAll(
            @RequestParam(required = false) String sport,
            @RequestParam(required = false) String provincia,
            @RequestParam(required = false) EventLevel nivel,
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta
    ) {
        boolean hasFilters = sport != null || provincia != null || nivel != null || desde != null || hasta != null;
        if (hasFilters) {
            return eventService.filter(new EventFilterRequest(sport, provincia, nivel, desde, hasta));
        }
        return eventService.getAll();
    }

    @GetMapping("/this-weekend")
    public List<EventResponse> getThisWeekend() {
        return eventService.getThisWeekend();
    }

    @GetMapping("/{id}")
    public EventResponse getById(@PathVariable UUID id) {
        return eventService.getById(id);
    }

    @GetMapping("/slug/{slug}")
    public EventResponse getBySlug(@PathVariable String slug) {
        return eventService.getBySlug(slug);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse create(@Valid @RequestBody EventRequest request) {
        return eventService.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        eventService.delete(id);
    }
}
