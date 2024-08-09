import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthenticationService } from './auth/service/authentication.service';
import { HttpClient } from '@angular/common/http';
import { concat, concatMap, EMPTY, filter, map, merge, mergeMap, Observable, of, tap } from 'rxjs';
import { fromPromise } from 'rxjs/internal/observable/innerFrom';
import { PublicComponent } from './home/public/public.component';
import { PublicCategoryComponent } from './category/public-category/public-category.component';
import { UserInfo } from './auth/model/user-info';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    CommonModule, 
    PublicComponent, 
    PublicCategoryComponent
  ],
  styleUrl: './app.component.scss',
  template: `
    <div *ngIf="authStatus$ | async as status">
      <!-- <ng-template [ngIf]="isHome$ | async"> -->
        <div>{{status.val === 'LOGGED_IN' ? 'User Logged in' : 'No user Logged in'}}</div>
        
        <div *ngIf="!(status.val === 'LOGGED_IN')">
          <div>Message from backend</div>
          <button (click)="loginViaOauth2()">Login using oauth2</button>
        </div>

        <div *ngIf="status.val === 'LOGGED_IN'">
          <button (click)="logout()">Logout</button>
        </div>

        <div *ngIf="user$ | async as userInfo">{{userInfo.sub}}</div>
      <!-- </ng-template> -->
    </div>
    <router-outlet />
  `

})
export class AppComponent implements OnInit {

  authStatus$!: Observable<AuthStatus>;
  isHome$!: Observable<boolean>;
  user$!: Observable<UserInfo>;

  constructor(
    private auth: AuthenticationService,
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) { }
  
  ngOnInit(): void {
    this.authStatus$ = this.checkLoggedInStatus();
    this.user$ = this.authStatus$.pipe(this.getUserInfoIfLoggedIn());
    // this.isHome$ = this.checkIfHome();
  
  }

  loginViaOauth2() {
    this.auth.redirectToOauthLogin();
  }

  logout() {
    this.authStatus$ = this.auth.logout().pipe(
      filter(data => data),
      
      map(data => ({ val: 'LOGGED_OUT' }))

    )
  }
  
  private authAction(authStatus: AuthStatus): Observable<AuthStatus> {
    switch (authStatus.val) {
      case 'LOGGED_IN': return of(authStatus);
      case 'LOGGED_OUT': return this.route.queryParams.pipe(
        mergeMap(params => { 
          if (params?.['code']) {
            return this.auth.exchangeCodeForToken(params['code']).pipe(
              mergeMap((_) => fromPromise(this.router.navigate([],{
                queryParams: { code: null, state: null },
                queryParamsHandling: 'merge',
                replaceUrl: true
              }))),
              map(() => ({ val: 'LOGGED_IN' } as AuthStatus)));
          } else {
            return of({ val: 'LOGGED_OUT' } as AuthStatus);
          }
        })
      );
      case 'TOKEN_EXPIRED': return of(authStatus);
      case 'NEW_USER': return of(authStatus);
    }

  }

  private checkLoggedInStatus() {
    /**
     * 1. Check logged in status
     * 2. If not logged in, check if there is a code in the query params (since we may be redirected from the oauth2 server)
     * 3. If there is a code, exchange it for a token
     */
    return this.auth.checkStatus().pipe(
      tap(data => console.log("Is user logged in: " + data)),
      mergeMap(authStatus => this.authAction(authStatus)),
    )
  }

  private checkIfHome() {
    return this.router.events.pipe(
      map(event => event instanceof NavigationEnd && event.url === '/'),
    )
  }

  private getUserInfoIfLoggedIn() {
    return (source: Observable<AuthStatus>) => source.pipe(
      filter(status => status.val === 'LOGGED_IN'),
      mergeMap(_ => this.auth.getUserInfo())
    )
  }

}
