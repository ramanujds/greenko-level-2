# Advanced Logback Configuration for Spring Boot Microservices)

Spring Boot uses **Logback by default**, and when you introduce a custom `logback-spring.xml`, you take full control over:

* Log format
* Log files
* Log rotation
* Async logging
* Environment-based config

---

# 1. Why `logback-spring.xml`?

Always use:

```
src/main/resources/logback-spring.xml
```

Why?

Because `logback-spring.xml` allows:

* Spring profiles
* Property resolution from `application.yml`


---

# 2. Basic Production-Ready logback-spring.xml

Here’s a clean starting template.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <!-- Import Spring properties -->
    <springProperty scope="context"
                    name="APP_NAME"
                    source="spring.application.name"/>

    <!-- Log Pattern -->
    <property name="LOG_PATTERN"
              value="%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"/>

    <!-- Console Appender -->
    <appender name="CONSOLE"
              class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>

    <!-- Rolling File Appender -->
    <appender name="FILE"
              class="ch.qos.logback.core.rolling.RollingFileAppender">

        <file>logs/${APP_NAME}.log</file>

        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- Daily rollover -->
            <fileNamePattern>logs/${APP_NAME}.%d{yyyy-MM-dd}.log</fileNamePattern>

            <!-- Keep logs for 30 days -->
            <maxHistory>30</maxHistory>
        </rollingPolicy>

        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>

    <!-- Root Logger -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>

</configuration>
```

Now your logs:

* Go to console
* Go to file
* Rotate daily
* Keep 30 days

---

# 3. Add Correlation ID (For Microservices)

Update pattern:

```xml
<property name="LOG_PATTERN"
          value="%d{yyyy-MM-dd HH:mm:ss} [%thread] [%X{correlationId}] %-5level %logger{36} - %msg%n"/>
```

Now every log shows:

```
2026-02-13 12:55:00 [http-nio-8080-exec-1] [abc-123] INFO ...
```

This is essential for:

* Gateway → Asset Service → Telemetry Service tracing

---

# 4. Separate Error Logs (Best Practice)

Add a dedicated error appender.

```xml
<appender name="ERROR_FILE"
          class="ch.qos.logback.core.rolling.RollingFileAppender">

    <file>logs/${APP_NAME}-error.log</file>

    <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
        <level>ERROR</level>
    </filter>

    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>logs/${APP_NAME}-error.%d{yyyy-MM-dd}.log</fileNamePattern>
        <maxHistory>30</maxHistory>
    </rollingPolicy>

    <encoder>
        <pattern>${LOG_PATTERN}</pattern>
    </encoder>
</appender>
```

Add to root:

```xml
<appender-ref ref="ERROR_FILE"/>
```

Now:

* Normal logs → main file
* Errors → separate file

Super helpful in production.

---

# 5. Enable DEBUG Only for Your Package

Instead of:

```xml
<root level="DEBUG">
```

Do:

```xml
<logger name="com.yourcompany.asset" level="DEBUG"/>
```

Keep root at INFO.

Professional logging strategy:

* Root → INFO
* Your package → DEBUG (when needed)
* Hibernate SQL → DEBUG only in dev

---

# 6. Async Logging 

Telemetry systems can log heavily. Use async wrapper.

```xml
<appender name="ASYNC_FILE"
          class="ch.qos.logback.classic.AsyncAppender">
    <appender-ref ref="FILE"/>
</appender>
```

Then use:

```xml
<root level="INFO">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="ASYNC_FILE"/>
</root>
```

Now file logging happens asynchronously.

Much better under load.

---

# 7. Profile-Based Logging (Dev vs Prod)

This is why we use `logback-spring.xml`.

```xml
<springProfile name="dev">
    <root level="DEBUG">
        <appender-ref ref="CONSOLE"/>
    </root>
</springProfile>

<springProfile name="prod">
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="ASYNC_FILE"/>
    </root>
</springProfile>
```

Now:

* Dev → DEBUG + console
* Prod → INFO + file + async

Clean separation.

---

# 8. JSON Logging (For ELK / Loki)

If you’re using ELK (which you recently configured), switch to JSON encoder.

Add dependency:

```
net.logstash.logback:logstash-logback-encoder
```

Then:

```xml
<appender name="JSON_FILE"
          class="ch.qos.logback.core.rolling.RollingFileAppender">

    <file>logs/${APP_NAME}.json</file>

    <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
</appender>
```

Now logs are structured JSON — perfect for:

* ELK
* Grafana Loki
* Splunk

---

# 9. Telemetry System Logging Strategy 

For your asset + telemetry system:

Controllers:

* Log request entry at INFO

Service:

* Log business events at INFO
* Detailed processing at DEBUG

Repository:

* Don’t log manually
* Enable SQL only in dev

Global Exception Handler:

* Log all exceptions at ERROR

---

