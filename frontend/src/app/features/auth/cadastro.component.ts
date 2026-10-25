import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, MatFormFieldModule, MatInputModule, MatButtonModule],
  template: `
    <div class="tela">
      <div class="form-box">
        <h1 class="wordmark">Taverna<span class="on">Online</span></h1>
        <h2 class="titulo">Criar conta de aventureiro</h2>
        <p class="sub">Forje seu nome e junte-se à mesa.</p>

        <form [formGroup]="form" (ngSubmit)="cadastrar()">
          <label class="rot">Nome</label>
          <mat-form-field class="full-width" appearance="fill">
            <input matInput formControlName="nome" placeholder="Seu nome de aventureiro" />
          </mat-form-field>

          <label class="rot">E-mail</label>
          <mat-form-field class="full-width" appearance="fill">
            <input matInput type="email" formControlName="email" placeholder="voce@taverna.rpg" />
          </mat-form-field>

          <label class="rot">Senha (mínimo 8 caracteres)</label>
          <mat-form-field class="full-width" appearance="fill">
            <input matInput type="password" formControlName="senha" placeholder="••••••••••" />
          </mat-form-field>

          <button mat-flat-button class="full-width btn-grande" [disabled]="form.invalid || carregando()">
            Forjar conta
          </button>
        </form>

        <p class="novo">Já faz parte da mesa? <a routerLink="/login">Entrar</a></p>
      </div>
    </div>
  `,
  styles: [`
    .tela {
      position: fixed; inset: 0;
      display: flex; align-items: center; justify-content: center;
      padding: 40px;
      background:
        radial-gradient(700px 420px at 50% 0%, rgba(216,40,60,.08), transparent 70%),
        #0c0c0e;
      overflow-y: auto;
    }
    .form-box { width: 100%; max-width: 420px; }
    .wordmark { font-family: 'Cinzel', serif; font-size: 30px; color: var(--cream); margin: 0 0 24px; }
    .wordmark .on { color: var(--red); }
    .titulo { font-family: 'Cinzel', serif; font-size: 24px; color: var(--cream); margin: 0 0 8px; }
    .sub { color: var(--muted); margin: 0 0 24px; }
    .rot { display: block; font-size: 11px; letter-spacing: 1.5px; text-transform: uppercase; color: var(--muted); margin: 4px 0 2px; }
    .btn-grande { height: 54px; margin-top: 10px; }
    .novo { text-align: center; color: var(--muted); margin-top: 22px; }
  `]
})
export class CadastroComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly carregando = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.maxLength(120)]],
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required, Validators.minLength(8)]]
  });

  cadastrar(): void {
    if (this.form.invalid) {
      return;
    }
    this.carregando.set(true);
    this.auth.cadastrar(this.form.getRawValue()).subscribe({
      next: () => this.router.navigate(['/campanhas']),
      error: () => this.carregando.set(false)
    });
  }
}
