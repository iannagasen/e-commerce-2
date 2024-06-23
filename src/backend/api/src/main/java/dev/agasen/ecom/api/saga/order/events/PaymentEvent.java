package dev.agasen.ecom.api.saga.order.events;

import java.time.Instant;

import dev.agasen.ecom.api.messaging.DomainEvent;
import dev.agasen.ecom.api.saga.order.OrderSaga;

public interface PaymentEvent extends DomainEvent, OrderSaga {
  
  record Initiated(
      Long orderId,
      Instant createdAt,
      Long payment,
      Long customerId
  ) implements PaymentEvent {}

  record Refunded(
      Long orderId,
      Instant createdAt,
      Long amountRefunded,
      Long customerId
  ) implements PaymentEvent {}

  // TODO
}
