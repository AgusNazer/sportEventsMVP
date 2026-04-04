package com.sportevents.sporteventsMVP.dto.auth;

public record RegisterRequest(
        String nombre,
        String email,
        String password
) {}