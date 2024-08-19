package dev.agasen.ecom.inventory.rest;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import dev.agasen.ecom.inventory.BaseIntegrationTest;
import dev.agasen.ecom.inventory.messaging.OrderEventRouterConfig;
import dev.agasen.ecom.inventory.repository.InventoryRepository;
import dev.agasen.ecom.inventory.repository.InventoryUpdateRepository;
import dev.agasen.ecom.inventory.rest.service.InventoryQueryService;
import dev.agasen.ecom.util.mongo.SequenceGeneratorService;

@WebFluxTest(controllers = InventoryController.class)
// @Import(InventoryQueryService.class)
public class InventoryControllerTest /* extends BaseIntegrationTest */ {

  @Autowired WebTestClient webTestClient;
  @MockBean InventoryQueryService queryService;
  // @MockBean InventoryUpdateRepository repository;
  // @MockBean OrderEventRouterConfig orderEventRouterConfig;
  // @MockBean InventoryRepository inventoryRepository;
  // @MockBean SequenceGeneratorService sequenceGeneratorService;

  @Test
  void testDeduct() {

  }

  @Test
  void testGetInventories() {
    when(queryService.getInventories(null)).thenReturn(null);

    webTestClient.get()
    .uri("/inventory?category")
    .exchange()
    .expectStatus()
    .isOk();
  }

  @Test
  void testGetInventory() {

  }

  @Test
  void testGetInventoryHistory() {

  }
}
