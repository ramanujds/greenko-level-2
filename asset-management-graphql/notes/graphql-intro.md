# What is GraphQL?

**GraphQL** is a query language for APIs and a runtime to execute those queries.

It was developed by **Facebook** (now Meta) in 2012 and open-sourced in 2015.

Instead of exposing multiple endpoints like REST, GraphQL exposes **a single endpoint** and lets the client specify exactly what data it wants.

Think of it like:

> REST = “Here’s the fixed menu.”
> GraphQL = “Tell me exactly what you want on your plate.”

---

## How GraphQL Works (High Level)

Client sends a query:

```graphql
query {
  user(id: "1") {
    name
    email
    orders {
      id
      amount
    }
  }
}
```

Server responds with **exactly** that shape:

```json
{
  "data": {
    "user": {
      "name": "Ram",
      "email": "ram@example.com",
      "orders": [
        { "id": "101", "amount": 2000 }
      ]
    }
  }
}
```

Single endpoint:

```
POST /graphql
```

---

# REST vs GraphQL — Proper Comparison

Let’s compare from a real system design perspective.

---

## API Structure

### REST

Multiple endpoints:

```
GET /users/1
GET /users/1/orders
GET /orders/101
```

Each resource has its own URL.

---

### GraphQL

Single endpoint:

```
POST /graphql
```

Everything is query-driven.

---

## Overfetching & Underfetching

### REST Problem

Suppose your UI needs:

* user name
* user email

But `/users/1` returns:

```json
{
  "id": 1,
  "name": "Ram",
  "email": "ram@example.com",
  "address": "...",
  "createdAt": "...",
  "updatedAt": "..."
}
```

You fetched more than needed → **Overfetching**

If you need orders too → you make another call → **Underfetching**

---

### GraphQL Solution

You request exactly what you need:

```graphql
{
  user(id: "1") {
    name
    email
  }
}
```

No extra fields. No extra network call.

---

## Number of API Calls

### REST

Frontend often makes multiple calls:

```
User → Orders → Payments → Profile
```

Mobile apps suffer more here.

---

### GraphQL

Single request can fetch nested data:

```graphql
{
  user(id: "1") {
    name
    orders {
      id
      amount
      payment {
        status
      }
    }
  }
}
```

One call. Deep graph fetch.

---

## Versioning

### REST

You usually do:

```
/v1/users
/v2/users
```

API versioning becomes messy over time.

---

### GraphQL

No versioning needed typically.

You:

* Deprecate fields
* Add new fields

Clients request what they understand.

---

## Strong Typing

GraphQL has a **schema**:

```graphql
type User {
  id: ID!
  name: String!
  email: String!
}
```

Strictly typed.

REST?
Mostly contract-based via OpenAPI, but not enforced at runtime the same way.

---

## Caching

### REST

Easy caching:

```
GET /users/1
```

HTTP caching works naturally.

---

### GraphQL

Harder to cache because:

```
POST /graphql
```

You need:

* Persisted queries
* Query hashing
* Apollo caching layer

So REST wins in simple HTTP caching.

---

## Learning Curve

REST → Simple.
GraphQL → Requires understanding:

* Schema
* Resolvers
* Query complexity
* N+1 problem

---

# When Should You Use GraphQL?

GraphQL is great when:

* You have multiple frontend clients (web + mobile)
* UI needs flexible data shapes
* You want to reduce network calls
* You have complex nested data
* You’re building a BFF (Backend for Frontend)

Examples:

* Social media apps
* E-commerce dashboards
* Aggregation layer over microservices

---

# When REST is Better

REST is better when:

* Simple CRUD APIs
* Internal microservice communication
* You need strong HTTP caching
* System is simple
* You want low complexity

---

# Microservices Perspective 

In most real production systems:

* Internal communication → REST or gRPC
* API Gateway layer → GraphQL (optional)
* Aggregation layer → GraphQL shines

GraphQL is often used as:

> A smart API gateway over multiple microservices

---

# Simple Visual Difference

REST:

```
Client → /users
Client → /orders
Client → /payments
```

GraphQL:

```
Client → /graphql → Orchestrates internally
```

# Conclusion

If you're building:

* A simple asset management microservice → REST is perfect.
* A frontend-heavy system where UI changes frequently → GraphQL is powerful.

GraphQL is not a replacement for REST.
It’s an evolution for specific use cases.
