/* tslint:disable */
/* eslint-disable */
export interface InventoryUpdate {
  createdAt?: string;
  inventoryId?: number;
  orderId?: number;
  quantity?: number;
  type?: 'PURCHASE' | 'RESTOCK' | 'CUSTOMER_RETURN' | 'DAMAGED' | 'INVENTORY_ADJUSTMENT' | 'SUPPLIER_RETURN';
  updateId?: number;
}
