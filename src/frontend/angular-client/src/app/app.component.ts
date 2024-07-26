import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './api/authservice/auth.service';
import { AuthenticationService } from './auth/service/authentication.service';
import { HttpClient } from '@angular/common/http';
import { EMPTY, map, merge, mergeMap, Observable, of, tap } from 'rxjs';
import { fromPromise } from 'rxjs/internal/observable/innerFrom';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet,CommonModule],
  styleUrl: './app.component.scss',
  template: `
    <div *ngIf="authStatus$ | async as status">
      <div>{{status.isLoggedIn ? 'User Logged in' : 'No user Logged in'}}</div>

      <div *ngIf="!status.isLoggedIn">
        <div>Message from backend</div>

        <button (click)="loginViaOauth2()">Login using oauth2</button>
      </div>
    </div>
  `

})
export class AppComponent implements OnInit {

  authStatus$!: Observable<{ isLoggedIn: boolean}>;

  constructor(
    private auth: AuthenticationService,
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) { }
  
  ngOnInit(): void {
    this.authStatus$ = this.auth.isLoggedIn().pipe(
      tap(data => console.log("Is user logged in: " + data)),
      mergeMap(loggedIn => {
        if (loggedIn) {
          return of(true);
        } else {
          return this.route.queryParams.pipe(
            mergeMap(params => { 
              if (params?.['code']) {
                return this.auth.exchangeCodeForToken(params['code']).pipe(
                  mergeMap((_) => fromPromise(this.router.navigate([],{
                    queryParams: { code: null, state: null },
                    queryParamsHandling: 'merge',
                    replaceUrl: true
                  }))),
                  map(() => true));
              } else {
                return of(false);
              } 
            })
          );
        }
      }),
      map(isLoggedIn => ({ isLoggedIn }))
    )
  }

  loginViaOauth2() {
    this.auth.redirectToOauthLogin();
  }


}
