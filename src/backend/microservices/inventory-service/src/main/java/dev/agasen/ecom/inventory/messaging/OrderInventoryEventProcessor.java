package dev.agasen.ecom.inventory.messaging;

import java.time.Instant;

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
    return service.deduct(new InventoryDeductionRequest(e.orderId(), e.customerId(), e.items()))
      .doOnNext(r -> log.info("Inventory deducted for Order: {}", r))
      .map(updates -> InventoryEvent.Deducted.builder()
            .orderId(e.orderId())
            .customerId(e.customerId())
            .items(e.items())
            .createdAt(Instant.now())
            .build()
      )
      .cast(InventoryEvent.class)
      // handle also duplicate events and return Mono.empty()
      .onErrorResume(err -> Mono.just(new InventoryEvent.Declined(e.orderId(), Instant.now(), err.getMessage())));
  }

  @Override
  public Mono<InventoryEvent> handle(Cancelled e) {
    return service.restoreUpdate(e.orderId())
      .doOnNext(r -> log.info("Inventory restored for Order: {}", r))
      .map(r -> InventoryEvent.Restored.builder()
        .orderId(e.orderId())
        .customerId(e.customerId())
        .items(e.items())
        .build()
      );
  }

  @Override
  public Mono<InventoryEvent> handle(Completted arg0) {
    return Mono.empty();
  }
}