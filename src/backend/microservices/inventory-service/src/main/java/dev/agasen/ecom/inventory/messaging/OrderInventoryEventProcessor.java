package dev.agasen.ecom.inventory.messaging;

import org.springframework.stereotype.Service;

import dev.agasen.ecom.api.saga.order.events.InventoryEvent;
import dev.agasen.ecom.api.saga.order.events.OrderEvent.Cancelled;
import dev.agasen.ecom.api.saga.order.events.OrderEvent.Completted;
import dev.agasen.ecom.api.saga.order.events.OrderEvent.Created;
import dev.agasen.ecom.api.saga.order.processor.OrderEventProcessor;
import dev.agasen.ecom.inventory.service.UpdateInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderInventoryEventProcessor implements OrderEventProcessor<InventoryEvent> {

  private final UpdateInventoryService service;

  @Override
  public Mono<InventoryEvent> handle(Created e) {
    return service.deduct(e.toRequest())
      .doOnNext(r -> log.info("Inventory deducted for Order: {}", r))
      .map(r -> InventoryEvent.Deducted.builder()
        .orderId(e.orderId())
        .productId(r.getInventoryId())
        .customerId(null) // TODO: add customer id
        .quantity(r.getQuantity())
        .unitPrice(0) // TODO: add unit price
        .totalAmount(0) // TODO: add total amount
        .build());
  }

  @Override
  public Mono<InventoryEvent> handle(Cancelled e) {
    return service.restoreUpdate(e.orderId())
      .doOnNext(r -> log.info("Inventory restored for Order: {}", r))
      .map(r -> InventoryEvent.Restored.builder()
        .orderId(e.orderId())
        .productId(r.get(0).getInventoryId())
        .customerId(null) // TODO: add customer id
        .quantity(r.get(0).getQuantity())
        .unitPrice(0) // TODO: add unit price
        .totalAmount(0) // TODO: add total amount
        .build()
      );
  }

  @Override
  public Mono<InventoryEvent> handle(Completted arg0) {
    return Mono.empty();
  }
  
}