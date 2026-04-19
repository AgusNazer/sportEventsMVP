package com.sportevents.sporteventsMVP.controller;

import com.sportevents.sporteventsMVP.dto.reminder.ReminderRequest;
import com.sportevents.sporteventsMVP.dto.reminder.ReminderResponse;
import com.sportevents.sporteventsMVP.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping
    public ResponseEntity<List<ReminderResponse>> getReminders(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(reminderService.getReminders(email));
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<ReminderResponse> createReminder(
            @AuthenticationPrincipal String email,
            @PathVariable UUID eventId,
            @RequestBody ReminderRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reminderService.createReminder(email, eventId, req));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteReminder(
            @AuthenticationPrincipal String email,
            @PathVariable UUID eventId) {
        reminderService.deleteReminder(email, eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}/check")
    public ResponseEntity<Boolean> hasReminder(
            @AuthenticationPrincipal String email,
            @PathVariable UUID eventId) {
        return ResponseEntity.ok(reminderService.hasReminder(email, eventId));
    }
}