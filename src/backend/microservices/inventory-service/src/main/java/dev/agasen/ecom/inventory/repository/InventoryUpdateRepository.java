package dev.agasen.ecom.inventory.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;

public interface InventoryUpdateRepository extends ReactiveMongoRepository<InventoryUpdateEntity, String> {
  

  Flux<InventoryUpdateEntity> findAllByInventoryId(Long inventoryId);

  default Flux<InventoryUpdateEntity> saveAndFindAllByInventoryId(InventoryUpdateEntity entity) {
    return save(entity).thenMany(findAllByInventoryId(entity.getInventoryId()));
  }

  Flux<InventoryUpdateEntity> findAllByOrderId(Long orderId);


}
