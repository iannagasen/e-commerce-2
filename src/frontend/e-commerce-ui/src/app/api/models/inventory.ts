/* tslint:disable */
/* eslint-disable */
import { InventoryUpdate } from '../models/inventory-update';
export interface Inventory {
  history?: Array<InventoryUpdate>;
  inventoryId?: number;
  productId?: number;
  stock?: number;
}
