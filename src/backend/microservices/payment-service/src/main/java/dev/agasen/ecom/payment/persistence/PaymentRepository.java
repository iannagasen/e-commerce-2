package dev.agasen.ecom.payment.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import java.util.List;
import dev.agasen.ecom.api.core.payment.model.TransactionType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface PaymentRepository extends ReactiveMongoRepository<PaymentEntity, String>{

  Mono<PaymentEntity> findByOrderIdAndTransactionType(Long orderId, TransactionType transactionType);

  Flux<PaymentEntity> findAllByOrderId(Long orderId);

}
