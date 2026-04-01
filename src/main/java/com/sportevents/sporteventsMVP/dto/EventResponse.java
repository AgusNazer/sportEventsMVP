package com.sportevents.sporteventsMVP.dto;

import com.sportevents.sporteventsMVP.entity.Event;
import com.sportevents.sporteventsMVP.enums.EventLevel;

import java.time.LocalDate;
import java.util.UUID;

public record EventResponse(
        UUID id,
        String nombre,
        String slug,
        String sport,
        String ciudad,
        String provincia,
        LocalDate fecha,
        LocalDate fechaLimiteInscripcion,
        String distancias,
        String precio,
        EventLevel nivel,
        String urlInscripcion,
        boolean activo
) {
    public static EventResponse from(Event e) {
        return new EventResponse(
                e.getId(),
                e.getNombre(),
                e.getSlug(),
                e.getSport().getNombre(),
                e.getLocation().getCiudad(),
                e.getLocation().getProvincia(),
                e.getFecha(),
                e.getFechaLimiteInscripcion(),
                e.getDistancias(),
                e.getPrecio(),
                e.getNivel(),
                e.getUrlInscripcion(),
                e.getActivo()
        );
    }
}
