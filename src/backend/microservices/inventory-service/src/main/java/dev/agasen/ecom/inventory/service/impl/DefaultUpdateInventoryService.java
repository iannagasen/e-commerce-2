package dev.agasen.ecom.inventory.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.agasen.ecom.api.core.inventory.model.InventoryDeductionRequest;
import dev.agasen.ecom.api.core.inventory.model.InventoryUpdateType;
import dev.agasen.ecom.inventory.repository.InventoryRepository;
import dev.agasen.ecom.inventory.repository.InventoryUpdateEntity;
import dev.agasen.ecom.inventory.repository.InventoryUpdateRepository;
import dev.agasen.ecom.inventory.service.UpdateInventoryService;
import dev.agasen.ecom.util.mongo.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DefaultUpdateInventoryService implements UpdateInventoryService {

  private final InventoryUpdateRepository updateRepo;
  private final InventoryRepository inventoryRepo;
  private final SequenceGeneratorService sequenceGenerator;


  @Override
  public Mono<InventoryUpdateEntity> deduct(InventoryDeductionRequest req) {
    return inventoryRepo.findByProductId(req.getProductId())
      .switchIfEmpty(Mono.error(new RuntimeException("Inventory not found for product: " + req.getProductId())))
      .filter(i -> i.getStock() >= req.getQuantity())
      .switchIfEmpty(Mono.error(new RuntimeException("Insufficient stock for product: " + req.getProductId())))
      // perform deduction
      .doOnNext(inv -> inv.deduct(req.getQuantity()))
      .flatMap(inventoryRepo::save)
      .zipWith(sequenceGenerator.generateSequence(InventoryUpdateEntity.SEQUENCE_NAME)
          .map(updateId -> InventoryUpdateEntity.newDeductionUpdate(updateId, req.getProductId(), req.getOrderId(), req.getQuantity()))
          .flatMap(updateRepo::save),
          (inv, update) -> update
      );
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
