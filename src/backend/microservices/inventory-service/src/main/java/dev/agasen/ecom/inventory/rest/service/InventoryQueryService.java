package dev.agasen.ecom.inventory.rest.service;

import dev.agasen.ecom.api.core.inventory.model.Inventory;
import reactor.core.publisher.Mono;

public interface InventoryQueryService {

  Mono<Inventory> getInventory(Long productId);

  
}
