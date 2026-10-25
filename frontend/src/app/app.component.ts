import { Component, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from './core/auth/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, MatToolbarModule, MatButtonModule, MatIconModule],
  template: `
    @if (auth.estaAutenticado()) {
    <mat-toolbar>
      <span class="brand-logo" routerLink="/campanhas">
        <svg width="26" height="30" viewBox="0 0 26 30" fill="none" aria-hidden="true">
          <path d="M13 1.5 23 5v10c0 7-4.4 11.3-10 13.5C7.4 26.3 3 22 3 15V5l10-3.5Z" fill="#161619" stroke="#d8283c" stroke-width="1.6"/>
          <path d="M13 8.2l1.6 3.3h3.4l-2.7 2.2 1 3.4-3.3-2.1-3.3 2.1 1-3.4-2.7-2.2h3.4L13 8.2Z" fill="#d8283c"/>
        </svg>
        <span>Taverna<span class="on">Online</span></span>
      </span>
      <span class="spacer"></span>
      @if (auth.estaAutenticado()) {
        <button mat-button (click)="auth.sair()">Sair</button>
        <span class="avatar-top">E</span>
      }
    </mat-toolbar>
    }
    <main class="container">
      <router-outlet></router-outlet>
    </main>
  `,
  styles: [`
    .avatar-top {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 36px;
      height: 36px;
      margin-left: 14px;
      border-radius: 50%;
      background: #1c1c21;
      border: 1px solid #2a2a30;
      color: #f2ece3;
      font-weight: 600;
      font-size: 14px;
    }
  `]
})
export class AppComponent {
  protected readonly auth = inject(AuthService);
}
