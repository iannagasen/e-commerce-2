/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';

import { getOrderInventory } from '../fn/order-controller/get-order-inventory';
import { GetOrderInventory$Params } from '../fn/order-controller/get-order-inventory';
import { getOrders } from '../fn/order-controller/get-orders';
import { GetOrders$Params } from '../fn/order-controller/get-orders';
import { Inventory } from '../models/inventory';
import { OrderDetails } from '../models/order-details';
import { placeOrder } from '../fn/order-controller/place-order';
import { PlaceOrder$Params } from '../fn/order-controller/place-order';
import { PurchaseOrder } from '../models/purchase-order';

@Injectable({ providedIn: 'root' })
export class OrderControllerService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /** Path part for operation `placeOrder()` */
  static readonly PlaceOrderPath = '/order';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `placeOrder()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  placeOrder$Response(params: PlaceOrder$Params, context?: HttpContext): Observable<StrictHttpResponse<PurchaseOrder>> {
    return placeOrder(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `placeOrder$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  placeOrder(params: PlaceOrder$Params, context?: HttpContext): Observable<PurchaseOrder> {
    return this.placeOrder$Response(params, context).pipe(
      map((r: StrictHttpResponse<PurchaseOrder>): PurchaseOrder => r.body)
    );
  }

  /** Path part for operation `getOrders()` */
  static readonly GetOrdersPath = '/orders';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getOrders()` instead.
   *
   * This method doesn't expect any request body.
   */
  getOrders$Response(params?: GetOrders$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<OrderDetails>>> {
    return getOrders(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getOrders$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getOrders(params?: GetOrders$Params, context?: HttpContext): Observable<Array<OrderDetails>> {
    return this.getOrders$Response(params, context).pipe(
      map((r: StrictHttpResponse<Array<OrderDetails>>): Array<OrderDetails> => r.body)
    );
  }

  /** Path part for operation `getOrderInventory()` */
  static readonly GetOrderInventoryPath = '/order/inventory/{productId}';

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `getOrderInventory()` instead.
   *
   * This method doesn't expect any request body.
   */
  getOrderInventory$Response(params: GetOrderInventory$Params, context?: HttpContext): Observable<StrictHttpResponse<Inventory>> {
    return getOrderInventory(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `getOrderInventory$Response()` instead.
   *
   * This method doesn't expect any request body.
   */
  getOrderInventory(params: GetOrderInventory$Params, context?: HttpContext): Observable<Inventory> {
    return this.getOrderInventory$Response(params, context).pipe(
      map((r: StrictHttpResponse<Inventory>): Inventory => r.body)
    );
  }

}
