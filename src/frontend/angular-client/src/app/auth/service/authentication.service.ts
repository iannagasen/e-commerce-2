import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AuthenticationResponse } from "../model/authentication-response";
import { flatMap, map, mergeMap, tap } from 'rxjs/operators';
import { EMPTY, Observable, of } from "rxjs";
import { ActivatedRoute } from "@angular/router";
import { UserInfo } from "../model/user-info";
import { AuthStatus } from "../model/auth-status";

export const ACCESS_TOKEN = 'access_token';
export const REFRESH_TOKEN = 'refresh_token';

@Injectable({ providedIn: 'root' })
export class AuthenticationService {
  
  // TODO: use environment variables
  private readonly AUTH_SERVER_URL = 'http://localhost:8080';
  private readonly REDIRECT_URL = 'http://localhost:4200';
  private readonly CLIENT_ID = 'angular_client';
  private readonly CLIENT_SECRET = 'angular_client_secret';
  private readonly SCOPE = 'openid';
  
  
  constructor(
    private _http: HttpClient, 
  ) {}

  public checkStatus(): Observable<AuthStatus> {
    console.log('Checking if user is logged in')
    const accessToken = localStorage.getItem(ACCESS_TOKEN);
    
    if (accessToken === null || accessToken === '') {
      return of({ val: 'LOGGED_OUT' })
    }
    
    return this.introspectToken(accessToken).pipe(
      map(data => ({ val: data.active ? 'LOGGED_IN' : 'LOGGED_OUT' }))
    );
  }
  
  public redirectToOauthLogin() {
    window.location.href = 
    `${this.AUTH_SERVER_URL}/oauth2/authorize?response_type=code` +
    `&client_id=${this.CLIENT_ID}` +
    `&redirect_uri=${this.REDIRECT_URL}` +
    `&scope=${this.SCOPE}` +
    `&state=abc123`;
  }
  
  
  public exchangeCodeForToken(code: string): Observable<AuthenticationResponse> {
    const params = new URLSearchParams();
    params.append('grant_type', 'authorization_code');
    params.append('code', code);
    
    params.append('redirect_uri', this.REDIRECT_URL);
    params.append('client_id', this.CLIENT_ID);
    params.append('client_secret', this.CLIENT_SECRET);
    
    const basicAuth = btoa(`${this.CLIENT_ID}:${this.CLIENT_SECRET}`);
    const headers = {
      'Content-type': 'application/x-www-form-urlencoded',
      'Authorization': `Basic ${basicAuth}`
    };
    
    console.log("performing login");
    
    return this._http.post(`${this.AUTH_SERVER_URL}/oauth2/token`, params.toString(), { headers }).pipe(
      map(data => data as AuthenticationResponse),
      tap(data => this.saveToken(data))
    );
  }
  
  public getUserInfo() {
    const accessToken = localStorage.getItem(ACCESS_TOKEN);
    console.log("Access Token" + accessToken);
    if (accessToken === null || accessToken === '') {
      return EMPTY;
    }
    
    const headers = {
      'Content-type': 'application/x-www-form-urlencoded',
      'Authorization': `Bearer ${accessToken}`
    };
    
    return this._http.get(`${this.AUTH_SERVER_URL}/userinfo`, { headers })
    .pipe(map(data => data as UserInfo), tap(console.log));
  }
  
  private saveToken(token: AuthenticationResponse) {
    localStorage.setItem(REFRESH_TOKEN, token.refresh_token)
    localStorage.setItem(ACCESS_TOKEN, token.access_token)
    console.log('Obtained access token')
  }
  
  private introspectToken(token: string): Observable<{ active: boolean}>  {
    const params = new URLSearchParams();
    params.append('token', token);
    console.log('Introspecting token: ' + token);
    
    const basicAuth = btoa(`${this.CLIENT_ID}:${this.CLIENT_SECRET}`);
    const headers = {
      'Content-type': 'application/x-www-form-urlencoded',
      'Authorization': `Basic ${basicAuth}`
    };
    
    return this._http.post(`${this.AUTH_SERVER_URL}/oauth2/introspect`, params.toString(), { headers }).pipe(
      map((data: any) => ({
        active: data.active
      })),
      tap(data => console.log('Token is active: ' + data.active)) 
    );
  }
  
  logout() {
    const params = new URLSearchParams()

    const accessToken = localStorage.getItem(ACCESS_TOKEN);
    console.log("Access Token" + accessToken);
    if (accessToken === null || accessToken === '') {
      return EMPTY;
    }

    params.append('token', accessToken)
    const basicAuth = btoa(`${this.CLIENT_ID}:${this.CLIENT_SECRET}`);
    const headers = {
      'Content-type': 'application/x-www-form-urlencoded',
      'Authorization': `Basic ${basicAuth}`
    };
    return this._http.post(`${this.AUTH_SERVER_URL}/oauth2/revoke`, params.toString(), { headers }).pipe(
      tap(data => console.log('data: ' + data)),
      mergeMap(d => {
        localStorage.removeItem(ACCESS_TOKEN)
        localStorage.removeItem(REFRESH_TOKEN)
        return of(true)
      })
    );   
  }
  
}