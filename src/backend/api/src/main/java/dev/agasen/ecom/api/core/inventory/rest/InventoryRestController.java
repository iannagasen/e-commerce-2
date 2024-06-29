package dev.agasen.ecom.api.core.inventory.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import dev.agasen.ecom.api.core.inventory.model.Inventory;
import dev.agasen.ecom.api.core.inventory.model.InventoryDeductionRequest;
import dev.agasen.ecom.api.core.inventory.model.InventoryUpdate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InventoryRestController {

  String GET_INVENTORY_PATH = "/inventory/{productId}";
  String GET_INVENTORIES_PATH = "/inventory";
  String GET_INVENTORY_HISTORY_PATH = "/inventory/{productId}/history";
  String DEDUCT_INVENTORY_PATH = "/inventory/deduct";

  @GetMapping(GET_INVENTORY_PATH)
  Mono<Inventory> getInventory(@PathVariable Long productId);

  @GetMapping(GET_INVENTORIES_PATH)
  Flux<Inventory> getInventories();

  @GetMapping(GET_INVENTORY_HISTORY_PATH)
  Flux<InventoryUpdate> getInventoryHistory(@PathVariable("productId") Long productId);

  @PostMapping(DEDUCT_INVENTORY_PATH)
  Mono<Inventory> deduct(InventoryDeductionRequest req);

} 