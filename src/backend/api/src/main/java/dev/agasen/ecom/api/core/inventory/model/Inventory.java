package dev.agasen.ecom.api.core.inventory.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Inventory {
  private Long inventoryId;
  private Long productId;
  private int stock;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<InventoryUpdate> history;
}
