package com.example.healthtech;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.UUID;

public final class InfraiClient {
    // POST /v1/email/send is the write boundary used by the notification service.
    private final InfraiConfig config;
    private final HttpClient http = HttpClient.newHttpClient();
    public InfraiClient(InfraiConfig config) { this.config = config; }

    public String post(String path, String json) throws Exception {
        for (int attempt = 0; attempt < 3; attempt++) {
            HttpRequest request = HttpRequest.newBuilder(URI.create(config.baseUrl() + path))
                .timeout(Duration.ofSeconds(20)).header("Authorization", "Bearer " + config.apiKey())
                .header("Content-Type", "application/json").header("Idempotency-Key", UUID.randomUUID().toString())
                .method("POST", HttpRequest.BodyPublishers.ofString(json)).build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            if (body.contains("\"ok\":false")) throw new IllegalStateException("Infrai response error: " + body);
            if (response.statusCode() != 429) return body;
            long wait = Math.min(2000L, 200L * (1L << attempt));
            String retryAfter = response.headers().firstValue("Retry-After").orElse(null);
            if (retryAfter != null) try { wait = Long.parseLong(retryAfter) * 1000L; } catch (NumberFormatException ignored) { }
            Thread.sleep(wait);
        }
        throw new IllegalStateException("request was rate limited");
    }
}
