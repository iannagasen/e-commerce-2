package dev.agasen.ecom.payment.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface BalanceRepository extends ReactiveMongoRepository<BalanceEntity, String> {
  
}
