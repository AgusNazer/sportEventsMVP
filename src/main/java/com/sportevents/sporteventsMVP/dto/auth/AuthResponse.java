package com.sportevents.sporteventsMVP.dto.auth;

public record AuthResponse(
        String token,
        String email,
        String rol

) {}
