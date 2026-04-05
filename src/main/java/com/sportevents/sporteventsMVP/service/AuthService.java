package com.sportevents.sporteventsMVP.service;

import com.sportevents.sporteventsMVP.dto.auth.AuthResponse;
import com.sportevents.sporteventsMVP.dto.auth.LoginRequest;
import com.sportevents.sporteventsMVP.dto.auth.RegisterRequest;
import com.sportevents.sporteventsMVP.entity.User;
import com.sportevents.sporteventsMVP.enums.UserRole;
import com.sportevents.sporteventsMVP.repository.UserRepository;
import com.sportevents.sporteventsMVP.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       JwtTokenProvider jwtTokenProvider,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        User user = User.builder()
                .nombre(req.nombre())
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .rol(UserRole.USER)
                .build();

        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRol().name());
        return new AuthResponse(token, user.getEmail(), user.getNombre(), user.getRol().name());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRol().name());
        return new AuthResponse(token, user.getEmail(), user.getNombre(), user.getRol().name());
    }
    public AuthResponse getFromToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Token inválido");
        }
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return new AuthResponse(null, user.getEmail(), user.getNombre(), user.getRol().name());
    }
}