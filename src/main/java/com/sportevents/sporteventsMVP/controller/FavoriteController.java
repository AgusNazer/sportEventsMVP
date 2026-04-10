package com.sportevents.sporteventsMVP.controller;

import com.sportevents.sporteventsMVP.dto.favorite.FavoriteResponse;
import com.sportevents.sporteventsMVP.service.FavoriteService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> getFavorites(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(favoriteService.getFavorites(email));
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<FavoriteResponse> addFavorite(
            @AuthenticationPrincipal String email,
            @PathVariable UUID eventId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(favoriteService.addFavorite(email, eventId));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal String email,
            @PathVariable UUID eventId) {
        favoriteService.removeFavorite(email, eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}/check")
    public ResponseEntity<Boolean> isFavorite(
            @AuthenticationPrincipal String email,
            @PathVariable UUID eventId) {
        return ResponseEntity.ok(favoriteService.isFavorite(email, eventId));
    }
}