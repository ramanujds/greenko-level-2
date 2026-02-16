# OAuth Basics Explained Simply

You (user)
Google (data owner)
Swiggy (third-party app)

When you click:

> “Login with Google”

Swiggy does NOT get your Google password.

Instead:

* You authenticate directly with Google
* Google asks: “Allow Swiggy to access your profile?”
* You say Yes
* Google gives Swiggy a **token**
* Swiggy uses that token to fetch your profile

That’s OAuth.

---

# The 4 Main Actors

Here are the official roles:

1. Resource Owner → You
2. Client → Swiggy
3. Authorization Server → Google Auth Server
4. Resource Server → Google API


# Step-by-Step OAuth Flow 


## Step 1 — User Clicks Login

Client (Swiggy) redirects browser to:

```
https://accounts.google.com/oauth/authorize?
  client_id=abc123
  &redirect_uri=https://swiggy.com/callback
  &response_type=code
  &scope=profile email
```

What’s happening?

Swiggy is saying:

> “Hey Google, this user wants to login and give me profile access.”

---

## Step 2 — User Logs into Google

You enter username/password on Google.

Important:
Swiggy never sees your password.

---

## Step 3 — Consent Screen

Google asks:

> “Allow Swiggy to access your profile?”

You approve.

---

## Step 4 — Authorization Code Returned

Google redirects browser back to:

```
https://swiggy.com/callback?code=xyz987
```

Swiggy now has an **authorization code**.

This code is temporary (usually valid for few minutes).

---

## Step 5 — Exchange Code for Access Token

Now Swiggy’s backend sends:

```
POST https://accounts.google.com/oauth/token

{
  client_id: abc123,
  client_secret: secret,
  code: xyz987,
  grant_type: authorization_code
}
```

Google verifies everything and responds:

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6...",
  "expires_in": 3600,
  "refresh_token": "def456"
}
```

---

## Step 6 — Access Protected Resource

Swiggy calls:

```
GET https://googleapis.com/userinfo
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

Google returns user profile.

Done.

---

# Why Authorization Code Flow Is Secure

Because:

* Access token never travels through browser
* Code is short-lived
* Backend exchanges code securely
* Client secret is never exposed to frontend

---

# What is Access Token?

Access Token is:

* Proof of authorization
* Usually JWT
* Short lived
* Sent with every API request

---

# What is Refresh Token?

If access token expires:

Client sends refresh token to Authorization Server
Gets new access token
No need for user to login again

---

# OAuth vs JWT

OAuth → Framework/Protocol
JWT → Token format

OAuth can use:

* JWT tokens
* Opaque tokens

---

# How OAuth Fits in Microservices

In your Asset Management architecture:

Instead of custom JWT Auth Service:

You use:

* OAuth2 Authorization Server (like Keycloak)
* Microservices act as Resource Servers
* API Gateway validates JWT
* Services validate JWT again

Flow becomes:

```
Client
  ↓
Authorization Server (login)
  ↓
Access Token
  ↓
API Gateway
  ↓
Asset Service
```

---

# Very Small Spring Boot Example

Let’s say:

You use Keycloak.

### Resource Server config:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/asset-realm
```

Spring automatically:

* Fetches public key
* Validates JWT
* Maps roles

That’s it.

No manual JWT parsing needed.

---



Q: Why not share password directly?

Because OAuth ensures:

* Password never shared with client
* Fine-grained permissions
* Token-based access
* Revocation possible

---

Q: What is difference between OAuth and OpenID Connect?

OAuth → Authorization
OpenID Connect → Authentication layer on top of OAuth

When you say “Login with Google”, that’s OIDC.

---
