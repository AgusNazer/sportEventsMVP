package com.sportevents.sporteventsMVP.service;

import com.sportevents.sporteventsMVP.entity.Reminder;
import com.sportevents.sporteventsMVP.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final ReminderRepository reminderRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void processReminders() {
        LocalDate today = LocalDate.now();

        List<Reminder> pending = reminderRepository.findPendingForDate(today);

        System.out.println("Scheduler ejecutado — fecha: " + today + " — recordatorios encontrados: " + pending.size());

        for (Reminder reminder : pending) {
            try {
                emailService.sendReminderEmail(
                        reminder.getUser().getEmail(),
                        reminder.getUser().getNombre(),
                        reminder.getEvent().getNombre(),
                        reminder.getEvent().getFecha().toString(),
                        reminder.getEvent().getLocation().getCiudad(),
                        reminder.getDiasAntes()
                );
                reminder.setEnviado(true);
                reminderRepository.save(reminder);
            } catch (Exception e) {
                System.err.println("Error procesando reminder " + reminder.getId() + ": " + e.getMessage());
            }
        }

        System.out.println("Scheduler finalizado — " + pending.size() + " emails enviados");
    }
}