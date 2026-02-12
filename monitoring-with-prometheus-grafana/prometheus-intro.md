# What is Prometheus?


Prometheus is an **open-source monitoring and alerting system**.

Think of it as:

> A system that **scrapes metrics from applications**, stores them as time-series data, and lets you query them.

---

### Core Concepts

| Concept            | Meaning                                   |
| ------------------ | ----------------------------------------- |
| **Pull Model**     | Prometheus pulls metrics from apps        |
| **Time-Series DB** | Stores metrics with timestamp             |
| **Targets**        | Applications being monitored              |
| **Scrape**         | Fetch metrics from `/actuator/prometheus` |
| **PromQL**         | Query language of Prometheus              |

---

# How Prometheus Works with Spring Boot


```yaml
scrape_configs:
  - job_name: 'spring-microservices'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['asset-service:8080']
        labels:
          service: 'asset-service'
      - targets: ['telemetry-service:8080']
        labels:
          service: 'telemetry-service'
```

### What is happening?

* Prometheus scrapes:

    * `http://asset-service:8080/actuator/prometheus`
    * `http://telemetry-service:8080/actuator/prometheus`

* Both belong to job:

  ```
  job = "spring-microservices"
  ```

* You added a **custom label**:

  ```
  service="asset-service"
  service="telemetry-service"
  ```

This label becomes very powerful in queries.

---

# What is a Prometheus Query (PromQL)?

PromQL = Prometheus Query Language.

It allows you to:

* Filter metrics
* Aggregate metrics
* Calculate rates
* Group by labels
* Detect anomalies

Think of it like:

> SQL for metrics

Example mental mapping:

| SQL      | PromQL          |
| -------- | --------------- |
| SELECT   | metric_name     |
| WHERE    | {label="value"} |
| GROUP BY | by(label)       |
| SUM()    | sum()           |

---

# Important Metrics in Spring Boot

When using:

## Spring Boot

## Micrometer

You automatically get metrics like:

| Metric                               | Meaning             |
| ------------------------------------ | ------------------- |
| `http_server_requests_seconds_count` | Total HTTP requests |
| `http_server_requests_seconds_sum`   | Total response time |
| `jvm_memory_used_bytes`              | JVM memory used     |
| `jvm_gc_pause_seconds_count`         | GC count            |
| `process_cpu_usage`                  | CPU usage           |

---

# Simple Prometheus Queries

## Check if service is UP

```
up{job="spring-microservices"}
```

Returns:

* 1 → UP
* 0 → DOWN

---

## Requests Count of asset-service

```
http_server_requests_seconds_count{service="asset-service"}
```

This shows total HTTP requests served by asset-service.

---

## Requests Per Second

```
rate(http_server_requests_seconds_count{service="asset-service"}[1m])
```

Meaning:

> Requests per second over last 1 minute.

This is what you show in dashboards.

---

## Compare Both Services

```
rate(http_server_requests_seconds_count{job="spring-microservices"}[1m])
```

Or group:

```
sum(rate(http_server_requests_seconds_count[1m])) by (service)
```

This gives:

| service           | RPS |
| ----------------- | --- |
| asset-service     | 12  |
| telemetry-service | 7   |

---

## JVM Memory Usage

```
jvm_memory_used_bytes{service="asset-service"}
```

Group by memory area:

```
sum(jvm_memory_used_bytes{service="asset-service"}) by (area)
```

---

## CPU Usage

```
process_cpu_usage{service="asset-service"}
```

---

# Production-Level Useful Query

### Error Rate %

```
sum(rate(http_server_requests_seconds_count{status=~"5.."}[1m])) 
/
sum(rate(http_server_requests_seconds_count[1m])) 
* 100
```

This gives:

> % of 5xx errors in last 1 minute.

Very powerful for alerting.

---

# How Labels Help You

Because we added:

```yaml
labels:
  service: 'asset-service'
```

We can now:

* Compare services
* Create per-service dashboards
* Set service-specific alerts

Without labels, everything would be messy.


## Prometheus Flow:

```
Spring Boot App
   ↓
/actuator/prometheus
   ↓
Prometheus Scrapes
   ↓
Time Series DB
   ↓
PromQL Query
   ↓
Grafana Dashboard
```

