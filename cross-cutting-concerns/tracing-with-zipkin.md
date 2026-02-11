# Tracing with Zipkin in Spring Cloud Gateway

## How Zipkin fits into your system

```text
Client
  |
Spring Cloud Gateway
  |
Inventory Service
  |
Database
```

Each hop:

* creates a **span**
* shares the **same traceId**
* sends data to Zipkin

Zipkin stores and visualizes it.

---

## What You Use in Spring Boot 

In modern Spring Boot (3.x):

* Micrometer Tracing
* Zipkin exporter

You don’t write tracing code manually.

---

## Start Zipkin 

### Fastest way (Docker)

```bash
docker run -d -p 9411:9411 openzipkin/zipkin
```

UI:

```
http://localhost:9411
```

This is enough for local + Kubernetes dev.

---

## Add Dependencies (Gateway + Services)

### Maven (all services + gateway)

```xml
<dependency>
  <groupId>io.micrometer</groupId>
  <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>

<dependency>
  <groupId>io.zipkin.reporter2</groupId>
  <artifactId>zipkin-reporter-brave</artifactId>
</dependency>
```


---

## 7Configure Tracing 

### `application.yml`

```yaml
management:
  tracing:
    sampling:
      probability: 1.0   # dev only

  zipkin:
    tracing:
      endpoint: http://localhost:9411/api/v2/spans
```

### Sampling guidance

* `1.0` → dev / debugging
* `0.1` → prod (10%)
* `0.01` → high traffic prod

---

## Automatic Trace Propagation

Spring Boot automatically:

* creates a trace at entry (Gateway)
* propagates headers:

  ```
  traceparent
  X-B3-TraceId
  X-B3-SpanId
  ```
* continues trace downstream

You do **nothing** in code.

---

## Zipkin + Spring Cloud Gateway 

Gateway is critical because:

* It creates the **root span**
* Everything downstream hangs from it

Gateway spans typically show:

* route matching time
* filter execution
* downstream latency

This is why tracing *starts* at the gateway.

---

## Connecting Traces with Logs

Zipkin shows **flow**, logs show **details**.

To connect them, you add **traceId to logs**.

### Logback pattern

```yaml
logging:
  pattern:
    level: "%5p [traceId=%X{traceId} spanId=%X{spanId}]"
```

Now logs look like:

```
INFO [traceId=4f3a9c... spanId=2b1d...] InventoryController : Fetching parts
```

Click trace in Zipkin → Copy traceId → Search logs



## What You’ll See in Zipkin UI

* Timeline per request
* Service dependency graph
* Slow spans highlighted
* Errors correlated



## Where Zipkin Stops, What Comes Next

Zipkin is great for:

* Tracing
* Latency analysis
* Dependency visualization

But:

* Logs → ELK / Loki
* Metrics → Prometheus + Grafana

Together:

```
Metrics → What is wrong
Tracing → Where it is wrong
Logs → Why it is wrong
```

