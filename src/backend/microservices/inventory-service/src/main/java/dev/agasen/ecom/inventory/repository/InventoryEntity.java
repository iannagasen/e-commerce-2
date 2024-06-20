package dev.agasen.ecom.inventory.repository;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import dev.agasen.ecom.api.core.inventory.model.Inventory;
import dev.agasen.ecom.api.core.inventory.model.InventoryUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "inventory")
public class InventoryEntity implements Inventory {
  
  public @Transient static final String SEQUENCE_NAME = "product-inventory";

  private @Id String id;
  private @Indexed(unique=true) Long inventoryId;
  private @Indexed(unique=true) Long productId;
  private int stock;

  private @Transient List<InventoryUpdate> history;
  private @Transient InventoryUpdate lastUpdate;

  public InventoryEntity withInventoryId(Long inventoryId) {
    this.inventoryId = inventoryId;
    return this;
  }

  public InventoryEntity deduct(int amount) {
    this.stock -= amount;
    return this;
  }

  public InventoryEntity restore(int amount) {
    this.stock += amount;
    return this;
  }

  public InventoryEntity addUpdate(InventoryUpdate update) {
    this.history.add(update);
    this.lastUpdate = update;
    return this;
  }

}
