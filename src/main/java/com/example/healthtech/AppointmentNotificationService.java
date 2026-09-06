package com.example.healthtech;

import java.time.Instant;

public final class AppointmentNotificationService {
    private final InfraiClient client;
    private final ReminderPolicy policy = new ReminderPolicy();
    public AppointmentNotificationService(InfraiClient client) { this.client = client; }
    public ReminderPolicy.Decision notify(String to, Instant appointment, boolean consent, Instant now) throws Exception {
        ReminderPolicy.Decision decision = policy.decide(appointment, consent, now);
        if (decision == ReminderPolicy.Decision.SEND) {
            String json = "{\"to\":\"" + to + "\",\"subject\":\"Appointment reminder\",\"body\":\"Your appointment is scheduled within the next day.\"}";
            client.post("/v1/email/send", json);
        }
        return decision;
    }
}
