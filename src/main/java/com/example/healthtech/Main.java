package com.example.healthtech;

import java.time.Instant;

public final class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) throw new IllegalArgumentException("usage: Main <to> <appointment-iso> <consent>");
        InfraiClient client = new InfraiClient(InfraiConfig.fromEnvironment());
        AppointmentNotificationService service = new AppointmentNotificationService(client);
        System.out.println(service.notify(args[0], Instant.parse(args[1]), Boolean.parseBoolean(args[2]), Instant.now()));
    }
}
