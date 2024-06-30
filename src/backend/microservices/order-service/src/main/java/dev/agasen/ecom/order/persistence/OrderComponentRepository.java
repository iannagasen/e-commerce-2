package dev.agasen.ecom.order.persistence;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderComponentRepository extends ReactiveMongoRepository<OrderComponentEntity, String> {

  Flux<OrderComponentEntity> findAllByOrderId(Long orderId);

  @Query("{'orderId': ?0, '_class': 'order_payment'}")
  Mono<OrderComponentEntity> findOrderPaymentByOrderId(Long orderId);

  @Query("{'orderId': ?0, '_class': 'order_inventory'}")
  Mono<OrderComponentEntity>  findOrderInventoryByOrderId(Long orderId);
  
}
