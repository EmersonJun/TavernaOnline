import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { DiarioService } from '../../core/services/diario.service';
import { Anotacao, Visibilidade } from '../../core/models/diario.model';

@Component({
  selector: 'app-diario',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatChipsModule
  ],
  templateUrl: './diario.component.html'
})
export class DiarioComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly diarioService = inject(DiarioService);

  private campanhaId = '';
  protected readonly anotacoes = signal<Anotacao[]>([]);
  protected readonly visibilidades: Visibilidade[] = ['PUBLICA', 'SOMENTE_MESTRE'];

  protected readonly form = this.fb.nonNullable.group({
    titulo: ['', [Validators.required]],
    conteudo: ['', [Validators.required]],
    visibilidade: ['PUBLICA' as Visibilidade, [Validators.required]]
  });

  ngOnInit(): void {
    this.campanhaId = this.route.snapshot.paramMap.get('campanhaId') ?? '';
    this.carregar();
  }

  carregar(): void {
    this.diarioService.listar(this.campanhaId).subscribe((lista) => this.anotacoes.set(lista));
  }

  salvar(): void {
    if (this.form.invalid) {
      return;
    }
    const valor = this.form.getRawValue();
    this.diarioService.criar(this.campanhaId, {
      titulo: valor.titulo,
      conteudo: valor.conteudo,
      visibilidade: valor.visibilidade,
      sessaoId: null
    }).subscribe(() => {
      this.form.reset({ visibilidade: 'PUBLICA' });
      this.carregar();
    });
  }

  excluir(anotacao: Anotacao): void {
    this.diarioService.excluir(anotacao.id).subscribe(() => this.carregar());
  }
}
