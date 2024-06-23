package dev.agasen.ecom.payment.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PaymentRepository extends ReactiveMongoRepository<PaymentEntity, String>{
  
}
