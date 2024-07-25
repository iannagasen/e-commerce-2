import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './api/authservice/auth.service';
import { AuthenticationService } from './auth/service/authentication.service';
import { HttpClient } from '@angular/common/http';
import { EMPTY, map, merge, mergeMap, Observable, of, tap } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet,CommonModule],
  styleUrl: './app.component.scss',
  template: `
    <!-- <div *ngIf="loggedIn$ | async as isLoggedIn">
      <div>{{isLoggedIn ? 'User Logged in' : 'No user Logged in'}}</div>

      <div *ngIf="!isLoggedIn">
        <div>Message from backend</div>
        <div *ngIf="publicMessage$ | async as msg">{{msg.data}}</div>

        <button (click)="loginViaOauth2()">Login using oauth2</button>
      </div>
    </div> -->
    <div>{{isLoggedIn ? 'User Logged in' : 'No user Logged in'}}</div>

    <div *ngIf="!isLoggedIn">
      <div>Message from backend</div>
      <div *ngIf="publicMessage$ | async as msg">{{msg.data}}</div>

      <button (click)="loginViaOauth2()">Login using oauth2</button>
    </div>

  `

})
export class AppComponent implements OnInit {

  publicMessage$!: Observable<{data: string}>;
  isLoggedIn = false;


  constructor(
    private auth: AuthenticationService,
    private route: ActivatedRoute,
    private http: HttpClient
  ) { }
  
  ngOnInit(): void {
    this.auth.isLoggedIn().pipe(
      tap(data => console.log("Is user logged in: " + data)),
      mergeMap(loggedIn => {
        if (loggedIn) {
          console.log("1");
          return of(true);
        } else {
          return this.route.queryParams.pipe(
            mergeMap(params => { 
              if (params?.['code']) {
                console.log("2");
                return this.auth.exchangeCodeForToken(params['code']).pipe(map(() => true));
              } else {
                console.log("3");
                return of(false);
              } 
            })
          );
        }
      })
    )
    .subscribe(data => {
      console.log("Data: " + data)
      this.isLoggedIn = data;
    });
  }

  loginViaOauth2() {
    this.auth.redirectToOauthLogin();
  }


}
