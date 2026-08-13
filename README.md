# Coco-codes
### By- Anshul, Ansh, Abhivesh, Saloni & Coco


## Test Data IDs

### Restaurants

| Restaurant           | Restaurant ID                          |
| -------------------- | -------------------------------------- |
| Pizza Palace Updated | `ab5b17b0-d0a6-402c-bf48-2b1354ac8188` |

### Menu Items

| Menu Item        | Menu Item ID                           | Restaurant ID                          | Prices
| ---------------- | -------------------------------------- | -------------------------------------- |
| Margherita Pizza | `e67c935c-bcd7-4269-b8ff-f2f71a86f4fe` |`ab5b17b0-d0a6-402c-bf48-2b1354ac8188` | `299` cents |
| Pasta | `92fb7b5e-4191-4dc1-8482-874dc6558f5a` | `ab5b17b0-d0a6-402c-bf48-2b1354ac8188` | `450` cents |

### Orders

| Order              | Order ID                               | Status    | Total       |
| ------------------ | -------------------------------------- | --------- | ----------- |
| Pizza Palace Order | `f0253d3e-cb9e-4383-92b5-4b7b617f927c` | `PENDING` | `598` cents |

| Pizza Palace Order | `9e1d3109-2f07-4cbe-9b70-0e06dd815e24` | `PENDING` | `598` cents |

| Pizza Palace Order | `8f87d96a-5e09-4989-9f82-173c4ca6e35b` | `PENDING` | `749` cents |


### Deliveries

| Delivery | Delivery ID | Driver ID | Order ID | ETA | Status |
| -------- | ----------- | --------- | -------- | --- | ------ |
| Pizza Palace Delivery | `be322fd3-59df-4c07-be5a-3a47bd225ed2` | `driver-002` | `8f87d96a-5e09-4989-9f82-173c4ca6e35b` | `30 minutes` | `ASSIGNED` |