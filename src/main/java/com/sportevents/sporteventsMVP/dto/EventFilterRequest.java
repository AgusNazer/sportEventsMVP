package com.sportevents.sporteventsMVP.dto;

import com.sportevents.sporteventsMVP.enums.EventLevel;

import java.time.LocalDate;

public record EventFilterRequest(
        String sport,
        String provincia,
        EventLevel nivel,
        LocalDate desde,
        LocalDate hasta
) {}
