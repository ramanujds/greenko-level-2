# What is Kafka?

**Apache Kafka** is a **distributed event streaming platform** used for:

* High-throughput data ingestion
* Event-driven microservices
* Real-time data pipelines
* Streaming analytics

> Think of Kafka as a **distributed, durable, ordered commit log**.

---

# Why Kafka Was Needed (The Real Problem)

Traditional systems used:

* REST calls
* Databases
* Message queues (JMS, RabbitMQ)

These break down when:

* Millions of events/sec
* Many consumers
* Need replay & audit
* Producers and consumers scale independently

Kafka was built to solve **scale + decoupling + durability**.

---

# Core Kafka Concepts (ABSOLUTELY ESSENTIAL)

## 1. Event (Message)

An **event** represents something that happened.

```json
{
  "assetId": "TURBINE-101",
  "temperature": 102.5,
  "timestamp": "2026-01-10T10:16:05Z"
}
```

Events are:

* Immutable
* Append-only
* Time-ordered (within a partition)

---

## 2. Topic

A **topic** is a logical stream of related events.

Examples:

* `asset.events`
* `telemetry.events`
* `alert.events`

> A topic is like a **table**, but you only append rows.

---

## 3. Partitions (MOST IMPORTANT CONCEPT)

Each topic is split into **partitions**.

```
telemetry.events
 ├── partition-0
 ├── partition-1
 ├── partition-2
```

### Why partitions exist

* Parallelism
* Scalability
* Ordering (per partition)

### Key rules

* Order is guaranteed **only within a partition**
* More partitions = more throughput
* Consumers scale with partitions

---

## 4. Producers

Producers:

* Write events to Kafka
* Choose topic + key + value

```java
kafkaTemplate.send(
   "telemetry.events",
   assetId,      // key
   event         // value
);
```

### Why keys matter

* Same key → same partition
* Preserves ordering per entity (assetId, orderId, userId)

---

## 5. Consumers

Consumers:

* Read events from topics
* Track progress using offsets

```java
@KafkaListener(topics = "telemetry.events")
public void consume(TelemetryEvent event) { }
```

Consumers **pull**, Kafka never pushes.

---

## 6. Consumer Groups (CRITICAL)

A **consumer group** is a set of consumers working together.

Rules:

* One partition → one consumer (per group)
* Same topic, different groups → independent consumption

### Example

```
Topic partitions: 6
Consumers in group: 3
→ each consumer gets 2 partitions
```

This is how Kafka **scales horizontally**.

---

## 7. Offset

An **offset** is a position in a partition.

```
partition-0
  offset 0 → event
  offset 1 → event
  offset 2 → event
```

Kafka stores offsets:

* Per consumer group
* In Kafka itself

Consumers control:

* When to commit
* How to replay

---

# How Kafka Works Internally (High Level)

## 1. Broker

A **broker** is a Kafka server.

A Kafka cluster = multiple brokers.

Responsibilities:

* Store partitions
* Serve producers & consumers
* Replicate data

---

## 2. Replication & Leader Election

Each partition has:

* One **leader**
* Zero or more **followers**

Only leader:

* Accepts writes
* Serves reads

Followers:

* Replicate data
* Take over on failure

This gives **fault tolerance**.

---

## 3. Retention (Kafka ≠ Queue)

Kafka does NOT delete messages when consumed.

Messages are deleted based on:

* Time (e.g., 7 days)
* Size (e.g., 1 TB)

This enables:

* Replay
* Backfill
* Auditing

---

# Delivery Semantics (INTERVIEW FAVORITE)

## At-most-once

* Fast
* Data loss possible
* Rarely acceptable

## At-least-once (MOST COMMON)

* Messages may be processed twice
* No data loss
* Consumers must be **idempotent**

## Exactly-once

* Strong guarantee
* Uses transactions
* Complex
* Used selectively

> In practice: **At-least-once + idempotency wins**

---

# Kafka vs Traditional Messaging (Key Difference)

| Feature           | Kafka           | Traditional MQ |
| ----------------- | --------------- | -------------- |
| Message retention | Yes             | No             |
| Replay            | Yes             | No             |
| Throughput        | Very high       | Moderate       |
| Scaling           | Partition-based | Broker-based   |
| Use case          | Event streaming | Task queue     |

---

# Common Kafka Patterns (REAL WORLD)

## 1. Event Streaming

* Telemetry
* Logs
* Metrics

## 2. Event-Driven Microservices

* Publish domain events
* React asynchronously

## 3. CQRS

* Write model → events
* Read model → projections

## 4. Outbox Pattern

* DB write + event publish safely

## 5. Dead Letter Queue (DLQ)

* Capture poison messages
* Debug safely

---

# Schema & Compatibility (CRITICAL)

Never send raw JSON blindly.

Use:

* Avro / JSON Schema
* Schema Registry

Rules:

* Backward compatible changes only
* Never remove required fields
* Never change semantics

---

# Performance Characteristics (Why Kafka Scales)

Kafka is fast because:

* Sequential disk writes
* Zero-copy reads
* OS page cache
* Minimal locking

Kafka prefers:

* Disk over memory
* Throughput over latency

---

# When Kafka is a BAD Choice

* Simple request-response
* Low volume CRUD apps
* Strong transactional consistency
* Real-time <1ms latency systems

Kafka is **not a replacement for REST or DB**.

---

