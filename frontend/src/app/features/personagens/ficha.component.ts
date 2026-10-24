import { Component, OnInit, inject, signal } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatListModule } from '@angular/material/list';
import { PersonagemService } from '../../core/services/personagem.service';
import { Personagem, PersonagemRequest } from '../../core/models/personagem.model';

@Component({
  selector: 'app-ficha',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCheckboxModule,
    MatListModule
  ],
  templateUrl: './ficha.component.html'
})
export class FichaComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly personagemService = inject(PersonagemService);

  private campanhaId = '';
  protected readonly personagens = signal<Personagem[]>([]);
  protected readonly editandoId = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    nome: ['', [Validators.required]],
    classe: [''],
    raca: [''],
    nivel: [1, [Validators.required, Validators.min(1)]],
    pvAtual: [0, [Validators.min(0)]],
    pvMaximo: [0, [Validators.min(0)]],
    classeArmadura: [10, [Validators.min(0)]],
    atributos: this.fb.nonNullable.group({
      forca: [10, [Validators.min(1), Validators.max(30)]],
      destreza: [10, [Validators.min(1), Validators.max(30)]],
      constituicao: [10, [Validators.min(1), Validators.max(30)]],
      inteligencia: [10, [Validators.min(1), Validators.max(30)]],
      sabedoria: [10, [Validators.min(1), Validators.max(30)]],
      carisma: [10, [Validators.min(1), Validators.max(30)]]
    }),
    pericias: this.fb.array<ReturnType<FichaComponent['criarPericia']>>([]),
    inventario: this.fb.array<ReturnType<FichaComponent['criarItem']>>([])
  });

  ngOnInit(): void {
    this.campanhaId = this.route.snapshot.paramMap.get('campanhaId') ?? '';
    this.carregar();
  }

  get pericias(): FormArray {
    return this.form.get('pericias') as FormArray;
  }

  get inventario(): FormArray {
    return this.form.get('inventario') as FormArray;
  }

  criarPericia() {
    return this.fb.nonNullable.group({
      nome: ['', [Validators.required]],
      bonus: [0],
      proficiente: [false]
    });
  }

  criarItem() {
    return this.fb.nonNullable.group({
      nome: ['', [Validators.required]],
      quantidade: [1, [Validators.min(1)]],
      peso: [0],
      descricao: ['']
    });
  }

  adicionarPericia(): void {
    this.pericias.push(this.criarPericia());
  }

  removerPericia(indice: number): void {
    this.pericias.removeAt(indice);
  }

  adicionarItem(): void {
    this.inventario.push(this.criarItem());
  }

  removerItem(indice: number): void {
    this.inventario.removeAt(indice);
  }

  carregar(): void {
    this.personagemService.listar(this.campanhaId).subscribe((lista) => this.personagens.set(lista));
  }

  editar(personagem: Personagem): void {
    this.editandoId.set(personagem.id);
    this.pericias.clear();
    this.inventario.clear();
    personagem.pericias.forEach((p) => {
      const grupo = this.criarPericia();
      grupo.setValue({ nome: p.nome, bonus: p.bonus, proficiente: p.proficiente });
      this.pericias.push(grupo);
    });
    personagem.inventario.forEach((item) => {
      const grupo = this.criarItem();
      grupo.setValue({
        nome: item.nome,
        quantidade: item.quantidade,
        peso: item.peso,
        descricao: item.descricao ?? ''
      });
      this.inventario.push(grupo);
    });
    this.form.patchValue({
      nome: personagem.nome,
      classe: personagem.classe ?? '',
      raca: personagem.raca ?? '',
      nivel: personagem.nivel,
      pvAtual: personagem.pvAtual,
      pvMaximo: personagem.pvMaximo,
      classeArmadura: personagem.classeArmadura,
      atributos: personagem.atributos
    });
  }

  novo(): void {
    this.editandoId.set(null);
    this.pericias.clear();
    this.inventario.clear();
    this.form.reset({
      nivel: 1,
      pvAtual: 0,
      pvMaximo: 0,
      classeArmadura: 10,
      atributos: {
        forca: 10, destreza: 10, constituicao: 10,
        inteligencia: 10, sabedoria: 10, carisma: 10
      }
    });
  }

  salvar(): void {
    if (this.form.invalid) {
      return;
    }
    const valor = this.form.getRawValue();
    const request: PersonagemRequest = {
      nome: valor.nome,
      classe: valor.classe || null,
      raca: valor.raca || null,
      nivel: valor.nivel,
      pvAtual: valor.pvAtual,
      pvMaximo: valor.pvMaximo,
      classeArmadura: valor.classeArmadura,
      atributos: valor.atributos,
      pericias: valor.pericias,
      inventario: valor.inventario.map((item) => ({
        nome: item.nome,
        quantidade: item.quantidade,
        peso: item.peso,
        descricao: item.descricao || null
      }))
    };

    const id = this.editandoId();
    const acao = id
      ? this.personagemService.atualizar(id, request)
      : this.personagemService.criar(this.campanhaId, request);

    acao.subscribe(() => {
      this.novo();
      this.carregar();
    });
  }

  excluir(personagem: Personagem): void {
    this.personagemService.excluir(personagem.id).subscribe(() => this.carregar());
  }
}
