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
    <mat-toolbar color="primary">
      <span routerLink="/campanhas" style="cursor: pointer">🍺 TavernaOnline</span>
      <span class="spacer"></span>
      @if (auth.estaAutenticado()) {
        <button mat-button (click)="auth.sair()">
          <mat-icon>logout</mat-icon>
          Sair
        </button>
      }
    </mat-toolbar>
    <main class="container">
      <router-outlet></router-outlet>
    </main>
  `
})
export class AppComponent {
  protected readonly auth = inject(AuthService);
}
