package dev.agasen.ecom.order.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.agasen.ecom.api.core.order.model.CreateOrderRequest;
import dev.agasen.ecom.api.core.order.model.GetOrdersType;
import dev.agasen.ecom.api.saga.order.events.listener.OrderEventListener;
import dev.agasen.ecom.order.OrderService;
import dev.agasen.ecom.order.persistence.OrderComponentEntity;
import dev.agasen.ecom.order.persistence.OrderComponentRepository;
import dev.agasen.ecom.order.persistence.PurchaseOrderEntity;
import dev.agasen.ecom.order.persistence.PurchaseOrderRepository;
import dev.agasen.ecom.util.mongo.SequenceGeneratorService;
import dev.agasen.ecom.util.security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultOrderService implements OrderService {

  private final PurchaseOrderRepository purchaseOrderRepo;
  private final OrderComponentRepository orderComponentRepo;
  private final SequenceGeneratorService sequenceGenerator;
  private final OrderEventListener orderEventListener;
  private final AuthenticationFacade authFacade;
  
  @Override
  @Transactional
  public Mono<PurchaseOrderEntity> placeOrder(CreateOrderRequest req) {
    return sequenceGenerator.generateSequence(PurchaseOrderEntity.SEQUENCE_NAME)
      .map(orderId -> PurchaseOrderEntity.fromPending(orderId, req.getCustomerId(), req.getItems()))
      .flatMap(purchaseOrderRepo::save)

      // also save the order components in PENDING STATE
      .doOnNext(po -> log.info("Saving order components for order: {}", po))
      .zipWhen(po -> sequenceGenerator.generateSequence(OrderComponentEntity.SEQUENCE_NAME)
          .flatMap(id -> Mono.zip(
              orderComponentRepo.save(OrderComponentEntity.Inventory.newPending(id, po.getOrderId())),
              orderComponentRepo.save(OrderComponentEntity.Payment.newPending(id+1, po.getOrderId()))
          )),
          (po, components) -> po
      )
      .doOnNext(po -> orderEventListener.emitOrderCreatedEvent(po.toRestModel()))
      .doOnSuccess(order -> log.info("Order placed: {}", order))
      ;
  }

  @Override
  public Flux<PurchaseOrderEntity> getOrders(GetOrdersType type) {
    return authFacade.getAuthentication().doOnNext(auth -> log.info("Auth: {}", auth)).thenMany(purchaseOrderRepo.findAll());
  }

}
