package com.example.healthtech;

public final class DomainOnboardingService {
    private final InfraiClient client;
    public DomainOnboardingService(InfraiClient client) { this.client = client; }
    public String verify(String domain) throws Exception {
        return client.post("/v1/email/domain/verify", "{\"domain\":\"" + domain + "\"}");
    }
}
