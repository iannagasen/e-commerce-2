package dev.agasen.ecom.api.saga.order.events;

import java.time.Instant;

import dev.agasen.ecom.api.core.inventory.model.Inventory;
import dev.agasen.ecom.api.messaging.DomainEvent;
import dev.agasen.ecom.api.saga.order.OrderSaga;
import lombok.Builder;

public sealed interface InventoryEvent extends DomainEvent, OrderSaga permits InventoryEvent.Deducted, InventoryEvent.Restored, InventoryEvent.Declined {
  
  @Builder
  record Deducted(Long orderId,
                  Instant createdAt,
                  Long productId,
                  Long customerId,
                  int quantity,
                  int unitPrice,
                  int totalAmount) implements InventoryEvent {
  }

  @Builder
  record Restored(Long orderId,
                  Instant createdAt,
                  Long productId,
                  Long customerId,
                  int quantity,
                  int unitPrice,
                  int totalAmount) implements InventoryEvent {
  }

  @Builder
  record Declined(Long orderId,
                  Instant createdAt,
                  String message) implements InventoryEvent {}



}
