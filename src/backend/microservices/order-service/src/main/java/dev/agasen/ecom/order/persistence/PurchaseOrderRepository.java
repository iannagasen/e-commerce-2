package dev.agasen.ecom.order.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Mono;

public interface PurchaseOrderRepository extends ReactiveMongoRepository<PurchaseOrderEntity, String> {

  Mono<PurchaseOrderEntity> findByOrderId(Long orderId);
  
}
