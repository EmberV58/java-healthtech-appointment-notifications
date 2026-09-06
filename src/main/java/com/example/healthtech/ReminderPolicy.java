package com.example.healthtech;

import java.time.*;

public final class ReminderPolicy {
    public enum Decision { SEND, HOLD }
    public Decision decide(Instant appointment, boolean consent, Instant now) {
        Duration until = Duration.between(now, appointment);
        return consent && !until.isNegative() && until.compareTo(Duration.ofHours(24)) <= 0 ? Decision.SEND : Decision.HOLD;
    }
}
