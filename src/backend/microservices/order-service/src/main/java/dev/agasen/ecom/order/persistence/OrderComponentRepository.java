package dev.agasen.ecom.order.persistence;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderComponentRepository extends ReactiveMongoRepository<OrderComponentEntity, String> {

  Flux<OrderComponentEntity> findAllByOrderId(Long orderId);

  @Query("{'orderId': ?0, '_class': 'order_payment'}")
  Mono<OrderComponentEntity> findInternalOrderPaymentByOrderId(Long orderId);

  @Query("{'orderId': ?0, '_class': 'order_inventory'}")
  Mono<OrderComponentEntity>  findInternalOrderInventoryByOrderId(Long orderId);

  default Mono<OrderComponentEntity.Payment> findOrderPaymentByOrderId(Long orderId) {
    return findInternalOrderPaymentByOrderId(orderId) .cast(OrderComponentEntity.Payment.class);
  }

  default Mono<OrderComponentEntity.Inventory> findOrderInventoryByOrderId(Long orderId) {
    return findInternalOrderInventoryByOrderId(orderId) .cast(OrderComponentEntity.Inventory.class);
  }


  
}
