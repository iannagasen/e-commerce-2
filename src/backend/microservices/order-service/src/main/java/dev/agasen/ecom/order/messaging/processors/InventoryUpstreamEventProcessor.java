package dev.agasen.ecom.order.messaging.processors;

import java.time.Instant;

import org.springframework.stereotype.Service;

import dev.agasen.ecom.api.saga.order.events.InventoryEvent.Declined;
import dev.agasen.ecom.api.saga.order.events.InventoryEvent.Deducted;
import dev.agasen.ecom.api.saga.order.events.InventoryEvent.Restored;
import dev.agasen.ecom.api.saga.order.events.OrderEvent;
import dev.agasen.ecom.api.saga.order.processor.InventoryEventProcessor;
import dev.agasen.ecom.order.messaging.service.InventoryParticipantService;
import dev.agasen.ecom.order.messaging.service.OrderFulfillmentService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class InventoryUpstreamEventProcessor implements InventoryEventProcessor<OrderEvent> {

  private final OrderFulfillmentService fulfillmentService;
  private final InventoryParticipantService inventoryService;

  @Override
  public Mono<OrderEvent> handle(Deducted event) {
    System.out.println("Inventory Event Deducted processed: " + event);
    return inventoryService.doOnSuccess(event.orderId())
      .then(fulfillmentService.complete(event.orderId()))
      .map(order -> OrderEvent.Completted.builder()
          .orderId(event.orderId())
          .items(order.getItems())
          .createdAt(Instant.now())
          .build()
      )
      ;
  }

  @Override
  public Mono<OrderEvent> handle(Declined event) {
    return inventoryService.doOnFailure(event.orderId())
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
  public Mono<OrderEvent> handle(Restored event) {
    return inventoryService.doOnRollback(event.orderId())
      .then(Mono.empty());
  }
  
}
