package com.sportevents.sporteventsMVP.dto;

import com.sportevents.sporteventsMVP.enums.EventLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record EventRequest(
        @NotBlank String nombre,
        @NotNull UUID sportId,
        @NotNull UUID locationId,
        @NotNull LocalDate fecha,
        LocalDate fechaLimiteInscripcion,
        String distancias,
        String precio,
        EventLevel nivel,
        String urlInscripcion,
        String fuente
) {}
