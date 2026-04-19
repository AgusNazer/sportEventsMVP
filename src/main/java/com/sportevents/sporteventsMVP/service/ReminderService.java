package com.sportevents.sporteventsMVP.service;

import com.sportevents.sporteventsMVP.dto.reminder.ReminderRequest;
import com.sportevents.sporteventsMVP.dto.reminder.ReminderResponse;
import com.sportevents.sporteventsMVP.entity.Event;
import com.sportevents.sporteventsMVP.entity.Reminder;
import com.sportevents.sporteventsMVP.entity.User;
import com.sportevents.sporteventsMVP.repository.EventRepository;
import com.sportevents.sporteventsMVP.repository.ReminderRepository;
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
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public List<ReminderResponse> getReminders(String email) {
        User user = getUser(email);
        return reminderRepository.findByUser_Id(user.getId())
                .stream()
                .map(ReminderResponse::from)
                .toList();
    }

    public ReminderResponse createReminder(String email, UUID eventId, ReminderRequest req) {
        User user = getUser(email);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));

        if (reminderRepository.existsByUser_IdAndEvent_Id(user.getId(), eventId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya tenés un recordatorio para este evento");
        }

        if (req.diasAntes() < 1 || req.diasAntes() > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "diasAntes debe estar entre 1 y 30");
        }

        Reminder reminder = Reminder.builder()
                .user(user)
                .event(event)
                .diasAntes(req.diasAntes())
                .build();

        return ReminderResponse.from(reminderRepository.save(reminder));
    }

    @Transactional
    public void deleteReminder(String email, UUID eventId) {
        User user = getUser(email);
        if (!reminderRepository.existsByUser_IdAndEvent_Id(user.getId(), eventId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recordatorio no encontrado");
        }
        reminderRepository.deleteByUser_IdAndEvent_Id(user.getId(), eventId);
    }

    public boolean hasReminder(String email, UUID eventId) {
        User user = getUser(email);
        return reminderRepository.existsByUser_IdAndEvent_Id(user.getId(), eventId);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}