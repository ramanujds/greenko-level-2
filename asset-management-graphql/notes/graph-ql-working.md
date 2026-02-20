# Core Components of GraphQL

Internally, GraphQL has three major parts:

1. **Schema**
2. **Resolvers**
3. **Execution Engine**

Think of it like:

> Schema = Contract
> Resolver = Data fetcher
> Execution Engine = Orchestrator

---

# The Schema 

GraphQL is schema-first.

Example:

```graphql
type User {
  id: ID!
  name: String!
  orders: [Order]
}

type Order {
  id: ID!
  amount: Float!
}

type Query {
  user(id: ID!): User
}
```

This schema defines:

* What clients can ask for
* What types exist
* How types are connected
* Entry points (Query, Mutation)

Important:

The schema does NOT fetch data.
It only describes structure.

---

# What is a Resolver?

A **resolver** is just a function that returns data for a field.

Think of it like:

> Spring Controller + Service method combined at field level.

Example in Java (conceptual):

```java
public User getUser(String id) {
    return userService.findById(id);
}
```

That’s a resolver for:

```graphql
user(id: ID!): User
```

But here’s where it gets interesting.

Every field can have its own resolver.

Even nested ones.

---

# How Execution Actually Happens

Let’s take this query:

```graphql
{
  user(id: "1") {
    name
    orders {
      id
      amount
    }
  }
}
```

Internally, execution happens in steps.

---

## Step 1: Root Query Resolver

GraphQL calls the resolver for:

```graphql
Query.user
```

That returns:

```json
{
  id: 1,
  name: "Ram"
}
```

Now execution engine moves to nested fields.

---

## Step 2: Resolve `name`

If no custom resolver exists,
GraphQL just reads the field directly from object.

Cheap operation.

---

## Step 3: Resolve `orders`

Now it sees:

```graphql
orders
```

GraphQL calls:

```java
List<Order> getOrders(User user)
```

Notice something important:

The parent object (`User`) is passed automatically.

That’s how nested resolution works.

GraphQL execution engine keeps passing parent results down the tree.

---

# Execution Engine Internals

Internally GraphQL does:

1. Parse query → AST (Abstract Syntax Tree)
2. Validate query against schema
3. Execute field-by-field (depth-first)
4. Merge results into final JSON

Very important:

GraphQL resolves fields **independently**.

It does NOT fetch everything at once.

It walks the query tree.

---

# Parallel Execution

Here’s something cool:

If fields are independent, GraphQL can resolve them in parallel.

Example:

```graphql
{
  user(id: "1") {
    name
    orders
    profile
  }
}
```

If `orders` and `profile` are independent resolvers,
GraphQL can execute them concurrently.

---

# The Famous N+1 Problem

Now comes the real production issue.

Imagine:

```graphql
{
  users {
    id
    name
    orders {
      id
    }
  }
}
```

Execution:

1. Fetch all users → 1 query
2. For each user → fetch orders → N queries

If 100 users:

1 + 100 queries

That’s the N+1 problem.

Very common in GraphQL.

---

# Solution: DataLoader

To fix N+1, GraphQL frameworks use batching tools like:

* **DataLoader**

DataLoader:

* Collects all order requests
* Batches them
* Makes one DB call:

```sql
SELECT * FROM orders WHERE user_id IN (...)
```

Now instead of 100 queries,
you make 1.

This is critical in production.

---

# Mutation Internals

Mutations are just resolvers too.

Example:

```graphql
mutation {
  createUser(name: "Ram")
}
```

GraphQL guarantees:

Mutations execute sequentially.

Queries may execute in parallel.

---

# In Microservices Architecture

GraphQL server often acts as:

API Aggregator layer.

Example:

Client query:

```graphql
{
  asset(id: "A1") {
    telemetry {
      temperature
    }
    alerts {
      level
    }
  }
}
```

Internally:

* asset → Asset Service (REST)
* telemetry → Telemetry Service
* alerts → Alert Service

GraphQL execution engine orchestrates calls to multiple microservices.

It becomes a smart composition layer.

---

# 1Lifecycle Summary

When a request hits `/graphql`:

1. Parse query
2. Validate schema
3. Build execution plan
4. Call root resolver
5. Resolve nested fields recursively
6. Combine results
7. Return JSON

Everything revolves around:

Field-level resolution.

