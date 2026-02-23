# Part Ordering Flow with Kafka Events


## Kafka Topic

- `order.placed.v1`


## Event Contract

### `OrderPlacedEvent`
Published by `part-order-service` and consumed by `part-inventory-service`.

Payload:
- `orderId` (String)
- `sku` (String)
- `quantity` (int)

Example message value (JSON):

```json
{"orderId":"dc73bdf4-c79e-45bc-ad12-f067a43ff49f","sku":"SEAT-SAFE45-001","quantity":50}
```


## Detailed Flow

## Place Order + Update Stock with Kafka Events

### Phase 1 — Place order in order service

`PartOrderService`:
   - Generates `orderId = UUID.randomUUID().toString()`
   - Publishes `OrderPlacedEvent(orderId, sku, quantity)` to Kafka topic `order.placed.v1`
     - Producer: `com.forvia.partorderservice.kafka.OrderRequestKafkaProducer`
   - Returns immediately to the caller with status:
     - `Order placed (inventory update pending)`

### Phase 2 — Inventory service consumes and decrements stock

1. Inventory consumer receives event:
   
2. Consumer updates DB
    - `PartInventoryService`:
      - Listens to `order.placed.v1` topic
      - On receiving `OrderPlacedEvent`, it:
         - Checks if sufficient stock is available for the given `sku`
         - If yes, decrements the stock quantity in the database
         - If no, logs an error 

## Limitations

- This is **eventual consistency**: the UI gets an order confirmation before inventory is updated.
- No retries/DLT/idempotency are implemented in this simplified flow.
- If you want stronger guarantees later, typical next steps are:
  - add an order status lifecycle (`PENDING` → `CONFIRMED/REJECTED`) with another event,
  - add idempotency using `orderId`,
  - add retry and dead-letter topic handling.
