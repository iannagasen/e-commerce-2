package dev.agasen.ecom.api.saga.order.processor;

import dev.agasen.ecom.api.messaging.DomainEvent;
import dev.agasen.ecom.api.messaging.processor.EventProcessor;
import dev.agasen.ecom.api.saga.order.events.PaymentEvent;
import reactor.core.publisher.Mono;

public interface PaymentEventProcessor<R extends DomainEvent> extends EventProcessor<PaymentEvent, R> {

  @Override
  default Mono<R> process(PaymentEvent event) {
    return switch (event) {
      case PaymentEvent.Processed e -> this.handle(e);
      case PaymentEvent.Refunded e -> this.handle(e);
      case PaymentEvent.Declined e -> this.handle(e);
    };
  }

  Mono<R> handle(PaymentEvent.Processed e);

  Mono<R> handle(PaymentEvent.Refunded e);

  Mono<R> handle(PaymentEvent.Declined e);
  
}
