# Security flow


This repo supports **two modes**:

1) **Basic Auth (default)** setup
- Uses **in-memory users**
- Works even when you have **no Keycloak/Okta/Auth0 running**
- Best for people new to microservices + security

2) **OIDC/JWT (optional, advanced)**
- Uses an external Identity Provider (example: Keycloak)
- Gateway does OAuth2 login and can relay JWTs downstream
- Enable by running with: `--spring.profiles.active=oidc`


## Mode 1: Basic Auth (default)

All 3 services have the same demo users:

- `user` / `password` (ROLE_USER)
- `admin` / `password` (ROLE_ADMIN)

What is protected?

- Gateway: everything except Swagger + actuator
- Asset service: `/api/assets/**`
- Telemetry service: `/api/telemetry/**`

---

## Mode 2: OIDC/JWT (profile: `oidc`)

When you run with `--spring.profiles.active=oidc`, the services switch to JWT validation mode. To use this:

- Start Keycloak (or another OIDC provider)
- Run services with `--spring.profiles.active=oidc`
- Configure `OIDC_ISSUER_URI`, `OIDC_CLIENT_ID`, `OIDC_CLIENT_SECRET`

In this mode, Asset/Telemetry validate Bearer JWT as resource servers.

---

## Full flow summary

This project uses:

- **Gateway** (`gateway-service`, Spring Cloud Gateway WebFlux) as:
  - an **OAuth2 Client** (OIDC login for browser users)
  - optionally an **OAuth2 Resource Server** (accept `Authorization: Bearer <jwt>` on incoming requests)
  - a **token relay** (forwards the JWT to downstream services)
- **Downstream services** (`asset-service`, `telemetry-service`, Spring MVC) as **OAuth2 Resource Servers** validating JWT.

---

## Actors

1. **Client**
2. **Gateway service**
3. **IdP / Authorization Server** (OIDC mode only)
4. **Asset service**
5. **Telemetry service**

---

## Flow A Browser login (OIDC Authorization Code) + token relay

1. Browser calls a protected endpoint at the gateway, e.g.
   - `GET /api/assets`
2. Gateway security sees there is no authenticated session.
3. Gateway triggers **OAuth2 login** and redirects the browser to the IdP authorization endpoint.
4. User authenticates at the IdP.
5. IdP redirects back to the gateway callback:
   - `/login/oauth2/code/{registrationId}`
6. Gateway exchanges the authorization code for tokens (access token + id token).
7. Gateway stores the authorized client in the session.
8. When the gateway routes to downstream services, the Gateway filter `TokenRelay`:
   - reads the access token from the authorized client
   - adds/forwards `Authorization: Bearer <access_token>` to the downstream request
9. Asset/Telemetry services validate the JWT using `issuer-uri` discovery:
   - download OIDC metadata
   - download JWKs
   - validate signature, issuer, audience (if configured), expiry, etc.
10. Service returns response → gateway → browser.

---

## Flow B — Non-browser client sends Bearer JWT to gateway

1. API client calls gateway with:
   - `Authorization: Bearer <jwt>`
2. Gateway (configured as resource server) validates JWT.
3. Gateway routes request and relays the same token downstream via `TokenRelay`.
4. Downstream services validate JWT again.

> Why validate twice? This is common. The gateway enforces edge security; services remain protected even if accessed directly.

---

## Telemetry → Asset call (internal call) and JWT propagation

Telemetry calls Asset using OpenFeign (`AssetServiceClient`). To avoid losing auth context:

- `telemetry-service` includes a Feign `RequestInterceptor` (`FeignSecurityConfig`) that:
  - reads the current `SecurityContext`
  - if authenticated with a `JwtAuthenticationToken`, forwards
    `Authorization: Bearer <same-jwt>`

This keeps downstream authorization behavior consistent.

---

## Configuration summary

### Shared: issuer URI

All three services use the same environment variable for dev:

- `OIDC_ISSUER_URI` (default: `http://localhost:8080/realms/greenko`)

### Gateway 

- OAuth2 Client registration (login):
  - `spring.security.oauth2.client.registration.greenko-gateway.*`
- Provider configuration:
  - `spring.security.oauth2.client.provider.keycloak.issuer-uri`
- Accept Bearer tokens:
  - `spring.security.oauth2.resourceserver.jwt.issuer-uri`
- Relay token to downstream:
  - add `TokenRelay` filter on secured routes

### Asset + Telemetry

- Resource server:
  - `spring.security.oauth2.resourceserver.jwt.issuer-uri`

---

## Expected HTTP status codes

- **401 Unauthorized**
  - no token / invalid token / expired token
- **403 Forbidden**
  - token valid but missing required roles/scopes (if you add `@PreAuthorize` rules)

---

## Local dev notes

If you use Keycloak locally, you typically create:

- Realm: `greenko`
- Client: `gateway-service` (confidential)
  - Redirect URI: `http://localhost:8080/login/oauth2/code/greenko-gateway`

Then export values:

```bash
export OIDC_ISSUER_URI="http://localhost:8080/realms/greenko"
export OIDC_CLIENT_ID="gateway-service"
export OIDC_CLIENT_SECRET="<secret>"
```

(Exact redirect URI depends on your gateway port and `registrationId`.)
