/* tslint:disable */
/* eslint-disable */
import { OrderItem } from '../models/order-item';
import { PurchaseOrder } from '../models/purchase-order';
export interface OrderDetails {
  orderItems?: Array<OrderItem>;
  purchaseOrder?: PurchaseOrder;
  totalPayment?: number;
}
