package dev.agasen.ecom.api.saga.order.processor;

import dev.agasen.ecom.api.messaging.DomainEvent;
import dev.agasen.ecom.api.messaging.processor.EventProcessor;
import dev.agasen.ecom.api.saga.order.events.InventoryEvent;
import reactor.core.publisher.Mono;

public interface InventoryEventProcessor<R extends DomainEvent> extends EventProcessor<InventoryEvent, R> {
  
  @Override
  default Mono<R> process(InventoryEvent event) {
    return switch (event) {
      case InventoryEvent.Declined e -> this.handle(e);
      case InventoryEvent.Deducted e -> this.handle(e);
      case InventoryEvent.Restored e -> this.handle(e);
    };
  }

  Mono<R> handle(InventoryEvent.Declined e);

  Mono<R> handle(InventoryEvent.Deducted e);

  Mono<R> handle(InventoryEvent.Restored e);


}