package com.sportevents.sporteventsMVP.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailService {

    private final WebClient webClient;

    @Value("${resend.api.key}")
    private String resendApiKey;

    public EmailService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.resend.com").build();
    }

    public void sendReminderEmail(String toEmail, String nombre, String eventNombre,
                                  String eventFecha, String eventCiudad, int diasAntes) {
        String subject = diasAntes == 1
                ? "🏃 Mañana es tu evento: " + eventNombre
                : "🏃 Tu evento en " + diasAntes + " días: " + eventNombre;

        String html = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <div style="background: #111827; padding: 24px; border-radius: 12px 12px 0 0;">
                        <h1 style="color: white; margin: 0; font-size: 20px;">
                            🏃 SportEvents AR
                        </h1>
                    </div>
                    <div style="background: #f9fafb; padding: 24px; border-radius: 0 0 12px 12px; border: 1px solid #e5e7eb;">
                        <p style="color: #374151;">Hola <strong>%s</strong>,</p>
                        <p style="color: #374151;">Te recordamos que en <strong>%d día%s</strong> tenés un evento:</p>
                        <div style="background: white; border: 1px solid #e5e7eb; border-radius: 8px; padding: 16px; margin: 16px 0;">
                            <h2 style="color: #111827; margin: 0 0 8px 0; font-size: 18px;">%s</h2>
                            <p style="color: #6b7280; margin: 4px 0;">📅 %s</p>
                            <p style="color: #6b7280; margin: 4px 0;">📍 %s</p>
                        </div>
                        <p style="color: #9ca3af; font-size: 12px; margin-top: 24px;">
                            SportEvents AR — Encontrá tu próximo evento deportivo
                        </p>
                    </div>
                </div>
                """.formatted(
                nombre,
                diasAntes,
                diasAntes == 1 ? "" : "s",
                eventNombre,
                eventFecha,
                eventCiudad
        );

        Map<String, Object> body = Map.of(
                "from", "SportEvents AR <onboarding@resend.dev>",
                "to", toEmail,
                "subject", subject,
                "html", html
        );

        webClient.post()
                .uri("/emails")
                .header("Authorization", "Bearer " + resendApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .subscribe(
                        res -> System.out.println("Email enviado a " + toEmail),
                        err -> System.err.println("Error enviando email: " + err.getMessage())
                );
    }
}