package dev.agasen.ecom.api.saga.order.events;

import java.time.Instant;
import java.util.List;

import dev.agasen.ecom.api.core.inventory.model.InventoryDeductionRequest;
import dev.agasen.ecom.api.core.order.model.OrderItem;
import dev.agasen.ecom.api.messaging.DomainEvent;
import dev.agasen.ecom.api.saga.order.OrderSaga;
import lombok.Builder;

public sealed interface OrderEvent extends DomainEvent, OrderSaga permits OrderEvent.Created, OrderEvent.Cancelled, OrderEvent.Completted {
  
  @Builder
  record Created(Long orderId,
                 Instant createdAt,
                 Long productId,
                 Long customerId,
                //  int quantity,
                //  int totalAmount
                 List<OrderItem> items
                 ) implements OrderEvent {}

  @Builder
  record Cancelled(Long orderId,
                   Instant createdAt,
                   List<OrderItem> items,
                   String message) implements OrderEvent {};

  @Builder
  record Completted(Long orderId,
                    List<OrderItem> items,
                    Instant createdAt) implements OrderEvent {};

}
