package dev.agasen.ecom.api.saga.order.processor;

import dev.agasen.ecom.api.messaging.DomainEvent;
import dev.agasen.ecom.api.messaging.processor.EventProcessor;
import dev.agasen.ecom.api.saga.order.events.OrderEvent;
import reactor.core.publisher.Mono;

/**
 * The OrderEventProcessor interface defines methods for processing order events.
 */
public interface OrderEventProcessor<R extends DomainEvent> extends EventProcessor<OrderEvent, R>{
  
  @Override
  default Mono<R> process(OrderEvent event) {
    return switch(event) {
      case OrderEvent.Cancelled e -> this.handle(e);
      case OrderEvent.Completted e -> this.handle(e);
      case OrderEvent.Created e -> this.handle(e);
    };
  }

  Mono<R> handle(OrderEvent.Cancelled e);

  Mono<R> handle(OrderEvent.Completted e);

  Mono<R> handle(OrderEvent.Created e);
}
