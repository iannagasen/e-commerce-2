package dev.agasen.ecom.inventory.service;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.agasen.ecom.api.core.inventory.model.InventoryDeductionRequest;
import dev.agasen.ecom.api.core.inventory.model.InventoryUpdate;
import dev.agasen.ecom.api.core.inventory.model.InventoryUpdateType;
import dev.agasen.ecom.inventory.repository.InventoryEntity;
import dev.agasen.ecom.inventory.repository.InventoryRepository;
import dev.agasen.ecom.inventory.repository.InventoryUpdateEntity;
import dev.agasen.ecom.inventory.repository.InventoryUpdateRepository;
import dev.agasen.ecom.util.mongo.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class InventoryService {

  private final InventoryRepository inventoryRepo;
  private final InventoryUpdateRepository updateRepo;
  private final SequenceGeneratorService seqGen;

  @Transactional
  public Mono<InventoryEntity> deduct(InventoryDeductionRequest req) {
    return inventoryRepo.findByProductId(req.getProductId())
      .transform(this.validate(req))
      .transform(this.updateInventory(req));
  }

  @Transactional
  public Mono<InventoryEntity> restore(Long orderId) {
    // TODO: current assumption of InventoryDeductionRequest is 1 order = 1 product
    // handle multiple products in an order
    return updateRepo.findAllByOrderId(orderId)
      .take(1)
      .next()
      .filter(update -> update.getType() == InventoryUpdateType.PURCHASE)
      .flatMap(update -> inventoryRepo
          .findByInventoryId(update.getInventoryId())
          .map(inv -> inv.restore(update.getQuantity()))
          .flatMap(inventoryRepo::save)
          .zipWhen(inv -> seqGen.generateSequence(InventoryUpdateEntity.SEQUENCE_NAME)
              .map(seq -> InventoryUpdateEntity.builder()
                  .updateId(seq)
                  .inventoryId(inv.getInventoryId())
                  .orderId(orderId)
                  .type(InventoryUpdateType.CUSTOMER_RETURN)
                  .quantity(update.getQuantity())
                  .createdAt(LocalDateTime.now())
                  .build()
              )
              .flatMap(updateRepo::save)
          )
          .map(t -> t.getT1())
      );
  }

  private UnaryOperator<Mono<InventoryEntity>> updateInventory(InventoryDeductionRequest req) {
    var saveInventoryUpdate = seqGen
      .generateSequence(InventoryUpdateEntity.SEQUENCE_NAME)
      .map(seq -> this.inventoryPurchasedMapper(seq).apply(req))
      .flatMap(update -> updateRepo.saveAndFindAllByInventoryId(update).collectList());

    return inventory -> inventory
      .doOnNext(inv -> inv.deduct(req.getQuantity()))
      .flatMap(inventoryRepo::save)
      .zipWith(saveInventoryUpdate, (inv, update) -> {
        inv.setHistory(update.stream().map(u -> (InventoryUpdate) u).toList());
        inv.setLastUpdate(update.get(update.size() - 1));
        return inv;
      });
  }

  private UnaryOperator<Mono<InventoryEntity>> validate(InventoryDeductionRequest req) {
    return inventory -> inventory
      .switchIfEmpty(Mono.error(new RuntimeException("Inventory not found for product: " + req.getProductId())))
      .filter(i -> i.getStock() >= req.getQuantity())
      .switchIfEmpty(Mono.error(new RuntimeException("Insufficient stock for product: " + req.getProductId())));
  }

  private Function<InventoryDeductionRequest, InventoryUpdateEntity> inventoryPurchasedMapper(Long updateId) {
    return req -> InventoryUpdateEntity.builder()
      .updateId(updateId)
      .inventoryId(req.getProductId())
      .orderId(req.getOrderId())
      .type(InventoryUpdateType.PURCHASE)
      .quantity(req.getQuantity())
      .createdAt(LocalDateTime.now())
      .build();
  }

}