import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  token: string = "";

  private readonly RESOURCE_SERVER_URL = 'http://localhost:8103';
  private readonly AUTH_SERVER_URL = 'http://localhost:8080';

  constructor(private http: HttpClient) { }
  
  getPrivateContent(url: string): Observable<string> {
    return this.http.get<string>(`${this.RESOURCE_SERVER_URL}/private${url}`, {
      headers: new HttpHeaders({"Authorization": `Bearer ${this.token}`})
    });
  }

  getPublicContent(url: string): Observable<string> {
    return this.http.get<string>(this.AUTH_SERVER_URL + url);
  }
  getToken(): Observable<any> {
    throw new Error('Method not implemented.');
  }

}
