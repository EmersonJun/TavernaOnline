import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, MatFormFieldModule, MatInputModule, MatButtonModule],
  template: `
    <div class="split">
      <section class="marca">
        <div class="pendoes">
          <span class="pendao"></span><span class="pendao"></span><span class="pendao"></span>
        </div>
        <div class="selo-linha">
          <span class="brasao">
            <svg width="26" height="30" viewBox="0 0 26 30" fill="none">
              <path d="M13 1.5 23 5v10c0 7-4.4 11.3-10 13.5C7.4 26.3 3 22 3 15V5l10-3.5Z" fill="#161619" stroke="#d8283c" stroke-width="1.6"/>
              <path d="M13 8.2l1.6 3.3h3.4l-2.7 2.2 1 3.4-3.3-2.1-3.3 2.1 1-3.4-2.7-2.2h3.4L13 8.2Z" fill="#d8283c"/>
            </svg>
          </span>
          <div>
            <div class="eyebrow">Mesa Virtual</div>
            <div class="stack">Java · Spring Boot · Angular</div>
          </div>
        </div>
        <div class="marca-base">
          <h1 class="wordmark">Taverna<span class="on">Online</span></h1>
          <p class="tagline">Reúna a mesa, role os dados e conduza a batalha em tempo real. Toda a aventura sincronizada, do convite ao golpe final.</p>
          <div class="destaque"><span class="eyebrow">Campanha em destaque</span><span class="sep"></span>Reino das Bandeiras Vermelhas</div>
        </div>
      </section>

      <section class="form-lado">
        <div class="form-box">
          <h2 class="titulo">Entrar na taverna</h2>
          <p class="sub">Acesse com sua conta de aventureiro ou use um código de convite para sentar à mesa.</p>

          <form [formGroup]="form" (ngSubmit)="entrar()">
            <label class="rot">E-mail</label>
            <mat-form-field class="full-width" appearance="fill">
              <input matInput type="email" formControlName="email" placeholder="voce@taverna.rpg" />
            </mat-form-field>

            <label class="rot">Senha</label>
            <mat-form-field class="full-width" appearance="fill">
              <input matInput type="password" formControlName="senha" placeholder="••••••••••" />
            </mat-form-field>

            <button mat-flat-button class="full-width btn-grande" [disabled]="form.invalid || carregando()">
              Entrar no reino
            </button>
          </form>

          <div class="ou"><span>ou</span></div>

          <label class="rot">Código de convite</label>
          <div class="convite-linha">
            <mat-form-field class="full-width" appearance="fill">
              <input matInput [value]="codigo()" (input)="codigo.set($any($event.target).value)" placeholder="BANDRAG8" />
            </mat-form-field>
            <button mat-stroked-button class="btn-sentar" (click)="sentar()">Sentar</button>
          </div>

          <p class="novo">Novo por aqui? <a routerLink="/cadastro">Criar conta de aventureiro</a></p>
        </div>
      </section>
    </div>
  `,
  styles: [`
    .split {
      position: fixed; inset: 0;
      display: grid; grid-template-columns: 1fr 1fr;
      background: #0c0c0e;
    }
    .marca {
      position: relative;
      padding: 52px 56px;
      display: flex; flex-direction: column;
      border-right: 1px solid rgba(255,255,255,.06);
      background:
        radial-gradient(600px 400px at 0% 100%, rgba(216,40,60,.08), transparent 70%),
        #0c0c0e;
    }
    .pendoes { display: flex; gap: 12px; }
    .pendao {
      width: 30px; height: 56px;
      background: linear-gradient(180deg, #e8404f, #9e1626);
      clip-path: polygon(0 0,100% 0,100% 100%,50% 78%,0 100%);
      box-shadow: 0 8px 18px rgba(0,0,0,.5);
    }
    .selo-linha { display: flex; align-items: center; gap: 16px; margin-top: 60px; }
    .brasao {
      width: 56px; height: 56px; border-radius: 12px;
      display: flex; align-items: center; justify-content: center;
      border: 1px solid rgba(216,40,60,.4); background: #121214;
    }
    .stack { color: var(--text); font-size: 14px; margin-top: 2px; }
    .marca-base { margin-top: auto; }
    .wordmark {
      font-family: 'Cinzel', serif; font-weight: 700;
      font-size: 62px; line-height: 1; color: var(--cream); margin: 0;
    }
    .wordmark .on { color: var(--red); }
    .tagline { color: var(--text); font-size: 17px; line-height: 1.6; max-width: 440px; margin: 22px 0 28px; }
    .destaque {
      display: inline-flex; align-items: center; gap: 14px;
      padding: 13px 20px; border-radius: 10px;
      border: 1px solid rgba(255,255,255,.08); background: #121214;
      color: var(--cream); font-size: 14px;
    }
    .destaque .sep { width: 1px; height: 16px; background: rgba(255,255,255,.15); }

    .form-lado { display: flex; align-items: center; justify-content: center; padding: 40px; }
    .form-box { width: 100%; max-width: 420px; }
    .titulo { font-family: 'Cinzel', serif; font-size: 30px; color: var(--cream); margin: 0 0 10px; }
    .sub { color: var(--muted); line-height: 1.6; margin: 0 0 26px; }
    .rot { display: block; font-size: 11px; letter-spacing: 1.5px; text-transform: uppercase; color: var(--muted); margin: 4px 0 2px; }
    .btn-grande { height: 54px; margin-top: 10px; font-size: 14px; }
    .ou { display: flex; align-items: center; gap: 14px; margin: 22px 0 12px; color: var(--muted-2); font-size: 12px; letter-spacing: 2px; }
    .ou::before, .ou::after { content: ''; flex: 1; height: 1px; background: rgba(255,255,255,.08); }
    .convite-linha { display: flex; gap: 12px; align-items: flex-start; }
    .convite-linha mat-form-field { flex: 1; }
    .btn-sentar { height: 54px; color: var(--red-bright) !important; border-color: rgba(216,40,60,.5) !important; }
    .novo { text-align: center; color: var(--muted); margin-top: 22px; }

    @media (max-width: 860px) {
      .split { grid-template-columns: 1fr; overflow-y: auto; }
      .marca { display: none; }
    }
  `]
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly snack = inject(MatSnackBar);

  protected readonly carregando = signal(false);
  protected readonly codigo = signal('');

  protected readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required]]
  });

  entrar(): void {
    if (this.form.invalid) {
      return;
    }
    this.carregando.set(true);
    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => this.router.navigate(['/campanhas']),
      error: () => this.carregando.set(false)
    });
  }

  sentar(): void {
    const codigo = this.codigo().trim();
    if (!codigo) {
      return;
    }
    try {
      localStorage.setItem('taverna.convite', codigo.toUpperCase());
    } catch {
    }
    this.snack.open('Entre na sua conta para sentar à mesa com este código.', 'Fechar', { duration: 5000 });
  }
}
