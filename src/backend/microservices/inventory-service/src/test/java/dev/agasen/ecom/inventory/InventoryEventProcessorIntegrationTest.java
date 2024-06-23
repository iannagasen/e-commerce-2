package dev.agasen.ecom.inventory;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;

import dev.agasen.ecom.api.core.inventory.model.InventoryUpdateType;
import dev.agasen.ecom.api.core.order.model.OrderItem;
import dev.agasen.ecom.api.saga.order.events.InventoryEvent;
import dev.agasen.ecom.api.saga.order.events.OrderEvent;
import dev.agasen.ecom.inventory.repository.InventoryEntity;
import dev.agasen.ecom.inventory.repository.InventoryRepository;
import dev.agasen.ecom.inventory.repository.InventoryUpdateEntity;
import dev.agasen.ecom.inventory.repository.InventoryUpdateRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;


public class InventoryEventProcessorIntegrationTest extends BaseIntegrationTest {

  @Autowired private InventoryRepository inventoryRepository;
  @Autowired private InventoryUpdateRepository updateRepository;

  @BeforeEach
  void setUp() {
    inventoryRepository.deleteAll().block();
    updateRepository.deleteAll().block();
    
    var inv1 = InventoryEntity.builder()
      .inventoryId(1L)
      .productId(1L)
      .stock(10)
      .build();

    var inv2 = InventoryEntity.builder()  
      .inventoryId(2L)
      .productId(2L)
      .stock(20)
      .build();

    var inv3 = InventoryEntity.builder()
      .inventoryId(3L)
      .productId(3L)
      .stock(30)
      .build();

    inventoryRepository.saveAll(List.of(inv1, inv2, inv3)).collectList().block();

    var inv2_update1 = InventoryUpdateEntity.builder()
      .updateId(1L)
      .inventoryId(2L)
      .orderId(10L)
      .type(InventoryUpdateType.PURCHASE)
      .quantity(5)
      .build();

    var inv2_update2 = InventoryUpdateEntity.builder()
      .updateId(2L)
      .inventoryId(2L)
      .orderId(20L)
      .type(InventoryUpdateType.PURCHASE)
      .quantity(6)
      .build();

    updateRepository.saveAll(List.of(inv2_update1, inv2_update2)).collectList().block();
  }


  @Test
  void processPaymentTest() {
    var customerId = 1L;
    var quantity = 4;
    var orderCreatedEvent = OrderEvent.Created.builder()
      .orderId(1L)
      .customerId(customerId)
      // .productId(1L)
      .items(List.of(OrderItem.builder().productId(1L).quantity(quantity).build()))
      .createdAt(Instant.now())
      .build();

    responseFlux
      .doFirst(() -> requestSink.tryEmitNext(orderCreatedEvent))
      .next()
      .timeout(Duration.ofSeconds(5)) // wait for 5 seconds
      .cast(InventoryEvent.Deducted.class)
      .as(StepVerifier::create)
      .assertNext(deducted -> {
        // assertEquals(customerId, deducted.customerId(), 0);
        assertEquals(orderCreatedEvent.orderId(), deducted.orderId());
        // TODO:test List of items
        // assertEquals(orderCreatedEvent.getItems, e.item);
        // assertEquals(orderCreatedEvent.quantity(), deducted.quantity());
        // assertEquals(quantity, deducted.quantity());
      })
      .verifyComplete();

    StepVerifier.create(inventoryRepository.findByProductId(1L))
      .assertNext(inv -> {
        assertEquals(6, inv.getStock(), 0); // 10 - 4 = 6
      })
      .verifyComplete();

    StepVerifier.create(updateRepository.findAllByInventoryId(1L).collectList())
      .assertNext(updates -> {
        assertEquals(1, updates.size());
        assertEquals(1L, updates.get(0).getOrderId(), 0);
        assertEquals(InventoryUpdateType.PURCHASE, updates.get(0).getType());
        assertEquals(quantity, updates.get(0).getQuantity());
      
      })
      .verifyComplete();
  }





}
