
# Step 1 — Create a Clean Container Monitoring Dashboard

Go to:

```
http://localhost:3000
```

→ Dashboards
→ New → New Dashboard
→ Add New Panel

---

# Panel 1: CPU Usage Per Container

### Query:

```
dockerstats_cpu_usage_ratio * 100
```

### Visualization:

**Time Series**

### Settings:

* Unit → Percent (0–100)
* Legend → `{{name}}`
* Min → 0
* Max → 100

Optional (cleaner look):

* Turn on “Fill opacity”
* Line width → 2

Now you’ll see one line per container:

* asset-service
* telemetry-service
* mysql
* gateway
* etc.

---

# Panel 2: Memory Usage Per Container

### Query:

```
dockerstats_memory_usage_bytes
```

### Visualization:

**Time Series**

### Settings:

* Unit → Bytes (Grafana auto converts to MB/GB)
* Legend → `{{name}}`

If graph looks noisy, use:

```
sum(dockerstats_memory_usage_bytes) by (name)
```

---

# Panel 3: Memory Usage % (Recommended)

If you have memory limit metric:

```
(
  dockerstats_memory_usage_bytes
  /
  dockerstats_memory_limit_bytes
) * 100
```

### Visualization:

Time Series

Unit → Percent (0–100)

This is much more meaningful than raw bytes.

---

# Panel 4: Top CPU Consumers 

Change visualization to:

**Bar Gauge**

Query:

```
dockerstats_cpu_usage_ratio * 100
```

Options:

* Orientation → Horizontal
* Reduce → Last value

Now you instantly see which container is heavy.

---

# Panel 5: Single Service Drill Down

If you want focused view (example: asset-service only):

```
dockerstats_cpu_usage_ratio{name=~".*asset-service.*"} * 100
```

This helps during troubleshooting.

---

# Pro-Level Dashboard Layout Tips

Instead of random layout, structure it like this:

Row 1 → Overall

* CPU Usage (all containers)
* Memory Usage %

Row 2 → Detailed

* Memory Bytes
* Top CPU Bar Gauge

Row 3 → Application Layer (from Spring Boot)

* HTTP RPS
* JVM Memory

Now you combine:

* Container layer
* Application layer

That’s real observability.

---

# Important Tip — Clean Container Names

Right now names look like:

```
asset-telemetry-services-gateway-service-1
```

To make dashboard cleaner:

In panel → Legend → use:

```
{{name}}
```

If you want shorter labels:

Use Transform → Rename by regex:

Regex:

```
asset-telemetry-services-(.*)-1
```

Replace:

```
$1
```

Now it shows:

* gateway-service
* asset-service
* mysql

Much cleaner.

---

# If You Want It To Look Production-Level

Use:

* CPU panel (top)
* Memory %
* Bar gauge for “Current CPU”
* Table panel showing:

    * Container
    * CPU %
    * Memory MB

Table Query example:

```
dockerstats_cpu_usage_ratio * 100
```

Visualization → Table
Add field override → Add memory query as second query.

