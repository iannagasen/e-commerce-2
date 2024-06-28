package dev.agasen.ecom.order.messaging.processors;

import java.time.Instant;

import org.springframework.stereotype.Service;

import dev.agasen.ecom.api.saga.order.events.OrderEvent;
import dev.agasen.ecom.api.saga.order.events.PaymentEvent.Declined;
import dev.agasen.ecom.api.saga.order.events.PaymentEvent.Processed;
import dev.agasen.ecom.api.saga.order.events.PaymentEvent.Refunded;
import dev.agasen.ecom.api.saga.order.processor.PaymentEventProcessor;
import dev.agasen.ecom.order.messaging.service.OrderFulfillmentService;
import dev.agasen.ecom.order.messaging.service.PaymentParticipantService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PaymentUpstreamEventProcessor implements PaymentEventProcessor<OrderEvent> {

  private final OrderFulfillmentService fulfillmentService;
  private final PaymentParticipantService paymentService;

  @Override
  public Mono<OrderEvent> handle(Processed event) {
    System.out.println("Payment Event Deducted processed: " + event);
    return paymentService.doOnSuccess(event.orderId())
      .then(fulfillmentService.complete(event.orderId()))
      .map(order -> OrderEvent.Completted.builder()
          .orderId(event.orderId())
          .items(order.getItems())
          .createdAt(Instant.now())
          .build()
      );
  }

  @Override
  public Mono<OrderEvent> handle(Declined event) {
    return paymentService.doOnFailure(event.orderId())
      .then(fulfillmentService.cancel(event.orderId()))
      .map(order -> OrderEvent.Cancelled.builder()
          .orderId(event.orderId())
          .items(order.getItems())
          .createdAt(Instant.now())
          .message(event.message())
          .build()
      );
  }

  @Override
  public Mono<OrderEvent> handle(Refunded event) {
    // WHY we are processing here??
    return paymentService.doOnRollback(event.orderId())
      .then(Mono.empty());
  }
}
