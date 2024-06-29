package dev.agasen.ecom.api.core.inventory.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import dev.agasen.ecom.api.core.inventory.model.Inventory;
import dev.agasen.ecom.api.core.inventory.model.InventoryDeductionRequest;
import dev.agasen.ecom.api.core.inventory.model.InventoryUpdate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InventoryServiceProxy {

  @GetExchange(InventoryRestController.GET_INVENTORY_PATH)
  Mono<Inventory> getInventory(@PathVariable("productId") Long productId);

  @GetExchange(InventoryRestController.GET_INVENTORIES_PATH)
  Flux<Inventory> getInventories();

  @GetExchange(InventoryRestController.GET_INVENTORY_HISTORY_PATH)
  Flux<InventoryUpdate> getOrderInventoryHistory(@PathVariable("productId") Long productId);

  @PostExchange(InventoryRestController.DEDUCT_INVENTORY_PATH)
  Mono<Inventory> deduct(InventoryDeductionRequest req);
  
}
