import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CadastroRequest, LoginRequest, TokenResponse } from '../models/auth.model';

const CHAVE_TOKEN = 'taverna.token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly url = `${environment.apiUrl}/auth`;

  private readonly tokenSignal = signal<string | null>(this.lerToken());
  readonly estaAutenticado = signal<boolean>(this.lerToken() !== null);

  cadastrar(request: CadastroRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.url}/cadastro`, request).pipe(
      tap((resposta) => this.guardarToken(resposta.token))
    );
  }

  login(request: LoginRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.url}/login`, request).pipe(
      tap((resposta) => this.guardarToken(resposta.token))
    );
  }

  sair(): void {
    this.tokenSignal.set(null);
    this.estaAutenticado.set(false);
    try {
      localStorage.removeItem(CHAVE_TOKEN);
    } catch {
    }
    this.router.navigate(['/login']);
  }

  obterToken(): string | null {
    return this.tokenSignal();
  }

  private guardarToken(token: string): void {
    this.tokenSignal.set(token);
    this.estaAutenticado.set(true);
    try {
      localStorage.setItem(CHAVE_TOKEN, token);
    } catch {
    }
  }

  private lerToken(): string | null {
    try {
      return localStorage.getItem(CHAVE_TOKEN);
    } catch {
      return null;
    }
  }
}
