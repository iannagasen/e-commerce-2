package dev.agasen.ecom.inventory.messaging;

import org.springframework.stereotype.Service;

import dev.agasen.ecom.api.saga.order.events.InventoryEvent;
import dev.agasen.ecom.api.saga.order.events.OrderEvent.Cancelled;
import dev.agasen.ecom.api.saga.order.events.OrderEvent.Completted;
import dev.agasen.ecom.api.saga.order.events.OrderEvent.Created;
import dev.agasen.ecom.api.saga.order.processor.OrderEventProcessor;
import dev.agasen.ecom.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderInventoryEventProcessor implements OrderEventProcessor<InventoryEvent> {

  private final InventoryService service;

  @Override
  public Mono<InventoryEvent> handle(Created e) {
    return service.deduct(e.toRequest())
      .doOnNext(r -> log.info("Inventory deducted for Order: {}", r))
      .map(r -> InventoryEvent.Deducted.builder()
        .orderId(e.orderId())
        .createdAt(e.createdAt())
        .productId(e.productId())
        .customerId(e.customerId())
        .quantity(e.quantity())
        .totalAmount(e.totalAmount())
        .build()
      );
  }

  @Override
  public Mono<InventoryEvent> handle(Cancelled arg0) {
    throw new UnsupportedOperationException("Unimplemented method 'handle'");
  }

  @Override
  public Mono<InventoryEvent> handle(Completted arg0) {
    throw new UnsupportedOperationException("Unimplemented method 'handle'");
  }
  
}