package dev.agasen.ecom.inventory.service;

import java.util.List;

import dev.agasen.ecom.api.core.inventory.model.InventoryDeductionRequest;
import dev.agasen.ecom.inventory.repository.InventoryUpdateEntity;
import reactor.core.publisher.Mono;

public interface UpdateInventoryService {

  Mono<List<InventoryUpdateEntity>> deduct(InventoryDeductionRequest req);

  /**
   * 
   * @param orderId
   * @return all the restoredUpdatedEntity
   */
  Mono<List<InventoryUpdateEntity>> restoreUpdate(Long orderId);

}