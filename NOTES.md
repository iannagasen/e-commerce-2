### WORKFLOWS

#### HappyPath
- **OrderCreated** 
  - **PaymentDeducted** 
    - **OrderCompletted**
  - **InventoryDeducted**
    - **OrderCompletted**
    - 
#### With Issue on Payment Service
- OrderCreated
  - PaymentDeclined
    - OrderCancelled
      - PaymentRefunded
        - OrderCompletted
      - InventoryRefunded
        - OrderCompletted
  - ***InventoryDeducted or InventoryDeclined - doesnt matter since it will be cancelled***

#### With Issue on Inventory Service
- OrderCreated
  - InventoryDeclined
    - OrderCancelled
      - InventoryRefunded
        - OrderCompletted
      - PaymentRefunded
        - OrderCompletted
  - ***PaymentDeducted or PaymentDeclined - doesnt matter since it will be cancelled***



URLS:
  - EurekaDiscoveryServer - http://localhost:8761

ERRORS:
  `MessageDeliveryException ... doesn't have subscribers to accept message`
    - Error during runtime/processing
    - usually runtime exception
    - or misconfiguration

- If you supply a Mono using `.map()` it will not trigger that Mono
  - always use `.flatMap()` when trying to sequencially trigger another Mono


              



