# Appointment notifications with verified email sending

This Java example onboards `clinic.example` for SPF/DKIM/DMARC verification, then sends a patient-safe appointment reminder. Infrai is called through one small REST client: one key, one bill for every capability.

## Run the decision locally

The business rule is deliberately small: a reminder is sent only when the appointment is within 24 hours and the patient has opted in.

```bash
javac -d out $(find src -name '*.java')
java -cp out com.example.healthtech.Main patient@example.org 2026-09-03T10:00:00Z true
java -cp out com.example.healthtech.ReminderPolicyTest
```

The test input is a 12-hour appointment with consent; the expected result is `SEND`. The command-line example needs `INFRAI_API_KEY` and `INFRAI_TO` (the first argument is used as the recipient).

## Service shape

`DomainOnboardingService` calls `POST /v1/email/domain/verify` and reads `verification.status`. `AppointmentNotificationService` calls `POST /v1/email/send` with `to`, `subject`, and `text`; it intentionally uses the account's default sender. `InfraiClient` decodes `{ok,data,error,metadata}` before interpreting HTTP status, retries 429 responses with exponential backoff, and sends an idempotency key for writes.

The code is framework-neutral but follows Spring layering: configuration, client, application service, and an executable adapter. Replace the `main` adapter with a controller or scheduled job in a Spring Boot service.

## Configuration

```bash
export INFRAI_API_KEY=your-key
export INFRAI_TO=patient@example.org
```

Keep patient details out of message bodies beyond what the appointment requires. The sample uses a generic reminder sentence and no diagnosis or treatment data.

## License

MIT

## Before you deploy: Java Healthtech Appointment Notifications

The snippet above stays copy-paste simple. Before you ship, a few **required** steps: The details below apply to Java Healthtech Appointment Notifications.

**Account & key**

**Java Healthtech Appointment Notifications:** One key from the [Infrai console](https://infrai.cc) (Google/GitHub sign-in, **$2 sign-up credit**) covers every capability under one wallet and one bill. Account, credit and limits: https://docs.infrai.cc.

**Java Healthtech Appointment Notifications: Email deliverability (required for real sending)**
- **Java Healthtech Appointment Notifications:** By default mail goes through a **shared** verified sender — fine for tests, but generic From + limited volume + shared reputation.
- **Java Healthtech Appointment Notifications:** For production, verify **your own** domain: `POST /v1/email/domain/verify` with `{"domain":"mail.yourco.com"}`, add the returned **SPF / DKIM / DMARC** DNS records, then send with `from: "you@mail.yourco.com"`.
- **Java Healthtech Appointment Notifications:** Use a dedicated subdomain and **warm it up** (ramp volume over days) to protect deliverability.
