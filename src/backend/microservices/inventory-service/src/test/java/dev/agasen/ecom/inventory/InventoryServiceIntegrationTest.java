package dev.agasen.ecom.inventory;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import dev.agasen.ecom.api.core.inventory.model.InventoryDeductionRequest;
import dev.agasen.ecom.api.core.inventory.model.InventoryUpdateType;
import dev.agasen.ecom.api.core.order.model.OrderItem;
import dev.agasen.ecom.inventory.repository.InventoryEntity;
import dev.agasen.ecom.inventory.repository.InventoryRepository;
import dev.agasen.ecom.inventory.repository.InventoryUpdateEntity;
import dev.agasen.ecom.inventory.repository.InventoryUpdateRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DirtiesContext
@SpringBootTest(properties={
  "logging.level.root=ERROR",
  "logging.level.dev.agasen*=INFO",
  "spring.cloud.stream.kafka.binder.configuration.auto.offset.reset=earliest",
  "spring.cloud.stream.kafka.binder.brokers=localhost:9092"
})
@EmbeddedKafka(
  partitions = 1,
  bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
public class InventoryServiceIntegrationTest extends MongoDBTestBase {

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
      .items(List.of(OrderItem.builder().productId(1L).quantity(5).build()))
      .orderId(1L)
      .customerId(1L)
      .build();

    Mono<List<InventoryUpdateEntity>> deductMono = inventoryService.deduct(deduct_5_fromStockOf30);

    StepVerifier.create(deductMono)
      .assertNext(updates -> {
        var update = updates.get(0);

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
      })
      .verifyComplete();

    StepVerifier.create(updateRepository.findAllByInventoryId(1L).collectList())
      .assertNext(updates -> {
        assertEquals(1, updates.size());
        assertEquals(1L, updates.get(0).getInventoryId(), 0);
        assertEquals(1L, updates.get(0).getOrderId(), 0);
        assertEquals(InventoryUpdateType.PURCHASE, updates.get(0).getType());
        assertEquals(5, updates.get(0).getQuantity());
      })
      .verifyComplete();

    var deduct_6_fromStockof20_productId_2L = InventoryDeductionRequest.builder()
      .items(List.of(OrderItem.builder().productId(2L).quantity(6).build()))
      .orderId(2L)
      .customerId(1L)
      .build();

    Mono<List<InventoryUpdateEntity>> deductMono2 = inventoryService.deduct(deduct_6_fromStockof20_productId_2L);

    StepVerifier.create(deductMono2)
      .assertNext(updates -> {
        var update = updates.get(0);
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
      })
      .verifyComplete();

    StepVerifier.create(updateRepository.findAllByInventoryId(2L).collectList())
      .assertNext(updates -> {
        assertEquals(2 + 1, updates.size()); // 2 existing + 1 new
        assertEquals(InventoryUpdateType.PURCHASE, updates.get(updates.size() - 1).getType());
        assertEquals(2L, updates.get(updates.size() - 1).getOrderId(), 0);
        assertEquals(6, updates.get(updates.size() - 1).getQuantity());
      })
      .verifyComplete();

  }

  @Test
  void testDeductWithInsufficientStock() {
    var deduct_15_fromStockOf10 = InventoryDeductionRequest.builder()
      .items(List.of(OrderItem.builder().productId(1L).quantity(15).build()))
      .orderId(1L)
      .customerId(1L)
      .build();

    Mono<List<InventoryUpdateEntity>> deductMono = inventoryService.deduct(deduct_15_fromStockOf10);

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
        assertEquals(2L, updates.get(0).getInventoryId(), 0);
        assertEquals(10L, updates.get(0).getOrderId(), 0);
        assertEquals(InventoryUpdateType.CUSTOMER_RETURN, updates.get(0).getType());
        assertEquals(5, updates.get(0).getQuantity());
      })
      .verifyComplete();

    StepVerifier.create(inventoryRepository.findByInventoryId(2L).delayElement(Duration.ofSeconds(3)))
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
