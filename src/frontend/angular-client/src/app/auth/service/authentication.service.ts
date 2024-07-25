import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AuthenticationResponse } from "../model/authentication-response";
import { map } from 'rxjs/operators';
import { Observable } from "rxjs";

export const ACCESS_TOKEN = 'access_token';
export const REFRESH_TOKEN = 'refresh_token';

@Injectable({ providedIn: 'root' })
export class AuthenticationService {

  // TODO: use environment variables
  private readonly AUTH_SERVER_URL = 'http://localhost:8080';
  private readonly REDIRECT_URL = 'http://localhost:4200';
  private readonly CLIENT_ID = 'angular_client';
  private readonly CLIENT_SECRET = 'angular_client_secret';


  constructor(
    private _http: HttpClient
  ) {}

  isLoggedIn() {
    console.log('Checking if user is logged in')
    const accessToken = localStorage.getItem(ACCESS_TOKEN);

    if (accessToken === null || accessToken === '') {
      return false;
    }

    let result = false;
    this.introspectToken(accessToken).subscribe({
      next: data => {
        if (data.active) {
          result = true;
        } else {
          localStorage.removeItem(ACCESS_TOKEN);
          localStorage.removeItem(REFRESH_TOKEN);
          result = false;
        }
      },
      error: err => {
        console.error(err);
        result = false;
      }
    });
    return result;
  }

  exchangeCodeForToken(code: string) {
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

    return this._http.post(`${this.AUTH_SERVER_URL}/oauth2/token`, params.toString(), { headers })
      .subscribe({
        next: data => this.saveToken(data as AuthenticationResponse),
        error: err => alert('Invalid code')
      })
  }

  private saveToken(token: AuthenticationResponse) {
    localStorage.setItem(ACCESS_TOKEN, token.access_token)
    localStorage.setItem(REFRESH_TOKEN, token.refresh_token)
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
      }))
    );
  }

}