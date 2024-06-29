package dev.agasen.ecom.inventory.rest;

import org.springframework.web.bind.annotation.RestController;

import dev.agasen.ecom.api.core.inventory.model.Inventory;
import dev.agasen.ecom.api.core.inventory.model.InventoryDeductionRequest;
import dev.agasen.ecom.api.core.inventory.model.InventoryUpdate;
import dev.agasen.ecom.api.core.inventory.rest.InventoryRestController;
import dev.agasen.ecom.inventory.rest.service.InventoryQueryService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class InventoryController implements InventoryRestController {

  private final InventoryQueryService queryService;

  @Override
  public Mono<Inventory> getInventory(Long productId) {
    return queryService.getInventory(productId);
  }

  @Override
  public Flux<Inventory> getInventories() {
    throw new UnsupportedOperationException("Unimplemented method 'getInventories'");
  }

  @Override
  public Flux<InventoryUpdate> getInventoryHistory(Long productId) {
    throw new UnsupportedOperationException("Unimplemented method 'getInventoryHistory'");
  }

  @Override
  public Mono<Inventory> deduct(InventoryDeductionRequest req) {
    throw new UnsupportedOperationException("Unimplemented method 'deduct'");
  }
  
}
