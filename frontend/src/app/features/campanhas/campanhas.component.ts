import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatListModule } from '@angular/material/list';
import { CampanhaService } from '../../core/services/campanha.service';
import { SessaoService } from '../../core/services/sessao.service';
import { Campanha } from '../../core/models/campanha.model';
import { Sessao } from '../../core/models/sessao.model';

@Component({
  selector: 'app-campanhas',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatExpansionModule,
    MatListModule
  ],
  templateUrl: './campanhas.component.html'
})
export class CampanhasComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly campanhaService = inject(CampanhaService);
  private readonly sessaoService = inject(SessaoService);

  protected readonly campanhas = signal<Campanha[]>([]);
  protected readonly sessoesPorCampanha = signal<Record<string, Sessao[]>>({});

  protected readonly formCriar = this.fb.nonNullable.group({
    nome: ['', [Validators.required]],
    sistema: ['', [Validators.required]],
    descricao: ['']
  });

  protected readonly formEntrar = this.fb.nonNullable.group({
    codigoConvite: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(8)]]
  });

  protected readonly formSessao = this.fb.nonNullable.group({
    titulo: ['', [Validators.required]]
  });

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.campanhaService.listarMinhas().subscribe((lista) => this.campanhas.set(lista));
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
        this.carregar();
      });
  }

  carregarSessoes(campanhaId: string): void {
    this.sessaoService.listar(campanhaId).subscribe((lista) => {
      this.sessoesPorCampanha.update((atual) => ({ ...atual, [campanhaId]: lista }));
    });
  }

  criarSessao(campanhaId: string): void {
    if (this.formSessao.invalid) {
      return;
    }
    this.sessaoService.criar(campanhaId, this.formSessao.getRawValue().titulo).subscribe(() => {
      this.formSessao.reset();
      this.carregarSessoes(campanhaId);
    });
  }

  sessoesDe(campanhaId: string): Sessao[] {
    return this.sessoesPorCampanha()[campanhaId] ?? [];
  }
}
