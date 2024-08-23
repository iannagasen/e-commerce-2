import { OrderItem } from "./order-item";

export interface PurchaseOrder {
  orderId: number;
  customerId: number;
  orderItems: Array<OrderItem>;
}
