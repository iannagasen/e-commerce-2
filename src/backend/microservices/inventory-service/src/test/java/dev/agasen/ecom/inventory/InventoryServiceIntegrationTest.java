package dev.agasen.ecom.inventory;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.agasen.ecom.api.core.inventory.model.InventoryDeductionRequest;
import dev.agasen.ecom.api.core.inventory.model.InventoryUpdateType;
import dev.agasen.ecom.inventory.repository.InventoryEntity;
import dev.agasen.ecom.inventory.repository.InventoryRepository;
import dev.agasen.ecom.inventory.repository.InventoryUpdateRepository;
import dev.agasen.ecom.inventory.service.InventoryService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class InventoryServiceIntegrationTest extends MongoDBTestBase {

  @Autowired private InventoryRepository inventoryRepository;
  @Autowired private InventoryUpdateRepository updateRepository;
  @Autowired private InventoryService inventoryService;


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

  }

    
  @Test
  void testDeduct() {
    var deduct_5_fromStockOf30 = InventoryDeductionRequest.builder()
      .productId(1L)
      .orderId(1L)
      .customerId(1L)
      .quantity(5)
      .build();

    Mono<InventoryEntity> deductMono = inventoryService.deduct(deduct_5_fromStockOf30);

    StepVerifier.create(deductMono)
      .assertNext(inv -> {
        assertEquals(5, inv.getStock(), 0);
        assertEquals(1L, inv.getInventoryId(), 0);
        assertEquals(1L, inv.getProductId(), 0);
        assertEquals(1, inv.getHistory().size());
        assertEquals(InventoryUpdateType.PURCHASE, inv.getLastUpdate().getType());
        assertEquals(5, inv.getLastUpdate().getQuantity());
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

    Mono<InventoryEntity> deductMono = inventoryService.deduct(deduct_15_fromStockOf10);

    StepVerifier.create(deductMono)
      .expectErrorMatches(e ->
           e instanceof RuntimeException && 
           e.getMessage().equals("Insufficient stock for product: 1"))
      .verify();
  }
}
