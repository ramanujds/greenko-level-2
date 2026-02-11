# Why Do We Need Actuator?

In production, you don’t just need:

* Business APIs
* Database access
* Feign clients

You also need:

* Health checks
* Metrics
* Environment visibility
* Thread & memory info
* Safe runtime controls

> Actuator exposes operational information about your running application.

Think of it as:

> **The management API of your service.**

---

# Without Actuator

You can’t:

* Tell if the service is healthy
* Know if DB is connected
* Monitor memory usage
* See active configs
* Integrate with Prometheus
* Support Kubernetes probes properly

You are blind.

---

# With Actuator

Your service exposes:

```
/actuator/health
/actuator/metrics
/actuator/env
/actuator/info
```

Operations teams, monitoring systems, and Kubernetes use these endpoints.

---

# How to Enable Actuator

### Dependency

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### Expose endpoints

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

By default, only `health` is exposed.

---

# Most Important Actuator Endpoints

Let’s go through the ones that actually matter.

---

## `/actuator/health` (Most Critical)

### Purpose:

Shows application health.

Example response:

```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" }
  }
}
```

### Why it matters:

* Used by Kubernetes
* Used by load balancers
* Used by monitoring systems

---

### Health Groups and Probes

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
```

Enables:

* `/actuator/health/liveness`
* `/actuator/health/readiness`

These integrate directly with Kubernetes probes.

---

## `/actuator/metrics`

Shows Micrometer metrics.

Example:

```
/actuator/metrics/jvm.memory.used
```

Used for:

* Prometheus scraping
* Grafana dashboards
* Performance analysis

---

## `/actuator/info`

Custom metadata.

Example:

```yaml
management:
  info:
    env:
      enabled: true

info:
  app:
    name: asset-service
    version: 1.2.3
```

Response:

```json
{
  "app": {
    "name": "asset-service",
    "version": "1.2.3"
  }
}
```

Used in:

* CI/CD validation
* Deployment tracking

---

## `/actuator/env`

Shows environment properties.

Used for:

* Debugging config issues
* Verifying config server integration

Should be protected in production.

---

## `/actuator/loggers`

Allows dynamic log level change.

Example:

```
POST /actuator/loggers/com.example
{
  "configuredLevel": "DEBUG"
}
```

Super useful for:

* Debugging production issues without restart

---

## `/actuator/beans`

Shows Spring beans in context.

Used for:

* Debugging auto-configuration issues

---

## `/actuator/threaddump`

Shows thread dump.

Used for:

* Deadlock debugging
* Performance analysis

---

## `/actuator/heapdump`

Memory snapshot.

Used for:

* Memory leak debugging

---

## `/actuator/prometheus`

When using Micrometer + Prometheus.

Used for:

* Metrics scraping
* Observability stack integration

---

# How Actuator Fits in Microservices

![Image](https://miro.medium.com/1%2Asvn2QOzp4nkBOWp_xwdDVg.png)

```
Microservice
   |
Actuator endpoints
   |
Prometheus / Zipkin / Kubernetes / Grafana
```

Actuator is the bridge between your app and observability tools.

---

# Security Considerations

Never expose all endpoints publicly.

Bad:

```yaml
include: "*"
```

Better:

```yaml
include: health,info,prometheus
```

Even better:

* Secure with Spring Security
* Separate management port

```yaml
management:
  server:
    port: 9090
```

---

# Actuator in Kubernetes 

Kubernetes uses:

```
/actuator/health/liveness
/actuator/health/readiness
```

If readiness fails:

* Traffic stops

If liveness fails:

* Pod restarts

Without Actuator, proper K8s integration is impossible.

---


# When Actuator is Mandatory

* Microservices
* Kubernetes
* CI/CD
* Monitoring stack
* Production environments

