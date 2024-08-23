package dev.agasen.ecom.inventory.rest;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import dev.agasen.ecom.inventory.rest.service.InventoryQueryService;

@WebFluxTest(controllers = InventoryController.class)
public class InventoryControllerTest /* extends BaseIntegrationTest */ {

  @Autowired WebTestClient webTestClient;
  @MockBean InventoryQueryService queryService;

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