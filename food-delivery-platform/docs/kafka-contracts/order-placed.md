# order.placed

**Producer:** order-service  
**Consumers:** assignment-service, delivery-service

## Schema

```json
{
  "eventType": "order.placed",
  "orderId": 123,
  "restaurantId": 45,
  "customerId": 67,
  "deliveryAddress": "<address>",
  "totalAmount": 24.99,
  "placedAt": "2024-01-01T12:00:00Z"
}
```
