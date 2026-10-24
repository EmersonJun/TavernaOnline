import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  template: `
    <mat-card>
      <mat-card-header>
        <mat-card-title>Criar conta</mat-card-title>
      </mat-card-header>
      <mat-card-content>
        <form [formGroup]="form" (ngSubmit)="cadastrar()">
          <mat-form-field class="full-width">
            <mat-label>Nome</mat-label>
            <input matInput formControlName="nome" />
          </mat-form-field>
          <mat-form-field class="full-width">
            <mat-label>E-mail</mat-label>
            <input matInput type="email" formControlName="email" />
          </mat-form-field>
          <mat-form-field class="full-width">
            <mat-label>Senha (minimo 8 caracteres)</mat-label>
            <input matInput type="password" formControlName="senha" />
          </mat-form-field>
          <button mat-flat-button color="primary" class="full-width"
                  [disabled]="form.invalid || carregando()">
            Cadastrar
          </button>
        </form>
      </mat-card-content>
      <mat-card-actions>
        <a mat-button routerLink="/login">Ja tenho conta</a>
      </mat-card-actions>
    </mat-card>
  `
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
