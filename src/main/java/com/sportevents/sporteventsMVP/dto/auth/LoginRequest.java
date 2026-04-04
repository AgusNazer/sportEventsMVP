package com.sportevents.sporteventsMVP.dto.auth;

public record LoginRequest (
        String email,
        String password
){ }
