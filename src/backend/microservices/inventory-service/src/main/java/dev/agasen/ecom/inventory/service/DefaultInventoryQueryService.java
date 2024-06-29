package dev.agasen.ecom.inventory.service;

import java.util.List;
import java.util.function.BiFunction;

import org.springframework.stereotype.Service;

import dev.agasen.ecom.api.core.inventory.model.Inventory;
import dev.agasen.ecom.api.core.inventory.model.InventoryUpdate;
import dev.agasen.ecom.inventory.repository.InventoryEntity;
import dev.agasen.ecom.inventory.repository.InventoryRepository;
import dev.agasen.ecom.inventory.repository.InventoryUpdateRepository;
import dev.agasen.ecom.inventory.rest.service.InventoryQueryService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DefaultInventoryQueryService implements InventoryQueryService {
  
  private final InventoryRepository inventoryRepository;
  private final InventoryUpdateRepository updateRepository;

  @Override
  public Mono<Inventory> getInventory(Long productId) {
    return inventoryRepository.findByProductId(productId)
      .zipWith(
          updateRepository.findAllByInventoryId(productId)
            .cast(InventoryUpdate.class)
            .collectList(),
          (BiFunction<? super InventoryEntity, ? super List<InventoryUpdate>, ? extends InventoryEntity>) InventoryEntity::withHistory
      )
      .cast(Inventory.class);
  }

}
