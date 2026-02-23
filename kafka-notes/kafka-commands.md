STEP 1 — Create a Topic with Partitions and Replication

Create topic with:

* 3 partitions
* Replication factor 2

```
kafka-topics --bootstrap-server localhost:9092 \
  --create \
  --topic orders \
  --partitions 3 \
  --replication-factor 2
```

Check topic details:

```
kafka-topics --bootstrap-server localhost:9092 \
  --describe \
  --topic orders
```

You’ll see something like:

```
Topic: orders
Partition: 0  Leader: 1  Replicas: 1,2  ISR: 1,2
Partition: 1  Leader: 2  Replicas: 2,3  ISR: 2,3
Partition: 2  Leader: 3  Replicas: 3,1  ISR: 3,1
```

Meaning:

* 3 partitions
* Each partition stored on 2 brokers
* Leader handles reads/writes
* ISR = In-Sync Replicas

---

STEP 2 — Produce Messages (Without Key)

Start console producer:

```
kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic orders
```

Now type:

```
Order-1
Order-2
Order-3
Order-4
```

Kafka will distribute across partitions in round-robin.

---

STEP 3 — Consume and See Partitions + Offsets

Run consumer with metadata printing:

```
kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic orders \
  --from-beginning \
  --property print.partition=true \
  --property print.offset=true
```

Output example:

```
Partition:0  Offset:0  Order-1
Partition:1  Offset:0  Order-2
Partition:2  Offset:0  Order-3
Partition:0  Offset:1  Order-4
```

Now you can clearly see:

* Each partition has its own offset sequence
* Offset starts at 0 per partition
* Offsets are NOT global across topic

That’s the partition + offset concept live.

---

STEP 4 — Produce Messages With Key (Demonstrate Ordering)

Start producer with key enabled:

```
kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic orders \
  --property parse.key=true \
  --property key.separator=:
```

Now type:

```
order1:Payment Initiated
order1:Inventory Reserved
order2:Payment Initiated
order1:Order Confirmed
```

Now consume again with key display:

```
kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic orders \
  --from-beginning \
  --property print.key=true \
  --property print.partition=true \
  --property print.offset=true
```

You’ll notice:

All messages with key `order1` go to same partition.

That guarantees ordering per key.

---

STEP 5 — Demonstrate Consumer Group

Start first consumer:

```
kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic orders \
  --group order-group \
  --property print.partition=true \
  --property print.offset=true
```

Now start a second consumer with SAME group:

```
kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic orders \
  --group order-group \
  --property print.partition=true \
  --property print.offset=true
```

Kafka will rebalance partitions.

You’ll see:

* Consumer 1 gets some partitions
* Consumer 2 gets remaining partitions

Important rule:
One partition → only one consumer in same group.

---

STEP 6 — Show Consumer Group Details

```
kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --describe \
  --group order-group
```

You’ll see:

```
TOPIC   PARTITION   CURRENT-OFFSET   LOG-END-OFFSET   LAG
orders  0           2                5                3
```

Meaning:

* Consumer has processed up to offset 2
* Latest offset is 5
* Lag = 3 messages

That’s how you monitor backpressure.

---

STEP 7 — Demonstrate Replication Factor

We already created with replication factor 2.

To see replica distribution:

```
kafka-topics \
  --bootstrap-server localhost:9092 \
  --describe \
  --topic orders
```

Check:

* Replicas column
* ISR column

If one broker goes down, ISR reduces but topic still works.

---

STEP 8 — Synchronous Message Send (Producer Level Concept)

Console producer is async by default.

To simulate stronger guarantees:

```
kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic orders \
  --producer-property acks=all \
  --producer-property retries=3
```

What this does:

* Waits for all ISR replicas to acknowledge
* More durable
* Slightly slower

That is synchronous durability behavior.

---

STEP 9 — Async Send (Default Behavior)

Default console producer:

```
kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic orders
```

By default:

* acks=1
* Fire and forget behavior
* Higher throughput

---

STEP 10 — Reset Offsets (Replay Concept)

Reset consumer group to beginning:

```
kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --group order-group \
  --topic orders \
  --reset-offsets \
  --to-earliest \
  --execute
```

Now consumer can replay all messages.

This is how you can reprocess data if needed.

## --to-earliest - means from beginning
## --execute - actually perform the reset (without it, it will just show what would happen)


