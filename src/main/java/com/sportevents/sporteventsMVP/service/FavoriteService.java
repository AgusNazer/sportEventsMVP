package com.sportevents.sporteventsMVP.service;

import com.sportevents.sporteventsMVP.dto.favorite.FavoriteResponse;
import com.sportevents.sporteventsMVP.entity.Event;
import com.sportevents.sporteventsMVP.entity.Favorite;
import com.sportevents.sporteventsMVP.entity.User;
import com.sportevents.sporteventsMVP.repository.EventRepository;
import com.sportevents.sporteventsMVP.repository.FavoriteRepository;
import com.sportevents.sporteventsMVP.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public List<FavoriteResponse> getFavorites(String email) {
        User user = getUser(email);
        return favoriteRepository.findByUser_Id(user.getId())
                .stream()
                .map(FavoriteResponse::from)
                .toList();
    }

    public FavoriteResponse addFavorite(String email, UUID eventId) {
        User user = getUser(email);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));

        if (favoriteRepository.existsByUser_IdAndEvent_Id(user.getId(), eventId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya está en favoritos");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .event(event)
                .build();

        return FavoriteResponse.from(favoriteRepository.save(favorite));
    }

    @Transactional
    public void removeFavorite(String email, UUID eventId) {
        User user = getUser(email);
        if (!favoriteRepository.existsByUser_IdAndEvent_Id(user.getId(), eventId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorito no encontrado");
        }
        favoriteRepository.deleteByUser_IdAndEvent_Id(user.getId(), eventId);
    }

    public boolean isFavorite(String email, UUID eventId) {
        User user = getUser(email);
        return favoriteRepository.existsByUser_IdAndEvent_Id(user.getId(), eventId);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}