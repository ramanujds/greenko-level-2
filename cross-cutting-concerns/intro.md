# 1. What is an API Gateway (and why you need it)

An **API Gateway** is a **single entry point** for all clients:

* Web UI
* Mobile apps
* IoT devices
* External consumers

Instead of clients calling:

```
asset-service
telemetry-service
alert-service
```

They call:

```
api-gateway
```

Gateway decides:

* **Where** to route
* **How** to secure
* **What** to allow or block

---

## Asset & Telemetry without Gateway

```
Client → Asset Service
Client → Telemetry Service
Client → Alert Service
```

Problems:

* Security duplicated everywhere
* CORS everywhere
* Rate limiting everywhere
* Client must know service URLs

---

## With API Gateway 


```
Client → API Gateway → Microservices
```

---

# 2. Spring Cloud Gateway 

**Spring Cloud Gateway** is:

* Reactive
* Non-blocking
* Built on Spring WebFlux
* Designed specifically for microservices

It replaces older:

* Zuul (Netflix)

---

# 3. Centralized Routing

### What routing means

Mapping:

```
/api/v1/assets/** → asset-service
/api/v1/telemetry/** → telemetry-service
/api/v1/alerts/** → alert-service
```

### Why centralize it?

* Clients don’t care about service names
* Services can move / scale freely
* One place to manage URLs

---

## Routing Example (application.yml)

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: asset-service
          uri: lb://asset-service
          predicates:
            - Path=/api/v1/assets/**
        - id: telemetry-service
          uri: lb://telemetry-service
          predicates:
            - Path=/api/v1/telemetry/**
```

`lb://` → load-balanced via service discovery.

---

# 4. Cross-Cutting Concerns 

## What are cross-cutting concerns?

Things that **apply to all APIs**, regardless of business logic:

* Authentication
* Authorization
* Logging
* Rate limiting
* CORS

If you put these in each service → duplication + inconsistency.

---

## 4.1 Authentication & Authorization

### Flow

```
Client → Gateway → Service
```

Gateway:

* Validates JWT
* Extracts user info
* Rejects unauthorized requests

Services:

* Trust gateway
* Focus on business logic

### Why this matters

Telemetry ingestion might:

* Allow devices
* Block users
  Asset APIs:
* Allow users
* Block devices

Gateway enforces this cleanly.

---

## 4.2 Logging & Correlation IDs

Gateway:

* Generates correlation ID
* Logs request/response
* Adds header to downstream services

Now you can trace:

```
Gateway → Asset → Alert → Maintenance
```

With **one ID**.

---

## 4.3 Rate Limiting

Very important for:

* Telemetry ingestion
* Public APIs

Example:

* Max 1000 req/sec per device
* Max 100 req/min per user

Implemented **once** at gateway.

---

# 5. API Gateway Filters 

Filters are logic executed:

* **Before** request is routed
* **After** response is returned

### Types

* Global Filters (all routes)
* Route-specific Filters

---

## 5.1 Global Filter Example (Logging)

```java
@Component
public class LoggingFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(
        ServerWebExchange exchange,
        GatewayFilterChain chain) {

        String path = exchange.getRequest().getPath().toString();
        log.info("Incoming request: {}", path);

        return chain.filter(exchange);
    }
}
```

Runs for **every request**.

---

## 5.2 Route-Specific Filter Example

```yaml
filters:
  - AddRequestHeader=X-Source, api-gateway
```

Or strip paths:

```yaml
- StripPrefix=2
```

---

## 6. CORS Handling (Centralized & Clean)

### Problem without Gateway

Each service configures:

* Allowed origins
* Methods
* Headers

Messy and error-prone.

---

### Centralized CORS in Gateway

```yaml
spring:
  cloud:
    gateway:
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins: "http://localhost:4200"
            allowedMethods:
              - GET
              - POST
              - PUT
              - DELETE
```

Now:

* All services inherit CORS rules
* No duplication

---

# 7. Versioned Routes 

### Why API versioning?

* Clients evolve slowly
* Backend changes fast

You **cannot** break old clients.

---

## Versioning at Gateway Level

### Example

```
/api/v1/assets → asset-service-v1
/api/v2/assets → asset-service-v2
```

Gateway routing:

```yaml
- id: asset-v1
  uri: lb://asset-service
  predicates:
    - Path=/api/v1/assets/**

- id: asset-v2
  uri: lb://asset-service
  predicates:
    - Path=/api/v2/assets/**
```

Same service, different controllers.

---

## Why Gateway-level versioning is better

* Services remain clean
* Clients choose version explicitly
* Easy rollback

---

# 8. What NOT to put in API Gateway

- Business logic 
- Database access 
- Heavy transformations
- Stateful sessions

Gateway should be:

> **Thin, fast, and dumb**

---


# 9. Asset & Telemetry – With API Gateway

```
Client
  ↓
API Gateway
  - Auth
  - Rate limit
  - CORS
  - Routing
  ↓
Microservices
  - Business logic
  - Events
  - Databases
```

---

# 10. Simple Rule to Remember

> **If it applies to all APIs, it belongs in the Gateway.**
> **If it’s business logic, it belongs in the service.**

---

