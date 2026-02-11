# Flyway Versioning

# Step 1 — The Problem

Your schema:

```sql
CREATE TABLE asset
(
    ...
);
```

In Flyway, we **never apply this manually**.

Instead:

* Every schema change is versioned
* Scripts are applied in order
* Database remembers what ran

---

# Step 2 — Standard Flyway Folder Structure

Inside your Spring Boot project:

```
src/main/resources/db/migration
```

Flyway automatically scans this folder.

---

# Step 3 — Create First Migration

## `V1__create_asset_table.sql`

```sql
CREATE TABLE asset
(
    asset_id       VARCHAR(255) NOT NULL,
    asset_name     VARCHAR(255),
    type           VARCHAR(255),
    installed_date DATE,
    location       VARCHAR(255),
    status         VARCHAR(255),
    CONSTRAINT pk_asset PRIMARY KEY (asset_id)
);
```

---

## Why this is V1

* First schema version
* Base structure
* Must never be modified after execution

Once applied → immutable.

---

# Step 4 — Simulate Real Evolution 

Schema versioning is meaningful only when schema evolves.

Let’s say later business needs:

* `status` must be NOT NULL
* Add index on `type`
* Add new column `manufacturer`

---

## `V2__add_manufacturer_column.sql`

```sql
ALTER TABLE asset
ADD COLUMN manufacturer VARCHAR(255);
```

---

## `V3__add_index_on_type.sql`

```sql
CREATE INDEX idx_asset_type ON asset(type);
```

---

## `V4__make_status_not_null.sql`

```sql
ALTER TABLE asset
ALTER COLUMN status SET NOT NULL;
```

Each change is:

* Atomic
* Independent
* Ordered
* Recorded

---

# What Flyway Does Internally

It creates:

```
flyway_schema_history
```


This ensures:

* No script runs twice
* Order is preserved
* History is auditable

---

# Step 5 — Spring Boot Integration

## Add Dependency

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

That’s it.

Spring Boot auto-runs Flyway at startup.

---

## Optional Configuration

```yaml
spring:
  flyway:
    locations: classpath:db/migration
    baseline-on-migrate: true
```

---

# Step 6 — What Happens at Application Startup

```
App starts
   ↓
Flyway checks flyway_schema_history
   ↓
Pending migrations?
   ↓
Apply in order
   ↓
Start application
```

If migration fails → app does not start.

This prevents:

* Half-baked deployments
* Schema mismatch
* Runtime errors

---

# Step 7 — Production Best Practices 

## Never edit an applied migration

Bad:

```
Modify V1__create_asset_table.sql
```

Correct:

```
Create V5__fix_asset_column.sql
```

---

## Never reorder migrations

Version numbers must always increase.

---

## Avoid destructive changes

Instead of:

```sql
DROP COLUMN type;
```

Prefer:

* Deprecate
* Migrate data
* Remove later

---


## Microservices Context

Each microservice:

```
asset-service
  └── db/migration
```

Never:

* Share DB
* Share migration folders

Flyway respects microservice autonomy.



# What Is a Repeatable Migration?

Normal (versioned) migration:

```
V1__create_asset_table.sql
V2__add_status_column.sql
```

✔ Runs once
✔ Never runs again

---

Repeatable migration:

```
R__create_asset_view.sql
R__asset_functions.sql
```

✔ Runs whenever its content changes
✔ No version number
✔ Identified by checksum

---

# When Should You Use Repeatable Migrations?

Repeatable migrations are best for:

* Views
* Stored procedures
* Reference data

They are **not** for table structure evolution.

---

# How Flyway Decides to Re-run Them

Flyway calculates a **checksum** of the file.

If:

* File content changes → checksum changes
* Flyway re-runs it

That’s it.

No version increment needed.

---

# Example with Your `asset` Table

Assume you want a view:

```
active_assets_view
```

---

## Step 1: Versioned migration for schema

```
V1__create_asset_table.sql
```

---

## Step 2: Repeatable migration for view

### `R__create_active_assets_view.sql`

```sql
CREATE OR REPLACE VIEW active_assets AS
SELECT asset_id, asset_name, type, location
FROM asset
WHERE status = 'ACTIVE';
```

---

## What happens?

First run:

* Flyway executes V1
* Then executes R__create_active_assets_view

Later:

* You modify the view
* Change query logic
* Restart application

Flyway:

* Detects checksum change
* Re-executes repeatable migration

---

# Order of Execution

Execution order:

1. All versioned migrations (V*)
2. All repeatable migrations (R*)

Always in that sequence.

---

# Best Practice: Always Make Repeatables Idempotent

Use:

```
CREATE OR REPLACE
```

Instead of:

```
CREATE VIEW
```

Because repeatables re-run.

---

# Repeatable Migration for Reference Data 

Example:

### `R__seed_asset_status_reference.sql`

```sql
DELETE FROM asset_status;

INSERT INTO asset_status (code, description)
VALUES ('ACTIVE', 'Asset is active'),
       ('INACTIVE', 'Asset is inactive');
```

Every time the file changes:

* Data resets
* Clean state maintained

This works only if:

* Table is small
* Data is reference-only

Never use this for large transactional tables.

---

# How It Looks in flyway_schema_history

Example:

| version | description               | type       |
| ------- | ------------------------- | ---------- |
| 1       | create_asset_table        | SQL        |
|         | create_active_assets_view | REPEATABLE |

Repeatables do not have version numbers.

---

# When NOT to Use Repeatables

- Adding columns
- Dropping tables
- Large data migrations
- Schema-breaking changes

Those must be versioned (V).

## Example for reference data:
``
db/migration-dev/R__seed_asset_reference_data.sql
``

```
spring:
flyway:
locations:
- classpath:db/migration
- classpath:db/migration-dev
```
