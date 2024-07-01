package dev.agasen.ecom.api.saga.order.events;

import java.time.Instant;

import dev.agasen.ecom.api.messaging.DomainEvent;
import dev.agasen.ecom.api.saga.order.OrderSaga;
import lombok.Builder;

public sealed interface PaymentEvent extends DomainEvent, OrderSaga permits PaymentEvent.Declined, PaymentEvent.Refunded, PaymentEvent.Processed {
  
  @Builder
  record Processed(
      Long orderId,
      Instant createdAt,
      Long payment,
      Long customerId
  ) implements PaymentEvent {}

  @Builder
  record Declined(
      Long orderId,
      Instant createdAt,
      Long amount,
      Long customerId,
      String message
  ) implements PaymentEvent {}

  @Builder
  record Refunded(
    // Rollback event
      Long orderId,
      Instant createdAt,
      Long amountRefunded,
      Long customerId,
      String message
  ) implements PaymentEvent {}


}
