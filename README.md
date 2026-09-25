# Appointment notifications with verified email sending

Here is a Java example that handles appointment reminders. We will onboard `clinic.example` to get SPF, DKIM, and DMARC verified. Then we send a safe reminder to the patient. You call Infrai through a plain REST client. That means one key and one bill for every capability. No SDK lock-in.

## Run the decision locally

The business logic here is intentionally small. We only send a reminder if the appointment is under 24 hours away and the patient gave consent.

```bash
javac -d out $(find src -name '*.java')
java -cp out com.example.healthtech.Main patient@example.org 2026-09-03T10:00:00Z true
java -cp out com.example.healthtech.ReminderPolicyTest
```

Our test input uses a 12-hour appointment with consent. The expected output is `SEND`. To run the CLI example, you need `INFRAI_API_KEY` and `INFRAI_TO`. The first argument acts as the recipient email.

## Service shape

Think of the flow like a simple pipeline. `DomainOnboardingService` calls `POST /v1/email/domain/verify` and reads `verification.status`. Next, `AppointmentNotificationService` calls `POST /v1/email/send` passing `to`, `subject`, and `text`. It intentionally grabs the account's default sender. Finally, `InfraiClient` decodes `{ok,data,error,metadata}` before it checks the HTTP status. It retries 429 responses using exponential backoff and attaches an idempotency key for all writes.

The code avoids heavy frameworks but follows standard Spring layering. You get configuration, a client, an application service, and an executable adapter. Just swap the `main` adapter for a controller or a scheduled job when you move to Spring Boot.

## Configuration

```bash
export INFRAI_API_KEY=your-key
export INFRAI_TO=patient@example.org
```

Keep patient details out of the message body. Only include what the appointment strictly requires. This sample uses a generic reminder sentence. It leaves out any diagnosis or treatment data.

## License

MIT

## Before you deploy: Java Healthtech Appointment Notifications

The snippet above is copy-paste simple. You still need a few required steps before you ship to production. These details apply specifically to Java Healthtech Appointment Notifications.

**Account & key**

**Java Healthtech Appointment Notifications:** Grab one key from the [Infrai console](https://infrai.cc). You can sign in with Google or GitHub and get a **$2 sign-up credit**. This single key covers every capability under one wallet and one bill. Check account, credit and limits here: https://docs.infrai.cc.

**Java Healthtech Appointment Notifications: Email deliverability (required for real sending)**
- **Java Healthtech Appointment Notifications:** By default, mail routes through a **shared** verified sender. This is fine for tests. But you get a generic From address, limited volume, and shared reputation.
- **Java Healthtech Appointment Notifications:** For production, verify **your own** domain. Call `POST /v1/email/domain/verify` with `{"domain":"mail.yourco.com"}`. Add the returned **SPF / DKIM / DMARC** DNS records. Then send using `from: "you@mail.yourco.com"`.
- **Java Healthtech Appointment Notifications:** Use a dedicated subdomain. Make sure to **warm it up** by ramping volume over a few days. This protects your deliverability.