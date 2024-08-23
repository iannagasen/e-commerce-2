import { HttpErrorResponse, HttpHandlerFn, HttpHeaders, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { ACCESS_TOKEN, REFRESH_TOKEN } from '../service/authentication.service';
import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HomeRerouterService {

  constructor(
    private _router: Router
  ) { }

  routeToHome(req: HttpRequest<unknown>, next: HttpHandlerFn) {
    console.log('Intercepting request: ' + req.url);
    // insert other requests to be filter
    const excluding: boolean = 
        req.url.includes('/oauth2') || req.url.includes('/public') ; // /oauth2 request have other authentication methods

    if (excluding) return next(req);

    let baseHeaders: { [key: string]: string } = {
      // 'Accept': 'application/json',
      // 'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Headers': 'Content-Type',
      'Access-Control-Allow-Methods': 'GET,POST,OPTIONS,DELETE,PUT'
    };
  
    // TODO: maybe call /oauth2/introspect to check validity of token
    const isStillAuthenticated = typeof localStorage !== 'undefined'
        && localStorage.getItem(ACCESS_TOKEN) !== null
        && localStorage.getItem(ACCESS_TOKEN) !== '';
  
    if (isStillAuthenticated) {
      baseHeaders['Authorization'] = `Bearer ${localStorage.getItem(ACCESS_TOKEN)}`;
    }
  
    // update the headers of the request
    req = req.clone({
      setHeaders: baseHeaders
    })
  
    return next(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          localStorage?.removeItem(ACCESS_TOKEN)
          localStorage?.removeItem(REFRESH_TOKEN)
          this._router.navigate(['/'])
        }
        return throwError(() => error);
      }));
  }

}

export const AuthenticationInterceptor: HttpInterceptorFn = (req, next, ) => {
  return inject(HomeRerouterService).routeToHome(req, next);
};