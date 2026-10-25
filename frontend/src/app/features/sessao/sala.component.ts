import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { AuthService } from '../../core/auth/auth.service';
import { WebsocketService } from '../../core/services/websocket.service';
import { SessaoService } from '../../core/services/sessao.service';
import { CombateService, CombatenteEntrada } from '../../core/services/combate.service';
import { Combate, Mensagem, Rolagem } from '../../core/models/sessao.model';

@Component({
  selector: 'app-sala',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCheckboxModule
  ],
  templateUrl: './sala.component.html',
  styleUrl: './sala.component.scss'
})
export class SalaComponent implements OnInit, OnDestroy {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly auth = inject(AuthService);
  private readonly ws = inject(WebsocketService);
  private readonly sessaoService = inject(SessaoService);
  private readonly combateService = inject(CombateService);

  private readonly assinaturas = new Subscription();
  protected sessaoId = '';

  protected readonly mensagens = signal<Mensagem[]>([]);
  protected readonly rolagens = signal<Rolagem[]>([]);
  protected readonly online = signal<string[]>([]);
  protected readonly combate = signal<Combate | null>(null);
  protected readonly combatentes = signal<CombatenteEntrada[]>([]);

  protected readonly formMensagem = this.fb.nonNullable.group({
    conteudo: ['', [Validators.required]]
  });

  protected readonly formRolagem = this.fb.nonNullable.group({
    notacao: ['', [Validators.required]],
    motivo: [''],
    secreta: [false]
  });

  protected readonly formCombatente = this.fb.nonNullable.group({
    nome: ['', [Validators.required]],
    iniciativa: [0, [Validators.required]]
  });

  ngOnInit(): void {
    this.sessaoId = this.route.snapshot.paramMap.get('sessaoId') ?? '';
    const token = this.auth.obterToken();
    if (token) {
      this.ws.conectar(token);
    }

    this.observarTopicos();
    this.carregarHistorico();
    this.ws.enviar(`/app/sessao/${this.sessaoId}/entrar`, {});
  }

  private observarTopicos(): void {
    const base = `/topic/sessao.${this.sessaoId}`;
    this.assinaturas.add(
      this.ws.observar<Mensagem>(`${base}.chat`).subscribe((m) =>
        this.mensagens.update((lista) => [...lista, m]))
    );
    this.assinaturas.add(
      this.ws.observar<Rolagem>(`${base}.rolagem`).subscribe((r) =>
        this.rolagens.update((lista) => [...lista, r]))
    );
    this.assinaturas.add(
      this.ws.observar<Rolagem>(`/user/queue/sessao.${this.sessaoId}.rolagem`).subscribe((r) =>
        this.rolagens.update((lista) => [...lista, r]))
    );
    this.assinaturas.add(
      this.ws.observar<string[]>(`${base}.presenca`).subscribe((nomes) => this.online.set(nomes))
    );
    this.assinaturas.add(
      this.ws.observar<Combate>(`${base}.combate`).subscribe((c) => this.combate.set(c))
    );
  }

  private carregarHistorico(): void {
    this.sessaoService.historicoMensagens(this.sessaoId).subscribe((lista) => this.mensagens.set(lista));
    this.sessaoService.historicoRolagens(this.sessaoId).subscribe((lista) => this.rolagens.set(lista));
    this.combateService.estado(this.sessaoId).subscribe((c) => this.combate.set(c));
  }

  enviarMensagem(): void {
    if (this.formMensagem.invalid) {
      return;
    }
    this.ws.enviar(`/app/sessao/${this.sessaoId}/chat`, this.formMensagem.getRawValue());
    this.formMensagem.reset();
  }

  rolar(): void {
    if (this.formRolagem.invalid) {
      return;
    }
    const valor = this.formRolagem.getRawValue();
    this.ws.enviar(`/app/sessao/${this.sessaoId}/rolagem`, {
      notacao: valor.notacao,
      motivo: valor.motivo || null,
      secreta: valor.secreta,
      personagemId: null
    });
    this.formRolagem.reset({ secreta: false });
  }

  adicionarCombatente(): void {
    if (this.formCombatente.invalid) {
      return;
    }
    const valor = this.formCombatente.getRawValue();
    this.combatentes.update((lista) => [...lista, { ...valor, personagemId: null }]);
    this.formCombatente.reset({ iniciativa: 0 });
  }

  iniciarCombate(): void {
    this.combateService.iniciar(this.sessaoId, this.combatentes()).subscribe((c) => {
      this.combate.set(c);
      this.combatentes.set([]);
    });
  }

  proximoTurno(): void {
    this.combateService.proximoTurno(this.sessaoId).subscribe((c) => this.combate.set(c));
  }

  encerrarCombate(): void {
    this.combateService.encerrar(this.sessaoId).subscribe((c) => this.combate.set(c));
  }

  hora(iso: string | null): string {
    if (!iso) {
      return '';
    }
    const data = new Date(iso);
    return data.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
  }

  inicial(nome: string | null): string {
    return (nome ?? '?').trim().charAt(0).toUpperCase();
  }

  abs(valor: number): number {
    return Math.abs(valor);
  }

  ngOnDestroy(): void {
    this.assinaturas.unsubscribe();
    this.ws.desconectar();
  }
}
