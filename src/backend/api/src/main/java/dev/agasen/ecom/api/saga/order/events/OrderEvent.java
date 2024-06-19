package dev.agasen.ecom.api.saga.order.events;

import java.time.Instant;

import dev.agasen.ecom.api.core.inventory.model.InventoryDeductionRequest;
import dev.agasen.ecom.api.messaging.DomainEvent;
import dev.agasen.ecom.api.saga.order.OrderSaga;
import lombok.Builder;

public sealed interface OrderEvent extends DomainEvent, OrderSaga permits OrderEvent.Created, OrderEvent.Cancelled, OrderEvent.Completted {
  
  @Builder
  record Created(Long orderId,
                 Instant createdAt,
                 Long productId,
                 Long customerId,
                 int quantity,
                 int totalAmount) implements OrderEvent {}

  @Builder
  record Cancelled(Long orderId,
                   Instant createdAt,
                   String message) implements OrderEvent {};

  @Builder
  record Completted(Long orderId,
                    Instant createdAt) implements OrderEvent {};

  
  default InventoryDeductionRequest toRequest() {
    switch (this) {
      case Created event -> {
        return InventoryDeductionRequest.builder()
          .orderId(event.orderId())
          .productId(event.productId())
          .customerId(event.customerId())
          .quantity(event.quantity())
          .build();
      }
      default -> throw new UnsupportedOperationException("Can not convert to request " + this);
      
    }

  }

}
