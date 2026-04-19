package com.sportevents.sporteventsMVP.dto.reminder;

import com.sportevents.sporteventsMVP.entity.Reminder;
import java.util.UUID;

public record ReminderResponse(
        UUID id,
        UUID eventId,
        String eventNombre,
        String eventFecha,
        int diasAntes
) {
    public static ReminderResponse from(Reminder r) {
        return new ReminderResponse(
                r.getId(),
                r.getEvent().getId(),
                r.getEvent().getNombre(),
                r.getEvent().getFecha().toString(),
                r.getDiasAntes()
        );
    }
}