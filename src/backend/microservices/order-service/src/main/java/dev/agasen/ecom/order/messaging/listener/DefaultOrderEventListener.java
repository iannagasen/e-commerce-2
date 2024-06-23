package dev.agasen.ecom.order.messaging.listener;

import java.time.Duration;

import dev.agasen.ecom.api.core.order.model.PurchaseOrder;
import dev.agasen.ecom.api.messaging.publisher.EventPublisher;
import dev.agasen.ecom.api.saga.order.events.OrderEvent;
import dev.agasen.ecom.api.saga.order.events.listener.OrderEventListener;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RequiredArgsConstructor
public class DefaultOrderEventListener implements OrderEventListener, EventPublisher<OrderEvent> {

  private final Sinks.Many<OrderEvent> sink;
  private final Flux<OrderEvent> flux;

  @Override
  public Flux<OrderEvent> publish() {
    return this.flux;
  }

  @Override
  public void emitOrderCreatedEvent(PurchaseOrder order) {
    this.sink.emitNext(
      OrderEvent.Created.builder()
        .orderId(order.getOrderId())
        .customerId(order.getCustomerId())
        .items(order.getItems())
        .build(),
      Sinks.EmitFailureHandler.busyLooping(Duration.ofSeconds(1))
    );
  }
  
}
