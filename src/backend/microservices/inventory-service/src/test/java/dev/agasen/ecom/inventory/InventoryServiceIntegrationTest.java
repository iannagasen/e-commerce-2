package dev.agasen.ecom.inventory;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import dev.agasen.ecom.api.core.inventory.model.InventoryDeductionRequest;
import dev.agasen.ecom.api.core.inventory.model.InventoryUpdateType;
import dev.agasen.ecom.inventory.repository.InventoryEntity;
import dev.agasen.ecom.inventory.repository.InventoryRepository;
import dev.agasen.ecom.inventory.repository.InventoryUpdateEntity;
import dev.agasen.ecom.inventory.repository.InventoryUpdateRepository;
import dev.agasen.ecom.inventory.service.UpdateInventoryService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class InventoryServiceIntegrationTest extends BaseIntegrationTest {

  @Autowired private InventoryRepository inventoryRepository;
  @Autowired private InventoryUpdateRepository updateRepository;
  @Autowired private UpdateInventoryService inventoryService;


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
  void testDeduct() {
    var deduct_5_fromStockOf30 = InventoryDeductionRequest.builder()
      .productId(1L)
      .orderId(1L)
      .customerId(1L)
      .quantity(5)
      .build();

    Mono<InventoryUpdateEntity> deductMono = inventoryService.deduct(deduct_5_fromStockOf30);

    StepVerifier.create(deductMono)
      .assertNext(update -> {
        assertEquals(1L, update.getUpdateId(), 0);
        assertEquals(1L, update.getInventoryId(), 0);
        assertEquals(1L, update.getOrderId(), 0);
        assertEquals(InventoryUpdateType.PURCHASE, update.getType());
        assertEquals(5, update.getQuantity());
      })
      .verifyComplete();

    StepVerifier.create(inventoryRepository.findByProductId(1L))
      .assertNext(inv -> {
        assertEquals(5, inv.getStock(), 0);
        assertEquals(1L, inv.getInventoryId(), 0);
        assertEquals(1L, inv.getProductId(), 0);
        assertEquals(1, inv.getHistory().size());
        assertEquals(InventoryUpdateType.PURCHASE, inv.getLastUpdate().getType());
        assertEquals(1L, inv.getLastUpdate().getOrderId(), 0);
        assertEquals(5, inv.getLastUpdate().getQuantity());
      })
      .verifyComplete();

    var deduct_6_fromStockof20_productId_2L = InventoryDeductionRequest.builder()
      .productId(2L)
      .orderId(2L)
      .customerId(1L)
      .quantity(6)
      .build();

    Mono<InventoryUpdateEntity> deductMono2 = inventoryService.deduct(deduct_6_fromStockof20_productId_2L);

    StepVerifier.create(deductMono2)
      .assertNext(update -> {
        assertEquals(3L, update.getUpdateId(), 0);
        assertEquals(2L, update.getInventoryId(), 0);
        assertEquals(2L, update.getOrderId(), 0);
        assertEquals(InventoryUpdateType.PURCHASE, update.getType());
        assertEquals(6, update.getQuantity());
      })
      .verifyComplete();

    StepVerifier.create(inventoryRepository.findByProductId(2L))
      .assertNext(inv -> {
        assertEquals(14, inv.getStock(), 0);
        assertEquals(2L, inv.getInventoryId(), 0);
        assertEquals(2L, inv.getProductId(), 0);
        assertEquals(2 + 1, inv.getHistory().size()); // 2 existing + 1 new
        assertEquals(InventoryUpdateType.PURCHASE, inv.getLastUpdate().getType());
        assertEquals(2L, inv.getLastUpdate().getOrderId(), 0);
        assertEquals(6, inv.getLastUpdate().getQuantity());
      })
      .verifyComplete();
  }

  @Test
  void testDeductWithInsufficientStock() {
    var deduct_15_fromStockOf10 = InventoryDeductionRequest.builder()
      .productId(1L)
      .orderId(1L)
      .customerId(1L)
      .quantity(15) // 15 is greater than stock of 10
      .build();

    Mono<InventoryUpdateEntity> deductMono = inventoryService.deduct(deduct_15_fromStockOf10);

    StepVerifier.create(deductMono)
      .expectErrorMatches(e ->
           e instanceof RuntimeException && 
           e.getMessage().equals("Insufficient stock for product: 1"))
      .verify();
  }

  @Test
  void testRestore() {
    var restore_orderId_10 = 10L;

    Mono<List<InventoryUpdateEntity>> restoreMono = inventoryService.restoreUpdate(restore_orderId_10);

    StepVerifier.create(restoreMono)
      .assertNext(updates -> {
        assertEquals(1, updates.size());
        assertEquals(3L, updates.get(0).getUpdateId(), 0);
        assertEquals(2L, updates.get(0).getInventoryId(), 0);
        assertEquals(10L, updates.get(0).getOrderId(), 0);
        assertEquals(InventoryUpdateType.PURCHASE, updates.get(0).getType());
        assertEquals(6, updates.get(0).getQuantity());
      })
      .verifyComplete();

    StepVerifier.create(inventoryRepository.findByProductId(10L))
      .assertNext(inv -> {
        assertEquals(25, inv.getStock(), 0);
        assertEquals(2L, inv.getInventoryId(), 0);
        assertEquals(2L, inv.getProductId(), 0);
        // assertEquals(2, inv.getHistory().size());
        // assertEquals(InventoryUpdateType.PURCHASE, inv.getLastUpdate().getType());
        // assertEquals(2L, inv.getLastUpdate().getOrderId(), 0);
        // assertEquals(6, inv.getLastUpdate().getQuantity());
      })
      .verifyComplete();
  }
}
