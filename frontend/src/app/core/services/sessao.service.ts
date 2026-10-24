import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Mensagem, Rolagem, Sessao } from '../models/sessao.model';

@Injectable({ providedIn: 'root' })
export class SessaoService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  listar(campanhaId: string): Observable<Sessao[]> {
    return this.http.get<Sessao[]>(`${this.api}/campanhas/${campanhaId}/sessoes`);
  }

  criar(campanhaId: string, titulo: string): Observable<Sessao> {
    return this.http.post<Sessao>(`${this.api}/campanhas/${campanhaId}/sessoes`, { titulo });
  }

  iniciar(sessaoId: string): Observable<Sessao> {
    return this.http.post<Sessao>(`${this.api}/sessoes/${sessaoId}/iniciar`, {});
  }

  encerrar(sessaoId: string): Observable<Sessao> {
    return this.http.post<Sessao>(`${this.api}/sessoes/${sessaoId}/encerrar`, {});
  }

  historicoMensagens(sessaoId: string): Observable<Mensagem[]> {
    return this.http.get<Mensagem[]>(`${this.api}/sessoes/${sessaoId}/mensagens`);
  }

  historicoRolagens(sessaoId: string): Observable<Rolagem[]> {
    return this.http.get<Rolagem[]>(`${this.api}/sessoes/${sessaoId}/rolagens`);
  }
}
