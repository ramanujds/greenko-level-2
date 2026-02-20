# 1. High-level Microservices Design


```
Client / Device
     |
     v
 ┌───────────────┐
 | Asset Service |
 └───────────────┘
        |
        | AssetRegistered (event)
        v
 ┌──────────────────┐
 | Telemetry Service |
 └──────────────────┘
        |
        | TelemetryReceived (event)
        v
 ┌───────────────┐
 | Alert Service |
 └───────────────┘
```

---

# 2. Service 1 – Asset Service (System of Record)

### Responsibility

Owns **asset identity and lifecycle**.

### What it DOES

* Register asset
* Update asset status
* Decommission asset

### What it DOES NOT do

* Store telemetry
* Evaluate alerts

---

### Sample APIs

```
POST /api/assets
GET  /api/assets/{assetId}
PUT  /api/assets/{assetId}/status
```

---

### Asset Database (Relational)

```
Asset
- assetId
- assetName
- type
- location
- status
```

---

### Event Published

**AssetRegistered**

```json
{
  "assetId": "A101",
  "type": "GENERATOR"
}
```

This event tells the system:

> “A new asset exists. Prepare to monitor it.”

---

# 3. Service 2 – Telemetry Service (High-Volume Ingestion)

### Responsibility

Handle **sensor data** from devices.

### What it DOES

* Accept telemetry data
* Store telemetry
* Publish telemetry events

### What it DOES NOT do

* Validate asset details via DB join
* Trigger alerts directly

---

### Sample API (from devices)

```
POST /api/telemetry
```

```json
{
  "assetId": "A101",
  "metric": "temperature",
  "value": 92.5
}
```

---

### Telemetry Database (Time-series)

```
Telemetry
- assetId
- metric
- value
- timestamp
```

---

### Event Published

**TelemetryReceived**

```json
{
  "assetId": "A101",
  "metric": "temperature",
  "value": 92.5
}
```

This is a **fact**, not a command.

---

# 4. Service 3 – Alert Service (Decision Maker)

### Responsibility

Evaluate telemetry and **raise alerts**.

### What it DOES

* Subscribe to telemetry events
* Apply thresholds
* Raise alerts

### What it DOES NOT do

* Collect telemetry
* Update assets

---

### Alert Rules (Example)

```
temperature > 90 → CRITICAL
```

---

### Alert Database

```
Alert
- alertId
- assetId
- metric
- value
- severity
- status
```

---

### Event Published

**AlertRaised**

```json
{
  "alertId": "AL9001",
  "assetId": "A101",
  "severity": "CRITICAL",
  "reason": "Temperature exceeded limit"
}
```

---

# 5. Communication Between Services

## 5.1 Asset → Telemetry (Event-driven)

* Asset Service publishes `AssetRegistered`
* Telemetry Service listens and initializes tracking

✔ No REST call
✔ Loose coupling

---

## 5.2 Telemetry → Alert (Event-driven)

* Telemetry Service publishes `TelemetryReceived`
* Alert Service evaluates rules

✔ Scales independently
✔ Telemetry never waits for alert logic

---

## 5.3 Optional: Telemetry → Asset (REST – Read only)

Sometimes Telemetry might need to **verify asset exists**.

```
GET /api/assets/{assetId}
```

Rules:

* Read-only
* Cached
* Timeout + fallback

---

## 6. Benefits of This Design

### Independent scaling

* Telemetry scales horizontally
* Asset remains lightweight
* Alert scales with rule complexity

---

### Failure isolation

* Alert down → telemetry still stored
* Telemetry spike → asset APIs unaffected

---



These create **tight coupling**.

---

# 8. Simple Rule of Thumb

```
Commands → REST
Facts     → Events
```

* “Create asset” → REST
* “Telemetry received” → Event
* “Alert raised” → Event

