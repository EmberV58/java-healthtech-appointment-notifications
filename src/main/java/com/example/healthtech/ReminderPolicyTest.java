package com.example.healthtech;

import java.time.Instant;

public final class ReminderPolicyTest {
    public static void main(String[] args) {
        Instant now = Instant.parse("2026-09-02T00:00:00Z");
        Instant appointment = Instant.parse("2026-09-02T12:00:00Z");
        var result = new ReminderPolicy().decide(appointment, true, now);
        if (result != ReminderPolicy.Decision.SEND) throw new AssertionError(result);
        System.out.println("SEND");
    }
}
