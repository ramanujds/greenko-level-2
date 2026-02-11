
# Why Logging & Rate Limiting belong in the Gateway

The gateway is the **single choke point**.

So it’s the **only correct place** to:

* Log incoming requests once (not in every service)
* Enforce traffic limits centrally
* Protect downstream services

> If every microservice does this → chaos
> If the gateway does this → control

---

# LOGGING IN SPRING CLOUD GATEWAY

## What “proper logging” means in a Gateway

At gateway level, you typically want:

* HTTP method
* Path
* Request ID / Trace ID
* Status code
* Response time
* (Optionally) headers

---

## Implement Logging using a Global Filter (Best Practice)

### Create a Global Filter

```java
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoggingGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        long startTime = System.currentTimeMillis();

        ServerHttpRequest request = exchange.getRequest();

        return chain.filter(exchange)
                .doOnSuccess(done -> {
                    long duration = System.currentTimeMillis() - startTime;

                    log.info(
                        "method={} path={} status={} duration={}ms",
                        request.getMethod(),
                        request.getURI().getPath(),
                        exchange.getResponse().getStatusCode(),
                        duration
                    );
                });
    }

    @Override
    public int getOrder() {
        return -1; // run early
    }
}
```

### Why this works

* Non-blocking
* Reactive-safe
* Runs for **every request**
* No duplication in microservices

---

## Enable Correlation / Trace IDs 

### Add this in `application.yml`

```yaml
spring:
  cloud:
    gateway:
      default-filters:
        - AddRequestHeader=X-Request-Id, #{T(java.util.UUID).randomUUID().toString()}
```

Now every downstream service gets:

```
X-Request-Id
```

Your logs become **traceable across services**.

---

## Control Logging Verbosity

```yaml
logging:
  level:
    org.springframework.cloud.gateway: INFO
    reactor.netty.http.client: WARN
```

Avoid DEBUG in prod — it’s noisy and expensive.

---

# RATE LIMITING IN SPRING CLOUD GATEWAY

## Why Rate Limiting MUST be Reactive

Spring Cloud Gateway is:

* Netty-based
* Non-blocking
* Reactive

So rate limiting must be:

* Distributed
* Fast
* Non-blocking

**Redis is the correct backend**

---

## Add Required Dependencies

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>
```

---

## Configure Redis Rate Limiter

### `application.yml`

```yaml
spring:
  redis:
    host: localhost
    port: 6379

  cloud:
    gateway:
      routes:
        - id: inventory-service
          uri: http://part-inventory-service:8080
          predicates:
            - Path=/inventory/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
                key-resolver: "#{@userKeyResolver}"
```

---

## 9️⃣ Implement KeyResolver

This decides **who** is rate-limited.

### Option 1: Rate limit by IP

```java
@Bean
public KeyResolver userKeyResolver() {
    return exchange ->
        Mono.just(exchange.getRequest()
                .getRemoteAddress()
                .getAddress()
                .getHostAddress());
}
```

### Option 2: Rate limit by Header (API Key)

```java
@Bean
public KeyResolver apiKeyResolver() {
    return exchange ->
        Mono.justOrEmpty(
            exchange.getRequest()
                    .getHeaders()
                    .getFirst("X-API-KEY")
        ).defaultIfEmpty("anonymous");
}
```

---


## What Happens When Limit Is Exceeded

Gateway responds automatically with:

```
HTTP 429 – Too Many Requests
```

No custom code needed.

---


## Logging Best Practices

✔ Log at gateway, not services
✔ Add correlation ID
✔ Log latency
- Don’t log request bodies
- Don’t log secrets

---

## Rate Limiting Best Practices

✔ Use Redis (HA)
✔ Limit at gateway only
✔ Different limits per route
✔ KeyResolver based on identity

- Don’t rate limit inside services
- Don’t use in-memory limiter in prod

---

## 14️⃣ How This Looks in Real Architecture

```text
Client
  |
  |  Logging + Rate Limiting
  |
Spring Cloud Gateway
  |
  |  Routed traffic
  |
Microservices
```

Clean. Predictable. Scalable.

---


