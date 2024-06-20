package dev.agasen.ecom.inventory.repository;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import dev.agasen.ecom.api.core.inventory.model.InventoryUpdate;
import dev.agasen.ecom.api.core.inventory.model.InventoryUpdateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "inventory_updates")
public class InventoryUpdateEntity implements InventoryUpdate {

  public @Transient static final String SEQUENCE_NAME = "product-inventory";

  private @Id String id;
  private @Indexed(unique=true) Long updateId;
  private @Indexed Long inventoryId;
  private @Indexed Long orderId;
  private InventoryUpdateType type;
  private int quantity;
  private LocalDateTime createdAt;

  public InventoryUpdateEntity withUpdateId(Long updateId) {
    this.updateId = updateId;
    return this;
  }

}
