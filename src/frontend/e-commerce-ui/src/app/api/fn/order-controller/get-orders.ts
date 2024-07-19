/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { OrderDetails } from '../../models/order-details';

export interface GetOrders$Params {
}

export function getOrders(http: HttpClient, rootUrl: string, params?: GetOrders$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<OrderDetails>>> {
  const rb = new RequestBuilder(rootUrl, getOrders.PATH, 'get');
  if (params) {
  }

  return http.request(
    rb.build({ responseType: 'blob', accept: '*/*', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<Array<OrderDetails>>;
    })
  );
}

getOrders.PATH = '/orders';
