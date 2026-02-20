# Event-Driven Microservices with Kafka: Asset–Telemetry–Alert System

## 1. Overview

### Problem with REST-based communication

* Tight coupling (service must be up)
* Back-pressure issues
* Poor scalability for millions of events
* Hard to replay or audit

### What Kafka gives us

* Asynchronous communication
* Loose coupling
* High throughput (millions/sec)
* Event replay
* Natural fit for telemetry & alerts

> **Telemetry systems are event streams by nature → Kafka is the backbone**

---

## 2. Event-Driven High-Level Flow

```
Asset Service
   │
   │ AssetRegisteredEvent
   ▼
Kafka Topic: asset.events
   │
   ├────────► Telemetry Service
   │
   └────────► Alert Service


Telemetry Service
   │
   │ TelemetryReceivedEvent
   ▼
Kafka Topic: telemetry.events
   │
   └────────► Alert Service
```

No service calls another service’s database or REST API.

---

## 3. Bounded Context → Kafka Topic Mapping

| Service           | Owns Topic         | Publishes         | Consumes                       |
| ----------------- | ------------------ | ----------------- | ------------------------------ |
| Asset Service     | `asset.events`     | AssetRegistered   | —                              |
| Telemetry Service | `telemetry.events` | TelemetryReceived | asset.events                   |
| Alert Service     | —                  | AlertTriggered    | asset.events, telemetry.events |

**Rule**

> A service publishes events only about **its own domain**

---

## 4. Events (Contracts) – The Most Important Part

### AssetRegisteredEvent

```json
{
  "eventId": "uuid",
  "eventType": "AssetRegistered",
  "assetId": "TURBINE-101",
  "assetType": "TURBINE",
  "location": "AP-Plant-7",
  "timestamp": "2026-01-10T10:15:00Z"
}
```

Published by **Asset Service**.

---

### TelemetryReceivedEvent

```json
{
  "eventId": "uuid",
  "eventType": "TelemetryReceived",
  "assetId": "TURBINE-101",
  "power": 82.4,
  "temperature": 96.2,
  "timestamp": "2026-01-10T10:16:05Z"
}
```

Published by **Telemetry Service**.

---

### AlertTriggeredEvent (optional downstream)

```json
{
  "alertId": "uuid",
  "assetId": "TURBINE-101",
  "severity": "HIGH",
  "reason": "Temperature threshold breached",
  "timestamp": "2026-01-10T10:16:06Z"
}
```

Published by **Alert Service**.

---

## 5. Asset Service – Kafka Producer

### Responsibility

* Publish asset lifecycle events
* Never care who consumes them

### Spring Boot Producer Example

```java
@Service
public class AssetEventPublisher {

    private final KafkaTemplate<String, AssetRegisteredEvent> kafkaTemplate;

    public AssetEventPublisher(KafkaTemplate<String, AssetRegisteredEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishAssetRegistered(AssetRegisteredEvent event) {
        kafkaTemplate.send(
            "asset.events",
            event.getAssetId(),
            event
        );
    }
}
```

✔ Keyed by `assetId`
✔ Order preserved per asset

---

## 6. Telemetry Service – Consumer + Producer

### Consumes Asset Events

Why?

* Cache valid assets
* Avoid synchronous validation calls

```java
@KafkaListener(topics = "asset.events", groupId = "telemetry-service")
public void onAssetRegistered(AssetRegisteredEvent event) {
    assetCache.put(event.getAssetId(), event);
}
```

---

### Publishes Telemetry Events

```java
@Service
public class TelemetryEventPublisher {

    private final KafkaTemplate<String, TelemetryReceivedEvent> kafkaTemplate;

    public void publishTelemetry(TelemetryReceivedEvent event) {
        kafkaTemplate.send(
            "telemetry.events",
            event.getAssetId(),
            event
        );
    }
}
```

---

## 7. Alert Service – Event-Driven Consumer

### Consumes Telemetry Events

```java
@KafkaListener(topics = "telemetry.events", groupId = "alert-service")
public void onTelemetry(TelemetryReceivedEvent event) {

    if (event.getTemperature() > 100) {
        triggerAlert(event);
    }
}
```

### Why this is powerful

* Alert service scales independently
* No impact on telemetry ingestion
* New alert types = new consumers

---

## 8. Consumer Groups & Scaling (VERY IMPORTANT)

### Telemetry ingestion

```
Topic: telemetry.events
Partitions: 12

Alert Service instances: 6
→ each instance consumes 2 partitions
```

✔ Horizontal scaling
✔ Exactly-once per partition order

---

## 9. Delivery Semantics (Realistic View)

### Default choice: At-least-once

* Events may be processed twice
* Consumers must be **idempotent**

Example:

```java
if (alertAlreadyExists(eventId)) return;
```

---

### Exactly-once (Advanced, optional)

* Kafka transactions
* More complexity
* Rarely needed for alerts

> **Idempotency > exactly-once** in most systems

---

## 10. Failure Handling & Replay

### If Alert Service goes down

* Kafka retains events
* Consumers resume from last offset

### If bug in alert logic

* Reset consumer offset
* Replay telemetry events
* Recompute alerts

> This is **impossible with REST**

---

## 11. Schema Evolution (CRITICAL)

Use:

* Schema Registry (Avro/JSON Schema)
* Backward-compatible changes

Rules:

* Add fields → OK
* Remove fields → NO
* Change meaning → NO

---

## 12. Transaction Boundaries 

### Correct pattern

```
DB write → publish event
```

Or:

* Outbox Pattern
* CDC (Debezium)

Never:
1. DB commit in one service
2. REST call to another service
3. Shared transaction

---

## 13. Common Mistakes to Avoid
* Direct REST calls between services
* Sharing databases
* Tight coupling of event schemas
* Ignoring idempotency


---

## 14. Why This Design Works

* Loose coupling
* Massive scalability
* Natural back-pressure
* Event replay
* Clear service ownership

---



