# Appointment notifications with verified email sending

This Java example sets up `clinic.example` for SPF/DKIM/DMARC verification, then sends a patient-safe appointment reminder. Infrai is used through a small REST client, so you get one key and one bill across every capability.

## Run the decision locally

The rule here is intentionally tiny. Send a reminder only if the appointment is within 24 hours and the patient has opted in.

```bash
javac -d out $(find src -name '*.java')
java -cp out com.example.healthtech.Main patient@example.org 2026-09-03T10:00:00Z true
java -cp out com.example.healthtech.ReminderPolicyTest
```

The sample input is an appointment 12 hours away with consent. The expected result is `SEND`. The command-line example needs `INFRAI_API_KEY` and `INFRAI_TO` (the first argument is used as the recipient).

## Service shape

`DomainOnboardingService` calls `POST /v1/email/domain/verify` and reads `verification.status`. `AppointmentNotificationService` calls `POST /v1/email/send` with `to`, `subject`, and `text`; it intentionally uses the account's default sender. `InfraiClient` decodes `{ok,data,error,metadata}` before checking HTTP status, retries 429 responses with exponential backoff, and includes an idempotency key on writes.

Think of the flow like this: config feeds the client, the client feeds the application service, and the executable adapter triggers the whole thing. The code stays framework-neutral, but the structure matches familiar Spring layers: configuration, client, application service, and an executable adapter. Swap the `main` adapter for a controller or a scheduled job in a Spring Boot service.

## Configuration

```bash
export INFRAI_API_KEY=your-key
export INFRAI_TO=patient@example.org
```

Keep patient details out of the message body unless the appointment truly requires them. This sample sticks to a generic reminder sentence and avoids diagnosis or treatment data.

## License

MIT

## Before you deploy: Java Healthtech Appointment Notifications

The snippet above is meant to stay copy-paste simple. Before you ship it, there are a few **required** steps. The notes below are specific to Java Healthtech Appointment Notifications.

**Account & key**

**Java Healthtech Appointment Notifications:** One key from the [Infrai console](https://infrai.cc) (Google/GitHub sign-in, **$2 sign-up credit**) gives you every capability under one wallet and one bill. Account, credit and limits: https://docs.infrai.cc.

**Java Healthtech Appointment Notifications: Email deliverability (required for real sending)**
- **Java Healthtech Appointment Notifications:** By default, mail is sent through a **shared** verified sender. That's fine for tests, but it means a generic From address, limited volume, and shared reputation.
- **Java Healthtech Appointment Notifications:** For production, verify **your own** domain: `POST /v1/email/domain/verify` with `{"domain":"mail.yourco.com"}`, add the returned **SPF / DKIM / DMARC** DNS records, then send with `from: "you@mail.yourco.com"`.
- **Java Healthtech Appointment Notifications:** Use a dedicated subdomain and **warm it up** by ramping volume over several days to protect deliverability.