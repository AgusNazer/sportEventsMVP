package com.sportevents.sporteventsMVP.dto.favorite;

import com.sportevents.sporteventsMVP.dto.EventResponse;
import com.sportevents.sporteventsMVP.entity.Favorite;

import java.util.UUID;

public record FavoriteResponse(
        UUID id,
        EventResponse event
) {
    public static FavoriteResponse from(Favorite favorite) {
        return new FavoriteResponse(
                favorite.getId(),
                EventResponse.from(favorite.getEvent())
        );
    }
}