package dev.agasen.ecom.inventory.messaging;

import org.springframework.stereotype.Service;

import dev.agasen.ecom.api.core.inventory.model.InventoryDeductionRequest;
import dev.agasen.ecom.api.saga.order.events.InventoryEvent;
import dev.agasen.ecom.api.saga.order.events.OrderEvent.Cancelled;
import dev.agasen.ecom.api.saga.order.events.OrderEvent.Completted;
import dev.agasen.ecom.api.saga.order.events.OrderEvent.Created;
import dev.agasen.ecom.api.saga.order.processor.OrderEventProcessor;
import dev.agasen.ecom.inventory.UpdateInventoryService;
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
    var req = InventoryDeductionRequest.builder()
          .orderId(e.orderId())
          .items(e.items())
          .customerId(e.customerId())
          .build();

    return service.deduct(req)
      .doOnNext(r -> log.info("Inventory deducted for Order: {}", r))
      .map(r -> InventoryEvent.Deducted.builder()
        .orderId(e.orderId())
        .customerId(null) // TODO: add customer id
        .items(e.items())
        .build());
  }

  @Override
  public Mono<InventoryEvent> handle(Cancelled e) {
    return service.restoreUpdate(e.orderId())
      .doOnNext(r -> log.info("Inventory restored for Order: {}", r))
      .map(r -> InventoryEvent.Restored.builder()
        .orderId(e.orderId())
        .customerId(null) // TODO: add customer id
        .items(e.items())
        .build()
      );
  }

  @Override
  public Mono<InventoryEvent> handle(Completted arg0) {
    return Mono.empty();
  }
  
}