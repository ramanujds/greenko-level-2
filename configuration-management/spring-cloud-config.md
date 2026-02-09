## 1. Why a Config Server Is Needed 

### Without Config Server

Each microservice contains configs like:

```text
asset-service
 ├── application.yml
 ├── application-dev.yml
 ├── application-prod.yml
```

Problems grow quickly:

1. **Config duplication**

    * Same DB URL, Kafka brokers, logging levels copied everywhere
2. **Environment drift**

    * Dev and Prod configs diverge silently
3. **Redeploy required for config change**

    * Change timeout → rebuild → redeploy
4. **Secrets mixed with code**

    * DB passwords, API keys in repo
5. **Hard to manage at scale**

    * 10 services × 3 envs = 30 config files

In short:

> Microservices + local configs = operational pain

---

## 2. What Spring Cloud Config Server Actually Solves

A **Config Server** gives you:

* Centralized configuration
* Environment-specific configs
* Git-backed versioning
* Runtime refresh (with Actuator)
* Separation of **code vs configuration**

Think of it as:

> **Git = single source of truth for configs**

---

## 3. High-Level Architecture

```text
Git Repo (configs)
   |
   |--- asset-service-dev.yml
   |--- asset-service-prod.yml
   |--- telemetry-service-dev.yml
   |--- common.yml
   |
Config Server
   |
   |
Microservices (Asset, Telemetry, Alert)
```

Each microservice **fetches config at startup** from Config Server.

---

## 4. Mapping Your Existing YAMLs to Config Server

Let’s assume your current `application-prod.yml` looks like this:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://prod-db:5432/assets
    username: asset_user
    password: secret

kafka:
  bootstrap-servers: kafka-prod:9092

asset:
  cache-ttl: 300
```

### Step 1: Create Config Git Repository

Example repo: `config-repo`

```text
config-repo/
 ├── asset-service-dev.yml
 ├── asset-service-prod.yml
 ├── telemetry-service-dev.yml
 ├── common.yml
```

---

### Step 2: Move Configs Out of Microservice

#### `asset-service-prod.yml` (in Git)

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://prod-db:5432/assets
    username: asset_user
    password: ${DB_PASSWORD}

kafka:
  bootstrap-servers: kafka-prod:9092

asset:
  cache-ttl: 300
```

Secrets can now come from:

* Kubernetes Secrets
* Vault
* Environment variables

---

### Step 3: Shared Configuration (Very Important)

Create `common.yml`:

```yaml
logging:
  level:
    root: INFO
    org.springframework: WARN

management:
  endpoints:
    web:
      exposure:
        include: health,refresh
```

All services can reuse this.

---

## 5. Config Server Setup (Once per system)

### Add dependency

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

### Enable Config Server

```java
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {}
```

### `application.yml` of Config Server

```yaml
server:
  port: 8888

spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-org/config-repo
```

---

## 6. How Your Microservice Uses Config Server

### Minimal `application.yml` in Asset Service

```yaml
spring:
  application:
    name: asset-service
  profiles:
    active: prod
  config:
    import: optional:configserver:http://config-server:8888
```

That’s it.

No DB configs.
No Kafka configs.
No environment-specific values.

---

## 7. How Config Resolution Works (Important)

When Asset Service starts:

1. Reads `spring.application.name = asset-service`
2. Reads `spring.profiles.active = prod`
3. Config Server fetches:

    * `common.yml`
    * `asset-service-prod.yml`
4. Merges them
5. Injects into application context

---

## 8. Runtime Config Changes (Big Win)

If you update Git:

```yaml
asset:
  cache-ttl: 600
```

Then call:

```http
POST /actuator/refresh
```

Result:

* No restart
* New config applied live

This is gold in production.

---

## 9. Kubernetes + Config Server (Real World)

In Kubernetes:

* Config Server runs as a Deployment
* Microservices refer via service DNS

```yaml
config:
  import: configserver:http://config-server.default.svc.cluster.local:8888
```

Secrets:

* DB password → Kubernetes Secret
* Mounted as env var

---

## 10. When You SHOULD Use Config Server

Use it when:

* You have **multiple microservices**
* Multiple environments
* Frequent config changes
* Need Git-based auditing

Avoid it when:

* Single Spring Boot app
* No environment complexity


