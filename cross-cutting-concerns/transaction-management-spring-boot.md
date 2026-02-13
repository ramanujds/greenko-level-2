# 1. What Is a Transaction?

A transaction is a **unit of work** that must follow ACID:

* **Atomicity** – All or nothing
* **Consistency** – DB remains valid
* **Isolation** – Concurrent safety
* **Durability** – Once committed, it's permanent

Example in your system:

Creating an Asset:

1. Insert into `assets`
2. Insert into `asset_location`
3. Insert audit record

If step 3 fails → everything should rollback.

That’s a transaction.

---

# 2. How Spring Boot Handles Transactions

Spring Boot + Spring Data JPA uses:

* `@Transactional`
* AOP proxies
* Hibernate session management

You don’t manually manage `Connection.commit()` or `rollback()`.

---

# 3. Basic Usage

```java
@Service
public class AssetService {

    @Transactional
    public void createAsset(AssetRequest request) {

        assetRepository.save(asset);

        auditRepository.save(auditLog);

        // if exception occurs → rollback
    }
}
```

If any unchecked exception occurs → entire transaction rolls back.

---

# 4. Where to Put @Transactional?

Best practice:

* Service layer
* NOT controller
* NOT repository (usually)

Correct layering:

Controller → Service (@Transactional) → Repository

Because transaction defines **business boundary**, not API boundary.

---

# 5. Rollback Rules

By default:

* Rolls back on `RuntimeException`
* Does NOT rollback on checked exceptions

Example:

```java
@Transactional
public void process() throws IOException {
    ...
}
```

If `IOException` happens → no rollback.

To fix:

```java
@Transactional(rollbackFor = Exception.class)
```

---

# 6. Read-Only Transactions

For queries:

```java
@Transactional(readOnly = true)
public Page<Telemetry> getTelemetry(Long assetId, Pageable pageable) {
    return telemetryRepository.findByAssetId(assetId, pageable);
}
```

Why?

* Optimization hint to Hibernate
* Prevents accidental writes
* Slight performance improvement

In telemetry-heavy systems, use this.

---

# 7. Isolation Levels 

Imagine:

* Two users updating same asset
* Telemetry being inserted concurrently

Spring supports DB isolation levels:

```java
@Transactional(isolation = Isolation.READ_COMMITTED)
```

Common levels:

| Level            | What It Prevents     |
| ---------------- | -------------------- |
| READ_UNCOMMITTED | Nothing              |
| READ_COMMITTED   | Dirty reads          |
| REPEATABLE_READ  | Non-repeatable reads |
| SERIALIZABLE     | Phantom reads        |

Default (Postgres): READ_COMMITTED

For asset updates:

* READ_COMMITTED is usually enough.

For financial systems:

* Use higher isolation.

---

# 8. Propagation 

This defines how transactions behave when one method calls another.

### Default

```
Propagation.REQUIRED
```

Meaning:

* Join existing transaction
* Or create new one

---

### Example

```java
@Transactional
public void createAsset() {
    saveAsset();
    saveAudit();
}
```

Both inside same transaction.

---

### REQUIRES_NEW

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void saveAudit() {
    auditRepository.save(...);
}
```

Now:

* Even if main transaction rolls back
* Audit will still commit

Used in:

* Logging
* Audit trails

---

# 9. Transaction Boundary Problem 

Spring uses proxies.

If you call internal method:

```java
@Transactional
public void methodA() {
    methodB(); // NO transaction applied if B is annotated
}

@Transactional
public void methodB() {
    ...
}
```

`methodB` will NOT start a new transaction.

Why?

Because proxy is bypassed.

Solution:

* Move methodB to another bean.
* Or use `@Transactional` on class level.

# 10. Lazy Loading & Transactions

If you fetch:

```java
Asset asset = assetRepository.findById(id).get();
```

And later access:

```java
asset.getTelemetryList();
```

Outside transaction → LazyInitializationException.

Because session is closed.

Solution:

* Use DTO projection
* Or fetch inside transaction
* Or use `@EntityGraph`

---

# 11. Transaction in Telemetry System 

Telemetry insert flow:

```java
@Transactional
public void processTelemetry(TelemetryDto dto) {

    Asset asset = assetRepository.findById(dto.getAssetId())
            .orElseThrow();

    Telemetry telemetry = mapper.toEntity(dto);
    telemetry.setAsset(asset);

    telemetryRepository.save(telemetry);
}
```

All in one transaction.

But for high-volume ingestion:

* Keep transaction small
* Avoid long-running transactions
* Don’t mix heavy processing + DB writes

---

# 12. Transaction + Microservices

Important:

Transactions do NOT span multiple microservices.

If:

* Asset service
* Telemetry service
* Alert service

You cannot use single DB transaction across them.

Instead use:

* Event-driven architecture
* Saga pattern


---

# 13. Performance Considerations

Bad:

```java
@Transactional
public void processHugeLoop() {
    for (Telemetry t : hugeList) {
        repository.save(t);
    }
}
```

This keeps transaction open too long.

Better:

* Batch processing
* Flush & clear periodically
* Use Spring Batch for bulk jobs

---

# 14. Best Practices 

For Asset Management:

* Use transactions for create/update operations
* Use optimistic locking (`@Version`) to avoid lost updates

For Telemetry:

* Keep transactions short
* Use batch inserts if high volume
* Avoid nested transactions unless necessary

---

