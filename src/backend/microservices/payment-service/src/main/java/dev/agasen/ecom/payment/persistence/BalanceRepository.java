package dev.agasen.ecom.payment.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Mono;

public interface BalanceRepository extends ReactiveMongoRepository<BalanceEntity, String> {

  Mono<BalanceEntity> findByCustomerId(Long customerId);
  
}
