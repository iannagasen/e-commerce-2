package dev.agasen.ecom.inventory.rest;

import dev.agasen.ecom.api.core.inventory.model.Inventory;
import dev.agasen.ecom.api.core.inventory.model.InventoryDeductionRequest;
import dev.agasen.ecom.api.core.inventory.model.InventoryUpdate;
import dev.agasen.ecom.api.core.inventory.rest.InventoryRestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class InventoryController implements InventoryRestController {

  @Override
  public Mono<Inventory> getInventory(Long productId) {
    throw new UnsupportedOperationException("Unimplemented method 'getInventory'");
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
