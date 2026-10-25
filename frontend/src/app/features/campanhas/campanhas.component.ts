import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CampanhaService } from '../../core/services/campanha.service';
import { SessaoService } from '../../core/services/sessao.service';
import { Campanha, Participante } from '../../core/models/campanha.model';
import { Sessao } from '../../core/models/sessao.model';

@Component({
  selector: 'app-campanhas',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './campanhas.component.html',
  styleUrl: './campanhas.component.scss'
})
export class CampanhasComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly campanhaService = inject(CampanhaService);
  private readonly sessaoService = inject(SessaoService);
  private readonly router = inject(Router);
  private readonly snack = inject(MatSnackBar);

  protected readonly campanhas = signal<Campanha[]>([]);
  protected readonly sessoesDestaque = signal<Sessao[]>([]);
  protected readonly participantesDestaque = signal<Participante[]>([]);
  protected readonly mostrarForm = signal(false);

  protected readonly destaque = computed(() => this.campanhas()[0] ?? null);
  protected readonly outras = computed(() => this.campanhas().slice(1));

  protected readonly formCriar = this.fb.nonNullable.group({
    nome: ['', [Validators.required]],
    sistema: ['', [Validators.required]],
    descricao: ['']
  });

  protected readonly formEntrar = this.fb.nonNullable.group({
    codigoConvite: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(8)]]
  });

  ngOnInit(): void {
    const convite = this.lerConvitePendente();
    if (convite) {
      this.formEntrar.patchValue({ codigoConvite: convite });
      this.mostrarForm.set(true);
    }
    this.carregar();
  }

  carregar(): void {
    this.campanhaService.listarMinhas().subscribe((lista) => {
      this.campanhas.set(lista);
      const destaque = lista[0];
      if (destaque) {
        this.sessaoService.listar(destaque.id).subscribe((s) => this.sessoesDestaque.set(s));
        this.campanhaService.listarParticipantes(destaque.id)
          .subscribe((p) => this.participantesDestaque.set(p));
      }
    });
  }

  toggleForm(): void {
    this.mostrarForm.update((v) => !v);
  }

  criar(): void {
    if (this.formCriar.invalid) {
      return;
    }
    const valor = this.formCriar.getRawValue();
    this.campanhaService.criar({
      nome: valor.nome,
      sistema: valor.sistema,
      descricao: valor.descricao || null
    }).subscribe(() => {
      this.formCriar.reset();
      this.mostrarForm.set(false);
      this.carregar();
    });
  }

  entrar(): void {
    if (this.formEntrar.invalid) {
      return;
    }
    this.campanhaService.entrarPorCodigo(this.formEntrar.getRawValue().codigoConvite.toUpperCase())
      .subscribe(() => {
        this.formEntrar.reset();
        this.mostrarForm.set(false);
        this.carregar();
      });
  }

  criarSessao(campanhaId: string): void {
    const numero = this.sessoesDestaque().length + 1;
    this.sessaoService.criar(campanhaId, `Sessão ${numero}`).subscribe((s) => {
      this.sessoesDestaque.update((lista) => [...lista, s]);
      this.router.navigate(['/sessoes', s.id, 'sala']);
    });
  }

  abrirSala(campanha: Campanha): void {
    const sessoes = this.sessoesDestaque();
    const emAndamento = sessoes.find((s) => s.status === 'EM_ANDAMENTO');
    const alvo = emAndamento ?? sessoes[sessoes.length - 1];
    if (alvo) {
      this.router.navigate(['/sessoes', alvo.id, 'sala']);
      return;
    }
    if (campanha.meuPapel === 'MESTRE') {
      this.criarSessao(campanha.id);
      return;
    }
    this.snack.open('O mestre ainda não abriu uma sessão.', 'Fechar', { duration: 4000 });
  }

  verFichas(campanha: Campanha): void {
    this.router.navigate(['/campanhas', campanha.id, 'personagens']);
  }

  abrirDiario(campanha: Campanha): void {
    this.router.navigate(['/campanhas', campanha.id, 'diario']);
  }

  copiarCodigo(codigo: string): void {
    try {
      navigator.clipboard.writeText(codigo);
      this.snack.open('Código copiado!', 'Fechar', { duration: 2500 });
    } catch {
      this.snack.open(`Código: ${codigo}`, 'Fechar', { duration: 4000 });
    }
  }

  iniciais(nome: string): string {
    return nome.trim().charAt(0).toUpperCase();
  }

  private lerConvitePendente(): string | null {
    try {
      const codigo = localStorage.getItem('taverna.convite');
      if (codigo) {
        localStorage.removeItem('taverna.convite');
      }
      return codigo;
    } catch {
      return null;
    }
  }
}
