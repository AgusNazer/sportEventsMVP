package com.sportevents.sporteventsMVP.service;

import com.sportevents.sporteventsMVP.dto.chat.ChatRequest;
import com.sportevents.sporteventsMVP.dto.chat.ChatResponse;
import com.sportevents.sporteventsMVP.entity.Event;
import com.sportevents.sporteventsMVP.repository.EventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final EventRepository eventRepository;
    private final WebClient webClient;

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.model}")
    private String groqModel;

    public ChatService(EventRepository eventRepository, WebClient.Builder webClientBuilder,
                       @Value("${groq.api.url}") String groqApiUrl) {
        this.eventRepository = eventRepository;
        this.webClient = webClientBuilder.baseUrl(groqApiUrl).build();
    }

    public ChatResponse chat(ChatRequest req) {
        List<Event> eventos = eventRepository.findByActivoTrueOrderByFechaAsc();

        StringBuilder contexto = new StringBuilder();
        contexto.append("Sos un asistente de SportEvents AR, una app de eventos deportivos en Argentina. ");
        contexto.append("Respondé siempre en español, de forma amigable y concisa. ");
        contexto.append("Solo respondé preguntas relacionadas con eventos deportivos. ");
        contexto.append("Estos son los eventos disponibles:\n\n");

        for (Event e : eventos) {
            contexto.append(String.format("- %s | Deporte: %s | Fecha: %s | Lugar: %s, %s",
                    e.getNombre(),
                    e.getSport().getNombre(),
                    e.getFecha(),
                    e.getLocation().getCiudad(),
                    e.getLocation().getProvincia()
            ));
            if (e.getDistancias() != null) contexto.append(" | Distancias: ").append(e.getDistancias());
            if (e.getPrecio() != null) contexto.append(" | Precio: ").append(e.getPrecio());
            if (e.getFechaLimiteInscripcion() != null) contexto.append(" | Inscripción hasta: ").append(e.getFechaLimiteInscripcion());
            contexto.append("\n");
        }

        Map<String, Object> body = Map.of(
                "model", groqModel,
                "messages", List.of(
                        Map.of("role", "system", "content", contexto.toString()),
                        Map.of("role", "user", "content", req.mensaje())
                ),
                "max_tokens", 500,
                "temperature", 0.7
        );

        Map response = webClient.post()
                .header("Authorization", "Bearer " + groqApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        String respuesta = extractResponse(response);
        return new ChatResponse(respuesta);
    }

    @SuppressWarnings("unchecked")
    private String extractResponse(Map response) {
        try {
            List<Map> choices = (List<Map>) response.get("choices");
            Map message = (Map) choices.get(0).get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            return "Lo siento, no pude procesar tu consulta. Intentá de nuevo.";
        }
    }
}