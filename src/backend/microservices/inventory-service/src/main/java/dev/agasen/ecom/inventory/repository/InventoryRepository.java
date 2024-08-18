package dev.agasen.ecom.inventory.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InventoryRepository extends ReactiveMongoRepository<InventoryEntity, String> {
  
  Mono<InventoryEntity> findByProductId(Long productId);
  Mono<InventoryEntity> findByInventoryId(Long inventoryId);
  Flux<InventoryEntity> findAllByCategory(String category);

}
