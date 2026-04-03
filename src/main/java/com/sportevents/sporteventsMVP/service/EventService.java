package com.sportevents.sporteventsMVP.service;

import com.sportevents.sporteventsMVP.dto.EventFilterRequest;
import com.sportevents.sporteventsMVP.dto.EventRequest;
import com.sportevents.sporteventsMVP.dto.EventResponse;
import com.sportevents.sporteventsMVP.entity.Event;
import com.sportevents.sporteventsMVP.entity.Location;
import com.sportevents.sporteventsMVP.entity.Sport;
import com.sportevents.sporteventsMVP.enums.EventLevel;
import com.sportevents.sporteventsMVP.repository.EventRepository;
import com.sportevents.sporteventsMVP.repository.LocationRepository;
import com.sportevents.sporteventsMVP.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final SportRepository sportRepository;
    private final LocationRepository locationRepository;

    public List<EventResponse> getAll() {
        return eventRepository.findByActivoTrueOrderByFechaAsc()
                .stream()
                .map(EventResponse::from)
                .toList();
    }

    public EventResponse getById(UUID id) {
        return eventRepository.findById(id)
                .map(EventResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
    }

    public EventResponse getBySlug(String slug) {
        return eventRepository.findBySlug(slug)
                .map(EventResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
    }

    public List<EventResponse> filter(EventFilterRequest req) {
        // Normalizamos filtros vacíos → null
        String sport = (req.sport() != null && !req.sport().isBlank()) ? req.sport() : null;
        String provincia = (req.provincia() != null && !req.provincia().isBlank()) ? req.provincia() : null;
        EventLevel nivel = req.nivel() != null ? req.nivel() : null;

        // Si hay rango de fechas
        if (req.desde() != null || req.hasta() != null) {
            LocalDate desde = req.desde() != null ? req.desde() : LocalDate.now();
            LocalDate hasta = req.hasta() != null ? req.hasta() : LocalDate.now().plusYears(1);
            return eventRepository.findByFechaBetweenAndActivoTrue(desde, hasta)
                    .stream()
                    .map(EventResponse::from)
                    .toList();
        }

        // Query general con filtros opcionales
        return eventRepository.findWithFilters(sport, provincia, nivel)
                .stream()
                .map(EventResponse::from)
                .toList();
    }

    public List<EventResponse> getThisWeekend() {
        LocalDate today = LocalDate.now();
        // Próximo sábado
        LocalDate saturday = today.with(java.time.DayOfWeek.SATURDAY);
        LocalDate sunday = saturday.plusDays(1);
        return eventRepository.findByFechaBetweenAndActivoTrue(saturday, sunday)
                .stream()
                .map(EventResponse::from)
                .toList();
    }

    public EventResponse create(EventRequest req) {
        Sport sport = sportRepository.findById(req.sportId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deporte no encontrado"));

        Location location = locationRepository.findById(req.locationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ubicación no encontrada"));

        Event event = Event.builder()
                .nombre(req.nombre())
                .slug(generateSlug(req.nombre()))
                .sport(sport)
                .location(location)
                .fecha(req.fecha())
                .fechaLimiteInscripcion(req.fechaLimiteInscripcion())
                .distancias(req.distancias())
                .precio(req.precio())
                .nivel(req.nivel())
                .urlInscripcion(req.urlInscripcion())
                .fuente(req.fuente())
                .activo(true)
                .build();

        return EventResponse.from(eventRepository.save(event));
    }

    public void delete(UUID id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
        event.setActivo(false);
        eventRepository.save(event);
    }

    private String generateSlug(String nombre) {
        String normalized = Normalizer.normalize(nombre, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
        return normalized + "-" + UUID.randomUUID().toString().substring(0, 6);
    }
}
