package dev.agasen.ecom.inventory.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.agasen.ecom.api.core.inventory.model.InventoryDeductionRequest;
import dev.agasen.ecom.api.core.inventory.model.InventoryUpdateType;
import dev.agasen.ecom.inventory.UpdateInventoryService;
import dev.agasen.ecom.inventory.repository.InventoryRepository;
import dev.agasen.ecom.inventory.repository.InventoryUpdateEntity;
import dev.agasen.ecom.inventory.repository.InventoryUpdateRepository;
import dev.agasen.ecom.util.mongo.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DefaultUpdateInventoryService implements UpdateInventoryService {

  private final InventoryUpdateRepository updateRepo;
  private final InventoryRepository inventoryRepo;
  private final SequenceGeneratorService sequenceGenerator;


  @Override
  public Mono<List<InventoryUpdateEntity>> deduct(InventoryDeductionRequest req) {
    return Flux.fromIterable(req.getItems())
      .flatMap(item -> inventoryRepo.findByProductId(item.getProductId())
          .switchIfEmpty(Mono.error(new RuntimeException("Inventory not found for product: " + item.getProductId())))
          .filter(i -> i.getStock() >= item.getQuantity())
          .switchIfEmpty(Mono.error(new RuntimeException("Insufficient stock for product: " + item.getProductId())))
          // perform deduction
          .doOnNext(inv -> inv.deduct(item.getQuantity()))
          .flatMap(inventoryRepo::save)
          .zipWith(sequenceGenerator.generateSequence(InventoryUpdateEntity.SEQUENCE_NAME)
              .map(updateId -> InventoryUpdateEntity.newDeductionUpdate(updateId, item.getProductId(), req.getOrderId(), item.getQuantity()))
              .flatMap(updateRepo::save),
              (inv, update) -> update
          )
      )
      .collectList();
  }

  @Override
  public Mono<List<InventoryUpdateEntity>> restoreUpdate(Long orderId) {
    return updateRepo.findAllByOrderId(orderId)
      .filter(update -> update.getType() == InventoryUpdateType.PURCHASE)
      .flatMap(update -> sequenceGenerator
          .generateSequence(InventoryUpdateEntity.SEQUENCE_NAME)
          .map(updateId -> InventoryUpdateEntity.newRestoreUpdate(updateId, update.getInventoryId(), orderId, update.getQuantity()))
          .flatMap(updateRepo::save)
          .zipWith(inventoryRepo.findByInventoryId(update.getInventoryId())
              .map(inv -> inv.restore(update.getQuantity()))
              .flatMap(inventoryRepo::save),
              (updateEntity, inventoryEntity) -> updateEntity
          )
      )
      .collectList();
  }

}
