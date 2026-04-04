package com.sportevents.sporteventsMVP.controller;

import com.sportevents.sporteventsMVP.dto.auth.AuthResponse;
import com.sportevents.sporteventsMVP.dto.auth.LoginRequest;
import com.sportevents.sporteventsMVP.dto.auth.RegisterRequest;
import com.sportevents.sporteventsMVP.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req,
                                                 HttpServletResponse response) {
        AuthResponse auth = authService.register(req);
        setAuthCookie(response, auth.token());
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req,
                                              HttpServletResponse response) {
        AuthResponse auth = authService.login(req);
        setAuthCookie(response, auth.token());
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("auth_token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(jakarta.servlet.http.HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return ResponseEntity.status(401).build();

        String token = null;
        for (Cookie c : cookies) {
            if ("auth_token".equals(c.getName())) {
                token = c.getValue();
                break;
            }
        }

        if (token == null) return ResponseEntity.status(401).build();

        AuthResponse auth = authService.getFromToken(token);
        return ResponseEntity.ok(auth);
    }

    private void setAuthCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("auth_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtExpiration / 1000));
        cookie.setAttribute("SameSite", "None");
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
}