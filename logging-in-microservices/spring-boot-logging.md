# 1. Default Logging in Spring Boot

Spring Boot comes with:

* **SLF4J (API)**
* **Logback (implementation)**

You don’t need to add anything.

Just write:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AssetService {

    private static final Logger log =
            LoggerFactory.getLogger(AssetService.class);

    public void createAsset(String name) {
        log.info("Creating asset with name: {}", name);
    }
}
```

Notice `{}` placeholder — never use string concatenation.

Good:

```java
log.info("Asset id: {}", id);
```

Bad:

```java
log.info("Asset id: " + id);
```

---

# 2. Log Levels 

| Level | When to Use                 |
| ----- | --------------------------- |
| ERROR | System failure              |
| WARN  | Suspicious but not breaking |
| INFO  | Business-level event        |
| DEBUG | Developer details           |
| TRACE | Very detailed flow          |

---

### Example in Telemetry System

```java
log.info("Received telemetry for assetId={}", assetId);
log.debug("Telemetry payload: {}", payload);
log.warn("Telemetry delay detected for assetId={}", assetId);
log.error("Failed to save telemetry", ex);
```

---

# 3. Configure Log Level

In `application.yml`:

```yaml
logging:
  level:
    root: INFO
    com.example.asset: DEBUG
```

This means:

* Entire app → INFO
* Your package → DEBUG

In production, keep:

```
root: INFO
```

---

# 4. Custom Log Format

Default logs look like:

```
2026-02-13 12:15:34.123  INFO 12345 --- [nio-8080-exec-1]
```

You can customize:

```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

---

# 5. Logging to File

```yaml
logging:
  file:
    name: logs/asset-app.log
```

Now logs go to:

```
logs/asset-app.log
```

---

# 6. Structured Logging (Recommended for Microservices)

Instead of:

```
User created
```

Do:

```java
log.info("event=asset_created assetId={} userId={}", assetId, userId);
```

Better for:

* ELK
* Splunk
* Grafana Loki


---

# 7. Logging in Controllers (Best Practice)

```java
@RestController
@RequestMapping("/assets")
public class AssetController {

    private static final Logger log =
            LoggerFactory.getLogger(AssetController.class);

    @PostMapping
    public ResponseEntity<?> create(@RequestBody AssetRequest request) {

        log.info("POST /assets called. assetName={}", request.getName());

        return ResponseEntity.ok().build();
    }
}
```

Avoid logging entire request body if it contains:

* Sensitive data
* Large payloads (telemetry JSON)

---

# 8. Exception Logging

Bad:

```java
log.error("Something failed");
```

Good:

```java
log.error("Failed to process telemetry for assetId={}", assetId, ex);
```

Always pass exception as last argument.

---

# 9. Using Lombok (Cleaner)

If you're using Lombok:

```java
@Slf4j
@Service
public class TelemetryService {

    public void process() {
        log.info("Processing telemetry...");
    }
}
```

Much cleaner.

---

# 10. Correlation ID (Very Important for Microservices)

In distributed systems:

Gateway → Asset Service → Telemetry Service

You need to trace a single request.

Use MDC:

```java
import org.slf4j.MDC;

MDC.put("correlationId", UUID.randomUUID().toString());
```

Update log pattern:

```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%X{correlationId}] %-5level %logger - %msg%n"
```

Now logs show:

```
2026-02-13 12:40:01 [abc-123] INFO TelemetryService - Processing telemetry
```

This is production-grade logging.

---

# 11. Logging for Telemetry System


### Log at INFO:

* Asset created
* Telemetry received
* Alert triggered

### Log at DEBUG:

* Payload details
* SQL params
* Calculation steps

### Log at ERROR:

* DB failures
* External service failure
* Kafka failure

---

# 12. Enable SQL Logging (For Debugging JPA)

```yaml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

This prints:

* SQL query
* Bound parameters

Use only in development.

---

# 13. Production Best Practices

Never:

* Log passwords
* Log tokens
* Log huge JSON payloads
* Log inside tight loops at INFO

Always:

* Use structured logs
* Rotate log files
* Centralize logs

