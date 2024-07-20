/* tslint:disable */
/* eslint-disable */
import { OrderItem } from '../models/order-item';
export interface CreateOrderRequest {
  customerId?: number;
  items?: Array<OrderItem>;
}
